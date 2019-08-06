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

import com.sbgapps.scoreit.app.model.*
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.TarotLapData
import com.sbgapps.scoreit.data.solver.TarotSolver
import io.uniflow.core.flow.UIState

class TarotEditionViewModel(private val useCase: GameUseCase, private val solver: TarotSolver) : BaseViewModel() {

    init {
        setState { getContent() }
    }

    fun setTaker(taker: Int) {
        setState {
            useCase.updateEdition(getEditedLap().copy(taker = taker))
            getContent()
        }
    }

    fun setPartner(partner: Int) {
        setState {
            useCase.updateEdition(getEditedLap().copy(partner = partner))
            getContent()
        }
    }

    fun setBid(bid: TarotBid) {
        setState {
            useCase.updateEdition(getEditedLap().copy(bid = bid.ordinal))
            getContent()
        }
    }

    fun setOudlers(oudlers: Int) {
        setState {
            useCase.updateEdition(getEditedLap().copy(oudlers = oudlers))
            getContent()
        }
    }

    fun incrementScore(points: Int) {
        setState {
            val lap = getEditedLap()
            useCase.updateEdition(lap.copy(points = lap.points + points))
            getContent()
        }
    }

    fun addBonus(bonus: Pair<Int, TarotBonus> /* Player to Bonus */) {
        setState {
            val lap = getEditedLap()
            val bonuses = lap.bonuses.toMutableList()
            bonuses += bonus.first to bonus.second.ordinal
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

    private fun getContent(): TarotEditionState.Content {
        val lap = getEditedLap()
        return TarotEditionState.Content(
            useCase.getPlayers().map { Player(it) },
            lap.taker,
            lap.partner,
            lap.bid.toTarotBid(),
            lap.oudlers,
            lap.points,
            lap.bonuses.map { it.first to it.second.toTarotBonus()  /* Player to Bonus */ },
            solver.getAvailableBonuses(lap).map { it.toTarotBonus() },
            canIncrement(lap),
            canDecrement(lap)
        )
    }

    private fun getEditedLap(): TarotLapData = useCase.getEditedLap() as TarotLapData

    fun canIncrement(lap: TarotLapData): StepScore = StepScore(
        lap.points <= 82,
        lap.points <= 91
    )

    fun canDecrement(lap: TarotLapData): StepScore = StepScore(
        lap.points > 10,
        lap.points > 1
    )
}

sealed class TarotEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val taker: Int,
        val partner: Int,
        val bid: TarotBid,
        val oudlers: Int,
        val points: Int,
        val selectedBonuses: List<Pair<Int, TarotBonus>>,
        val availableBonuses: List<TarotBonus>,
        val canIncrement: StepScore,
        val canDecrement: StepScore
    ) : TarotEditionState()

    object Completed : TarotEditionState()
}

data class StepScore(
    val canStepTen: Boolean,
    val canStepOne: Boolean
)
