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
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.BeloteLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_CAPOT
import com.sbgapps.scoreit.data.solver.BeloteSolver.Companion.POINTS_TOTAL
import com.sbgapps.scoreit.data.source.DataStore

class BeloteSolver(private val dataStore: DataStore) {

    fun getResults(lap: BeloteLapData): List<Int> {
        val results = computeResults(lap)
        if (!isWon(results, lap.scorer)) {
            results[lap.scorer.index] = 0
            results[lap.counter().index] = POINTS_TOTAL
            addBonuses(results, lap.bonuses)
        }
        return results.toList()
    }

    fun getDisplayResults(lap: BeloteLapData): Pair<List<String>, Boolean> =
        getResults(lap).mapIndexed { index, points ->
            listOfNotNull(
                getPointsForDisplay(points).toString(),
                "★".takeIf { lap.bonuses.firstOrNull { it.bonus == BeloteBonus.BELOTE }?.player?.index == index }
            ).joinToString(" ")
        } to isWon(computeResults(lap), lap.scorer)

    private fun computeResults(lap: BeloteLapData): IntArray {
        val results = IntArray(2)
        results[lap.scorer.index] = lap.points
        results[lap.counter().index] = lap.counterPoints()
        addBonuses(results, lap.bonuses)
        return results
    }

    private fun isWon(results: IntArray, scorer: PlayerPosition): Boolean =
        results[scorer.index] >= results[scorer.counter().index]

    private fun addBonuses(results: IntArray, bonuses: List<BeloteBonusData>) {
        for ((player, bonus) in bonuses) results[player.index] += bonus.points
    }

    fun computeScores(laps: List<BeloteLapData>): List<Int> {
        val scores = MutableList(2) { 0 }
        laps.forEachIndexed { index, lap ->
            val points = getResults(lap)
            if (isLitigation(points)) {
                val counter = lap.counter()
                scores[counter.index] += points[counter.index]
            } else {
                for (player in 0 until 2) scores[player] += points[player]
            }
            if (index > 0) {
                val previousLap = laps[index - 1]
                val previousPoints = getResults(previousLap)
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

    private fun isLitigation(points: List<Int>): Boolean =
        (81 == points[PlayerPosition.ONE.index] && 81 == points[PlayerPosition.TWO.index]) ||
                (91 == points[PlayerPosition.ONE.index] && 91 == points[PlayerPosition.TWO.index])

    fun isCapot(lap: BeloteLapData): Boolean = lap.points == POINTS_CAPOT

    private fun roundPoint(score: Int): Int = when (score) {
        POINTS_TOTAL -> 160
        POINTS_CAPOT -> 250
        else -> (score + 5) / 10 * 10
    }

    private fun getPointsForDisplay(points: Int): Int =
        if (dataStore.isBeloteScoreRounded()) roundPoint(points) else points

    fun getAvailableBonuses(lap: BeloteLapData): List<BeloteBonus> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        return listOfNotNull(
            BeloteBonus.BELOTE.takeUnless { currentBonuses.contains(BeloteBonus.BELOTE) },
            BeloteBonus.RUN_3,
            BeloteBonus.RUN_5,
            BeloteBonus.FOUR_NORMAL,
            BeloteBonus.FOUR_NINE.takeUnless { currentBonuses.contains(BeloteBonus.FOUR_NINE) },
            BeloteBonus.FOUR_JACK.takeUnless { currentBonuses.contains(BeloteBonus.FOUR_JACK) }
        )
    }

    companion object {
        const val POINTS_TOTAL = 162
        const val POINTS_CAPOT = 252
    }
}

fun BeloteLapData.counterPoints(): Int = if (points == POINTS_CAPOT) 0 else POINTS_TOTAL - points

fun BeloteLapData.counter(): PlayerPosition = scorer.counter()

fun PlayerPosition.counter(): PlayerPosition =
    if (this == PlayerPosition.ONE) PlayerPosition.TWO else PlayerPosition.ONE