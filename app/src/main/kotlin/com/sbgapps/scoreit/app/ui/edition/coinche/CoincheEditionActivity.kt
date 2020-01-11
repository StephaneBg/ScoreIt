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
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityEditionCoincheBinding
import com.sbgapps.scoreit.app.databinding.ListItemEditionBonusBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.CoincheSolver
import io.uniflow.androidx.flow.onStates
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CoincheEditionActivity : EditionActivity() {

    private val viewModel by viewModel<CoincheEditionViewModel>()
    private val solver by inject<CoincheSolver>()
    private lateinit var binding: ActivityEditionCoincheBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionCoincheBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        binding.switchPoints.setOnClickListener {
            viewModel.switchScorer()
        }

        onStates(viewModel) { state ->
            when (state) {
                is CoincheEditionState.Content -> {
                    setupBidButton(binding.bidPlusTen, 10, state.canIncrement.first)
                    setupBidButton(binding.bidMinusTen, -10, state.canDecrement.first)
                    setupPointsButton(binding.pointsPlusTen, 10, state.canIncrement.second)
                    setupPointsButton(binding.pointsMinusTen, -10, state.canDecrement.second)

                    binding.nameTeamOne.apply {
                        text = state.players[PlayerPosition.ONE.index].name
                        setTextColor(state.players[PlayerPosition.ONE.index].color)
                    }
                    binding.nameTeamTwo.apply {
                        text = state.players[PlayerPosition.TWO.index].name
                        setTextColor(state.players[PlayerPosition.TWO.index].color)
                    }

                    binding.pointsTeamOne.text = solver.getPointsForDisplay(state.points).toString()
                    binding.pointsTeamTwo.text = solver.getPointsForDisplay(state.points).toString()//TODO

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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done -> {
            viewModel.completeEdition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupBidButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementBid(increment)
            }
        }
    }

    private fun setupPointsButton(button: MaterialButton, increment: Int, enabled: Boolean) {
        button.apply {
            isEnabled = enabled
            setOnClickListener {
                viewModel.incrementPoints(increment)
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
