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

import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBid
import com.sbgapps.scoreit.data.model.TarotBonus
import com.sbgapps.scoreit.data.model.TarotBonusData
import com.sbgapps.scoreit.data.model.TarotLapData
import com.sbgapps.scoreit.data.model.TarotOudler
import com.sbgapps.scoreit.data.solver.TarotSolver
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_TOTAL
import io.uniflow.core.flow.UIState

class TarotEditionViewModel(private val useCase: GameUseCase, private val solver: TarotSolver) : BaseViewModel() {

    init {
        setState { getContent() }
    }

    fun setTaker(taker: PlayerPosition) {
        setState {
            useCase.updateEdition(getEditedLap().copy(taker = taker))
            getContent()
        }
    }

    fun setPartner(partner: PlayerPosition) {
        setState {
            useCase.updateEdition(getEditedLap().copy(partner = partner))
            getContent()
        }
    }

    fun setBid(bid: TarotBid) {
        setState {
            useCase.updateEdition(getEditedLap().copy(bid = bid))
            getContent()
        }
    }

    fun setOudlers(oudlers: List<TarotOudler>) {
        setState {
            useCase.updateEdition(getEditedLap().copy(oudlers = oudlers))
            getContent()
        }
    }

    fun incrementScore(increment: Int) {
        setState {
            val lap = getEditedLap()
            useCase.updateEdition(lap.copy(points = lap.points + increment))
            getContent()
        }
    }

    fun addBonus(bonus: Pair<PlayerPosition, TarotBonus> /* Player to Bonus */) {
        setState {
            val lap = getEditedLap()
            val bonuses = lap.bonuses.toMutableList()
            bonuses += TarotBonusData(bonus.first, bonus.second)
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
            TarotEditionState.Completed
        }
    }

    fun cancelEdition() {
        setState {
            useCase.cancelEdition()
            TarotEditionState.Completed
        }
    }

    private fun getContent(): TarotEditionState.Content {
        val lap = getEditedLap()
        return TarotEditionState.Content(
            useCase.getPlayers().map { Player(it) },
            lap.taker,
            lap.partner,
            lap.bid,
            lap.oudlers,
            lap.points,
            lap.bonuses.map { it.player to it.bonus },
            solver.getAvailableBonuses(lap),
            canStepPointsByOne(lap),
            canStepPointsByTen(lap)
        )
    }

    private fun getEditedLap(): TarotLapData = useCase.getEditedLap() as TarotLapData

    private fun canStepPointsByOne(lap: TarotLapData): Step = Step(
        (lap.points < POINTS_TOTAL),
        lap.points > 0
    )

    private fun canStepPointsByTen(lap: TarotLapData): Step = Step(
        lap.points < (POINTS_TOTAL - 10),
        lap.points > 10
    )
}

sealed class TarotEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val partner: PlayerPosition,
        val bid: TarotBid,
        val oudlers: List<TarotOudler>,
        val points: Int,
        val selectedBonuses: List<Pair<PlayerPosition, TarotBonus>>,
        val availableBonuses: List<TarotBonus>,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : TarotEditionState()

    object Completed : TarotEditionState()
}
