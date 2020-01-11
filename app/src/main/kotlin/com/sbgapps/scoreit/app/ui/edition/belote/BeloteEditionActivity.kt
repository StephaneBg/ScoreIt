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

package com.sbgapps.scoreit.app.ui.edition.belote

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityEditionBeloteBinding
import com.sbgapps.scoreit.app.databinding.ListItemEditionBonusBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.PlayerPosition
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class BeloteEditionActivity : EditionActivity() {

    private val viewModel by viewModel<BeloteEditionViewModel>()
    private lateinit var binding: ActivityEditionBeloteBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionBeloteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        onStates(viewModel) { state ->
            when (state) {
                is BeloteEditionState.Content -> {
                    binding.buttonTeamOne.text = state.players[PlayerPosition.ONE.index].name
                    binding.buttonTeamTwo.text = state.players[PlayerPosition.TWO.index].name

                    binding.scorerGroup.removeOnButtonCheckedListener(scorerCheckedListener)
                    when (state.scorer) {
                        PlayerPosition.ONE -> binding.scorerGroup.check(R.id.buttonTeamOne)
                        PlayerPosition.TWO -> binding.scorerGroup.check(R.id.buttonTeamTwo)
                        else -> error("Only two players for Belote")
                    }
                    binding.scorerGroup.addOnButtonCheckedListener(scorerCheckedListener)

                    binding.buttonPoints.text = getString(R.string.belote_button_score)
                    binding.pointsModeGroup.removeOnButtonCheckedListener(pointModeCheckedListener)
                    when (state.pointMode) {
                        is PointMode.Score -> {
                            binding.pointsModeGroup.check(R.id.buttonPoints)
                            binding.buttonGroup.isVisible = true
                        }
                        PointMode.Capot -> {
                            binding.pointsModeGroup.check(R.id.buttonCapot)
                            binding.buttonGroup.isVisible = false
                        }
                    }
                    binding.pointsModeGroup.addOnButtonCheckedListener(pointModeCheckedListener)

                    setupButton(binding.pointsPlusTen, 10, state.canIncrement.canStepTen)
                    setupButton(binding.pointsMinusTen, -10, state.canDecrement.canStepTen)
                    setupButton(binding.pointsPlusOne, 1, state.canIncrement.canStepOne)
                    setupButton(binding.pointsMinusOne, -1, state.canDecrement.canStepOne)

                    binding.nameTeamOne.apply {
                        text = state.players[PlayerPosition.ONE.index].name
                        setTextColor(state.players[PlayerPosition.ONE.index].color)
                    }
                    binding.nameTeamTwo.apply {
                        text = state.players[PlayerPosition.TWO.index].name
                        setTextColor(state.players[PlayerPosition.TWO.index].color)
                    }

                    val (teamOne, teamTwo) = state.teamPoints
                    binding.pointsTeamOne.text = teamOne
                    binding.pointsTeamTwo.text = teamTwo

                    binding.addBonus.isVisible = state.availableBonuses.isNotEmpty()
                    binding.addBonus.setOnClickListener {
                        BeloteEditionBonusFragment().show(supportFragmentManager, null)
                    }
                    val model = state.selectedBonuses.map { (player, bonus) ->
                        state.players[player.index].name to bonus
                    }
                    binding.bonusContainer.adapter = BonusAdapter(model)
                }

                is BeloteEditionState.Completed -> finish()
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

    private val pointModeCheckedListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                viewModel.editMode(
                    when (checkedId) {
                        R.id.buttonPoints -> PointMode.Score(81)
                        R.id.buttonCapot -> PointMode.Capot
                        else -> error("Unknown mode")
                    }
                )
            }
        }

    private val scorerCheckedListener = MaterialButtonToggleGroup.OnButtonCheckedListener { _, checkedId, isChecked ->
        if (isChecked) {
            viewModel.changeScorer(
                when (checkedId) {
                    R.id.buttonTeamOne -> PlayerPosition.ONE
                    R.id.buttonTeamTwo -> PlayerPosition.TWO
                    else -> error("Unknown player")
                }
            )
        }
    }

    private fun setupButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementScore(
                    if (binding.scorerGroup.checkedButtonId == R.id.buttonTeamOne) increment else -increment
                )
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
