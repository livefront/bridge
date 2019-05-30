package com.livefront.bridgesample.scenario.view

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.evernote.android.state.State
import com.evernote.android.state.StateSaver
import com.livefront.bridgesample.R
import kotlinx.android.synthetic.main.view_large_data_content.view.bitmapGeneratorView

class NonBridgeLargeDataView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyleAttr, defStyleRes) {
    @State
    var savedBitmap: Bitmap? = null

    init {
        inflate(context, R.layout.view_large_data_content, this)
        bitmapGeneratorView.apply {
            setHeaderText(R.string.non_bridge_large_data_header)
            onBitmapGeneratedListener = { savedBitmap = it }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return StateSaver.saveInstanceState(this, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(StateSaver.restoreInstanceState(this, state))
        bitmapGeneratorView.generatedBitmap = savedBitmap
    }
}
