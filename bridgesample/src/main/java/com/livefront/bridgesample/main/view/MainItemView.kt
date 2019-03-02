package com.livefront.bridgesample.main.view

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.livefront.bridgesample.R
import com.livefront.bridgesample.util.layoutInflater
import kotlinx.android.synthetic.main.view_main_item_content.view.descriptionView
import kotlinx.android.synthetic.main.view_main_item_content.view.imageView
import kotlinx.android.synthetic.main.view_main_item_content.view.titleView

class MainItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr, defStyleRes) {
    init {
        layoutInflater.inflate(R.layout.view_main_item_content, this)
    }

    var description: CharSequence
        get() = descriptionView.text
        set(value) {
            descriptionView.text = value
        }

    var title: CharSequence
        get() = titleView.text
        set(value) {
            titleView.text = value
        }

    fun setImageResource(@DrawableRes value: Int) {
        imageView.setImageResource(value)
    }
}
