package com.livefront.bridgesample.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import com.livefront.bridge.Bridge

abstract class BridgeBaseFragment : Fragment() {
    /**
     * Determines whether or not [Bridge.clear] will be called in [onDestroy]. This is enabled by
     * default but may be disabled in scenarios where Fragments are "recycled" (such as when using
     * a [FragmentStatePagerAdapter].
     */
    open val shouldClearOnDestroy: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Bridge.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Bridge.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (shouldClearOnDestroy) Bridge.clear(this)
    }
}
