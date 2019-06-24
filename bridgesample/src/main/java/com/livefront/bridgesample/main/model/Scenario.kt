package com.livefront.bridgesample.main.model

import android.content.Context
import com.livefront.bridgesample.R
import com.livefront.bridgesample.scenario.activity.FragmentContainerActivity
import com.livefront.bridgesample.scenario.activity.LargeDataActivity
import com.livefront.bridgesample.scenario.activity.LargeDataActivityArguments
import com.livefront.bridgesample.scenario.activity.NonBridgeLargeDataActivity
import com.livefront.bridgesample.scenario.activity.NonBridgeLargeDataActivityArguments
import com.livefront.bridgesample.scenario.activity.ViewContainerActivity
import com.livefront.bridgesample.scenario.activity.ViewData
import com.livefront.bridgesample.scenario.fragment.LargeDataArguments
import com.livefront.bridgesample.scenario.fragment.LargeDataFragment
import com.livefront.bridgesample.scenario.fragment.NonBridgeLargeDataArguments
import com.livefront.bridgesample.scenario.fragment.NonBridgeLargeDataFragment
import com.livefront.bridgesample.scenario.fragment.StatePagerArguments
import com.livefront.bridgesample.scenario.fragment.StatePagerFragment

/**
 * This is the list of all Bridge scenarios demonstrated in this app.
 */
fun getScenarios(context: Context): List<MainItem> = listOf(
        MainItem(
                R.string.large_data_scenario_title,
                R.string.large_data_scenario_description,
                LargeDataActivity.getNavigationIntent(context,
                        LargeDataActivityArguments(infiniteBackstack = false))
        ),
        MainItem(
                R.string.non_bridge_large_data_scenario_title,
                R.string.non_bridge_large_data_scenario_description,
                NonBridgeLargeDataActivity.getNavigationIntent(context,
                        NonBridgeLargeDataActivityArguments(infiniteBackstack = false))
        ),
        MainItem(
                R.string.large_data_backstack_scenario_title,
                R.string.large_data_backstack_scenario_description,
                LargeDataActivity.getNavigationIntent(context,
                        LargeDataActivityArguments(infiniteBackstack = true))
        ),
        MainItem(
                R.string.non_bridge_large_data_backstack_scenario_title,
                R.string.non_bridge_large_data_backstack_scenario_description,
                NonBridgeLargeDataActivity.getNavigationIntent(context,
                        NonBridgeLargeDataActivityArguments(infiniteBackstack = true))
        ),
        MainItem(
                R.string.large_data_fragment_scenario_title,
                R.string.large_data_fragment_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        LargeDataFragment.getFragmentData()
                )
        ),
        MainItem(
                R.string.non_bridge_large_data_fragment_scenario_title,
                R.string.non_bridge_large_data_fragment_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        NonBridgeLargeDataFragment.getFragmentData()
                )
        ),
        MainItem(
                R.string.large_data_fragment_backstack_scenario_title,
                R.string.large_data_fragment_backstack_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        LargeDataFragment.getFragmentData(
                                LargeDataArguments(infiniteBackstack = true)
                        )
                )
        ),
        MainItem(
                R.string.non_bridge_large_data_fragment_backstack_scenario_title,
                R.string.non_bridge_large_data_fragment_backstack_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        NonBridgeLargeDataFragment.getFragmentData(
                                NonBridgeLargeDataArguments(infiniteBackstack = true)
                        )
                )
        ),
        MainItem(
                R.string.state_pager_scenario_title,
                R.string.state_pager_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        StatePagerFragment.getFragmentData(
                                StatePagerArguments(
                                        StatePagerFragment.Mode.BRIDGE
                                )
                        )
                )
        ),
        MainItem(
                R.string.non_bridge_state_pager_scenario_title,
                R.string.non_bridge_state_pager_scenario_description,
                FragmentContainerActivity.getNavigationIntent(
                        context,
                        StatePagerFragment.getFragmentData(
                                StatePagerArguments(
                                        StatePagerFragment.Mode.NON_BRIDGE
                                )
                        )
                )
        ),
        MainItem(
                R.string.large_data_view_scenario_title,
                R.string.large_data_view_scenario_description,
                ViewContainerActivity.getNavigationIntent(
                        context,
                        ViewData(
                                R.string.large_data_screen_title,
                                R.layout.view_large_data_inflatable
                        )
                )
        ),
        MainItem(
                R.string.non_bridge_large_data_view_scenario_title,
                R.string.non_bridge_large_data_view_scenario_description,
                ViewContainerActivity.getNavigationIntent(
                        context,
                        ViewData(
                                R.string.non_bridge_large_data_screen_title,
                                R.layout.non_bridge_large_data_view_inflatable
                        )
                )
        )
)
