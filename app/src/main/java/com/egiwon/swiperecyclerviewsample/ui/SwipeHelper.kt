package com.egiwon.swiperecyclerviewsample.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

abstract class SwipeHelper(
    context: Context,
    private val recyclerView: RecyclerView
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {


    private val buttons = mutableListOf<UnderButton>()
    private var swipedPos = -1
    private val buttonBuffer: HashMap<Int, List<UnderButton>> = hashMapOf()
    private var swipeThreshold = 0.5f

    private val recoverQueue: Queue<Int> = object : LinkedList<Int>() {
        override fun add(element: Int): Boolean {
            return if (contains(element)) true else super.add(element)
        }
    }

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
    private val onTouchListener: View.OnTouchListener = View.OnTouchListener { v, event ->
        if (swipedPos < 0) return@OnTouchListener false
        val point = Point(event.rawX.toInt(), event.rawY.toInt())

        val swipeViewHolder: RecyclerView.ViewHolder? =
            recyclerView.findViewHolderForAdapterPosition(swipedPos)
        val swipeView = swipeViewHolder?.itemView
        val rect = Rect()
        swipeView?.getGlobalVisibleRect(rect)

        if (event.action == MotionEvent.ACTION_DOWN ||
            event.action == MotionEvent.ACTION_UP ||
            event.action == MotionEvent.ACTION_MOVE
        ) {
            if (rect.top < point.y && rect.bottom > point.y)
                gestureDetector.onTouchEvent(event)
            else {
                recoverQueue.add(swipedPos)
                swipedPos = -1
                recoverSwipedItem()
            }
        }

        false
    }

    @Synchronized
    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val pos = requireNotNull(recoverQueue.poll())
            if (pos > -1) {
                recyclerView.adapter?.notifyItemChanged(pos)
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

        if (swipedPos != pos) {
            recoverQueue.add(swipedPos)
        }

        swipedPos = pos

        if (buttonBuffer.containsKey(swipedPos)) {
            buttons.clear()
            buttons.addAll(buttonBuffer[swipedPos] ?: emptyList())
        } else {
            buttons.clear()
        }

        buttonBuffer.clear()
        swipeThreshold = 0.5f * buttons.size * BUTTON_WIDTH
        recoverSwipedItem()
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
                var buffer: List<UnderButton>? = mutableListOf()

                if (!buttonBuffer.containsKey(pos)) {
                    instantiateUnderlayButton(viewHolder, buffer)
                    buttonBuffer[pos] = buffer ?: emptyList()
                } else {
                    buffer = buttonBuffer[pos]
                }

                translateX = dX * requireNotNull(buffer?.size) * BUTTON_WIDTH / itemView.width
                drawButtons(c, itemView, buffer, pos, translateX)
            }
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
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

    abstract fun instantiateUnderlayButton(
        viewHolder: RecyclerView.ViewHolder?,
        underlayButtons: List<UnderButton>?
    )

    companion object {
        private const val BUTTON_WIDTH = 200
    }
}

