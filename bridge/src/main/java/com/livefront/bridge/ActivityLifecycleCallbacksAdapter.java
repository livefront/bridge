package com.livefront.bridge;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
abstract class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        // no-op
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // no-op
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // no-op
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // no-op
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // no-op
    }

    @Override
    public void onActivityStarted(Activity activity) {
        // no-op
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // no-op
    }

}
