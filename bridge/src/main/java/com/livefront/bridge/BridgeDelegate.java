package com.livefront.bridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Base64;

import com.livefront.bridge.wrapper.WrapperUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class BridgeDelegate {

    private static final String TAG = BridgeDelegate.class.getName();

    /**
     * Time to wait (in milliseconds) while processing the parcel data when going into the
     * background. This timeout simply serves as a safety precaution and is very unlikely to be
     * reached under normal conditions.
     */
    private static final long BACKGROUND_WAIT_TIMEOUT_MS = 5000;

    private static final String KEY_BUNDLE = "bundle_%s";
    private static final String KEY_UUID = "uuid_%s";

    private int mActivityCount = 0;
    private boolean mIsClearAllowed = false;
    private boolean mIsConfigChange = false;
    private boolean mIsFirstCreateCall = true;
    private volatile CountDownLatch mPendingWriteTasksLatch = null;
    private ExecutorService mExecutorService = Executors.newCachedThreadPool();
    private List<Runnable> mPendingWriteTasks = new CopyOnWriteArrayList<>();
    private Map<String, Bundle> mUuidBundleMap = new HashMap<>();
    private Map<Object, String> mObjectUuidMap = new WeakHashMap<>();
    private SavedStateHandler mSavedStateHandler;
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
        clearDataForUuid(uuid);
    }

    void clearAll() {
        mUuidBundleMap.clear();
        mObjectUuidMap.clear();
        mSharedPreferences.edit()
                .clear()
                .apply();
    }

    private void clearDataForUuid(@NonNull String uuid) {
        mUuidBundleMap.remove(uuid);
        clearDataFromDisk(uuid);
    }

    private void clearDataFromDisk(@NonNull String uuid) {
        mSharedPreferences.edit()
                .remove(getKeyForEncodedBundle(uuid))
                .apply();
    }

    private String getKeyForEncodedBundle(@NonNull String uuid) {
        return String.format(KEY_BUNDLE, uuid);
    }

    private String getKeyForUuid(@NonNull Object target) {
        return String.format(KEY_UUID, target.getClass().getName());
    }

    private boolean isAppInForeground() {
        return mActivityCount > 0;
    }

    /**
     * When the app is foregrounded, the given Bundle will be processed on a background thread and
     * then persisted to disk. When the app is proceeding to the background, this method will wait
     * for this task (and any others currently running in the background) to complete before
     * proceeding in order to prevent the app from becoming fully "stopped" (and therefore killable
     * by the OS before the data is saved).
     */
    private void queueDiskWriting(@NonNull final String uuid,
                                  @NonNull final Bundle bundle) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Process the Parcel and write the data to disk
                writeToDisk(uuid, bundle);

                // Remove this Runnable from the pending list
                mPendingWriteTasks.remove(this);

                // If the pending list is now empty, we can trigger the latch countdown to continue
                if (mPendingWriteTasks.isEmpty() && mPendingWriteTasksLatch != null) {
                    mPendingWriteTasksLatch.countDown();
                }
            }
        };
        if (mPendingWriteTasksLatch == null) {
            mPendingWriteTasksLatch = new CountDownLatch(1);
        }
        mPendingWriteTasks.add(runnable);
        mExecutorService.execute(runnable);
        if (isAppInForeground()) {
            // Allow the data to be processed in the background.
            return;
        }
        // Wait until (a) all pending tasks are complete or (b) we've reached the safety timeout.
        // In the meantime we will block to avoid the app prematurely going into the "stopped"
        // state.
        try {
            mPendingWriteTasksLatch.await(BACKGROUND_WAIT_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            // Interrupted for an unknown reason, simply proceed.
        }
        mPendingWriteTasksLatch = null;
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
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacksAdapter() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        mIsClearAllowed = true;
                        mIsConfigChange = false;

                        // Make sure we clear all data after creating the first Activity if it does
                        // does not have a saved stated Bundle. (During state restoration, the
                        // first Activity will always have a non-null saved state Bundle.)
                        if (!mIsFirstCreateCall) {
                            return;
                        }
                        mIsFirstCreateCall = false;
                        if (savedInstanceState == null) {
                            mSharedPreferences.edit()
                                    .clear()
                                    .apply();
                        }
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        // Don't allow clearing during known configuration changes (and other
                        // events unrelated to calling "finish()".)
                        mIsClearAllowed = activity.isFinishing();
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        // As soon as we have an indication that we are changing configurations for
                        // some Activity we'll remain in the "config change" state until the next
                        // time an Activity is created. We can ignore certain things (like
                        // processing the Bundle and writing it to disk on a background thread)
                        // during this period.
                        mIsConfigChange = activity.isChangingConfigurations();
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        mActivityCount++;
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        mActivityCount--;
                    }
                }
        );
    }

    void restoreInstanceState(@NonNull Object target, @Nullable Bundle state) {
        if (state == null) {
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
        mUuidBundleMap.put(uuid, bundle);
        if (mIsConfigChange) {
            // Don't process the Bundle or write it to disk during a config change
            return;
        }
        queueDiskWriting(uuid, bundle);
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