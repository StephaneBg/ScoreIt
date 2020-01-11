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

import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.ui.edition.belote.PointMode
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.CoincheBid
import com.sbgapps.scoreit.data.model.CoincheLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
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
                    bidPoints = if (PlayerPosition.ONE == lap.bidder) lap.bidPoints + increment
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
                    points = if (PlayerPosition.ONE == lap.scorer) lap.points + increment
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
                getEditedLap().copy(
                    scorer = if (PlayerPosition.ONE == lap.scorer) PlayerPosition.TWO
                    else PlayerPosition.ONE
                )
            )
            getContent()
        }
    }

    fun addBonus(bonus: Pair<PlayerPosition, BeloteBonus>) {
        setState {
            val lap = getEditedLap()
            val bonuses = lap.bonuses.toMutableList()
            bonuses += BeloteBonusData(bonus.first, bonus.second)
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
            lap.coincheBid,
            lap.bonuses.map { it.player to it.bonus },
            solver.getAvailableBonuses(lap),
            solver.canIncrement(lap),
            solver.canDecrement(lap)
        )
    }

    private fun getEditedLap(): CoincheLapData = useCase.getEditedLap() as CoincheLapData
}

sealed class CoincheEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val scorer: PlayerPosition,
        val bidder: PlayerPosition,
        val bidPoints: Int,
        val points: Int,
        val coincheBid: CoincheBid,
        val selectedBonuses: List<Pair<PlayerPosition, BeloteBonus>>,
        val availableBonuses: List<BeloteBonus>,
        val canIncrement: Pair<Boolean, Boolean>,
        val canDecrement: Pair<Boolean, Boolean>
    ) : CoincheEditionState()

    object Completed : CoincheEditionState()
}
