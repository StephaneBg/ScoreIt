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
import android.database.DataSetObserver
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import com.sbgapps.scoreit.ui.ext.inflate


class LinearListView : LinearLayout {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private var adapter: Adapter<*>? = null
    private val dataObserver = object : DataSetObserver() {
        override fun onChanged() {
            setupChildren()
        }

        override fun onInvalidated() {
            setupChildren()
        }
    }

    fun setAdapter(_adapter: Adapter<*>) {
        adapter?.unregisterDataSetObserver(dataObserver)
        adapter = _adapter
        adapter?.registerDataSetObserver(dataObserver)
        setupChildren()
    }

    private fun setupChildren() {
        removeAllViews()

        adapter?.let {
            it.children.clear()
            (0 until it.count)
                    .asSequence()
                    .map { i ->
                        val view = it.getView(i, null, this)
                        it.children.add(view)
                        view
                    }
                    .forEach {
                        addViewInLayout(it, -1, it?.layoutParams, true)
                    }
        }
    }

    abstract class Adapter<Model> : BaseAdapter() {

        abstract val layoutId: Int
        val children = mutableListOf<View>()
        var items = emptyList<Model>()
            set(value) {
                if (value.count() == items.count()) {
                    field = value
                    updateItems()
                } else {
                    field = value
                    notifyDataSetChanged()
                }
            }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: parent.inflate(layoutId)
            bind(position, view)
            return view
        }

        override fun getItem(position: Int) = items[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = items.size

        abstract fun bind(position: Int, view: View)

        private fun updateItems() {
            for (i in 0 until children.count()) bind(i, children[i])
        }
    }
}