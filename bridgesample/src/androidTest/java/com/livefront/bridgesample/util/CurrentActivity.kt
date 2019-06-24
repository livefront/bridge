package com.livefront.bridgesample.util

import android.app.Activity
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage.RESUMED

/**
 * @return the current activity being displayed.
 */
fun getCurrentActivity(): Activity {
    var currentActivity: Activity? = null

    InstrumentationRegistry.getInstrumentation().runOnMainSync {
        val resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(
                RESUMED
        )
        // This will throw an exception if there are no resumed activities.
        currentActivity = resumedActivities.iterator().next()
    }

    return currentActivity!!
}
