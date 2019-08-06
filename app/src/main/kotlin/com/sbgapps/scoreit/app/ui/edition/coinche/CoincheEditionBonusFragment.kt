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

package com.sbgapps.scoreit.app.ui.edition.coinche

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.FragmentEditionCoincheBonusBinding
import com.sbgapps.scoreit.app.model.BeloteBonus
import com.sbgapps.scoreit.data.model.PLAYER_1
import com.sbgapps.scoreit.data.model.PLAYER_2
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CoincheEditionBonusFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<CoincheEditionViewModel>()
    private var beloteBonus: BeloteBonus = BeloteBonus.Belote
    private lateinit var binding: FragmentEditionCoincheBonusBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEditionCoincheBonusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onStates(viewModel) { state ->
            if (state is CoincheEditionState.Content) {
                binding.teamOne.text = state.players[PLAYER_1].name
                binding.teamTwo.text = state.players[PLAYER_2].name

                beloteBonus = state.availableBonuses.first()
                binding.bonus.text = getString(beloteBonus.resId)
                binding.bonus.setOnClickListener {
                    MaterialAlertDialogBuilder(requireContext())
                        .setSingleChoiceItems(
                            state.availableBonuses.map { getString(it.resId) }.toTypedArray(),
                            state.availableBonuses.indexOf(beloteBonus)
                        ) { dialog, which ->
                            beloteBonus = state.availableBonuses[which]
                            binding.bonus.text = getString(beloteBonus.resId)
                            dialog.dismiss()
                        }
                        .show()
                }

                binding.addBonus.setOnClickListener {
                    viewModel.addBonus(getTeam() to beloteBonus)
                    dismiss()
                }
            }
        }
    }

    private fun getTeam(): Int = if (R.id.teamOne == binding.teamGroup.checkedButtonId) PLAYER_1 else PLAYER_2
}
