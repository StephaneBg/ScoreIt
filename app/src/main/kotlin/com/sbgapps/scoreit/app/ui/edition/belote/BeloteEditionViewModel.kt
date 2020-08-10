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

import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.utils.string.StringFactory
import com.sbgapps.scoreit.core.utils.string.fromResources
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusValue
import com.sbgapps.scoreit.data.model.BeloteLap
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.BeloteSolver
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.solver.counter
import com.sbgapps.scoreit.data.solver.counterPoints
import io.uniflow.core.flow.data.UIState

class BeloteEditionViewModel(
    private val useCase: GameUseCase,
    private val solver: BeloteSolver
) : BaseViewModel() {

    private val editedLap
        get() = useCase.getEditedLap() as BeloteLap

    fun loadContent() {
        action {
            setState { getContent() }
        }
    }

    fun incrementScore(increment: Int) {
        action {
            val result = editedLap.points + increment
            val points = when {
                result >= POINTS_TOTAL -> POINTS_TOTAL
                result < 2 -> 2
                else -> result
            }
            useCase.updateEdition(editedLap.copy(points = points))
            setState { getContent() }
        }
    }

    fun changeTaker(taker: PlayerPosition) {
        action {
            useCase.updateEdition(editedLap.copy(taker = taker))
            setState { getContent() }
        }
    }

    fun addBonus(bonus: Pair<PlayerPosition, BeloteBonusValue>) {
        action {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses += BeloteBonus(bonus.first, bonus.second)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            setState { getContent() }
        }
    }

    fun removeBonus(bonusIndex: Int) {
        action {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses.removeAt(bonusIndex)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            setState { getContent() }
        }
    }

    fun completeEdition() {
        action {
            useCase.completeEdition()
            setState { BeloteEditionState.Completed }
        }
    }

    fun cancelEdition() {
        action {
            useCase.cancelEdition()
            setState { BeloteEditionState.Completed }
        }
    }

    private fun getContent(): BeloteEditionState.Content =
        BeloteEditionState.Content(
            useCase.getPlayers(),
            editedLap.taker,
            getInfo(editedLap),
            getResults(editedLap),
            editedLap.bonuses.map { it.player to it.bonus },
            getAvailableBonuses(editedLap),
            canStepPointsByOne(editedLap),
            canStepPointsByTen(editedLap)
        )

    private fun getInfo(lap: BeloteLap): StringFactory {
        val (results, isWon) = solver.getResults(lap)
        return if (isWon) {
            when {
                lap.points == POINTS_TOTAL -> fromResources(
                    R.string.belote_info_capot,
                    results[lap.taker.index].toString()
                )
                solver.isLitigation(results.toList()) -> fromResources(R.string.belote_info_litigation)
                else -> fromResources(R.string.belote_info_win, results[lap.taker.index].toString())
            }
        } else {
            fromResources(R.string.belote_info_lose, results[lap.counter().index].toString())
        }
    }

    private fun canStepPointsByOne(lap: BeloteLap): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun canStepPointsByTen(lap: BeloteLap): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun getResults(lap: BeloteLap): Pair<String, String> =
        lap.points.toString() to lap.counterPoints().toString()

    private fun getAvailableBonuses(lap: BeloteLap): List<BeloteBonusValue> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        return listOfNotNull(
            BeloteBonusValue.BELOTE.takeUnless { currentBonuses.contains(BeloteBonusValue.BELOTE) },
            BeloteBonusValue.RUN_3,
            BeloteBonusValue.RUN_4,
            BeloteBonusValue.RUN_5,
            BeloteBonusValue.FOUR_NORMAL,
            BeloteBonusValue.FOUR_NINE.takeUnless { currentBonuses.contains(BeloteBonusValue.FOUR_NINE) },
            BeloteBonusValue.FOUR_JACK.takeUnless { currentBonuses.contains(BeloteBonusValue.FOUR_JACK) }
        )
    }
}

sealed class BeloteEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val lapInfo: StringFactory,
        val results: Pair<String, String>,
        val selectedBonuses: List<Pair<PlayerPosition, BeloteBonusValue>>,
        val availableBonuses: List<BeloteBonusValue>,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : BeloteEditionState()

    object Completed : BeloteEditionState()
}
