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

package com.sbgapps.scoreit.app.ui.edition.universal

import com.google.android.material.button.MaterialButton
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ListItemEditionUniversalBinding
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.core.widget.ItemAdapter
import com.sbgapps.scoreit.data.model.Player

class UniversalEditionAdapter(
    val player: Player,
    val score: Int,
    private val incrementCallback: (Int, Int) -> Unit,
    private val inputCallback: (Int) -> Unit
) : ItemAdapter(R.layout.list_item_edition_universal) {

    private lateinit var binding: ListItemEditionUniversalBinding

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        binding = ListItemEditionUniversalBinding.bind(viewHolder.itemView)
        binding.name.apply {
            text = player.name
            setTextColor(player.color)
        }
        val position = viewHolder.adapterPosition
        initButton(binding.pointsPlusOne, position, 1)
        initButton(binding.pointsPlusFive, position, 5)
        initButton(binding.pointsPlusTen, position, 10)
        initButton(binding.pointsPlusHundred, position, 100)
        initButton(binding.pointsMinusOne, position, -1)
        initButton(binding.pointsMinusFive, position, -5)
        initButton(binding.pointsMinusTen, position, -10)
        initButton(binding.pointsMinusHundred, position, -100)
        binding.score.apply {
            text = score.toString()
            setOnClickListener { inputCallback(position) }
        }
    }

    private fun initButton(button: MaterialButton, position: Int, increment: Int) {
        button.setOnClickListener {
            incrementCallback(position, increment)
        }
    }
}
