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

package com.sbgapps.scoreit.app.ui.history

import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ListItemLapTarotBinding
import com.sbgapps.scoreit.app.model.TarotLap
import com.sbgapps.scoreit.core.utils.string.build
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.core.widget.ItemAdapter

class TarotLapAdapter(private val model: TarotLap) : ItemAdapter(R.layout.list_item_lap_tarot) {

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        val binding = ListItemLapTarotBinding.bind(viewHolder.itemView)
        binding.info.text = model.info.build(viewHolder.context)
        binding.recyclerView.adapter = LapResultAdapter(model.results)
        binding.done.setBackgroundResource(if (model.isWon) R.color.game_won else R.color.game_lost)
    }
}
