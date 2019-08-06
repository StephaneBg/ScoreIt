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
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore

class BeloteSolver(private val dataStore: DataStore) {

    fun computeResults(lap: BeloteLapData): Pair<List<Int>, Boolean> {
        val results = IntArray(2)
        results[lap.scorer] = lap.points
        results[lap.counter()] = lap.counterPoints()

        addBonuses(results, lap.bonuses)

        val isWon = results[lap.scorer] >= results[lap.counter()]

        if (!isWon) {
            results[lap.scorer] = 0
            results[lap.counter()] = POINTS_TOTAL
            addBonuses(results, lap.bonuses)
        }

        return results.toList() to isWon
    }

    private fun addBonuses(results: IntArray, bonuses: List<Pair<Int, Int>>) {
        for ((player, bonus) in bonuses) {
            when (bonus) {
                BONUS_BELOTE, BONUS_RUN_3 -> results[player] += 20
                BONUS_RUN_4 -> results[player] += 50
                BONUS_RUN_5, BONUS_FOUR_NORMAL -> results[player] += 100
                BONUS_FOUR_NINE -> results[player] += 150
                BONUS_FOUR_JACK -> results[player] += 200
            }
        }
    }

    fun computeScores(laps: List<BeloteLapData>): List<Int> {
        val scores = MutableList(2) { 0 }
        laps.forEachIndexed { index, lap ->
            val points = computeResults(lap).first
            if (isLitigation(points)) {
                val counter = lap.counter()
                scores[counter] += points[counter]
            } else {
                for (player in 0 until 2) scores[player] += points[player]
            }
            if (index > 0) {
                val previousLap = laps[index - 1]
                val previousPoints = computeResults(previousLap).first
                if (isLitigation(previousPoints)) {
                    val winner = if (points[PLAYER_1] > points[PLAYER_2]) PLAYER_1 else PLAYER_2
                    scores[winner] += previousPoints[winner]
                }
            }
        }
        return scores.map { getPointsForDisplay(it) }
    }

    private fun isLitigation(points: List<Int>): Boolean =
        (81 == points[PLAYER_1] && 81 == points[PLAYER_2]) || (91 == points[PLAYER_1] && 91 == points[PLAYER_2])

    private fun isScoreRounded(): Boolean = dataStore.isBeloteScoreRounded()

    fun getLapPoints(lap: BeloteLapData): Pair<Int, Int> =
        if (PLAYER_1 == lap.scorer) {
            lap.points to lap.counterPoints()
        } else {
            lap.counterPoints() to lap.points
        }

    fun isCapot(lap: BeloteLapData): Boolean = lap.points == POINTS_CAPOT

    private fun roundPoint(score: Int): Int = when (score) {
        POINTS_TOTAL -> 160
        POINTS_CAPOT -> 250
        else -> (score + 5) / 10 * 10
    }

    fun getPointsForDisplay(points: Int): Int = if (isScoreRounded()) roundPoint(points) else points

    fun getAvailableBonuses(lap: BeloteLapData): List<Int> {
        val currentBonuses = lap.bonuses.map { it.second }
        return listOfNotNull(
            BONUS_BELOTE.takeUnless { currentBonuses.contains(BONUS_BELOTE) },
            BONUS_RUN_3,
            BONUS_RUN_5,
            BONUS_FOUR_NORMAL,
            BONUS_FOUR_NINE.takeUnless { currentBonuses.contains(BONUS_FOUR_NINE) },
            BONUS_FOUR_JACK.takeUnless { currentBonuses.contains(BONUS_FOUR_JACK) }
        )
    }

    companion object {
        const val POINTS_TOTAL = 162
        const val POINTS_CAPOT = 252
    }
}

fun BeloteLapData.counterPoints(): Int = if (points == POINTS_CAPOT) 0 else POINTS_TOTAL - points

fun BeloteLapData.counter(): Int = if (PLAYER_1 == scorer) PLAYER_2 else PLAYER_1
