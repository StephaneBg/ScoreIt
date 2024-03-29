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

package com.sbgapps.scoreit.app.ui.edition.universal

import com.sbgapps.scoreit.core.ext.replace
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.ui.Empty
import com.sbgapps.scoreit.core.ui.State
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.UniversalLap

class UniversalEditionViewModel(private val useCase: GameUseCase) : BaseViewModel(Empty) {

    private val editedLap
        get() = useCase.getEditedLap() as UniversalLap

    fun loadContent() {
        action {
            setState(getContent())
        }
    }

    fun incrementScore(increment: Int, position: Int) {
        action {
            val oldPoints = editedLap.points
            val newScore = oldPoints[position] + increment
            val newPoints = oldPoints.replace(position, newScore)
            useCase.updateEdition(UniversalLap(newPoints))
            setState(getContent())
        }
    }

    fun setScore(position: Int, score: Int) {
        action {
            val newPoints = editedLap.points.replace(position, score)
            useCase.updateEdition(UniversalLap(newPoints))
            setState(getContent())
        }
    }

    fun cancelEdition() {
        action {
            useCase.cancelEdition()
            setState(UniversalEditionState.Completed)
        }
    }

    fun completeEdition() {
        action {
            useCase.completeEdition()
            setState(UniversalEditionState.Completed)
        }
    }

    private fun getContent(): UniversalEditionState.Content =
        UniversalEditionState.Content(useCase.getPlayers(), editedLap.points)
}

sealed class UniversalEditionState : State {
    data class Content(val players: List<Player>, val results: List<Int>) : UniversalEditionState()
    data object Completed : UniversalEditionState()
}
