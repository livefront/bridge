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
import com.livefront.bridgesample.base.BridgeBaseFragment
import com.livefront.bridgesample.scenario.activity.FragmentData
import com.livefront.bridgesample.util.FragmentNavigationManager
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_large_data.bitmapGeneratorView

class LargeDataFragment : BridgeBaseFragment() {
    override val shouldClearOnDestroy: Boolean
        get() = getArguments(this).shouldClearOnDestroy

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
            setHeaderText(R.string.large_data_header)
            generatedBitmap = savedBitmap
            onBitmapGeneratedListener = { savedBitmap = it }
            if (getArguments(this@LargeDataFragment).infiniteBackstack) {
                onNavigateButtonClickListener = {
                    fragmentNavigationManager.navigateTo(
                            newInstance(
                                    LargeDataArguments(
                                            getArguments(this@LargeDataFragment).shouldClearOnDestroy,
                                            infiniteBackstack = true
                                    )
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
            fragment: LargeDataFragment
        ): LargeDataArguments = fragment
                .arguments!!
                .getParcelable(ARGUMENTS_KEY)

        fun getFragmentData(
            largeDataArguments: LargeDataArguments = LargeDataArguments()
        ) = FragmentData(
                R.string.large_data_screen_title,
                LargeDataFragment::class.java,
                getInitialArguments(largeDataArguments)
        )

        fun getInitialArguments(
            largeDataArguments: LargeDataArguments = LargeDataArguments()
        ) = Bundle().apply {
            putParcelable(ARGUMENTS_KEY, largeDataArguments)
        }

        fun newInstance(
            largeDataArguments: LargeDataArguments = LargeDataArguments()
        ) = LargeDataFragment().apply {
            arguments = getInitialArguments(largeDataArguments)
        }
    }
}

@Parcelize
data class LargeDataArguments(
    val shouldClearOnDestroy: Boolean = true,
    val infiniteBackstack: Boolean = false
) : Parcelable
