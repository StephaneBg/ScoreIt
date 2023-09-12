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

package com.sbgapps.scoreit.app.ui.edition.tarot

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBidValue
import com.sbgapps.scoreit.data.model.TarotBonusValue
import com.sbgapps.scoreit.data.model.TarotOudlerValue
import com.sbgapps.scoreit.databinding.ActivityEditionTarotBinding
import com.sbgapps.scoreit.databinding.ListItemEditionBonusBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class TarotEditionActivity : EditionActivity() {

    private val viewModel by viewModel<TarotEditionViewModel>()
    private lateinit var binding: ActivityEditionTarotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionTarotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        viewModel.observeStates(this) { state ->
            when (state) {
                is TarotEditionState.Content -> {
                    binding.taker.text = state.players[state.taker.index].name
                    binding.taker.setOnClickListener {
                        MaterialAlertDialogBuilder(this)
                            .setSingleChoiceItems(
                                state.players.map { it.name }.toTypedArray(),
                                state.taker.index
                            ) { dialog, which ->
                                viewModel.setTaker(PlayerPosition.fromIndex(which))
                                dialog.dismiss()
                            }
                            .show()
                    }

                    if (PlayerPosition.NONE == state.partner) {
                        binding.headerPartner.isVisible = false
                        binding.partner.isVisible = false
                    } else {
                        binding.partner.text = state.players[state.partner.index].name
                        binding.partner.setOnClickListener {
                            MaterialAlertDialogBuilder(this)
                                .setSingleChoiceItems(
                                    state.players.map { it.name }.toTypedArray(),
                                    state.partner.index
                                ) { dialog, which ->
                                    viewModel.setPartner(PlayerPosition.fromIndex(which))
                                    dialog.dismiss()
                                }
                                .show()
                        }
                    }

                    binding.bid.text = getString(state.bid.resId)
                    binding.bid.setOnClickListener {
                        MaterialAlertDialogBuilder(this)
                            .setSingleChoiceItems(
                                R.array.tarot_bids,
                                state.bid.ordinal
                            ) { dialog, which ->
                                viewModel.setBid(TarotBidValue.values()[which])
                                dialog.dismiss()
                            }
                            .show()
                    }

                    binding.buttonPetit.text = getString(R.string.tarot_oudler_petit)
                    binding.buttonTwentyOne.text = getString(R.string.tarot_oudler_twentyone)
                    binding.buttonExcuse.text = getString(R.string.tarot_oudler_excuse)
                    binding.oudlersButtonGroup.removeOnButtonCheckedListener(buttonCheckedListener)
                    if (state.oudlers.contains(TarotOudlerValue.PETIT)) binding.oudlersButtonGroup.check(R.id.buttonPetit)
                    if (state.oudlers.contains(TarotOudlerValue.TWENTY_ONE)) binding.oudlersButtonGroup.check(R.id.buttonTwentyOne)
                    if (state.oudlers.contains(TarotOudlerValue.EXCUSE)) binding.oudlersButtonGroup.check(R.id.buttonExcuse)
                    binding.oudlersButtonGroup.addOnButtonCheckedListener(buttonCheckedListener)

                    binding.points.text = state.points.toString()
                    bindButton(binding.pointsPlusTen, 10, state.stepPointsByTen.canAdd)
                    bindButton(binding.pointsMinusTen, -10, state.stepPointsByTen.canSubtract)
                    bindButton(binding.pointsPlusOne, 1, state.stepPointsByOne.canAdd)
                    bindButton(binding.pointsMinusOne, -1, state.stepPointsByOne.canSubtract)

                    binding.addBonus.isVisible = state.availableBonuses.isNotEmpty()
                    binding.addBonus.setOnClickListener {
                        TarotEditionBonusFragment().show(supportFragmentManager, null)
                    }
                    val model = state.selectedBonuses.map { (player, bonus) ->
                        state.players[player.index].name to bonus
                    }
                    binding.bonusContainer.adapter = BonusAdapter(model)
                }

                TarotEditionState.Completed -> super.onBackPressed()
            }
        }
        viewModel.loadContent()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done -> {
            viewModel.completeEdition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        viewModel.cancelEdition()
    }

    private val buttonCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { view, _, _ ->
        val oudlers = mutableListOf<TarotOudlerValue>()
        view.checkedButtonIds.forEach {
            when (it) {
                R.id.buttonPetit -> oudlers += TarotOudlerValue.PETIT
                R.id.buttonTwentyOne -> oudlers += TarotOudlerValue.TWENTY_ONE
                R.id.buttonExcuse -> oudlers += TarotOudlerValue.EXCUSE
            }
        }
        viewModel.setOudlers(oudlers)
    }

    private fun bindButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementScore(increment)
            }
        }
    }

    inner class BonusAdapter(val model: List<Pair<String, TarotBonusValue>>) : AdaptableLinearLayoutAdapter {

        @SuppressLint("SetTextI18n")
        override fun getView(position: Int, parent: ViewGroup): View {
            val view = ListItemEditionBonusBinding.inflate(layoutInflater)
            view.bonus.text = "${model[position].first} • ${getString(model[position].second.resId)}"
            view.remove.setOnClickListener {
                viewModel.removeBonus(position)
            }
            return view.root
        }

        override fun getCount(): Int = model.size
    }
}
