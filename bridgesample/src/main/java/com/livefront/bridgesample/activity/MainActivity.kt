package com.livefront.bridgesample.activity

import android.os.Bundle
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }
}
