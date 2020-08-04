package com.egiwon.swiperecyclerviewsample.ui

import android.graphics.*

data class UnderButton(
    val icon: Bitmap,
    val color: Int,
    var pos: Int = 0,
    var clickRegion: RectF? = null,
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
        val x = (rectF.width() - icon.width) / 2
        val y = (rectF.height() - icon.height) / 2
        c.drawBitmap(icon, x, y, paint)

        clickRegion = rectF
        this.pos = pos
    }
}