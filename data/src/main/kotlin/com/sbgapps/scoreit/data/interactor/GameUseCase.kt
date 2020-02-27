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

package com.sbgapps.scoreit.data.interactor

import androidx.annotation.ColorInt
import com.sbgapps.scoreit.core.ext.asListOfType
import com.sbgapps.scoreit.core.ext.asMutableListOfType
import com.sbgapps.scoreit.core.ext.replace
import com.sbgapps.scoreit.data.model.BeloteGame
import com.sbgapps.scoreit.data.model.BeloteLap
import com.sbgapps.scoreit.data.model.CoincheGame
import com.sbgapps.scoreit.data.model.CoincheLap
import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.Lap
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.SavedGameInfo
import com.sbgapps.scoreit.data.model.TarotGame
import com.sbgapps.scoreit.data.model.TarotLap
import com.sbgapps.scoreit.data.model.UniversalGame
import com.sbgapps.scoreit.data.model.UniversalLap
import com.sbgapps.scoreit.data.solver.BeloteSolver
import com.sbgapps.scoreit.data.solver.CoincheSolver
import com.sbgapps.scoreit.data.solver.TarotSolver
import com.sbgapps.scoreit.data.solver.UniversalSolver
import com.sbgapps.scoreit.data.source.DataStore

class GameUseCase(
    private val dataStore: DataStore,
    private val universalSolver: UniversalSolver,
    private val tarotSolver: TarotSolver,
    private val beloteSolver: BeloteSolver,
    private val coincheSolver: CoincheSolver
) {

    private var editionState: EditionState? = null

    fun setGameType(gameType: GameType) {
        editionState = null
        dataStore.setCurrentGame(gameType)
    }

    fun getGame(): Game = dataStore.getGame()

    fun getPlayers(withTotal: Boolean = false): List<Player> {
        val game = getGame()
        return if (withTotal && game is UniversalGame && dataStore.isUniversalTotalDisplayed()) {
            game.players.toMutableList().apply { add(dataStore.totalPlayer) }
        } else {
            game.players
        }
    }

    fun getResults(lap: Lap): List<Int> = when (lap) {
        is UniversalLap -> universalSolver.getResults(lap, dataStore.isUniversalTotalDisplayed())
        is TarotLap -> tarotSolver.getResults(lap)
        is BeloteLap -> beloteSolver.getResults(lap).first
        is CoincheLap -> coincheSolver.getResults(lap).first
    }

    fun getDisplayResults(lap: Lap): Pair<List<String>, Boolean> = when (lap) {
        is UniversalLap -> error("Not needed for this game")
        is TarotLap -> tarotSolver.getDisplayResults(lap)
        is BeloteLap -> beloteSolver.getDisplayResults(lap)
        is CoincheLap -> coincheSolver.getDisplayResults(lap)
    }

    fun getScores(): List<Int> = when (val game = getGame()) {
        is UniversalGame -> universalSolver.computeScores(
            game.laps,
            getPlayers().size,
            dataStore.isUniversalTotalDisplayed()
        )
        is BeloteGame -> beloteSolver.computeScores(game.laps)
        is CoincheGame -> coincheSolver.computeScores(game.laps)
        is TarotGame -> tarotSolver.computeScores(game.laps, getPlayers().size)
    }

    fun getLapCount(): Int = getGame().laps.size

    fun isGameStarted(): Boolean = getGame().laps.isNotEmpty()

    fun setPlayerCount(count: Int) {
        editionState = null
        dataStore.setPlayerCount(count)
    }

    fun editPlayerName(position: Int, name: String) {
        val player = getPlayers()[position]
        editPlayer(position, name, player.color)
    }

    fun editPlayerColor(position: Int, @ColorInt color: Int) {
        val player = getPlayers()[position]
        editPlayer(position, player.name, color)
    }

    private fun editPlayer(position: Int, name: String, @ColorInt color: Int) {
        val players = getPlayers().replace(position, Player(name, color))
        val editedGame = when (val game = getGame()) {
            is UniversalGame -> UniversalGame(players, game.laps)
            is TarotGame -> TarotGame(players, game.laps)
            is BeloteGame -> BeloteGame(players, game.laps)
            is CoincheGame -> CoincheGame(players, game.laps)
        }
        dataStore.saveGame(editedGame)
    }

    fun getEditedLap(): Lap = when (val state = editionState) {
        null -> {
            val lap = when (val game = getGame()) {
                is UniversalGame -> UniversalLap(game.players.size)
                is TarotGame -> TarotLap(game.players.size)
                is BeloteGame -> BeloteLap()
                is CoincheGame -> CoincheLap()
            }
            editionState = EditionState.Creation(lap)
            lap
        }
        is EditionState.Creation -> state.createdLap
        is EditionState.Modification -> state.modifiedLap
    }

    fun updateEdition(lap: Lap) {
        when (val state = editionState) {
            is EditionState.Creation -> editionState = EditionState.Creation(lap)
            is EditionState.Modification -> editionState = EditionState.Modification(state.initialLap, lap)
        }
    }

    fun completeEdition() {
        val game = when (val state = editionState) {
            is EditionState.Creation -> {
                when (val game = getGame()) {
                    is UniversalGame -> UniversalGame(game.players, actualAddLap(game, state))
                    is TarotGame -> TarotGame(game.players, actualAddLap(game, state))
                    is BeloteGame -> BeloteGame(game.players, actualAddLap(game, state))
                    is CoincheGame -> CoincheGame(game.players, actualAddLap(game, state))
                }
            }
            is EditionState.Modification -> {
                when (val game = getGame()) {
                    is UniversalGame -> UniversalGame(game.players, actualEditLap(game, state))
                    is TarotGame -> TarotGame(game.players, actualEditLap(game, state))
                    is BeloteGame -> BeloteGame(game.players, actualEditLap(game, state))
                    is CoincheGame -> CoincheGame(game.players, actualEditLap(game, state))
                }
            }
            else -> error("Unknown state")
        }
        dataStore.saveGame(game)
        editionState = null
    }

    fun cancelEdition() {
        if (editionState is EditionState.Modification) editionState = null
    }

    private inline fun <reified T> actualAddLap(game: Game, state: EditionState.Creation): List<T> {
        val laps = game.laps.asMutableListOfType<T>()
        laps += state.createdLap as T
        return laps
    }

    private inline fun <reified T> actualEditLap(game: Game, state: EditionState.Modification): List<T> {
        val laps = game.laps.asListOfType<T>()
        val index = laps.indexOf(state.initialLap as T)
        return laps.replace(index, state.modifiedLap as T)
    }

    fun reset() {
        val newGame = when (val game = getGame()) {
            is UniversalGame -> UniversalGame(game.players, emptyList())
            is TarotGame -> TarotGame(game.players, emptyList())
            is BeloteGame -> BeloteGame(game.players, emptyList())
            is CoincheGame -> CoincheGame(game.players, emptyList())
        }
        editionState = null
        dataStore.saveGame(newGame)
    }

    fun createGame(name: String) {
        editionState = null
        dataStore.createGame(name)
    }

    fun modifyLap(position: Int) {
        val lap = getGame().laps[position]
        editionState = EditionState.Modification(lap, lap)
    }

    fun deleteLap(position: Int) {
        val game = when (val game = getGame()) {
            is UniversalGame -> UniversalGame(game.players, actualDeleteLap(game, position))
            is TarotGame -> TarotGame(game.players, actualDeleteLap(game, position))
            is BeloteGame -> BeloteGame(game.players, actualDeleteLap(game, position))
            is CoincheGame -> CoincheGame(game.players, actualDeleteLap(game, position))
        }
        dataStore.saveGame(game)
    }

    private inline fun <reified T> actualDeleteLap(game: Game, position: Int): List<T> {
        val laps = game.laps.asMutableListOfType<T>()
        laps.removeAt(position)
        return laps
    }

    fun canEditPlayer(position: Int): Boolean = when (getGame()) {
        is UniversalGame -> if (dataStore.isUniversalTotalDisplayed()) position != getPlayers().size else true
        else -> true
    }

    fun getSavedFiles(): List<SavedGameInfo> = dataStore.getSavedFiles()

    fun loadGame(name: String) {
        dataStore.loadGame(name)
    }

    fun removeGame(fileName: String) {
        dataStore.removeGame(fileName)
    }

    fun renameGame(oldName: String, newName: String) {
        dataStore.renameGame(oldName, newName)
    }

    fun getMarkers(): List<Boolean> {
        val players = getPlayers(false)
        val modulo = getLapCount() % players.size
        return players.mapIndexed { index, _ ->
            if (getGame() is UniversalGame || getGame() is TarotGame) index == modulo else false
        }
    }
}

sealed class EditionState {
    data class Creation(val createdLap: Lap) : EditionState()
    data class Modification(val initialLap: Lap, val modifiedLap: Lap) : EditionState()
}
