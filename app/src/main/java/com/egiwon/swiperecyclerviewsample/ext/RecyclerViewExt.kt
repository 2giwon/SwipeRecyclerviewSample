package com.egiwon.swiperecyclerviewsample.ext

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.egiwon.swiperecyclerviewsample.base.BaseAdapter2
import com.egiwon.swiperecyclerviewsample.base.BaseIdentifier

@Suppress("UNCHECKED_CAST")
@BindingAdapter("replaceItems")
fun RecyclerView.replaceItems(items: List<BaseIdentifier>?) {
    (adapter as? BaseAdapter2)?.run {
        replaceItems(items)
        notifyDataSetChanged()
    }
}