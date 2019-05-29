package com.livefront.bridgesample.scenario.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import com.evernote.android.state.State
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.NonBridgeBaseActivity
import com.livefront.bridgesample.util.handleHomeAsBack
import com.livefront.bridgesample.util.setHomeAsUpToolbar
import kotlinx.android.synthetic.main.activity_large_data.bitmapGeneratorView
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

class NonBridgeLargeDataActivity : NonBridgeBaseActivity() {
    @State
    var savedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_data)
        setHomeAsUpToolbar(toolbar, R.string.non_bridge_large_data_screen_title)

        bitmapGeneratorView.apply {
            setHeaderText(R.string.non_bridge_large_data_header)
            generatedBitmap = savedBitmap
            onNavigateButtonClickListener = {
                startActivity(Intent(this@NonBridgeLargeDataActivity, SuccessActivity::class.java))
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleHomeAsBack(item) {
        super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        savedBitmap = bitmapGeneratorView.generatedBitmap
        super.onSaveInstanceState(outState)
    }

    companion object {
        fun getNavigationIntent(context: Context) = Intent(
                context,
                NonBridgeLargeDataActivity::class.java
        )
    }
}
