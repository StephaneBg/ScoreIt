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

package com.sbgapps.scoreit.app.ui

import androidx.annotation.ColorInt
import androidx.annotation.IdRes
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.model.BeloteLapRow
import com.sbgapps.scoreit.app.model.CoincheLapRow
import com.sbgapps.scoreit.app.model.LapRow
import com.sbgapps.scoreit.app.model.TarotLapRow
import com.sbgapps.scoreit.app.model.UniversalLapRow
import com.sbgapps.scoreit.app.ui.history.adapter.Header
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.utils.string.StringFactory
import com.sbgapps.scoreit.core.utils.string.fromResources
import com.sbgapps.scoreit.core.utils.string.join
import com.sbgapps.scoreit.core.utils.string.toStringFactory
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.model.BeloteGame
import com.sbgapps.scoreit.data.model.CoincheGame
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.TarotGame
import com.sbgapps.scoreit.data.model.TarotLap
import com.sbgapps.scoreit.data.model.UniversalGame
import io.uniflow.core.flow.UIEvent
import io.uniflow.core.flow.UIState

class GameViewModel(private val useCase: GameUseCase) : BaseViewModel() {

    fun loadGame(name: String? = null) {
        setState {
            name?.let { useCase.loadGame(it) }
            getContent()
        }
    }

    fun selectGame(gameType: GameType) {
        setState {
            useCase.setGameType(gameType)
            getContent()
        }
    }

    fun setPlayerCount(count: Int) {
        setState {
            useCase.setPlayerCount(count)
            getContent()
        }
    }

    fun resetGame() {
        setState {
            useCase.reset()
            getContent()
        }
    }

    fun createGame(name: String) {
        setState {
            useCase.createGame(name)
            getContent()
        }
    }

    fun addLap() {
        setState {
            sendEvent(GameEvent.Edition(getEditionAction()))
        }
    }

    fun editLap(position: Int) {
        setState {
            useCase.modifyLap(position)
            sendEvent(GameEvent.Edition(getEditionAction()))
        }
    }

    fun deleteLap(position: Int) {
        setState {
            val laps = getLaps().toMutableList()
            laps.removeAt(position)
            sendEvent(GameEvent.Deletion(position, laps))
        }
    }

    fun confirmLapDeletion(position: Int) {
        setState {
            useCase.deleteLap(position)
            getContent()
        }
    }

    fun canEditPlayer(position: Int): Boolean = useCase.canEditPlayer(position)

    fun setPlayerName(position: Int, name: String) {
        setState {
            useCase.editPlayerName(position, name)
            getContent()
        }
    }

    fun setPlayerColor(position: Int, @ColorInt color: Int) {
        setState {
            useCase.editPlayerColor(position, color)
            getContent()
        }
    }

    private fun getContent(): Content = Content(getHeader(), getLaps())

    fun getPlayerCountOptions(): List<Int> = when (useCase.getGame()) {
        is UniversalGame -> listOf(2, 3, 4, 5, 6, 7, 8)
        is TarotGame -> listOf(3, 4, 5)
        else -> error("Not managed for other games")
    }

    fun getEnabledMenuItems(): List<Int> {
        val items = when (useCase.getGame()) {
            is UniversalGame -> mutableListOf(R.id.menu_count)
            is BeloteGame, is CoincheGame -> mutableListOf()
            is TarotGame -> mutableListOf(R.id.menu_count)
        }
        if (useCase.isGameStarted()) {
            items.add(R.id.menu_chart)
            items.add(R.id.menu_clear)
        }
        if (useCase.getSavedFiles().size > 1) {
            items.add(R.id.menu_save)
        }
        return items
    }

    private fun getHeader(): Header = useCase.getPlayers(true)
        .zip(useCase.getScores()) { player, score ->
            Player(player.name, player.color) to score
        }

    private fun getLaps(): List<LapRow> = when (val game = useCase.getGame()) {
        is UniversalGame -> game.laps.map {
            UniversalLapRow(useCase.getResults(it))
        }
        is TarotGame -> game.laps.map {
            val (displayResults, isWon) = useCase.getDisplayResults(it)
            TarotLapRow(displayResults, getTarotLapInfo(it), isWon)
        }

        is BeloteGame -> game.laps.map {
            val (displayResults, isWon) = useCase.getDisplayResults(it)
            BeloteLapRow(displayResults, isWon)
        }
        is CoincheGame -> game.laps.map {
            val (displayResults, isWon) = useCase.getDisplayResults(it)
            CoincheLapRow(displayResults, isWon)
        }
    }

    private fun getTarotLapInfo(lap: TarotLap): StringFactory {
        val players = useCase.getPlayers()
        return when (lap.playerCount) {
            3, 4 -> join(
                " • ",
                players[lap.taker.index].name.toStringFactory(),
                fromResources(lap.bid.resId)
            )
            5 -> join(
                " • ",
                (if (lap.taker == lap.partner) players[lap.taker.index].name
                else "${players[lap.taker.index].name} & ${players[lap.partner.index].name}").toStringFactory(),
                fromResources(lap.bid.resId)
            )
            else -> error("Can't play Tarot with another player count")
        }
    }

    @IdRes
    private fun getEditionAction(): Int = when (useCase.getGame()) {
        is UniversalGame -> R.id.action_historyFragment_to_universalEditionActivity
        is TarotGame -> R.id.action_historyFragment_to_tarotEditionActivity
        is BeloteGame -> R.id.action_historyFragment_to_beloteEditionActivity
        is CoincheGame -> R.id.action_historyFragment_to_coincheEditionActivity
    }
}

data class Content(val header: Header, val results: List<LapRow>) : UIState()

sealed class GameEvent : UIEvent() {
    data class Edition(@IdRes val actionId: Int) : GameEvent()
    data class Deletion(val position: Int, val results: List<LapRow>) : GameEvent()
}
