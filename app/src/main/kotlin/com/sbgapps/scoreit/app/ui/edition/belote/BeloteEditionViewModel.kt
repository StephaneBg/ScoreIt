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
import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.utils.string.StringFactory
import com.sbgapps.scoreit.core.utils.string.fromResources
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.BeloteLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.BeloteSolver
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.solver.counter
import com.sbgapps.scoreit.data.solver.counterPoints
import io.uniflow.core.flow.UIState

class BeloteEditionViewModel(
    private val useCase: GameUseCase,
    private val solver: BeloteSolver
) : BaseViewModel() {

    private val editedLap
        get() = useCase.getEditedLap() as BeloteLapData

    fun loadContent() {
        setState { getContent() }
    }

    fun incrementScore(increment: Int) {
        setState {
            val result = editedLap.points + increment
            val points = when {
                result >= POINTS_TOTAL -> POINTS_TOTAL
                result < 2 -> 2
                else -> result
            }
            useCase.updateEdition(editedLap.copy(points = points))
            getContent()
        }
    }

    fun changeTaker(taker: PlayerPosition) {
        setState {
            useCase.updateEdition(editedLap.copy(taker = taker))
            getContent()
        }
    }

    fun addBonus(bonus: Pair<PlayerPosition, BeloteBonus>) {
        setState {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses += BeloteBonusData(bonus.first, bonus.second)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun removeBonus(bonusIndex: Int) {
        setState {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses.removeAt(bonusIndex)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun completeEdition() {
        setState {
            useCase.completeEdition()
            BeloteEditionState.Completed
        }
    }

    fun cancelEdition() {
        setState {
            useCase.cancelEdition()
            BeloteEditionState.Completed
        }
    }

    private fun getContent(): BeloteEditionState.Content =
        BeloteEditionState.Content(
            useCase.getPlayers().map { Player(it) },
            editedLap.taker,
            getInfo(editedLap),
            getResults(editedLap),
            editedLap.bonuses.map { it.player to it.bonus },
            getAvailableBonuses(editedLap),
            canStepPointsByOne(editedLap),
            canStepPointsByTen(editedLap)
        )

    private fun getInfo(lap: BeloteLapData): StringFactory {
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

    private fun canStepPointsByOne(lap: BeloteLapData): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun canStepPointsByTen(lap: BeloteLapData): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun getResults(lap: BeloteLapData): Pair<String, String> =
        lap.points.toString() to lap.counterPoints().toString()

    private fun getAvailableBonuses(lap: BeloteLapData): List<BeloteBonus> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        return listOfNotNull(
            BeloteBonus.BELOTE.takeUnless { currentBonuses.contains(BeloteBonus.BELOTE) },
            BeloteBonus.RUN_3,
            BeloteBonus.RUN_5,
            BeloteBonus.FOUR_NORMAL,
            BeloteBonus.FOUR_NINE.takeUnless { currentBonuses.contains(BeloteBonus.FOUR_NINE) },
            BeloteBonus.FOUR_JACK.takeUnless { currentBonuses.contains(BeloteBonus.FOUR_JACK) }
        )
    }
}

sealed class BeloteEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val lapInfo: StringFactory,
        val results: Pair<String, String>,
        val selectedBonuses: List<Pair<PlayerPosition, BeloteBonus>>,
        val availableBonuses: List<BeloteBonus>,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : BeloteEditionState()

    object Completed : BeloteEditionState()
}
