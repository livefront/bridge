package com.livefront.bridgesample.scenario.fragment

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.livefront.bridge.Bridge
import com.livefront.bridgesample.R
import com.livefront.bridgesample.base.BridgeBaseFragment
import com.livefront.bridgesample.scenario.activity.FragmentData
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_pager.viewPager

/**
 * A [Fragment] with a [ViewPager] that uses a [FragmentStatePagerAdapter]. This illustrates how
 * we currently must omit the call to [Bridge.clear] in [Fragment.onDestroy] of the child
 * Fragments. Failure to do so will lose data as you page between screens.
 */
class StatePagerFragment : BridgeBaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_pager, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(
                position: Int
            ): Fragment = when (getArguments(this@StatePagerFragment).mode) {
                Mode.BRIDGE -> LargeDataFragment.newInstance(
                        // Note here that we specifically specify that the data should not be
                        // cleared in onDestroy because a FragmentStatePagerAdapter will destroy
                        // Fragments while still manually holding their Bundles for reuse later.
                        // A future feature may allow clearing to be "deferred" when some parent
                        // is being cleared.
                        LargeDataArguments(shouldClearOnDestroy = false)
                )
                Mode.NON_BRIDGE -> NonBridgeLargeDataFragment.newInstance()
            }

            override fun getCount(): Int = NUMBER_OF_PAGES

            override fun getPageTitle(position: Int): CharSequence? = position.toString()
        }
    }

    companion object {
        private const val ARGUMENTS_KEY = "arguments"
        private const val NUMBER_OF_PAGES = 10

        fun getArguments(
            fragment: StatePagerFragment
        ): StatePagerArguments = fragment
                .arguments!!
                .getParcelable(ARGUMENTS_KEY)

        fun getFragmentData(arguments: StatePagerArguments) = FragmentData(
                when (arguments.mode) {
                    Mode.BRIDGE -> R.string.state_pager_screen_title
                    Mode.NON_BRIDGE -> R.string.non_bridge_state_pager_screen_title
                },
                StatePagerFragment::class.java,
                getInitialArguments(arguments)
        )

        fun getInitialArguments(arguments: StatePagerArguments) = Bundle().apply {
            putParcelable(ARGUMENTS_KEY, arguments)
        }

        fun newInstance(arguments: StatePagerArguments) = StatePagerFragment().apply {
            this.arguments = getInitialArguments(arguments)
        }
    }

    /**
     * Specifies whether or not Bridge will be used for the child Fragments.
     */
    enum class Mode {
        BRIDGE,
        NON_BRIDGE
    }
}

@Parcelize
data class StatePagerArguments(
    val mode: StatePagerFragment.Mode
) : Parcelable
