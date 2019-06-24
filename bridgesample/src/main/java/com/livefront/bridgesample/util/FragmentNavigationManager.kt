package com.livefront.bridgesample.util

import android.support.v4.app.Fragment

/**
 * Helper interface to provide fragment navigation.
 */
interface FragmentNavigationManager {
    /**
     * Navigates to the specified fragment, adding it to the backstack if requested.
     */
    fun navigateTo(fragment: Fragment, addToBackstack: Boolean)
}
