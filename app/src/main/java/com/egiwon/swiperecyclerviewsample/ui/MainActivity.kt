package com.egiwon.swiperecyclerviewsample.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.egiwon.swiperecyclerviewsample.BR
import com.egiwon.swiperecyclerviewsample.R
import com.egiwon.swiperecyclerviewsample.base.BaseActivity
import com.egiwon.swiperecyclerviewsample.base.BaseAdapter2
import com.egiwon.swiperecyclerviewsample.databinding.ActivityMainBinding
import com.egiwon.swiperecyclerviewsample.ui.model.SampleItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(R.layout.activity_main) {

    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
            val adapter = object : BaseAdapter2(
                BR.item,
                mapOf(BR.vm to viewModel),
                mapOf(SampleItem::class to R.layout.item_sample)
            ) {}

            rvSample.adapter = adapter
            val itemTouchHelper = ItemTouchHelper(
                object : SwipeHelper(this@MainActivity, rvSample) {
                    override fun instantiateUnderlayButton(
                        viewHolder: RecyclerView.ViewHolder?,
                        underlayButtons: List<UnderButton>?
                    ) {

                    }

                })

            itemTouchHelper.attachToRecyclerView(rvSample)


        }
    }
}