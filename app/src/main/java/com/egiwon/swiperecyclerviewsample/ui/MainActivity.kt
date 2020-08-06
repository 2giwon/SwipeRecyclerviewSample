package com.egiwon.swiperecyclerviewsample.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import com.egiwon.swiperecyclerviewsample.BR
import com.egiwon.swiperecyclerviewsample.R
import com.egiwon.swiperecyclerviewsample.base.BaseActivity
import com.egiwon.swiperecyclerviewsample.base.BaseAdapter2
import com.egiwon.swiperecyclerviewsample.databinding.ActivityMainBinding
import com.egiwon.swiperecyclerviewsample.ui.model.SampleItem
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>(
    R.layout.activity_main
) {

    override val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind {
            vm = viewModel
            initAdapter()
        }

        viewModel.loadSampleItems()
    }

    private fun ActivityMainBinding.initAdapter() {
        val adapter = object : BaseAdapter2(
            BR.item,
            mapOf(BR.vm to viewModel),
            mapOf(SampleItem::class to R.layout.item_sample)
        ) {}

        rvSample.adapter = adapter
        getItemTouchHelper().attachToRecyclerView(rvSample)
    }

    private fun ActivityMainBinding.getItemTouchHelper(): ItemTouchHelper {
        val underButtons = listOf(
            UnderButton(
                icon = getBitmapFromResourceId(R.drawable.ic_delete_24px),
                color = resources.getColor(R.color.colorAccent, null),
                click = { showToast("Clicked $it") }
            )
        )

        val swipeHelper =
            object : SwipeHelper(this@MainActivity, rvSample, underButtons) {}

        return ItemTouchHelper(swipeHelper)
    }

    private fun Context.getBitmapFromResourceId(@DrawableRes resourceId: Int): Bitmap {
        val drawable =
            requireNotNull(ContextCompat.getDrawable(this, resourceId))
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

}