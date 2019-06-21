package com.livefront.bridgesample.scenario.fragment

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.NonBridgeBaseFragment
import com.livefront.bridgesample.scenario.activity.FragmentData
import com.livefront.bridgesample.util.FragmentNavigationManager
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_large_data.bitmapGeneratorView

class NonBridgeLargeDataFragment : NonBridgeBaseFragment() {
    @State
    var savedBitmap: Bitmap? = null

    private lateinit var fragmentNavigationManager: FragmentNavigationManager

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        fragmentNavigationManager = context as FragmentNavigationManager
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_large_data, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bitmapGeneratorView.apply {
            setHeaderText(R.string.non_bridge_large_data_header)
            generatedBitmap = savedBitmap
            onBitmapGeneratedListener = { savedBitmap = it }
            if (getArguments(this@NonBridgeLargeDataFragment).infiniteBackstack) {
                onNavigateButtonClickListener = {
                    fragmentNavigationManager.navigateTo(
                            newInstance(
                                    NonBridgeLargeDataArguments(infiniteBackstack = true)
                            ),
                            addToBackstack = true
                    )
                }
            }
        }
    }

    companion object {
        private const val ARGUMENTS_KEY = "arguments"

        fun getArguments(
            fragment: NonBridgeLargeDataFragment
        ): NonBridgeLargeDataArguments = fragment
                .arguments!!
                .getParcelable(ARGUMENTS_KEY)

        fun getFragmentData(
            nonBridgeLargeDataArguments: NonBridgeLargeDataArguments = NonBridgeLargeDataArguments()
        ) = FragmentData(
                R.string.non_bridge_large_data_screen_title,
                NonBridgeLargeDataFragment::class.java,
                getInitialArguments(nonBridgeLargeDataArguments)
        )

        fun getInitialArguments(
            nonBridgeLargeDataArguments: NonBridgeLargeDataArguments = NonBridgeLargeDataArguments()
        ) = Bundle().apply {
            putParcelable(ARGUMENTS_KEY, nonBridgeLargeDataArguments)
        }

        fun newInstance(
            nonBridgeLargeDataArguments: NonBridgeLargeDataArguments = NonBridgeLargeDataArguments()
        ) = NonBridgeLargeDataFragment().apply {
            arguments = getInitialArguments(nonBridgeLargeDataArguments)
        }
    }
}

@Parcelize
data class NonBridgeLargeDataArguments(
    val infiniteBackstack: Boolean = false
) : Parcelable
