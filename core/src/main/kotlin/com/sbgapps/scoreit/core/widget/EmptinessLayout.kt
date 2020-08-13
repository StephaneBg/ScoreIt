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

package com.sbgapps.scoreit.core.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbgapps.scoreit.core.R

class EmptinessLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    lateinit var recyclerView: RecyclerView
        private set
    private lateinit var emptyView: View

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        super.addView(child, index, params)
        when {
            child is RecyclerView -> recyclerView = child
            R.id.emptyView == child.id -> emptyView = child
        }
    }

    fun setAdapter(adapter: RecyclerView.Adapter<*>?) {
        recyclerView.adapter?.unregisterAdapterDataObserver(observer)
        recyclerView.adapter = adapter
        adapter?.registerAdapterDataObserver(observer)
        checkIfEmpty()
    }

    private val observer = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() {
            checkIfEmpty()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
            checkIfEmpty()
        }
    }

    fun checkIfEmpty() {
        val isEmptyVisible = recyclerView.adapter?.itemCount == 0
        emptyView.isVisible = isEmptyVisible
        isVisible = !isEmptyVisible
    }

    fun scrollToTop() = recyclerView.smoothScrollToPosition(0)

    fun isScrolled(): Boolean = (recyclerView.layoutManager as LinearLayoutManager)
        .findFirstVisibleItemPosition() != 0
}
