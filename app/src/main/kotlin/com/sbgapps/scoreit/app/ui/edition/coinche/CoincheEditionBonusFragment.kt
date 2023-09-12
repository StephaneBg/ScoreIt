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

package com.sbgapps.scoreit.app.ui.edition.coinche

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.data.model.BeloteBonusValue
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.databinding.FragmentEditionCoincheBonusBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CoincheEditionBonusFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<CoincheEditionViewModel>()
    private var beloteBonus: BeloteBonusValue = BeloteBonusValue.BELOTE
    private lateinit var binding: FragmentEditionCoincheBonusBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditionCoincheBonusBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.observeStates(this) { state ->
            if (state is CoincheEditionState.Content) {
                binding.teamOne.text = state.players[PlayerPosition.ONE.index].name
                binding.teamTwo.text = state.players[PlayerPosition.TWO.index].name

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

    private fun getTeam(): PlayerPosition =
        if (R.id.teamOne == binding.teamGroup.checkedButtonId) PlayerPosition.ONE else PlayerPosition.TWO
}
