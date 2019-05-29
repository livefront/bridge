package com.livefront.bridgesample.util

import android.app.Activity
import android.view.MenuItem

/**
 * Helper method for handling the standard "home as back press" behavior in
 * [Activity.onOptionsItemSelected]. The [default] is a function that will be called when the item
 * does not correspond to the "home" / "up" button.
 */
fun Activity.handleHomeAsBack(
    item: MenuItem,
    default: (MenuItem) -> Boolean
): Boolean = when (item.itemId) {
    android.R.id.home -> {
        onBackPressed()
        true
    }
    else -> default(item)
}
