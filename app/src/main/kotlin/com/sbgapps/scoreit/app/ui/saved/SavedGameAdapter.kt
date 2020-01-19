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

package com.sbgapps.scoreit.app.ui.saved

import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ListItemSavedGamesBinding
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.core.widget.ItemAdapter

class SavedGameAdapter(
    private val fileName: String,
    private val lastModified: String,
    private val players: String,
    private val callback: (fileName: String) -> Unit
) : ItemAdapter(R.layout.list_item_saved_games) {

    private lateinit var binding: ListItemSavedGamesBinding

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        binding = ListItemSavedGamesBinding.bind(viewHolder.itemView)
        binding.fileName.text = fileName
        binding.players.text = players
        binding.lastModified.text = lastModified
        binding.root.setOnClickListener { callback(fileName) }
    }
}
