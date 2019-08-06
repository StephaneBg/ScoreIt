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

import com.sbgapps.scoreit.app.model.BeloteBonus
import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.model.toBeloteBonus
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteLapData
import com.sbgapps.scoreit.data.model.PLAYER_1
import com.sbgapps.scoreit.data.solver.BeloteSolver
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.counterPoints
import io.uniflow.core.flow.UIState

class BeloteEditionViewModel(private val useCase: GameUseCase, private val solver: BeloteSolver) : BaseViewModel() {

    init {
        setState { getContent() }
    }

    private val editedLap
        get() = useCase.getEditedLap() as BeloteLapData

    fun editMode(pointMode: PointMode) {
        setState {
            useCase.updateEdition(editedLap.copy(points = pointMode.points))
            getContent()
        }
    }

    fun incrementScore(points: Int) {
        setState {
            val lap = editedLap
            useCase.updateEdition(
                lap.copy(
                    points = if (PLAYER_1 == lap.scorer) lap.points + points
                    else lap.points - points
                )
            )
            getContent()
        }
    }

    fun changeScorer(scorer: Int) {
        setState {
            useCase.updateEdition(editedLap.copy(scorer = scorer))
            getContent()
        }
    }

    fun addBonus(bonus: Pair<Int, BeloteBonus>) {
        setState {
            val lap = editedLap
            val bonuses = lap.bonuses.toMutableList()
            bonuses += bonus.first to bonus.second.ordinal
            useCase.updateEdition(lap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun removeBonus(bonusIndex: Int) {
        setState {
            val lap = editedLap
            val bonuses = lap.bonuses.toMutableList()
            bonuses.removeAt(bonusIndex)
            useCase.updateEdition(lap.copy(bonuses = bonuses))
            getContent()
        }
    }

    fun completeEdition() {
        setState {
            useCase.completeEdition()
            BeloteEditionState.Completed
        }
    }

    private fun getContent(): BeloteEditionState.Content {
        val lap = editedLap
        return BeloteEditionState.Content(
            useCase.getPlayers().map { Player(it) },
            lap.scorer,
            getPointMode(lap),
            getTeamPoints(),
            lap.bonuses.map { it.first to it.second.toBeloteBonus() },
            solver.getAvailableBonuses(lap).map { it.toBeloteBonus() },
            canIncrement(lap),
            canDecrement(lap)
        )
    }

    private fun getPointMode(lap: BeloteLapData): PointMode =
        if (solver.isCapot(lap)) PointMode.Capot else PointMode.Score(lap.points)

    private fun canIncrement(lap: BeloteLapData) = StepScore(lap.points <= 150, lap.points < 160)

    private fun canDecrement(lap: BeloteLapData) = StepScore(lap.points >= 12, lap.points > 2)

    private fun getTeamPoints(): Pair<String, String> {
        val lap = editedLap
        return if (PLAYER_1 == lap.scorer) {
            lap.points.toString() to lap.counterPoints().toString()
        } else {
            lap.counterPoints().toString() to lap.points.toString()
        }
    }
}

sealed class BeloteEditionState : UIState() {
    data class Content(
        val players: List<Player>,
        val scorer: Int,
        val pointMode: PointMode,
        val teamPoints: Pair<String, String>,
        val selectedBonuses: List<Pair<Int, BeloteBonus>>,
        val availableBonuses: List<BeloteBonus>,
        val canIncrement: StepScore,
        val canDecrement: StepScore
    ) : BeloteEditionState()

    object Completed : BeloteEditionState()
}

sealed class PointMode(val points: Int) {
    class Score(points: Int) : PointMode(points)
    object Capot : PointMode(POINTS_CAPOT)
}

data class StepScore(
    val canStepTen: Boolean,
    val canStepOne: Boolean
)
