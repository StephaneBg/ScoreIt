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
import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.utils.string.StringFactory
import com.sbgapps.scoreit.core.utils.string.fromResources
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.Coinche
import com.sbgapps.scoreit.data.model.CoincheLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.CoincheSolver
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.BID_MAX
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.BID_MIN
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.solver.counter
import com.sbgapps.scoreit.data.solver.counterPoints
import io.uniflow.core.flow.UIState

class CoincheEditionViewModel(
    private val useCase: GameUseCase,
    private val solver: CoincheSolver
) : BaseViewModel() {

    private val editedLap
        get() = useCase.getEditedLap() as CoincheLapData


    fun loadContent() {
        setState { getContent() }
    }

    fun stepBid(increment: Int) {
        setState {
            useCase.updateEdition(
                editedLap.copy(bid = editedLap.bid + increment)
            )
            getContent()
        }
    }

    fun setCoinche(coinche: Coinche) {
        setState {
            useCase.updateEdition(editedLap.copy(coinche = coinche))
            getContent()
        }
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
            CoincheEditionState.Completed
        }
    }

    fun cancelEdition() {
        setState {
            useCase.cancelEdition()
            CoincheEditionState.Completed
        }
    }

    private fun getContent(): CoincheEditionState.Content =
        CoincheEditionState.Content(
            useCase.getPlayers().map { Player(it) },
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

    private fun canStepBid(lap: CoincheLapData): Step =
        Step((lap.bid < BID_MAX), (lap.bid > BID_MIN))

    private fun canStepPointsByOne(lap: CoincheLapData): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun canStepPointsByTen(lap: CoincheLapData): Step =
        Step((lap.points < POINTS_TOTAL), (lap.points > 2))

    private fun getAvailableBonuses(lap: CoincheLapData): List<BeloteBonus> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        val bonuses = mutableListOf<BeloteBonus>()
        if (!currentBonuses.contains(BeloteBonus.BELOTE)) bonuses.add(BeloteBonus.BELOTE)
        bonuses.add(BeloteBonus.RUN_3)
        bonuses.add(BeloteBonus.RUN_4)
        bonuses.add(BeloteBonus.RUN_5)
        bonuses.add(BeloteBonus.FOUR_NORMAL)
        bonuses.add(BeloteBonus.FOUR_NINE)
        bonuses.add(BeloteBonus.FOUR_JACK)
        return bonuses
    }

    private fun getInfo(lap: CoincheLapData): StringFactory {
        val (results, isWon) = solver.getResults(lap)
        return if (isWon) {
            fromResources(R.string.coinche_info_win, results[lap.taker.index].toString())
        } else {
            fromResources(R.string.coinche_info_lose, results[lap.counter().index].toString())
        }
    }

    private fun getTeamPoints(lap: CoincheLapData): Pair<String, String> =
        if (PlayerPosition.ONE == lap.taker) {
            lap.points.toString() to lap.counterPoints().toString()
        } else {
            lap.counterPoints().toString() to lap.points.toString()
        }
}

sealed class CoincheEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val lapInfo: StringFactory,
        val bidPoints: Int,
        val teamPoints: Pair<String, String>,
        val coinche: Coinche,
        val selectedBonuses: List<Pair<PlayerPosition, BeloteBonus>>,
        val availableBonuses: List<BeloteBonus>,
        val stepBid: Step,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : CoincheEditionState()

    object Completed : CoincheEditionState()
}
