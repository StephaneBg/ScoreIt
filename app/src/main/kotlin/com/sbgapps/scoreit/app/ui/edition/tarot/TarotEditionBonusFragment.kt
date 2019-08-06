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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.databinding.FragmentEditionTarotBonusBinding
import com.sbgapps.scoreit.app.model.TarotBonus
import com.sbgapps.scoreit.data.model.PLAYER_1
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class TarotEditionBonusFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<TarotEditionViewModel>()
    private lateinit var binding: FragmentEditionTarotBonusBinding
    private var tarotBonus: TarotBonus = TarotBonus.PetitAuBout
    private var player: Int = PLAYER_1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditionTarotBonusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onStates(viewModel) { state ->
            if (state is TarotEditionState.Content) {
                if (state.availableBonuses.isEmpty()) return@onStates

                binding.player.text = state.players[player].name
                binding.player.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setSingleChoiceItems(
                            state.players.map { it.name }.toTypedArray(),
                            player
                        ) { dialog, which ->
                            player = which
                            binding.player.text = state.players[player].name
                            dialog.dismiss()
                        }
                        .show()
                }

                tarotBonus = state.availableBonuses.first()
                binding.bonus.text = getString(tarotBonus.resId)
                binding.bonus.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setSingleChoiceItems(
                            state.availableBonuses.map { getString(it.resId) }.toTypedArray(),
                            state.availableBonuses.indexOf(tarotBonus)
                        ) { dialog, which ->
                            tarotBonus = state.availableBonuses[which]
                            binding.bonus.text = getString(tarotBonus.resId)
                            dialog.dismiss()
                        }
                        .show()
                }

                binding.addBonus.setOnClickListener {
                    viewModel.addBonus(player to tarotBonus)
                    dismiss()
                }
            }
        }
    }
}
