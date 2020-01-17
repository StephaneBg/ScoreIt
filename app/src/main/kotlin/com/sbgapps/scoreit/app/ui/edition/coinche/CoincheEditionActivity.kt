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
import com.sbgapps.scoreit.app.databinding.ActivityEditionCoincheBinding
import com.sbgapps.scoreit.app.databinding.ListItemEditionBonusBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.core.utils.string.build
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.Coinche
import com.sbgapps.scoreit.data.model.PlayerPosition
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoincheEditionActivity : EditionActivity() {

    private val viewModel by viewModel<CoincheEditionViewModel>()
    private lateinit var binding: ActivityEditionCoincheBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionCoincheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        onStates(viewModel) { state ->
            when (state) {
                is CoincheEditionState.Content -> {
                    binding.buttonTeamOne.text = state.players[PlayerPosition.ONE.index].name
                    binding.buttonTeamTwo.text = state.players[PlayerPosition.TWO.index].name

                    binding.lapInfo.text = state.lapInfo.build(this)

                    binding.scorerGroup.removeOnButtonCheckedListener(scorerCheckedListener)
                    when (state.taker) {
                        PlayerPosition.ONE -> binding.scorerGroup.check(R.id.buttonTeamOne)
                        PlayerPosition.TWO -> binding.scorerGroup.check(R.id.buttonTeamTwo)
                        else -> error("Only two players for Belote")
                    }
                    binding.scorerGroup.addOnButtonCheckedListener(scorerCheckedListener)

                    binding.bid.text = state.bidPoints.toString()
                    setupBidButton(binding.bidPlusTen, 10, state.stepBid.canAdd)
                    setupBidButton(binding.bidMinusTen, -10, state.stepBid.canSubtract)

                    binding.nameTeamOne.text = state.players[PlayerPosition.ONE.index].name
                    binding.nameTeamTwo.text = state.players[PlayerPosition.TWO.index].name

                    binding.coinche.text = getString(state.coinche.resId)
                    binding.coinche.setOnClickListener {
                        MaterialAlertDialogBuilder(this)
                            .setSingleChoiceItems(
                                R.array.coinche,
                                state.coinche.ordinal
                            ) { dialog, which ->
                                viewModel.setCoinche(Coinche.values()[which])
                                dialog.dismiss()
                            }
                            .show()
                    }

                    binding.pointsTeamOne.text = state.teamPoints.first
                    binding.pointsTeamTwo.text = state.teamPoints.second
                    setupPointsButton(binding.pointsPlusTen, 10, state.stepPointsByTen.canAdd)
                    setupPointsButton(binding.pointsMinusTen, -10, state.stepPointsByTen.canSubtract)
                    setupPointsButton(binding.pointsPlusOne, 1, state.stepPointsByOne.canAdd)
                    setupPointsButton(binding.pointsMinusOne, -1, state.stepPointsByOne.canSubtract)

                    binding.addBonus.isVisible = state.availableBonuses.isNotEmpty()
                    binding.addBonus.setOnClickListener {
                        CoincheEditionBonusFragment().show(supportFragmentManager, null)
                    }
                    val model = state.selectedBonuses.map { (player, bonus) ->
                        state.players[player.index].name to bonus
                    }
                    binding.bonusContainer.adapter = BonusAdapter(model)
                }

                is CoincheEditionState.Completed -> finish()
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

    private val scorerCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
        if (isChecked) {
            viewModel.changeTaker(
                when (checkedId) {
                    R.id.buttonTeamOne -> PlayerPosition.ONE
                    R.id.buttonTeamTwo -> PlayerPosition.TWO
                    else -> error("Unknown player")
                }
            )
        }
    }

    private fun setupBidButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.stepBid(increment)
            }
        }
    }

    private fun setupPointsButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementScore(increment)
            }
        }
    }

    inner class BonusAdapter(val model: List<Pair<String, BeloteBonus>>) : AdaptableLinearLayoutAdapter {

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
