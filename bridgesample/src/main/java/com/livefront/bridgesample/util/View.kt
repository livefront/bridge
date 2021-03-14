package com.livefront.bridgesample.util

import android.view.LayoutInflater
import android.view.View
import androidx.annotation.StringRes

fun View.getString(@StringRes stringRes: Int): String = context.getString(stringRes)

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(this.context)
