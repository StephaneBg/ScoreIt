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

package com.sbgapps.scoreit.core.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.sbgapps.scoreit.core.R
import com.sbgapps.scoreit.core.ext.colorAttr
import com.sbgapps.scoreit.core.ext.dip

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val height = context.dip(1)
    private val paint = Paint().apply {
        color = context.colorAttr(R.attr.colorOnBackground)
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (0 == position) {
            outRect.setEmpty()
        } else {
            outRect.bottom = height
        }
    }

    override fun onDrawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val itemCount = parent.childCount
        parent.children.forEachIndexed { index, view ->
            if (index < itemCount - 1) drawDivider(canvas, view)
        }
    }

    private fun drawDivider(canvas: Canvas, view: View) {
        canvas.drawRect(
            view.left.toFloat(),
            view.bottom.toFloat(),
            view.right.toFloat(),
            (view.bottom + height).toFloat(),
            paint
        )
    }
}
