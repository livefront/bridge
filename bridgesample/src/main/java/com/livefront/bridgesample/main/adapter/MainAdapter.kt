package com.livefront.bridgesample.main.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.livefront.bridgesample.R
import com.livefront.bridgesample.main.adapter.MainAdapter.MainViewHolder
import com.livefront.bridgesample.main.model.MainItem
import com.livefront.bridgesample.main.view.MainItemView
import com.livefront.bridgesample.util.getString
import com.livefront.bridgesample.util.layoutInflater

typealias OnMainItemClickListener = (MainItem) -> Unit

class MainAdapter(
    private val data: List<MainItem>
) : RecyclerView.Adapter<MainViewHolder>() {
    var onMainItemClickListener: OnMainItemClickListener? = null

    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): MainViewHolder {
        return MainViewHolder(
                viewGroup
                        .layoutInflater
                        .inflate(
                                R.layout.view_main_item_inflatable,
                                viewGroup,
                                false
                        ) as MainItemView
        )
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(viewHolder: MainViewHolder, position: Int) {
        val mainItem = data[position]
        val mainItemView = viewHolder.mainItemView
        mainItemView.setImageResource(
                if (position % 2 == 0) R.drawable.blue_circle else R.drawable.green_circle
        )
        mainItemView.title = mainItemView.getString(mainItem.title)
        mainItemView.description = mainItemView.getString(mainItem.description)
        mainItemView.setOnClickListener { onMainItemClickListener?.invoke(mainItem) }
    }

    class MainViewHolder(
        mainItemView: MainItemView
    ) : RecyclerView.ViewHolder(mainItemView) {
        val mainItemView: MainItemView get() = itemView as MainItemView
    }
}
