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

package com.sbgapps.scoreit.app.ui.color

import android.content.res.ColorStateList
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.core.widget.ItemAdapter
import com.sbgapps.scoreit.databinding.ListItemColorBinding

class ColorAdapter(
    private val color: Int,
    private val callback: (Int) -> Unit
) : ItemAdapter(R.layout.list_item_color) {

    private lateinit var binding: ListItemColorBinding

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        binding = ListItemColorBinding.bind(viewHolder.itemView)
        binding.colorCircle.imageTintList = ColorStateList.valueOf(color)
        binding.root.setOnClickListener { callback(color) }
    }
}
