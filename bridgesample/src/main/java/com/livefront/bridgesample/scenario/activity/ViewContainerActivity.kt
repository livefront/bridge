package com.livefront.bridgesample.scenario.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.MenuItem
import android.view.View
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BridgeBaseActivity
import com.livefront.bridgesample.scenario.activity.FragmentContainerActivity.Companion.getNavigationIntent
import com.livefront.bridgesample.scenario.activity.ViewContainerActivity.Companion.getNavigationIntent
import com.livefront.bridgesample.util.handleHomeAsBack
import com.livefront.bridgesample.util.setHomeAsUpToolbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_view_container.viewContainer
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

/**
 * A simple [Activity] with a container in which an initial [Fragment] may be placed. This is
 * done by supplying [ViewData] via the [getNavigationIntent] method.
 */
class ViewContainerActivity : BridgeBaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_container)
        val data = getViewData(this)
        setHomeAsUpToolbar(toolbar, data.titleRes)
        layoutInflater.inflate(data.viewRes, viewContainer, true)
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleHomeAsBack(item) {
        super.onOptionsItemSelected(item)
    }

    companion object {
        private const val DATA_KEY = "data"

        /**
         * Creates the appropriate [Intent] to show the [View] with the layout resource ID given
         * by [viewRes].
         */
        fun getNavigationIntent(
            context: Context,
            viewData: ViewData
        ) = Intent(context, ViewContainerActivity::class.java).apply {
            putExtra(DATA_KEY, viewData)
        }

        fun getViewData(
            activity: ViewContainerActivity
        ): ViewData = activity
                .intent
                .getParcelableExtra(DATA_KEY)
    }
}

@Parcelize
data class ViewData(
    @StringRes val titleRes: Int,
    @LayoutRes val viewRes: Int
) : Parcelable
