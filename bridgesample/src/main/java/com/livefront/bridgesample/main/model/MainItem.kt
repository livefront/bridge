package com.livefront.bridgesample.main.model

import android.content.Intent
import android.os.Parcelable
import android.support.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val intent: Intent
) : Parcelable
