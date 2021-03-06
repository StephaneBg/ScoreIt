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

package com.sbgapps.scoreit.app.ui.scoreboard

import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.data.interactor.ScoreBoardUseCase
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.ScoreBoard
import io.uniflow.core.flow.data.UIState

class ScoreBoardViewModel(private val useCase: ScoreBoardUseCase) : BaseViewModel() {

    init {
        action {
            setState { Content(useCase.getScoreBoard()) }
        }
    }

    fun incrementScore(increment: Int, player: PlayerPosition) {
        action {
            val currentInfo = getCurrentStateOrNull(Content::class)!!.scoreBoard
            val newInfo = if (PlayerPosition.ONE == player) {
                currentInfo.copy(scoreOne = currentInfo.scoreOne + increment)
            } else {
                currentInfo.copy(scoreTwo = currentInfo.scoreTwo + increment)
            }
            useCase.saveScoreBoard(newInfo)
            setState { Content(newInfo) }
        }
    }

    fun setPlayerName(name: String, player: PlayerPosition) {
        action {
            val currentInfo = getCurrentStateOrNull(Content::class)!!.scoreBoard
            val newInfo = if (PlayerPosition.ONE == player) {
                currentInfo.copy(nameOne = name)
            } else {
                currentInfo.copy(nameTwo = name)
            }
            useCase.saveScoreBoard(newInfo)
            setState { Content(newInfo) }
        }
    }

    fun reset() {
        action {
            setState { Content(useCase.reset()) }
        }
    }
}

data class Content(val scoreBoard: ScoreBoard) : UIState()
