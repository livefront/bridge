package com.livefront.bridgesample.util

import android.os.AsyncTask

/**
 * A simple wrapper for [AsyncTask] that allows the [onBackground] action to be performed in
 * the background with the result received in the [callback].
 */
class SimpleTask<T>(
    private val onBackground: () -> (T),
    private val callback: (T) -> Unit
) : AsyncTask<Unit, Unit, T>() {
    override fun doInBackground(vararg params: Unit): T = onBackground()

    override fun onPostExecute(result: T) = callback(result)
}
