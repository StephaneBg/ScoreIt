/*
 * Copyright 2020 Stéphane Baiget
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

import com.sbgapps.scoreit.app.ui.edition.Step
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.ui.Empty
import com.sbgapps.scoreit.core.ui.State
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBidValue
import com.sbgapps.scoreit.data.model.TarotBonus
import com.sbgapps.scoreit.data.model.TarotBonusValue
import com.sbgapps.scoreit.data.model.TarotLap
import com.sbgapps.scoreit.data.model.TarotOudlerValue
import com.sbgapps.scoreit.data.solver.TarotSolver
import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.POINTS_TOTAL

class TarotEditionViewModel(
    private val useCase: GameUseCase,
    private val solver: TarotSolver
) : BaseViewModel(Empty) {

    private val editedLap
        get() = useCase.getEditedLap() as TarotLap

    fun loadContent() {
        action {
            setState(getContent())
        }
    }

    fun setTaker(taker: PlayerPosition) {
        action {
            useCase.updateEdition(editedLap.copy(taker = taker))
            setState(getContent())
        }
    }

    fun setPartner(partner: PlayerPosition) {
        action {
            useCase.updateEdition(editedLap.copy(partner = partner))
            setState(getContent())
        }
    }

    fun setBid(bid: TarotBidValue) {
        action {
            useCase.updateEdition(editedLap.copy(bid = bid))
            setState(getContent())
        }
    }

    fun setOudlers(oudlers: List<TarotOudlerValue>) {
        action {
            useCase.updateEdition(editedLap.copy(oudlers = oudlers))
            setState(getContent())
        }
    }

    fun incrementScore(increment: Int) {
        action {
            useCase.updateEdition(editedLap.copy(points = editedLap.points + increment))
            setState(getContent())
        }
    }

    fun addBonus(bonus: Pair<PlayerPosition, TarotBonusValue> /* Player to Bonus */) {
        action {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses += TarotBonus(bonus.first, bonus.second)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            setState(getContent())
        }
    }

    fun removeBonus(bonusIndex: Int) {
        action {
            val bonuses = editedLap.bonuses.toMutableList()
            bonuses.removeAt(bonusIndex)
            useCase.updateEdition(editedLap.copy(bonuses = bonuses))
            setState(getContent())
        }
    }

    fun completeEdition() {
        action {
            useCase.completeEdition()
            setState(TarotEditionState.Completed)
        }
    }

    fun cancelEdition() {
        action {
            useCase.cancelEdition()
            setState(TarotEditionState.Completed)
        }
    }

    private fun getContent(): TarotEditionState.Content =
        TarotEditionState.Content(
            useCase.getPlayers(),
            editedLap.taker,
            editedLap.partner,
            editedLap.bid,
            editedLap.oudlers,
            editedLap.points,
            editedLap.bonuses.map { it.player to it.bonus },
            solver.getAvailableBonuses(editedLap),
            canStepPointsByOne(editedLap),
            canStepPointsByTen(editedLap)
        )

    private fun canStepPointsByOne(lap: TarotLap): Step = Step(
        (lap.points < POINTS_TOTAL),
        lap.points > 0
    )

    private fun canStepPointsByTen(lap: TarotLap): Step = Step(
        lap.points < (POINTS_TOTAL - 10),
        lap.points > 10
    )
}

sealed class TarotEditionState : State {
    data class Content(
        val players: List<Player>,
        val taker: PlayerPosition,
        val partner: PlayerPosition,
        val bid: TarotBidValue,
        val oudlers: List<TarotOudlerValue>,
        val points: Int,
        val selectedBonuses: List<Pair<PlayerPosition, TarotBonusValue>>,
        val availableBonuses: List<TarotBonusValue>,
        val stepPointsByOne: Step,
        val stepPointsByTen: Step
    ) : TarotEditionState()

    data object Completed : TarotEditionState()
}
