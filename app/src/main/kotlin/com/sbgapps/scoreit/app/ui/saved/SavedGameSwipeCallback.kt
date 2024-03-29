/*
 * Copyright 2020 Stéphane Baiget
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

package com.sbgapps.scoreit.app.ui.saved

import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.core.ext.colorAttr
import com.sbgapps.scoreit.core.utils.RecyclerViewSwipeDecorator

class SavedGameSwipeCallback(
    private val onEditName: (Int) -> Unit,
    private val onDelete: (Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.bindingAdapterPosition
        when (direction) {
            ItemTouchHelper.LEFT -> onEditName(position)
            ItemTouchHelper.RIGHT -> onDelete(position)
        }
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
        val context = recyclerView.context
        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, actionState)
            .addSwipeLeftBackgroundColor(context.colorAttr(R.attr.colorPrimary))
            .addSwipeLeftActionIcon(R.drawable.ic_edit_24dp)
            .addSwipeRightBackgroundColor(context.colorAttr(R.attr.colorAccent))
            .addSwipeRightActionIcon(R.drawable.ic_delete_24dp)
            .create()
            .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}
