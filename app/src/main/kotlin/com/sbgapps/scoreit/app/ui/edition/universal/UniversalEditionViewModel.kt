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

package com.sbgapps.scoreit.app.ui.edition.universal

import com.sbgapps.scoreit.app.model.Player
import com.sbgapps.scoreit.app.model.UniversalLap
import com.sbgapps.scoreit.core.ext.replace
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.UniversalLapData
import io.uniflow.core.flow.UIState

class UniversalEditionViewModel(private val useCase: GameUseCase) : BaseViewModel() {

    init {
        setState {
            UniversalEditionState.Content(
                useCase.getPlayers().map { Player(it) },
                UniversalLap(getEditedLap().points)
            )
        }
    }

    fun increment(position: Int, points: Int) {
        setState {
            val oldPoints = getEditedLap().points
            val newScore = oldPoints[position] + points
            val newPoints = oldPoints.replace(position, newScore)
            useCase.updateEdition(UniversalLapData(newPoints))
            UniversalEditionState.Incremented(position, newScore)
        }
    }

    fun cancelEdition() {
        setState {
            useCase.cancelEdition()
            UniversalEditionState.Completed
        }
    }

    fun completeEdition() {
        setState {
            useCase.completeEdition()
            UniversalEditionState.Completed
        }
    }

    private fun getEditedLap(): UniversalLapData = useCase.getEditedLap() as UniversalLapData
}

sealed class UniversalEditionState : UIState() {
    data class Content(val players: List<Player>, val lap: UniversalLap) : UniversalEditionState()
    data class Incremented(val position: Int, val points: Int) : UniversalEditionState()
    object Completed : UniversalEditionState()
}
