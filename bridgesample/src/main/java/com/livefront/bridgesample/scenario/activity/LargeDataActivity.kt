package com.livefront.bridgesample.scenario.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.MenuItem
import com.evernote.android.state.State
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BridgeBaseActivity
import com.livefront.bridgesample.util.handleHomeAsBack
import com.livefront.bridgesample.util.setHomeAsUpToolbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_large_data.bitmapGeneratorView
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

class LargeDataActivity : BridgeBaseActivity() {
    @State
    var savedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_large_data)
        setHomeAsUpToolbar(toolbar, R.string.large_data_screen_title)

        bitmapGeneratorView.apply {
            setHeaderText(R.string.large_data_header)
            generatedBitmap = savedBitmap
            onBitmapGeneratedListener = { savedBitmap = it }
            if (getArguments(this@LargeDataActivity).infiniteBackstack) {
                onNavigateButtonClickListener = {
                    startActivity(
                            getNavigationIntent(
                                    this@LargeDataActivity,
                                    getArguments(this@LargeDataActivity)
                            )
                    )
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleHomeAsBack(item) {
        super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ARGUMENTS_KEY = "arguments"

        fun getArguments(
            activity: LargeDataActivity
        ): LargeDataActivityArguments = activity
                .intent
                .getParcelableExtra(ARGUMENTS_KEY)

        fun getNavigationIntent(
            context: Context,
            arguments: LargeDataActivityArguments
        ) = Intent(context, LargeDataActivity::class.java).apply {
            putExtra(ARGUMENTS_KEY, arguments)
        }
    }
}

@Parcelize
data class LargeDataActivityArguments(
    val infiniteBackstack: Boolean = false
) : Parcelable
