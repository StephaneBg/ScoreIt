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
    private lateinit var players: MutableList<Player>
    private lateinit var scores: MutableList<Int>
    private lateinit var laps: MutableList<UniversalLap>

    fun getPlayers(): MutableList<Player> = players

    fun getScores(): MutableList<Int> {
        computeScores()
        return scores
    }

    fun getLaps(): MutableList<UniversalLap> = laps

    suspend fun addLap(lap: UniversalLap) {
        laps.add(lap)
        async { universalRepo.saveLap(gameId, lap) }
    }

    fun editLap(lap: UniversalLap) {
        TODO()
    }

    suspend fun initGame() {
        val name = prefsHelper.getUniversalGameName()
        name ?: run { prefsHelper.setUniversalGame(PreferencesHelper.DEFAULT_GAME_NAME) }
        gameId = universalRepo.getGameId(name)

        players = universalRepo.getPlayers(gameId).toMutableList()

        laps = universalRepo.getLaps(gameId).toMutableList()
        val isTotalDisplayed = prefsHelper.isTotalDisplayed()
        laps.asSequence().map { it.isTotalDisplayed = isTotalDisplayed }
    }

    private fun computeScores() {
        scores = ArrayList(players.size)
        for (i in 0 until players.size) scores.add(0)

        laps.forEach { lap ->
            lap.points.forEachIndexed { index, points ->
                scores[index] += points
            }
        }
    }
}