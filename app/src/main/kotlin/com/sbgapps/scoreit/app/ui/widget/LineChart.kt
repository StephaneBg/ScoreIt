/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.app.ui.widget


import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.core.ext.colorAttr
import com.sbgapps.scoreit.core.ext.dip

class LineChart @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    @Suppress("MemberVisibilityCanBePrivate")
    var lines = emptyList<LineSet>()
        set(value) {
            maxY = value.map { it.getMaxY() }.max() ?: 0f
            minY = value.map { it.getMinY() }.min() ?: 0f
            maxX = value.map { it.getMaxX() }.max() ?: 0f
            minX = value.map { it.getMinX() }.min() ?: 0f
            field = value
            postInvalidate()
        }

    private val axisColor = context.colorAttr(R.attr.colorOnBackground)
    private val paint = Paint()
    private val keySize = context.dip(2).toFloat()

    private var maxY = 0f
    private var minY = 0f
    private var maxX = 0f
    private var minX = 0f

    init {
        showEditMode()
    }

    public override fun onDraw(canvas: Canvas) {
        val padding = context.dip(32).toFloat()
        val usableHeight = height - 2 * padding
        val usableWidth = width - 2 * padding

        // Draw x-axis line
        updatePaint(axisColor, keySize / 2)
        val height: Float
        height = if (minY < 0) {
            getHeight().toFloat() - padding - usableHeight * (-minY / (maxY - minY))
        } else {
            getHeight() - padding
        }

        canvas.drawLine(
            padding,
            height,
            width - padding,
            height,
            paint
        )

        // Draw lines
        lines.forEach { line ->
            var index = 0
            var lastXPixels = 0f
            var newYPixels: Float
            var lastYPixels = 0f
            var newXPixels: Float

            updatePaint(line.color, keySize)
            line.points.forEach { point ->
                val yPercent = (point.y - minY) / (maxY - minY)
                val xPercent = (point.x - minX) / (maxX - minX)
                if (index == 0) {
                    lastXPixels = padding + xPercent * usableWidth
                    lastYPixels = getHeight().toFloat() - padding - usableHeight * yPercent
                } else {
                    newXPixels = padding + xPercent * usableWidth
                    newYPixels = getHeight().toFloat() - padding - usableHeight * yPercent
                    canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, paint)
                    lastXPixels = newXPixels
                    lastYPixels = newYPixels
                }
                index++
            }
        }

        // Draw points
        lines.forEach { line ->
            updatePaint(line.color, keySize)
            if (line.arePointsDisplayed) {
                line.points.forEach { point ->
                    val yPercent = (point.y - minY) / (maxY - minY)
                    val xPercent = (point.x - minX) / (maxX - minX)
                    val xPixels = padding + xPercent * usableWidth
                    val yPixels = getHeight().toFloat() - padding - usableHeight * yPercent
                    canvas.drawCircle(xPixels, yPixels, 2 * keySize, paint)
                }
            }
        }
    }

    private fun updatePaint(color: Int, strokeWidth: Float) {
        paint.reset()
        paint.isAntiAlias = true
        paint.strokeWidth = strokeWidth
        paint.color = color
    }

    private fun showEditMode() {
        if (isInEditMode) lines = listOf(
            LineSet(
                listOf(
                    LinePoint(),
                    LinePoint(1, 2),
                    LinePoint(2, -1)
                )
            )
        )
    }
}

data class LinePoint @JvmOverloads constructor(var x: Float = 0f, var y: Float = 0f) {

    constructor(x: Double, y: Double) : this(x.toFloat(), y.toFloat())

    constructor(x: Int, y: Int) : this(x.toFloat(), y.toFloat())

    override fun toString(): String = "x=$x, y=$y"
}

data class LineSet(
    val points: List<LinePoint>,
    @ColorInt val color: Int = Color.BLACK,
    val arePointsDisplayed: Boolean = true
) {

    val size: Int
        get() = points.size

    fun getMinX(): Float = points.map { it.x }.min() ?: 0f

    fun getMinY(): Float = points.map { it.y }.min() ?: 0f

    fun getMaxX(): Float = points.map { it.x }.max() ?: 0f

    fun getMaxY(): Float = points.map { it.y }.max() ?: 0f
}
