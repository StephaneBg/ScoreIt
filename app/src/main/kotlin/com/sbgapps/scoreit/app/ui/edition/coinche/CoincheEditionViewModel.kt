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

import com.sbgapps.scoreit.app.model.*
import com.sbgapps.scoreit.app.ui.edition.belote.PointMode
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.CoincheLapData
import com.sbgapps.scoreit.data.model.PLAYER_1
import com.sbgapps.scoreit.data.model.PLAYER_2
import com.sbgapps.scoreit.data.solver.CoincheSolver
import io.uniflow.core.flow.UIState

class CoincheEditionViewModel(private val useCase: GameUseCase, private val solver: CoincheSolver) : BaseViewModel() {

    init {
        setState { getContent() }
    }

    fun editMode(pointMode: PointMode) {
        setState {
            useCase.updateEdition(
                getEditedLap().copy(points = 12)
            )
            getContent()
        }
    }

    fun incrementBid(increment: Int) {
        setState {
            val lap = getEditedLap()
            useCase.updateEdition(
                getEditedLap().copy(
                    bidPoints = if (PLAYER_1 == lap.bidder) lap.bidPoints + increment
                    else lap.bidPoints - increment
                )
            )
            getContent()
        }
    }

    fun incrementPoints(increment: Int) {
        setState {
            val lap = getEditedLap()
            useCase.updateEdition(
                getEditedLap().copy(
                    points = if (PLAYER_1 == lap.scorer) lap.points + increment
                    else lap.points - increment
                )
            )
            getContent()
        }
    }

    fun switchScorer() {
        setState {
            val lap = getEditedLap()
            useCase.updateEdition(
                getEditedLap().copy(scorer = if (PLAYER_1 == lap.scorer) PLAYER_2 else PLAYER_1)
            )
            getContent()
        }
    }

    fun addBonus(bonus: Pair<Int, BeloteBonus>) {
        setState {
            val lap = getEditedLap()
            val bonuses = lap.bonuses.toMutableList()
            bonuses += bonus.first to bonus.second.toData()
            useCase.updateEdition(lap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun removeBonus(bonusIndex: Int) {
        setState {
            val lap = getEditedLap()
            val bonuses = lap.bonuses.toMutableList()
            bonuses.removeAt(bonusIndex)
            useCase.updateEdition(lap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun completeEdition() {
        setState {
            useCase.completeEdition()
            CoincheEditionState.Completed
        }
    }

    private fun getContent(): CoincheEditionState.Content {
        val lap = getEditedLap()
        return CoincheEditionState.Content(
            useCase.getPlayers().map { Player(it) },
            lap.scorer,
            lap.bidder,
            lap.bidPoints,
            lap.points,
            lap.coincheBid.toCoincheBid(),
            lap.bonuses.map { it.first to it.second.toBeloteBonus() },
            solver.getAvailableBonuses(lap).map { it.toBeloteBonus() },
            solver.canIncrement(lap),
            solver.canDecrement(lap)
        )
    }

    private fun getEditedLap(): CoincheLapData = useCase.getEditedLap() as CoincheLapData
}

sealed class CoincheEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val scorer: Int,
        val bidder: Int,
        val bidPoints: Int,
        val points: Int,
        val coincheBid: CoincheBid,
        val selectedBonuses: List<Pair<Int, BeloteBonus>>,
        val availableBonuses: List<BeloteBonus>,
        val canIncrement: Pair<Boolean, Boolean>,
        val canDecrement: Pair<Boolean, Boolean>
    ) : CoincheEditionState()

    object Completed : CoincheEditionState()
}
