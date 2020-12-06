package com.livefront.bridgesample.scenario.activity

import android.graphics.Bitmap
import android.support.test.uiautomator.UiDevice
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.livefront.bridgesample.R
import com.livefront.bridgesample.util.getCurrentActivity
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LargeDataBackstackActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<LargeDataActivity>(
            LargeDataActivity.getNavigationIntent(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    LargeDataActivityArguments(infiniteBackstack = true)
            )
    )

    @Test
    fun generateDataAndNavigateForward() {
        for (i in 1..5) {
            onView(withId(R.id.generateDataButton)).perform(click())
            waitForBitmap()
            onView(withId(R.id.navigateButton)).perform(click())
        }

        // Wait 1 second to allow a crash to occur
        Thread.sleep(1000)
    }

    @Test
    fun generateDataAndNavigateForwardAndBackward() {
        onView(withId(R.id.generateDataButton)).perform(click())

        val originalBitmap = waitForBitmap()
        onView(withId(R.id.navigateButton)).perform(click())

        for (i in 1 until 5) {
            onView(withId(R.id.generateDataButton)).perform(click())
            waitForBitmap()
            onView(withId(R.id.navigateButton)).perform(click())
        }

        for (i in 1..5) {
            pressBack()
        }

        (getCurrentActivity() as LargeDataActivity).apply {
            assertTrue(originalBitmap.sameAs(this.savedBitmap))
        }

        // Wait 1 second to allow a crash to occur
        Thread.sleep(1000)
    }

    @Test
    fun generateDataAndNavigateForwardRotateBackward() {
        onView(withId(R.id.generateDataButton)).perform(click())

        val originalBitmap = waitForBitmap()
        onView(withId(R.id.navigateButton)).perform(click())

        for (i in 1 until 5) {
            onView(withId(R.id.generateDataButton)).perform(click())
            waitForBitmap()
            onView(withId(R.id.navigateButton)).perform(click())
        }

        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).setOrientationLeft()

        for (i in 1..5) {
            pressBack()
        }

        (getCurrentActivity() as LargeDataActivity).apply {
            assertTrue(originalBitmap.sameAs(this.savedBitmap))
        }

        // Wait 1 second to allow a crash to occur
        Thread.sleep(1000)
    }
}

private fun getSavedBitmap(): Bitmap? = (getCurrentActivity() as LargeDataActivity).savedBitmap

private fun waitForBitmap(): Bitmap {
    while (getSavedBitmap() == null) {
        Thread.sleep(100)
    }
    return getSavedBitmap()!!
}
