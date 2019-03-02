package com.livefront.bridgesample.util

import android.support.annotation.StringRes
import android.view.LayoutInflater
import android.view.View

fun View.getString(@StringRes stringRes: Int): String = context.getString(stringRes)

val View.layoutInflater: LayoutInflater get() = LayoutInflater.from(this.context)
