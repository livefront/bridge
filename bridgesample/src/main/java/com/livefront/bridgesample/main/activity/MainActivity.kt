package com.livefront.bridgesample.main.activity

import android.os.Bundle
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BridgeBaseActivity
import com.livefront.bridgesample.main.adapter.MainAdapter
import com.livefront.bridgesample.main.model.getScenarios
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

class MainActivity : BridgeBaseActivity() {
    private lateinit var mainAdapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        mainAdapter = MainAdapter(getScenarios(this)).apply {
            onMainItemClickListener = { startActivity(it.intent) }
        }
        recyclerView.adapter = mainAdapter
    }
}
