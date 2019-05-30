package com.livefront.bridgesample.app

import android.app.Application
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.evernote.android.state.StateSaver
import com.livefront.bridge.Bridge
import com.livefront.bridge.SavedStateHandler
import com.livefront.bridge.ViewSavedStateHandler

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
                },
                object : ViewSavedStateHandler {
                    override fun <T : View> saveInstanceState(
                        target: T,
                        parentState: Parcelable?
                    ): Parcelable = StateSaver.saveInstanceState(target, parentState)

                    override fun <T : View> restoreInstanceState(
                        target: T,
                        state: Parcelable?
                    ): Parcelable? = StateSaver.restoreInstanceState(target, state)
                }
        )
    }
}
