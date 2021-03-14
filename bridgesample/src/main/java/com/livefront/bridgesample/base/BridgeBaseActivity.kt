package com.livefront.bridgesample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.livefront.bridge.Bridge

abstract class BridgeBaseActivity : AppCompatActivity() {
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
        Bridge.clear(this)
    }
}
