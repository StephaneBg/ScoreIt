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

import android.graphics.Color
import com.sbgapps.scoreit.core.ext.replace
import com.sbgapps.scoreit.data.model.*
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

    fun setCurrentGame(game: Game) {
        editionState = null
        dataStore.setCurrentGame(game)
    }

    fun getGame(): GameData = dataStore.getGame()

    fun getPlayers(withTotal: Boolean = false): List<PlayerData> = when (val game = getGame()) {
        is UniversalGameData -> game.players.toMutableList().apply {
            if (dataStore.isUniversalTotalDisplayed() && withTotal) add(PlayerData("Total", Color.RED))
        }
        else -> game.players
    }

    fun getLapResults(lap: LapData): Pair<List<Int>, Boolean> = when (lap) {
        is UniversalLapData -> universalSolver.computeResults(lap, dataStore.isUniversalTotalDisplayed())
        is TarotLapData -> tarotSolver.computeResults(lap)
        is BeloteLapData -> beloteSolver.computeResults(lap)
        is CoincheLapData -> coincheSolver.computeResults(lap)
    }

    fun getScores(): List<Int> = when (val game = getGame()) {
        is UniversalGameData -> universalSolver.computeScores(
            game.laps,
            getPlayers().size,
            dataStore.isUniversalTotalDisplayed()
        )
        is BeloteGameData -> beloteSolver.computeScores(game.laps)
        is CoincheGameData -> coincheSolver.computeScores(game.laps)
        is TarotGameData -> tarotSolver.computeScores(game.laps, getPlayers().size)
    }

    fun isGameStarted(): Boolean = getGame().laps.isNotEmpty()

    fun setPlayerCount(count: Int) {
        editionState = null
        dataStore.setPlayerCount(count)
    }

    fun getEditedLap(): LapData = when (val state = editionState) {
        null -> {
            val lap = when (val game = getGame()) {
                is UniversalGameData -> UniversalLapData(game.players.size)
                is TarotGameData -> TarotLapData(game.players.size)
                is BeloteGameData -> BeloteLapData()
                is CoincheGameData -> CoincheLapData()
            }
            editionState = EditionState.Creation(lap)
            lap
        }
        is EditionState.Creation -> state.createdLap
        is EditionState.Modification -> state.modifiedLapData
    }

    fun updateEdition(lap: LapData) {
        when (val state = editionState) {
            is EditionState.Creation -> editionState = EditionState.Creation(lap)
            is EditionState.Modification -> editionState = EditionState.Modification(state.initialLapData, lap)
        }
    }

    fun completeEdition() {
        val game = when (val state = editionState) {
            is EditionState.Creation -> {
                when (val game = getGame()) {
                    is UniversalGameData -> UniversalGameData(game.players, actualAddLap(game, state))
                    is TarotGameData -> TarotGameData(game.players, actualAddLap(game, state))
                    is BeloteGameData -> BeloteGameData(game.players, actualAddLap(game, state))
                    is CoincheGameData -> CoincheGameData(game.players, actualAddLap(game, state))
                }
            }
            is EditionState.Modification -> {
                when (val game = getGame()) {
                    is UniversalGameData -> UniversalGameData(game.players, actualEditLap(game, state))
                    is TarotGameData -> TarotGameData(game.players, actualEditLap(game, state))
                    is BeloteGameData -> BeloteGameData(game.players, actualEditLap(game, state))
                    is CoincheGameData -> CoincheGameData(game.players, actualEditLap(game, state))
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

    private inline fun <reified T> actualAddLap(game: GameData, state: EditionState.Creation): List<T> {
        val laps = game.laps.toMutableList() as MutableList<T>
        laps += state.createdLap as T
        return laps
    }

    private inline fun <reified T> actualEditLap(game: GameData, state: EditionState.Modification): List<T> {
        val laps = (game.laps as List<T>)
        val index = laps.indexOf(state.initialLapData as T)
        return laps.toMutableList().replace(index, state.modifiedLapData as T)
    }

    fun reset() {
        val newGame = when (val game = getGame()) {
            is UniversalGameData -> UniversalGameData(game.players, emptyList())
            is TarotGameData -> TarotGameData(game.players, emptyList())
            is BeloteGameData -> BeloteGameData(game.players, emptyList())
            is CoincheGameData -> CoincheGameData(game.players, emptyList())
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
            is UniversalGameData -> UniversalGameData(game.players, actualDeleteLap(game, position))
            is TarotGameData -> TarotGameData(game.players, actualDeleteLap(game, position))
            is BeloteGameData -> BeloteGameData(game.players, actualDeleteLap(game, position))
            is CoincheGameData -> CoincheGameData(game.players, actualDeleteLap(game, position))
        }
        dataStore.saveGame(game)
    }

    private inline fun <reified T> actualDeleteLap(game: GameData, position: Int): List<T> {
        val laps = game.laps.toMutableList()
        laps.removeAt(position)
        return laps as List<T>
    }
}

sealed class EditionState {
    data class Creation(val createdLap: LapData) : EditionState()
    data class Modification(val initialLapData: LapData, val modifiedLapData: LapData) : EditionState()
}
