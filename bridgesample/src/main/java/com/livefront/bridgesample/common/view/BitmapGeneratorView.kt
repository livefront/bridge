package com.livefront.bridgesample.common.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.support.annotation.StringRes
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.livefront.bridgesample.R
import com.livefront.bridgesample.scenario.activity.SuccessActivity
import com.livefront.bridgesample.util.generateNoisyStripedBitmap
import kotlinx.android.synthetic.main.view_bitmap_generator_content.view.generateDataButton
import kotlinx.android.synthetic.main.view_bitmap_generator_content.view.headerText
import kotlinx.android.synthetic.main.view_bitmap_generator_content.view.imageView
import kotlinx.android.synthetic.main.view_bitmap_generator_content.view.navigateButton
import kotlinx.android.synthetic.main.view_bitmap_generator_content.view.statusText

/**
 * A view that generates a [Bitmap] when clicking on a button. This `Bitmap` can then be retrieved
 * as the [generatedBitmap] in order to test saving / restoring it. The
 * [onNavigateButtonClickListener] may be set to provide some action when the corresponding button
 * is clicked. If no such listener is set, the default behavior is to navigate to the
 * [SuccessActivity].
 */
class BitmapGeneratorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    var generatedBitmap: Bitmap? = null
        /**
         * Sets the [Bitmap] to display in this view. This will be considered as "restored" from a
         * saved state.
         */
        set(value) {
            field = value
            value?.let {
                imageView.setImageBitmap(it)
                statusText.setText(R.string.restored_from_saved_state)
            }
        }

    var onBitmapGeneratedListener: ((Bitmap) -> Unit)? = null
    var onNavigateButtonClickListener: (() -> Unit)? = null

    init {
        inflate(context, R.layout.view_bitmap_generator_content, this)
        navigateButton.setOnClickListener {
            onNavigateButtonClickListener
                    ?.invoke()
                    ?: (context as Activity).startActivity(
                            Intent(context, SuccessActivity::class.java)
                    )
        }
        generateDataButton.setOnClickListener {
            generateDataButton.isEnabled = false
            generateNoisyStripedBitmap { bitmap ->
                generatedBitmap = bitmap
                generateDataButton.isEnabled = true
                imageView.setImageBitmap(bitmap)
                statusText.setText(R.string.image_generated)
                onBitmapGeneratedListener?.invoke(bitmap)
            }
        }
    }

    fun setHeaderText(@StringRes textRes: Int) {
        headerText.setText(textRes)
    }
}
