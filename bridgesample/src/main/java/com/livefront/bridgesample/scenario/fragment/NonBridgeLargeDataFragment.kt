package com.livefront.bridgesample.scenario.fragment

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.evernote.android.state.State
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.NonBridgeBaseFragment
import com.livefront.bridgesample.scenario.activity.FragmentData
import com.livefront.bridgesample.scenario.activity.SuccessActivity
import kotlinx.android.synthetic.main.activity_large_data.bitmapGeneratorView

class NonBridgeLargeDataFragment : NonBridgeBaseFragment() {
    @State
    var savedBitmap: Bitmap? = null

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
            onNavigateButtonClickListener = {
                startActivity(Intent(context!!, SuccessActivity::class.java))
            }
        }
    }

    companion object {
        fun getFragmentData() = FragmentData(
                R.string.non_bridge_large_data_screen_title,
                NonBridgeLargeDataFragment::class.java,
                getInitialArguments()
        )

        fun getInitialArguments() = Bundle()

        fun newInstance() = NonBridgeLargeDataFragment().apply {
            arguments = getInitialArguments()
        }
    }
}
