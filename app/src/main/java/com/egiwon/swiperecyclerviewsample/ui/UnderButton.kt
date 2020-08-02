package com.egiwon.swiperecyclerviewsample.ui

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable

data class UnderButton(
    val icon: BitmapDrawable,
    val color: Int,
    var pos: Int,
    var clickRegion: RectF?,
    val click: (Int) -> Unit
) {

    fun onClick(x: Float, y: Float): Boolean {
        clickRegion?.let { clickRegion ->
            if (clickRegion.contains(x, y)) {
                click(pos)
                return true
            }
        }

        return false
    }

    fun onDraw(c: Canvas, rectF: RectF, pos: Int) {
        val paint = Paint()

        paint.color = color
        c.drawRect(rectF, paint)

        paint.color = Color.WHITE
        val x = (rectF.width() - icon.bitmap.width) / 2
        val y = (rectF.height() - icon.bitmap.height) / 2
        c.drawBitmap(icon.bitmap, x, y, paint)

        clickRegion = rectF
        this.pos = pos
    }
}