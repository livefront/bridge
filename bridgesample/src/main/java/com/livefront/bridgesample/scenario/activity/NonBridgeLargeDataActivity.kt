package com.livefront.bridgesample.scenario.activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import com.evernote.android.state.State
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.NonBridgeBaseActivity
import com.livefront.bridgesample.util.generateNoisyStripedBitmap
import com.livefront.bridgesample.util.handleHomeAsBack
import kotlinx.android.synthetic.main.activity_large_data.generateDataButton
import kotlinx.android.synthetic.main.activity_large_data.headerText
import kotlinx.android.synthetic.main.activity_large_data.imageView
import kotlinx.android.synthetic.main.activity_large_data.navigateButton
import kotlinx.android.synthetic.main.activity_large_data.statusText
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

class NonBridgeLargeDataActivity : NonBridgeBaseActivity() {
    @State
    lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_data)
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            setTitle(R.string.non_bridge_large_data_screen_title)
        }

        headerText.setText(R.string.non_bridge_large_data_header)

        if (this::bitmap.isInitialized) {
            // The Bitmap is restored from a saved state. We can just show it directly.
            imageView.setImageBitmap(bitmap)
            statusText.setText(R.string.restored_from_saved_state)
        }

        generateDataButton.setOnClickListener {
            // Generate a large Bitmap, save it to the "bitmap" variable, and then show it in the
            // view. In general, it is not technically a great idea to manage Bitmaps this way;
            // this is only meant as a visual indication of "a large amount of data".
            generateDataButton.isEnabled = false
            generateNoisyStripedBitmap { generatedBitmap ->
                bitmap = generatedBitmap
                generateDataButton.isEnabled = true
                imageView.setImageBitmap(generatedBitmap)
                statusText.setText(R.string.image_generated)
            }
        }
        navigateButton.setOnClickListener {
            startActivity(Intent(this@NonBridgeLargeDataActivity, SuccessActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleHomeAsBack(item) {
        super.onOptionsItemSelected(item)
    }
}
