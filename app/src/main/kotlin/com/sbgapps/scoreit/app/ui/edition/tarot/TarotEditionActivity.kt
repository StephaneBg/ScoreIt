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

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityEditionTarotBinding
import com.sbgapps.scoreit.app.databinding.ListItemEditionBonusBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBid
import com.sbgapps.scoreit.data.model.TarotBonus
import com.sbgapps.scoreit.data.model.TarotOudler
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class TarotEditionActivity : EditionActivity() {

    private val viewModel by viewModel<TarotEditionViewModel>()
    private lateinit var binding: ActivityEditionTarotBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionTarotBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        onStates(viewModel) { state ->
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
                                viewModel.setBid(TarotBid.values()[which])
                                dialog.dismiss()
                            }
                            .show()
                    }

                    binding.oudlersButtonGroup.removeOnButtonCheckedListener(buttonCheckedListener)
                    if (state.oudlers.contains(TarotOudler.PETIT)) binding.oudlersButtonGroup.check(R.id.buttonPetit)
                    if (state.oudlers.contains(TarotOudler.TWENTY_ONE)) binding.oudlersButtonGroup.check(R.id.buttonTwentyOne)
                    if (state.oudlers.contains(TarotOudler.EXCUSE)) binding.oudlersButtonGroup.check(R.id.buttonExcuse)
                    binding.oudlersButtonGroup.addOnButtonCheckedListener(buttonCheckedListener)

                    binding.points.text = state.points.toString()

                    setupButton(binding.pointsPlusTen, 10, state.canIncrement.canStepTen)
                    setupButton(binding.pointsMinusTen, -10, state.canDecrement.canStepTen)
                    setupButton(binding.pointsPlusOne, 1, state.canIncrement.canStepOne)
                    setupButton(binding.pointsMinusOne, -1, state.canDecrement.canStepOne)

                    binding.addBonus.isVisible = state.availableBonuses.isNotEmpty()
                    binding.addBonus.setOnClickListener {
                        TarotEditionBonusFragment().show(supportFragmentManager, null)
                    }
                    val model = state.selectedBonuses.map { (player, bonus) ->
                        state.players[player.index].name to bonus
                    }
                    binding.bonusContainer.adapter = BonusAdapter(model)
                }

                TarotEditionState.Completed -> finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done -> {
            viewModel.completeEdition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private val buttonCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { view, _, _ ->
        val oudlers = mutableListOf<TarotOudler>()
        view.checkedButtonIds.forEach {
            when (it) {
                R.id.buttonPetit -> oudlers += TarotOudler.PETIT
                R.id.buttonTwentyOne -> oudlers += TarotOudler.TWENTY_ONE
                R.id.buttonExcuse -> oudlers += TarotOudler.EXCUSE
            }
        }
        viewModel.setOudlers(oudlers)
    }

    private fun setupButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementScore(increment)
            }
        }
    }

    inner class BonusAdapter(val model: List<Pair<String, TarotBonus>>) : AdaptableLinearLayoutAdapter {

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
