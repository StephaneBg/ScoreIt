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

import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.utils.string.StringFactory
import com.sbgapps.scoreit.core.utils.string.fromResources
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusValue
import com.sbgapps.scoreit.data.model.CoincheLap
import com.sbgapps.scoreit.data.model.CoincheValue
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.CoincheSolver
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.BID_MAX
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.BID_MIN
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.solver.counter
import com.sbgapps.scoreit.data.solver.counterPoints
import io.uniflow.core.flow.data.UIState

class CoincheEditionViewModel(
    private val useCase: GameUseCase,
    private val solver: CoincheSolver
) : BaseViewModel() {

    private val editedLap
        get() = useCase.getEditedLap() as CoincheLap


    fun loadContent() {
        action {
            setState { getContent() }
        }
    }

    fun stepBid(increment: Int) {
        action {
            useCase.updateEdition(
                editedLap.copy(bid = editedLap.bid + increment)
            )
            setState { getContent() }
        }
    }

    fun setCoinche(coinche: CoincheValue) {
        action {
            useCase.updateEdition(editedLap.copy(coinche = coinche))
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
            setState { CoincheEditionState.Completed }
        }
    }

    fun cancelEdition() {
        action {
            useCase.cancelEdition()
            setState { CoincheEditionState.Completed }
        }
    }

    private fun getContent(): CoincheEditionState.Content =
        CoincheEditionState.Content(
            useCase.getPlayers(),
            editedLap.taker,
            getInfo(editedLap),
            editedLap.bid,
            getTeamPoints(editedLap),
            editedLap.coinche,
            editedLap.bonuses.map { it.player to it.bonus },
            getAvailableBonuses(editedLap),
            canStepBid(editedLap),
            canStepPointsByOne(editedLap),
            canStepPointsByTen(editedLap)
        )

    private fun canStepBid(lap: CoincheLap): Step =
        Step((lap.bid < BID_MAX), (lap.bid > BID_MIN))

    private fun canStepPointsByOne(lap: CoincheLap): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun canStepPointsByTen(lap: CoincheLap): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun getAvailableBonuses(lap: CoincheLap): List<BeloteBonusValue> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        val bonuses = mutableListOf<BeloteBonusValue>()
        if (!currentBonuses.contains(BeloteBonusValue.BELOTE)) bonuses.add(BeloteBonusValue.BELOTE)
        bonuses.add(BeloteBonusValue.RUN_3)
        bonuses.add(BeloteBonusValue.RUN_4)
        bonuses.add(BeloteBonusValue.RUN_5)
        bonuses.add(BeloteBonusValue.FOUR_NORMAL)
        bonuses.add(BeloteBonusValue.FOUR_NINE)
        bonuses.add(BeloteBonusValue.FOUR_JACK)
        return bonuses
    }

    private fun getInfo(lap: CoincheLap): StringFactory {
        val (results, isWon) = solver.getResults(lap)
        return if (isWon) {
            fromResources(R.string.coinche_info_win, results[lap.taker.index].toString())
        } else {
            fromResources(R.string.coinche_info_lose, results[lap.counter().index].toString())
        }
    }

    private fun getTeamPoints(lap: CoincheLap): Pair<String, String> =
        lap.points.toString() to lap.counterPoints().toString()
}

sealed class CoincheEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val lapInfo: StringFactory,
        val bidPoints: Int,
        val teamPoints: Pair<String, String>,
        val coinche: CoincheValue,
        val selectedBonuses: List<Pair<PlayerPosition, BeloteBonusValue>>,
        val availableBonuses: List<BeloteBonusValue>,
        val stepBid: Step,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : CoincheEditionState()

    object Completed : CoincheEditionState()
}
