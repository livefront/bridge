package com.livefront.bridgesample.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.evernote.android.state.StateSaver

abstract class NonBridgeBaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StateSaver.restoreInstanceState(this, savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        StateSaver.saveInstanceState(this, outState)
    }
}
