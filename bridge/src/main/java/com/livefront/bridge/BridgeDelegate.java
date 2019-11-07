package com.livefront.bridge;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.livefront.bridge.util.BundleUtil;
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
    private static final String KEY_WRAPPED_VIEW_RESULT = "wrapped-view-result";

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
    private ViewSavedStateHandler mViewSavedStateHandler;

    BridgeDelegate(@NonNull Context context,
                   @NonNull SavedStateHandler savedStateHandler,
                   @Nullable ViewSavedStateHandler viewSavedStateHandler) {
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        mSavedStateHandler = savedStateHandler;
        mViewSavedStateHandler = viewSavedStateHandler;
        registerForLifecycleEvents(context);
    }

    private void checkForViewSavedStateHandler() {
        if (mViewSavedStateHandler == null) {
            throw new IllegalStateException("To save and restore the state of Views, a "
                    + "ViewSavedStateHandler must be specified when calling initialize.");
        }
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

    private String getOrGenerateUuid(@NonNull Object target) {
        String uuid = mObjectUuidMap.get(target);
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
            mObjectUuidMap.put(target, uuid);
        }
        return uuid;
    }

    @Nullable
    private Bundle getSavedBundleAndUnwrap(@NonNull String uuid) {
        Bundle bundle = mUuidBundleMap.containsKey(uuid)
                ? mUuidBundleMap.get(uuid)
                : readFromDisk(uuid);
        if (bundle != null) {
            WrapperUtils.unwrapOptimizedObjects(bundle);
        }
        clearDataForUuid(uuid);
        return bundle;
    }

    @Nullable
    private String getSavedUuid(@NonNull Object target,
                                @NonNull Bundle state) {
        String uuid = mObjectUuidMap.containsKey(target)
                ? mObjectUuidMap.get(target)
                : state.getString(getKeyForUuid(target), null);
        if (uuid != null) {
            mObjectUuidMap.put(target, uuid);
        }
        return uuid;
    }

    private boolean isAppInForeground() {
        return mActivityCount > 0 || mIsConfigChange;
    }

    /**
     * When the app is foregrounded, the given Bundle will be processed on a background thread and
     * then persisted to disk. When the app is proceeding to the background, this method will wait
     * for this task (and any others currently running in the background) to complete before
     * proceeding in order to prevent the app from becoming fully "stopped" (and therefore killable
     * by the OS before the data is saved).
     */
    private void queueDiskWritingIfNecessary(@NonNull final String uuid,
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
        return BundleUtil.fromEncodedString(encodedString);
    }

    @SuppressLint("NewApi")
    private void registerForLifecycleEvents(@NonNull Context context) {
        ((Application) context.getApplicationContext()).registerActivityLifecycleCallbacks(
                new ActivityLifecycleCallbacksAdapter() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                        if (openedFromDeeplink(activity)) {
                            return;
                        }
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
        String uuid = getSavedUuid(target, state);
        if (uuid == null) {
            return;
        }
        Bundle bundle = getSavedBundleAndUnwrap(uuid);
        if (bundle == null) {
            return;
        }
        mSavedStateHandler.restoreInstanceState(target, bundle);
    }

    @Nullable
    <T extends View> Parcelable restoreInstanceState(@NonNull T target,
                                                     @Nullable Parcelable state) {
        checkForViewSavedStateHandler();
        if (state == null) {
            return null;
        }
        String uuid = getSavedUuid(target, (Bundle) state);
        if (uuid == null) {
            return null;
        }
        Bundle bundle = getSavedBundleAndUnwrap(uuid);
        if (bundle == null) {
            return null;
        }
        // Figure out if we had to wrap the original result coming from the ViewSavedStateHandler
        // in our own Bundle. If so, grab the actual result. Otherwise the current Bundle *is* the
        // result.
        Parcelable originalResult = bundle.containsKey(KEY_WRAPPED_VIEW_RESULT)
                ? bundle.getParcelable(KEY_WRAPPED_VIEW_RESULT)
                : bundle;
        return mViewSavedStateHandler.restoreInstanceState(target, originalResult);
    }

    void saveInstanceState(@NonNull Object target, @NonNull Bundle state) {
        String uuid = getOrGenerateUuid(target);
        state.putString(getKeyForUuid(target), uuid);
        Bundle bundle = new Bundle();
        mSavedStateHandler.saveInstanceState(target, bundle);
        if (bundle.isEmpty()) {
            // Don't bother saving empty bundles
            return;
        }
        saveToMemoryAndDiskIfNecessary(uuid, bundle);
    }

    @NonNull
    <T extends View> Parcelable saveInstanceState(@NonNull T target,
                                                  @Nullable Parcelable parentState) {
        checkForViewSavedStateHandler();
        String uuid = getOrGenerateUuid(target);
        Bundle outBundle = new Bundle();
        outBundle.putString(getKeyForUuid(target), uuid);
        Parcelable result = mViewSavedStateHandler.saveInstanceState(target, parentState);
        Bundle saveBundle;
        if (result instanceof Bundle) {
            // The result is already a Bundle, so we can deal with it directly.
            saveBundle = (Bundle) result;
        } else {
            // The result is not a Bundle so we'll wrap it in one with a special key.
            saveBundle = new Bundle();
            saveBundle.putParcelable(KEY_WRAPPED_VIEW_RESULT, result);
        }
        if (saveBundle.isEmpty()) {
            // Don't bother saving empty bundles
            return outBundle;
        }
        saveToMemoryAndDiskIfNecessary(uuid, saveBundle);
        return outBundle;
    }

    private void saveToMemoryAndDiskIfNecessary(@NonNull String uuid,
                                                @NonNull Bundle bundle) {
        WrapperUtils.wrapOptimizedObjects(bundle);
        mUuidBundleMap.put(uuid, bundle);
        queueDiskWritingIfNecessary(uuid, bundle);
    }

    private void writeToDisk(@NonNull String uuid,
                             @NonNull Bundle bundle) {
        String encodedString = BundleUtil.toEncodedString(bundle);
        mSharedPreferences.edit()
                .putString(getKeyForEncodedBundle(uuid), encodedString)
                .apply();
    }

    private boolean openedFromDeeplink(Activity activity) {
        return Intent.ACTION_VIEW.equals(activity.getIntent().getAction())
                && activity.getIntent().getData() != null;
    }
}
