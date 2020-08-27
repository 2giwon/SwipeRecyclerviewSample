package com.egiwon.swiperecyclerviewsample.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView,
    private val buttons: List<UnderButton>
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private var swipedPos = -1
    private var swipeThreshold = 0.5f

    private val swipedList = mutableSetOf<Int>()

    private val gestureSimpleListener =
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                buttons.forEach {
                    if (it.onClick(e.x, e.y)) {
                        return@forEach
                    }
                }

                return true
            }
        }

    private val gestureDetector = GestureDetectorCompat(context, gestureSimpleListener)

    @Suppress("ClickableViewAccessibility")
    private val onTouchListener: View.OnTouchListener = View.OnTouchListener { _, event ->
        if (swipedPos < 0) return@OnTouchListener false
        if (event.action == MotionEvent.ACTION_UP) {
            if (!gestureDetector.onTouchEvent(event)) {
                swipedPos = -1
            }
        } else {
            gestureDetector.onTouchEvent(event)
        }

        false
    }

    init {
        @Suppress("ClickableViewAccessibility")
        recyclerView.setOnTouchListener(onTouchListener)
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (swipedList.isNotEmpty()) {
            val pos = swipedList.first()
            swipedList.remove(pos)
            recyclerView.adapter?.notifyItemChanged(pos)
        }
    }

    @Synchronized
    private fun recoverSwipedItem(currentIndex: Int) {
        val tempList = mutableListOf<Int>()
        tempList.addAll(swipedList)
        swipedList.clear()

        tempList.forEach {
            if (it == currentIndex) {
                swipedList.add(it)
            } else {
                recyclerView.adapter?.notifyItemChanged(it)
            }
        }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val pos = viewHolder.adapterPosition

        swipeThreshold = 0.5f * buttons.size * BUTTON_WIDTH
        recoverSwipedItem(pos)
        swipedPos = pos
        swipedList.add(pos)
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return swipeThreshold
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return 0.1f * defaultValue
    }

    override fun getSwipeVelocityThreshold(defaultValue: Float): Float {
        return 5.0f * defaultValue
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val pos = viewHolder.adapterPosition
        var translateX = dX
        val itemView = viewHolder.itemView

        if (pos < 0) {
            swipedPos = pos
            return
        }

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {

                translateX = dX * requireNotNull(buttons.size) * BUTTON_WIDTH / itemView.width
                drawButtons(c, itemView, buttons, pos, translateX)
            }
        }
        super.onChildDraw(
            c,
            recyclerView,
            viewHolder,
            translateX,
            dY,
            actionState,
            isCurrentlyActive
        )
    }

    private fun drawButtons(
        c: Canvas,
        itemView: View,
        buffer: List<UnderButton>?,
        pos: Int,
        dX: Float
    ) {
        var right = itemView.right
        buffer?.let { b ->
            val buttonWidth = -dX / b.size

            for (button: UnderButton in b) {
                val left = right - buttonWidth
                button.onDraw(
                    c,
                    RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat()),
                    pos
                )

                right = left.toInt()
            }
        }

    }

    companion object {
        private const val BUTTON_WIDTH = 200
    }
}

