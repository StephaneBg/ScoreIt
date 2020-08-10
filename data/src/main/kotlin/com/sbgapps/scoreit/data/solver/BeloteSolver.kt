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

import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusValue
import com.sbgapps.scoreit.data.model.BeloteLap
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore

class BeloteSolver(private val dataStore: DataStore) {

    fun getResults(lap: BeloteLap): Pair<List<Int>, Boolean> {
        val (results, isWon) = computeResults(lap)
        if (!isWon) {
            results[lap.taker.index] = 0
            results[lap.counter().index] = POINTS_TOTAL
            addBonuses(results, lap.bonuses, lap.counter())
        }
        return results.toList() to isWon
    }

    fun getDisplayResults(lap: BeloteLap): Pair<List<String>, Boolean> {
        val (results, isWon) = getResults(lap)
        return results.toList().mapIndexed { index, points ->
            listOfNotNull(
                getPointsForDisplay(points).toString(),
                "♛".takeIf { lap.bonuses.find { it.bonus == BeloteBonusValue.BELOTE && it.player.index == index } != null },
                "★".takeIf { lap.bonuses.find { it.bonus != BeloteBonusValue.BELOTE && it.player.index == index } != null }
            ).joinToString(" ")
        } to isWon
    }

    private fun computeResults(lap: BeloteLap): Pair<IntArray, Boolean> {
        val takerIndex = lap.taker.index
        val counterIndex = lap.counter().index
        val results = IntArray(2)
        if (POINTS_TOTAL == lap.points) {
            results[takerIndex] = POINTS_CAPOT
            results[counterIndex] = 0
        } else {
            results[takerIndex] = lap.points
            results[counterIndex] = lap.counterPoints()
        }
        addBonuses(results, lap.bonuses)
        val isWon = results[takerIndex] >= results[counterIndex]
        return results to isWon
    }

    private fun addBonuses(
        results: IntArray,
        bonuses: List<BeloteBonus>,
        counter: PlayerPosition = PlayerPosition.NONE
    ) {
        if (counter != PlayerPosition.NONE) {
            for ((player, bonus) in bonuses) {
                if (bonus == BeloteBonusValue.BELOTE) results[player.index] += bonus.points
                else results[counter.index] += bonus.points
            }
        } else {
            for ((player, bonus) in bonuses) results[player.index] += bonus.points
        }
    }

    fun computeScores(laps: List<BeloteLap>): List<Int> {
        val scores = MutableList(2) { 0 }
        laps.forEachIndexed { index, lap ->
            val (points, _) = getResults(lap)
            if (isLitigation(points)) {
                val counter = lap.counter()
                scores[counter.index] += points[counter.index]
            } else {
                for (player in 0 until 2) scores[player] += points[player]
            }
            if (index > 0) {
                val previousLap = laps[index - 1]
                val (previousPoints, _) = getResults(previousLap)
                if (isLitigation(previousPoints)) {
                    val winner =
                        if (points[PlayerPosition.ONE.index] > points[PlayerPosition.TWO.index]) PlayerPosition.ONE.index
                        else PlayerPosition.TWO.index
                    scores[winner] += previousPoints[winner]
                }
            }
        }
        return scores.map { getPointsForDisplay(it) }
    }

    fun isLitigation(points: List<Int>): Boolean =
        (81 == points[PlayerPosition.ONE.index] && 81 == points[PlayerPosition.TWO.index]) ||
                (91 == points[PlayerPosition.ONE.index] && 91 == points[PlayerPosition.TWO.index])

    private fun roundPoint(score: Int): Int = when (score) {
        POINTS_TOTAL -> 160
        POINTS_CAPOT -> 250
        else -> (score + 5) / 10 * 10
    }

    private fun getPointsForDisplay(points: Int): Int =
        if (dataStore.isBeloteScoreRounded()) roundPoint(points) else points

    companion object {
        const val POINTS_TOTAL = 162
        const val POINTS_CAPOT = 252
    }
}

fun BeloteLap.counterPoints(): Int = when (points) {
    0 -> POINTS_CAPOT
    POINTS_CAPOT -> 0
    else -> POINTS_TOTAL - points
}

fun BeloteLap.counter(): PlayerPosition = taker.counter()

fun PlayerPosition.counter(): PlayerPosition =
    if (this == PlayerPosition.ONE) PlayerPosition.TWO else PlayerPosition.ONE