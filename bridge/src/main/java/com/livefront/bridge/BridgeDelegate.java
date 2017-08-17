package com.livefront.bridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.livefront.bridge.wrapper.WrapperUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;

class BridgeDelegate {

    private static final String TAG = BridgeDelegate.class.getName();

    private static final String KEY_BUNDLE = "bundle_%s";
    private static final String KEY_UUID = "uuid_%s";

    /**
     * Time (in milliseconds) to wait between attempts to automatically clear stale data.
     */
    private static final int AUTO_DATA_CLEAR_INTERVAL_MS = 100;

    private boolean mIsClearAllowed = false;
    private boolean mIsFirstRestoreCall = true;
    private long mLastClearTime;
    private Map<String, Bundle> mUuidBundleMap = new HashMap<>();
    private Map<Object, String> mObjectUuidMap = new WeakHashMap<>();
    private SavedStateHandler mSavedStateHandler;
    private Set<String> mRecentUuids = new HashSet<>();
    private SharedPreferences mSharedPreferences;

    BridgeDelegate(@NonNull Context context,
                   @NonNull SavedStateHandler savedStateHandler) {
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mSavedStateHandler = savedStateHandler;
        registerForLifecycleEvents(context);
    }

    void clear(@NonNull Object target) {
        if (!mIsClearAllowed) {
            return;
        }
        String uuid = mObjectUuidMap.remove(target);
        if (uuid == null) {
            return;
        }
        clearDataFromDisk(uuid);
    }

    void clearAll() {
        mRecentUuids.clear();
        mUuidBundleMap.clear();
        mObjectUuidMap.clear();
        mSharedPreferences.edit()
                .clear()
                .apply();
    }

    private void clearDataForUuid(@NonNull String uuid) {
        mRecentUuids.remove(uuid);
        mUuidBundleMap.remove(uuid);
        clearDataFromDisk(uuid);
    }

    private void clearDataFromDisk(@NonNull String uuid) {
        mSharedPreferences.edit()
                .remove(getKeyForEncodedBundle(uuid))
                .apply();
    }

    /**
     * Attempts to clear data associated with references held weakly that have been cleared. Note
     * that there is no guarantee of success, as some references may still be held by the system
     * longer than is actually necessary.
     */
    private void clearStaleData() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastClearTime < AUTO_DATA_CLEAR_INTERVAL_MS) {
            // Avoid too many checks during the same stop event
            return;
        }
        mLastClearTime = currentTime;
        System.gc();

        // Remove all the remaining UUIDs in the object map from the recent UUID list. Anything
        // left represents objects that were garbage collected, so we should clear up any saved
        // state associated with them.
        Set<String> staleUuids = new HashSet<>(mRecentUuids);
        staleUuids.removeAll(mObjectUuidMap.values());
        for (String uuid : staleUuids) {
            clearDataForUuid(uuid);
        }
    }

    private String getKeyForEncodedBundle(@NonNull String uuid) {
        return String.format(KEY_BUNDLE, uuid);
    }

    private String getKeyForUuid(@NonNull Object target) {
        return String.format(KEY_UUID, target.getClass().getName());
    }

    @Nullable
    private Bundle readFromDisk(@NonNull String uuid) {
        String encodedString = mSharedPreferences.getString(getKeyForEncodedBundle(uuid), null);
        if (encodedString == null) {
            return null;
        }
        byte[] parcelBytes = Base64.decode(encodedString, 0);
        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(parcelBytes, 0, parcelBytes.length);
        parcel.setDataPosition(0);
        Bundle bundle = parcel.readBundle(BridgeDelegate.class.getClassLoader());
        parcel.recycle();
        return bundle;
    }

    @SuppressLint("NewApi")
    private void registerForLifecycleEvents(@NonNull Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // Below this version we'll simply never allow clearing because we don't have a great
            // hook for knowing when a config change is happening.
            mIsClearAllowed = false;
            return;
        }
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacksAdapter() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        mIsClearAllowed = true;
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        // Don't allow clearing during known configuration changes
                        mIsClearAllowed = !activity.isChangingConfigurations();
                    }
                }
        );
    }

    void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
        boolean isFirstRestoreCall = mIsFirstRestoreCall;
        mIsFirstRestoreCall = false;
        if (state == null) {
            if (isFirstRestoreCall) {
                mSharedPreferences.edit()
                        .clear()
                        .apply();
            }
            return;
        }
        String uuid = mObjectUuidMap.containsKey(target)
                ? mObjectUuidMap.get(target)
                : state.getString(getKeyForUuid(target), null);
        if (uuid == null) {
            return;
        }
        mObjectUuidMap.put(target, uuid);
        Bundle bundle = mUuidBundleMap.containsKey(uuid)
                ? mUuidBundleMap.get(uuid)
                : readFromDisk(uuid);
        if (bundle == null) {
            return;
        }
        WrapperUtils.unwrapOptimizedObjects(bundle);
        mSavedStateHandler.restoreInstanceState(target, bundle);
        clearDataForUuid(uuid);
    }

    void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
        String uuid = mObjectUuidMap.get(target);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            mObjectUuidMap.put(target, uuid);
        }
        state.putString(getKeyForUuid(target), uuid);
        Bundle bundle = new Bundle();
        mSavedStateHandler.saveInstanceState(target, bundle);
        if (bundle.isEmpty()) {
            // Don't bother saving empty bundles
            return;
        }
        WrapperUtils.wrapOptimizedObjects(bundle);
        mRecentUuids.add(uuid);
        mUuidBundleMap.put(uuid, bundle);
        writeToDisk(uuid, bundle);
        clearStaleData();
    }

    private void writeToDisk(@NonNull String uuid,
                             @NonNull Bundle bundle) {
        Parcel parcel = Parcel.obtain();
        parcel.writeBundle(bundle);
        String encodedString = Base64.encodeToString(parcel.marshall(), 0);
        mSharedPreferences.edit()
                .putString(getKeyForEncodedBundle(uuid), encodedString)
                .apply();
        parcel.recycle();
    }

}