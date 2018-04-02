/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.sbgapps.scoreit.ui.ext.inflate
import timber.log.Timber


class LinearListView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var adapter: Adapter<*>? = null
    private val children = mutableListOf<View>()

    fun setAdapter(_adapter: Adapter<*>) {
        adapter = _adapter
        setupChildren()
    }

    private fun setupChildren() {
        removeAllViews()
        adapter?.let {
            it.items.forEachIndexed { index, _ ->
                val view = children.getOrNull(index) ?: run {
                    val view = it.getView(index, null, this)
                    children.add(view)
                    view
                }
                Timber.d("Binding view $index for adapter")
                it.bind(index, view)
                addViewInLayout(view, -1, view.layoutParams, true)
            }
        }
    }

    abstract class Adapter<out Model>(val items: List<Model>) : BaseAdapter() {

        abstract val layoutId: Int

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            Timber.d("Creating view $position for adapter")
            return convertView ?: parent.inflate(layoutId)
        }

        override fun getItem(position: Int) = items[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = items.size

        abstract fun bind(position: Int, view: View)
    }
}