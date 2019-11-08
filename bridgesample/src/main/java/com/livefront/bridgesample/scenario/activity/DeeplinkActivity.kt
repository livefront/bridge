package com.livefront.bridgesample.scenario.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class DeeplinkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity(
                LargeDataActivity.getNavigationIntent(
                        this,
                        LargeDataActivityArguments(infiniteBackstack = false)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                }
        )
        finish()
    }
}
