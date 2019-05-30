package com.livefront.bridgesample.scenario.view

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import android.util.AttributeSet
import android.widget.FrameLayout
import com.evernote.android.state.State
import com.livefront.bridge.Bridge
import com.livefront.bridgesample.R
import kotlinx.android.synthetic.main.view_large_data_content.view.bitmapGeneratorView

class LargeDataView @JvmOverloads constructor(
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
            setHeaderText(R.string.large_data_header)
            onBitmapGeneratedListener = { savedBitmap = it }
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        return Bridge.saveInstanceState(this, super.onSaveInstanceState())
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(Bridge.restoreInstanceState(this, state))
        bitmapGeneratorView.generatedBitmap = savedBitmap
    }
}
