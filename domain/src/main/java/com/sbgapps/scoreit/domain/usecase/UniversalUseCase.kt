/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.domain.usecase

import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import com.sbgapps.scoreit.domain.repository.GameRepository


class UniversalUseCase(private val universalRepo: GameRepository<UniversalLap>,
                       private val prefsHelper: PreferencesHelper)
    : BaseUseCase() {

    private var gameId: Long = 0
    private lateinit var players: List<Player>
    private lateinit var laps: MutableList<UniversalLap>

    fun getPlayers(): List<Player> = internalGetPlayer(false)

    fun getLaps(): List<UniversalLap> {
        val isWithTotal = prefsHelper.isTotalDisplayed
        laps.map { it.isWithTotal = isWithTotal }
        return laps
    }

    fun getScores(): List<Pair<Player, Int>> {
        val isWithTotal = prefsHelper.isTotalDisplayed
        val count = if (isWithTotal) players.size + 1 else players.size

        val scores = mutableListOf<Int>()
        for (i in 0 until count) scores.add(0)

        getLaps().forEach { lap ->
            lap.getPoints().forEachIndexed { index, points ->
                scores[index] += points
            }
        }

        val players = internalGetPlayer(true)

        return players.zip(scores)
    }

    private fun internalGetPlayer(withTotal: Boolean): List<Player> {
        return if (prefsHelper.isTotalDisplayed && withTotal) {
            val _players = players.toMutableList()
            _players.add(prefsHelper.getTotalPlayer())
            _players
        } else players
    }

    fun createLap(): UniversalLap {
        val points = mutableListOf<Int>()
        for (i in 0 until getPlayers().size) points.add(0)
        val lap = UniversalLap(null, points)
        lap.isWithTotal = prefsHelper.isTotalDisplayed
        return lap
    }

    suspend fun addLap(lap: UniversalLap) {
        laps.add(lap)
        asyncAwait { universalRepo.saveLap(gameId, lap) }
    }

    suspend fun updateLap(lap: UniversalLap) {
        TODO()
    }

    suspend fun clearLaps() {
        asyncAwait {
            universalRepo.clearLaps(gameId)
            laps.clear()
        }
    }

    suspend fun initGame() {
        val name = prefsHelper.getUniversalGameName()
        name?.let {
            gameId = universalRepo.getGameId(name)
            players = universalRepo.getPlayers(gameId)
            laps = universalRepo.getLaps(gameId)
        } ?: run {
            createGame(PreferencesHelper.DEFAULT_GAME_NAME, PreferencesHelper.DEFAULT_UNIVERSAL_PLAYER_COUNT)
        }
    }

    suspend fun createGame(name: String, playerCount: Int) {
        prefsHelper.initUniversalGame(name, playerCount)
        asyncAwait {
            gameId = universalRepo.createGame(name, playerCount)
            players = universalRepo.getPlayers(gameId)
            laps = universalRepo.getLaps(gameId)
        }
    }

    fun toggleShowTotal(): Boolean {
        return if (prefsHelper.isTotalDisplayed) {
            prefsHelper.isTotalDisplayed = false
            false
        } else {
            prefsHelper.isTotalDisplayed = true
            true
        }
    }
}