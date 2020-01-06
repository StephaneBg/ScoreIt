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

import com.sbgapps.scoreit.data.model.*
import com.sbgapps.scoreit.data.source.DataStore

class CoincheSolver(private val dataStore: DataStore) {

    fun computeResults(lap: CoincheLapData): Pair<List<Int>, Boolean> {
        val points = IntArray(2)
        val results = IntArray(2)

        if (lap.points >= 158) {
            points[0] = lap.points
            points[1] = 0
        } else {
            points[0] = lap.points
            points[1] = 162 - lap.points
        }

        if (PLAYER_1 == lap.scorer) {
            results[PLAYER_1] = points[0]
            results[PLAYER_2] = points[1]
        } else {
            results[PLAYER_1] = points[1]
            results[PLAYER_2] = points[0]
        }

        // Add bonuses
        for ((player, bonus) in lap.bonuses) {
            when (bonus) {
                BONUS_BELOTE, BONUS_RUN_3 -> results[player] += 20
                BONUS_RUN_4 -> results[player] += 50
                BONUS_RUN_5, BONUS_FOUR_NORMAL -> results[player] += 100
                BONUS_FOUR_NINE -> results[player] += 150
                BONUS_FOUR_JACK -> results[player] += 200
            }
        }

        var bidderPts: Int
        var counterPts: Int
        if (PLAYER_1 == lap.bidder) {
            bidderPts = results[PLAYER_1]
            counterPts = results[PLAYER_2]
        } else {
            bidderPts = results[PLAYER_2]
            counterPts = results[PLAYER_1]
        }

        val isWon = bidderPts >= lap.bidPoints && bidderPts > counterPts
        if (isWon) {
            // Deal succeeded
            bidderPts += lap.bidPoints
            when (lap.coincheBid) {
                COINCHE_COINCHE -> bidderPts *= 2
                COINCHE_SURCOINCHE -> bidderPts *= 4
            }
        } else {
            // Deal failed
            bidderPts = 0
            counterPts = if (250 == lap.bidPoints) 500 else 160 + lap.bidPoints
            when (lap.coincheBid) {
                COINCHE_COINCHE -> counterPts *= 2
                COINCHE_SURCOINCHE -> counterPts *= 4
            }
        }

        if (PLAYER_1 == lap.bidder) {
            results[PLAYER_1] = bidderPts
            results[PLAYER_2] = counterPts
        } else {
            results[PLAYER_1] = counterPts
            results[PLAYER_2] = bidderPts
        }

        return results.toList() to isWon
    }

    fun computeScores(laps: List<CoincheLapData>): List<Int> {
        val scores = MutableList(2) { 0 }
        laps.map { computeResults(it).first }.forEach { points ->
            for (player in 0 until 2) scores[player] += points[player]
        }
        return scores.map { getPointsForDisplay(it) }
    }

    fun getPointsForDisplay(points: Int): Int = if (dataStore.isCoincheScoreRounded()) roundPoint(points) else points

    fun canIncrement(lap: CoincheLapData): Pair<Boolean, Boolean> /* Bid to Points */ =
        (lap.bidPoints <= 990) to (lap.points < 150)

    fun canDecrement(lap: CoincheLapData): Pair<Boolean, Boolean> /* Bid to Points */ =
        (lap.bidPoints >= 110) to (lap.points >= 20)

    fun getAvailableBonuses(lap: CoincheLapData): List<Int> {
        val currentBonuses = lap.bonuses.map { it.second }
        val bonuses = mutableListOf<Int>()
        if (!currentBonuses.contains(BONUS_BELOTE)) bonuses.add(BONUS_BELOTE)
        bonuses.add(BONUS_RUN_3)
        bonuses.add(BONUS_RUN_4)
        bonuses.add(BONUS_RUN_5)
        bonuses.add(BONUS_FOUR_NORMAL)
        bonuses.add(BONUS_FOUR_NINE)
        bonuses.add(BONUS_FOUR_JACK)
        return bonuses
    }

    private fun roundPoint(score: Int): Int = when (score) {
        162, 160 -> 160
        250 -> score
        else -> (score + 5) / 10 * 10
    }
}