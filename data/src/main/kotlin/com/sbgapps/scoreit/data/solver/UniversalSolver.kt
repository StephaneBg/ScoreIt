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

package com.sbgapps.scoreit.data.solver

import android.graphics.Color
import com.sbgapps.scoreit.data.model.PlayerData
import com.sbgapps.scoreit.data.model.UniversalGameData
import com.sbgapps.scoreit.data.model.UniversalLapData
import com.sbgapps.scoreit.data.source.DataStore

class UniversalSolver(private val dataStore: DataStore) {

    fun computeResults(lap: UniversalLapData): Pair<List<Int>, Boolean> =
        if (dataStore.isUniversalTotalDisplayed()) {
            val results = lap.points.toMutableList()
            results += results.sum()
            results to true
        } else {
            lap.points to true
        }


    fun computeScores(laps: List<UniversalLapData>, playerCount: Int): List<Int> {
        val scores = MutableList(playerCount) { 0 }
        laps.map { it.points }.forEach { points ->
            for (player in 0 until playerCount) scores[player] += points[player]
        }
        return scores
    }

    fun getPlayers(game: UniversalGameData, withTotal: Boolean): List<PlayerData> {
        val players = game.players.toMutableList()
        return if (dataStore.isUniversalTotalDisplayed() && withTotal) {
            players += PlayerData("Total", Color.RED)
            players
        } else {
            players
        }
    }
}
