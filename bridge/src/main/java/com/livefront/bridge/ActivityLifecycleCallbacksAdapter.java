package com.livefront.bridge;

import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import androidx.annotation.NonNull;

abstract class ActivityLifecycleCallbacksAdapter implements ActivityLifecycleCallbacks {

  @Override
  public void onActivityCreated(@NonNull Activity activity, Bundle savedInstanceState) {
    // no-op
  }

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {
    // no-op
  }

  @Override
  public void onActivityPaused(@NonNull Activity activity) {
    // no-op
  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {
    // no-op
  }

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {
    // no-op
  }

  @Override
  public void onActivityStarted(@NonNull Activity activity) {
    // no-op
  }

  @Override
  public void onActivityStopped(@NonNull Activity activity) {
    // no-op
  }
}
