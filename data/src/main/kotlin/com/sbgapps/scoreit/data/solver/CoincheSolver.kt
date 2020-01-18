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
import com.sbgapps.scoreit.data.model.CoincheLap
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.CoincheSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore

class CoincheSolver(private val dataStore: DataStore) {

    fun getResults(lap: CoincheLap): Pair<List<Int>, Boolean> {
        val takerIndex = lap.taker.index
        val counterIndex = lap.counter().index
        val (results, isWon) = computeResults(lap)
        if (isWon) {
            results[takerIndex] += lap.bid
            results[takerIndex] *= lap.coinche.coefficient
        } else {
            results[takerIndex] = 0
            results[counterIndex] = POINTS_TOTAL + lap.bid
            results[counterIndex] *= lap.coinche.coefficient
            addBonuses(results, lap.bonuses)
        }
        return results.toList() to isWon
    }

    fun getDisplayResults(lap: CoincheLap): Pair<List<String>, Boolean> {
        val (results, isWon) = getResults(lap)
        return results.toList().mapIndexed { index, points ->
            listOfNotNull(
                getPointsForDisplay(points).toString(),
                "♛".takeIf { lap.bonuses.find { it.bonus == BeloteBonusValue.BELOTE && it.player.index == index } != null },
                "★".takeIf { lap.bonuses.find { it.bonus != BeloteBonusValue.BELOTE && it.player.index == index } != null }
            ).joinToString(" ")
        } to isWon
    }

    private fun computeResults(lap: CoincheLap): Pair<IntArray, Boolean> {
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
        val isWon = results[takerIndex] >= lap.bid && results[takerIndex] > results[counterIndex]
        return results to isWon
    }

    private fun addBonuses(results: IntArray, bonuses: List<BeloteBonus>) {
        for ((player, bonus) in bonuses) results[player.index] += bonus.points
    }

    fun computeScores(laps: List<CoincheLap>): List<Int> {
        val scores = MutableList(2) { 0 }
        laps.map { getResults(it).first }.forEach { points ->
            for (player in 0 until 2) scores[player] += points[player]
        }
        return scores.map { getPointsForDisplay(it) }
    }

    private fun getPointsForDisplay(points: Int): Int =
        if (dataStore.isCoincheScoreRounded()) roundPoint(points) else points

    private fun roundPoint(score: Int): Int = when (score) {
        POINTS_TOTAL -> 160
        POINTS_CAPOT -> 250
        else -> (score + 5) / 10 * 10
    }

    companion object {
        const val POINTS_TOTAL = 162
        const val POINTS_CAPOT = 252
        const val BID_MIN = 80
        const val BID_MAX = 650
    }
}

fun CoincheLap.counterPoints(): Int = if (points == POINTS_CAPOT) 0 else POINTS_TOTAL - points

fun CoincheLap.counter(): PlayerPosition = taker.counter()
