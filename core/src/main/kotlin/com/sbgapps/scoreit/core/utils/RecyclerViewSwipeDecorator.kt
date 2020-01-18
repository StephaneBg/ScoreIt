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

package com.sbgapps.scoreit.core.utils

import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.sbgapps.scoreit.core.ext.dip
import timber.log.Timber

/**
 * A simple utility class to add a background and/or an icon while swiping a RecyclerView item left or right.
 */

class RecyclerViewSwipeDecorator private constructor(
    private val canvas: Canvas,
    private val recyclerView: RecyclerView,
    private val viewHolder: RecyclerView.ViewHolder,
    private val dX: Float,
    private val actionState: Int
) {

    private var swipeLeftBackgroundColor: Int = 0
    private var swipeLeftActionIconId: Int = 0
    private var swipeLeftActionIconTint: Int? = null

    private var swipeRightBackgroundColor: Int = 0
    private var swipeRightActionIconId: Int = 0
    private var swipeRightActionIconTint: Int? = null

    init {
        swipeLeftBackgroundColor = 0
        swipeRightBackgroundColor = 0
        swipeLeftActionIconId = 0
        swipeRightActionIconId = 0
        swipeLeftActionIconTint = null
        swipeRightActionIconTint = null
    }

    /**
     * Set the background color for left swipe direction
     *
     * @param swipeLeftBackgroundColor The resource id of the background color to be set
     */
    private fun setSwipeLeftBackgroundColor(swipeLeftBackgroundColor: Int) {
        this.swipeLeftBackgroundColor = swipeLeftBackgroundColor
    }

    /**
     * Set the action icon for left swipe direction
     *
     * @param swipeLeftActionIconId The resource id of the icon to be set
     */
    private fun setSwipeLeftActionIconId(swipeLeftActionIconId: Int) {
        this.swipeLeftActionIconId = swipeLeftActionIconId
    }

    /**
     * Set the tint color for action icon drawn while swiping left
     *
     * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
     */
    private fun setSwipeLeftActionIconTint(color: Int) {
        swipeLeftActionIconTint = color
    }

    /**
     * Set the background color for right swipe direction
     *
     * @param swipeRightBackgroundColor The resource id of the background color to be set
     */
    private fun setSwipeRightBackgroundColor(swipeRightBackgroundColor: Int) {
        this.swipeRightBackgroundColor = swipeRightBackgroundColor
    }

    /**
     * Set the action icon for right swipe direction
     *
     * @param swipeRightActionIconId The resource id of the icon to be set
     */
    private fun setSwipeRightActionIconId(swipeRightActionIconId: Int) {
        this.swipeRightActionIconId = swipeRightActionIconId
    }

    /**
     * Set the tint color for action icon drawn while swiping right
     *
     * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
     */
    private fun setSwipeRightActionIconTint(color: Int) {
        swipeRightActionIconTint = color
    }

    /**
     * Decorate the RecyclerView item with the chosen backgrounds and icons
     */
    fun decorate() {
        try {
            val iconHorizontalMargin = recyclerView.context.dip(16)
            val iconVisibilityDx = recyclerView.context.dip(16 + 24)
            if (actionState != ItemTouchHelper.ACTION_STATE_SWIPE) return

            if (dX > 0) {
                // Swiping Right
                if (swipeRightBackgroundColor != 0) {
                    val background = ColorDrawable(swipeRightBackgroundColor)
                    background.setBounds(
                        viewHolder.itemView.left,
                        viewHolder.itemView.top,
                        viewHolder.itemView.left + dX.toInt(),
                        viewHolder.itemView.bottom
                    )
                    background.draw(canvas)
                }

                if (swipeRightActionIconId != 0 && dX > iconVisibilityDx) {
                    val icon = ContextCompat.getDrawable(recyclerView.context, swipeRightActionIconId)
                    if (icon != null) {
                        val iconSize = icon.intrinsicHeight
                        val halfIcon = iconSize / 2
                        val top =
                            viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - halfIcon)
                        icon.setBounds(
                            viewHolder.itemView.left + iconHorizontalMargin,
                            top,
                            viewHolder.itemView.left + iconHorizontalMargin + icon.intrinsicWidth,
                            top + icon.intrinsicHeight
                        )
                        swipeRightActionIconTint?.let {
                            DrawableCompat.setTint(icon, it)
                        }
                        icon.draw(canvas)
                    }
                }
            } else if (dX < 0) {
                // Swiping Left
                if (swipeLeftBackgroundColor != 0) {
                    val background = ColorDrawable(swipeLeftBackgroundColor)
                    background.setBounds(
                        viewHolder.itemView.right + dX.toInt(),
                        viewHolder.itemView.top,
                        viewHolder.itemView.right,
                        viewHolder.itemView.bottom
                    )
                    background.draw(canvas)
                }

                if (swipeLeftActionIconId != 0 && dX < -iconVisibilityDx) {
                    val icon = ContextCompat.getDrawable(recyclerView.context, swipeLeftActionIconId)
                    if (icon != null) {
                        val iconSize = icon.intrinsicHeight
                        val halfIcon = iconSize / 2
                        val top =
                            viewHolder.itemView.top + ((viewHolder.itemView.bottom - viewHolder.itemView.top) / 2 - halfIcon)
                        icon.setBounds(
                            viewHolder.itemView.right - iconHorizontalMargin - halfIcon * 2,
                            top,
                            viewHolder.itemView.right - iconHorizontalMargin,
                            top + icon.intrinsicHeight
                        )
                        swipeLeftActionIconTint?.let {
                            DrawableCompat.setTint(icon, it)
                        }
                        icon.draw(canvas)
                    }
                }
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    /**
     * A Builder for the RecyclerViewSwipeDecorator class
     */
    class Builder
    /**
     * Create a builder for a RecyclerViewsSwipeDecorator
     *
     * @param canvas            The canvas which RecyclerView is drawing its children
     * @param recyclerView      The RecyclerView to which ItemTouchHelper is attached to
     * @param viewHolder        The ViewHolder which is being interacted by the User or it was interacted and simply animating to its original position
     * @param dX                The amount of horizontal displacement caused by user's action
     * @param actionState       The type of interaction on the View. Is either ACTION_STATE_DRAG or ACTION_STATE_SWIPE.
     */
        (
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        actionState: Int
    ) {

        private val mDecorator: RecyclerViewSwipeDecorator = RecyclerViewSwipeDecorator(
            canvas,
            recyclerView,
            viewHolder,
            dX,
            actionState
        )

        /**
         * Add a background color while swiping right
         *
         * @param color A single color value in the form 0xAARRGGBB
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightBackgroundColor(color: Int): Builder {
            mDecorator.setSwipeRightBackgroundColor(color)
            return this
        }

        /**
         * Add an action icon while swiping right
         *
         * @param drawableId The resource id of the icon to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeRightActionIcon(drawableId: Int): Builder {
            mDecorator.setSwipeRightActionIconId(drawableId)
            return this
        }

        /**
         * Set the tint color for action icon shown while swiping right
         *
         * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeRightActionIconTint(color: Int): Builder {
            mDecorator.setSwipeRightActionIconTint(color)
            return this
        }

        /**
         * Adds a background color while swiping left
         *
         * @param color A single color value in the form 0xAARRGGBB
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftBackgroundColor(color: Int): Builder {
            mDecorator.setSwipeLeftBackgroundColor(color)
            return this
        }

        /**
         * Add an action icon while swiping left
         *
         * @param drawableId The resource id of the icon to be set
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun addSwipeLeftActionIcon(drawableId: Int): Builder {
            mDecorator.setSwipeLeftActionIconId(drawableId)
            return this
        }

        /**
         * Set the tint color for action icon shown while swiping left
         *
         * @param color a color in ARGB format (e.g. 0xFF0000FF for blue)
         * @return This instance of @RecyclerViewSwipeDecorator.Builder
         */
        fun setSwipeLeftActionIconTint(color: Int): Builder {
            mDecorator.setSwipeLeftActionIconTint(color)
            return this
        }

        /**
         * Create a RecyclerViewSwipeDecorator
         *
         * @return The created @RecyclerViewSwipeDecorator
         */
        fun create(): RecyclerViewSwipeDecorator {
            return mDecorator
        }
    }
}