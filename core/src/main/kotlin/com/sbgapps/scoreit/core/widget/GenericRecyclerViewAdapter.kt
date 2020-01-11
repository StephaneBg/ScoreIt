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

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sbgapps.scoreit.core.ext.inflate

class GenericRecyclerViewAdapter(initialItems: List<ItemAdapter> = emptyList()) :
    RecyclerView.Adapter<BaseViewHolder>() {

    var items: List<ItemAdapter> = initialItems
        private set

    fun updateItems(newItems: List<ItemAdapter>, diffResult: DiffUtil.DiffResult? = null) {
        items = newItems
        diffResult?.dispatchUpdatesTo(this) ?: notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].layoutId

    override fun onCreateViewHolder(parent: ViewGroup, @LayoutRes layoutId: Int): BaseViewHolder {
        val itemView = parent.inflate(layoutId)
        return items.first { it.layoutId == layoutId }.onCreateViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) =
        items[position].onBindViewHolder(holder)
}
