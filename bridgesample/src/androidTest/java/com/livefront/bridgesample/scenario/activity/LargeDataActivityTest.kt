package com.livefront.bridgesample.scenario.activity

import android.graphics.Bitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.livefront.bridgesample.R
import com.livefront.bridgesample.util.getCurrentActivity
import org.junit.Assert.assertTrue
import org.junit.Rule

import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LargeDataActivityTest {

    @get:Rule
    var activityScenarioRule = ActivityScenarioRule<LargeDataActivity>(
            LargeDataActivity.getNavigationIntent(
                    InstrumentationRegistry.getInstrumentation().targetContext,
                    LargeDataActivityArguments(infiniteBackstack = false)
            )
    )

    @Test
    fun generateDataAndNavigateNoCrash() {
        onView(withId(R.id.generateDataButton)).perform(click())
        waitForBitmap()
        onView(withId(R.id.navigateButton)).perform(click())

        // Wait 1 second to allow a crash to occur
        Thread.sleep(1000)
    }

    @Test
    fun generateDataAndNavigateCheckData() {
        onView(withId(R.id.generateDataButton)).perform(click())

        val originalBitmap: Bitmap = waitForBitmap()

        onView(withId(R.id.navigateButton)).perform(click())

        pressBack()

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
