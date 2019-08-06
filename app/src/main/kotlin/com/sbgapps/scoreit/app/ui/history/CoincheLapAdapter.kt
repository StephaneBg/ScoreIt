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
import com.sbgapps.scoreit.app.databinding.ListItemLapBeloteCoincheBinding
import com.sbgapps.scoreit.app.model.CoincheLap
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.core.widget.ItemAdapter

class CoincheLapAdapter(private val model: CoincheLap) : ItemAdapter(R.layout.list_item_lap_belote_coinche) {

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        val binding = ListItemLapBeloteCoincheBinding.bind(viewHolder.itemView)
        binding.results.adapter = LapResultAdapter(model.results.map { it.toString() })
        binding.done.setBackgroundResource(if (model.isWon) R.color.game_won else R.color.game_lost)
    }
}
