package com.livefront.bridgesample.app

import android.app.Application
import android.os.Bundle
import com.evernote.android.state.StateSaver
import com.livefront.bridge.Bridge
import com.livefront.bridge.SavedStateHandler

class BridgeSampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Bridge.initialize(
                this,
                object : SavedStateHandler {
                    override fun saveInstanceState(target: Any, state: Bundle) {
                        StateSaver.saveInstanceState(target, state)
                    }

                    override fun restoreInstanceState(target: Any, state: Bundle?) {
                        StateSaver.restoreInstanceState(target, state)
                    }
                }
        )
    }
}
