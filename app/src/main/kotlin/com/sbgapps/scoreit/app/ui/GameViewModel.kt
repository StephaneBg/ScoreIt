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

package com.sbgapps.scoreit.app.ui

import androidx.annotation.ColorInt
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.model.BeloteLapRow
import com.sbgapps.scoreit.app.model.CoincheLapRow
import com.sbgapps.scoreit.app.model.DonationRow
import com.sbgapps.scoreit.app.model.Header
import com.sbgapps.scoreit.app.model.LapRow
import com.sbgapps.scoreit.app.model.TarotLapRow
import com.sbgapps.scoreit.app.model.UniversalLapRow
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
import com.sbgapps.scoreit.data.repository.BillingRepo
import io.uniflow.core.flow.data.UIEvent
import io.uniflow.core.flow.data.UIState

class GameViewModel(
    private val useCase: GameUseCase,
    private val billingRepository: BillingRepo
) : BaseViewModel() {

    private var displayedLaps = emptyList<LapRow>()
    private var removedPosition = NO_DELETION

    fun loadGame(name: String? = null) {
        action {
            name?.let { useCase.loadGame(it) }
            setState { getContent() }
        }
    }

    fun selectGame(gameType: GameType) {
        action {
            useCase.setGameType(gameType)
            setState { getContent() }
        }
    }

    fun setPlayerCount(count: Int) {
        action {
            useCase.setPlayerCount(count)
            setState { getContent() }
        }
    }

    fun resetGame() {
        action {
            useCase.reset()
            setState { getContent() }
        }
    }

    fun createGame(name: String) {
        action {
            useCase.createGame(name)
            setState { getContent() }
        }
    }

    fun addLap() {
        action {
            sendEvent(getEditionAction())
        }
    }

    fun editLap(position: Int) {
        action {
            useCase.modifyLap(position)
            sendEvent(getEditionAction())
        }
    }

    fun deleteLap(position: Int) {
        action {
            if (removedPosition != NO_DELETION) {
                useCase.deleteLap(removedPosition)
                setState { getContent() }
            }
            val laps = getLaps().toMutableList()
            laps.removeAt(position)
            displayedLaps = laps
            removedPosition = position
            sendEvent(GameEvent.Deletion(laps))
        }
    }

    fun confirmDeletion() {
        action {
            useCase.deleteLap(removedPosition)
            removedPosition = NO_DELETION
            setState { getContent() }
        }
    }

    fun undoDeletion() {
        action {
            removedPosition = NO_DELETION
            setState { getContent() }
        }
    }

    fun canEditPlayer(position: Int): Boolean = useCase.canEditPlayer(position)

    fun setPlayerName(position: Int, name: String) {
        action {
            useCase.editPlayerName(position, name)
            setState { getContent() }
        }
    }

    fun setPlayerColor(position: Int, @ColorInt color: Int) {
        action {
            useCase.editPlayerColor(position, color)
            setState { getContent() }
        }
    }

    fun onDonationPerformed() {
        action {
            setState { getContent() }
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
        if (useCase.getSavedFiles().isNotEmpty()) {
            items.add(R.id.menu_save)
        }
        return items
    }

    private fun getHeader(): Header = Header(
        useCase.getPlayers(true).map { Player(it.name, it.color) },
        useCase.getScores(),
        useCase.getMarkers()
    )

    private fun getLaps(): List<LapRow> {
        val laps = when (val game = useCase.getGame()) {
            is UniversalGame -> game.laps.mapIndexed { index, lap ->
                UniversalLapRow(index, useCase.getResults(lap))
            }
            is TarotGame -> game.laps.mapIndexed { index, lap ->
                val (displayResults, isWon) = useCase.getDisplayResults(lap)
                TarotLapRow(index, displayResults, getTarotLapInfo(lap), isWon)
            }

            is BeloteGame -> game.laps.mapIndexed { index, lap ->
                val (displayResults, isWon) = useCase.getDisplayResults(lap)
                BeloteLapRow(index, displayResults, isWon)
            }
            is CoincheGame -> game.laps.mapIndexed { index, lap ->
                val (displayResults, isWon) = useCase.getDisplayResults(lap)
                CoincheLapRow(index, displayResults, isWon)
            }
        }
        displayedLaps = laps

        return if (laps.size > 4) {
            billingRepository.getDonationSkus()?.let {
                laps.toMutableList().apply { add(DonationRow(it)) }
            } ?: laps
        } else {
            laps
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

    private fun getEditionAction(): GameEvent.Edition = GameEvent.Edition(useCase.getGame().type)

    companion object {
        private const val NO_DELETION = -1
    }
}

data class Content(val header: Header, val results: List<LapRow>) : UIState()

sealed class GameEvent : UIEvent() {
    data class Edition(val gameType: GameType) : GameEvent()
    data class Deletion(val results: List<LapRow>) : GameEvent()
}
