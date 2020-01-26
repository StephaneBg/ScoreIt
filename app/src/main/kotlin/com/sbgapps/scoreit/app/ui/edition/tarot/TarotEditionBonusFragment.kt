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

package com.sbgapps.scoreit.app.ui.edition.tarot

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.FragmentEditionTarotBonusBinding
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBonusValue
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TarotEditionBonusFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<TarotEditionViewModel>()
    private lateinit var binding: FragmentEditionTarotBonusBinding
    private var tarotBonus: TarotBonusValue = TarotBonusValue.PETIT_AU_BOUT
    private var player: PlayerPosition = PlayerPosition.ONE

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditionTarotBonusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onStates(viewModel) { state ->
            if (state is TarotEditionState.Content) {
                if (state.availableBonuses.isEmpty()) return@onStates

                // player
                binding.player.setText(state.players[player.index].name)
                val adapterPlayers = ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    state.players.map { it.name }.toTypedArray()
                )
                binding.player.setAdapter(adapterPlayers)
                binding.player.setOnItemClickListener { _, _, position, _ ->
                    player = PlayerPosition.fromIndex(position)
                }

                //bonus
                tarotBonus = state.availableBonuses.first()
                binding.bonus.setText(tarotBonus.resId)
                val adapterBonus = ArrayAdapter(
                    requireContext(),
                    R.layout.dropdown_menu_popup_item,
                    state.availableBonuses.map { getString(it.resId) }.toTypedArray()
                )
                binding.bonus.setAdapter(adapterBonus)
                binding.bonus.setOnItemClickListener { _, _, position, _ ->
                    tarotBonus = state.availableBonuses[position]
                }

                // button
                binding.addBonus.setOnClickListener {
                    viewModel.addBonus(player to tarotBonus)
                    dismiss()
                }
            }
        }
    }
}
