package com.livefront.bridgesample.base

import android.os.Bundle
import android.support.v4.app.Fragment
import com.evernote.android.state.StateSaver

open class NonBridgeBaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }
}
