package com.livefront.bridgesample.scenario.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.MenuItem
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BridgeBaseActivity
import com.livefront.bridgesample.scenario.activity.FragmentContainerActivity.Companion.getNavigationIntent
import com.livefront.bridgesample.util.FragmentNavigationManager
import com.livefront.bridgesample.util.handleHomeAsBack
import com.livefront.bridgesample.util.setHomeAsUpToolbar
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.basic_toolbar.toolbar

/**
 * A simple [Activity] with a container in which an initial [Fragment] may be placed. This is
 * done by supplying [FragmentData] via the [getNavigationIntent] method.
 */
class FragmentContainerActivity : BridgeBaseActivity(), FragmentNavigationManager {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        val data = getFragmentData(this)
        setHomeAsUpToolbar(toolbar, data.titleRes)

        if (savedInstanceState != null) return

        val fragment = Fragment.instantiate(
                this,
                data.fragmentClass.name,
                data.fragmentBundle
        )
        navigateTo(fragment, false)
    }

    override fun onOptionsItemSelected(item: MenuItem) = handleHomeAsBack(item) {
        super.onOptionsItemSelected(item)
    }

    override fun navigateTo(fragment: Fragment, addToBackstack: Boolean) {
        supportFragmentManager
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .apply {
                    replace(R.id.container, fragment)
                    if (addToBackstack) addToBackStack(null)
                }
                .commit()
    }

    companion object {
        private const val FRAGMENT_DATA_KEY = "data-key"

        fun getFragmentData(
            activity: FragmentContainerActivity
        ): FragmentData = activity
                .intent
                .getParcelableExtra(FRAGMENT_DATA_KEY)

        fun getNavigationIntent(
            context: Context,
            fragmentData: FragmentData
        ) = Intent(context, FragmentContainerActivity::class.java).apply {
            putExtra(FRAGMENT_DATA_KEY, fragmentData)
        }
    }
}

/**
 * Used to determine the initial [Fragment] to show in a [FragmentContainerActivity].
 */
@Parcelize
data class FragmentData(
    @StringRes val titleRes: Int,
    val fragmentClass: Class<out Fragment>,
    val fragmentBundle: Bundle?
) : Parcelable
