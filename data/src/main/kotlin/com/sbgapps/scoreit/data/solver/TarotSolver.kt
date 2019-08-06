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
import kotlin.math.abs

class TarotSolver {

    fun computeResults(lap: TarotLapData): Pair<List<Int>, Boolean> {
        val results = IntArray(lap.playerCount)
        val points = getPoints(lap)

        when (lap.playerCount) {
            3 -> {
                results[PLAYER_1] = if (PLAYER_1 == lap.taker) 2 * points else -points
                results[PLAYER_2] = if (PLAYER_2 == lap.taker) 2 * points else -points
                results[PLAYER_3] = if (PLAYER_3 == lap.taker) 2 * points else -points
            }

            4 -> {
                results[PLAYER_1] = if (PLAYER_1 == lap.taker) 3 * points else -points
                results[PLAYER_2] = if (PLAYER_2 == lap.taker) 3 * points else -points
                results[PLAYER_3] = if (PLAYER_3 == lap.taker) 3 * points else -points
                results[PLAYER_4] = if (PLAYER_4 == lap.taker) 3 * points else -points
            }

            5 -> {
                if (lap.taker != lap.partner) {
                    results[PLAYER_1] = when (PLAYER_1) {
                        lap.taker -> 2 * points
                        lap.partner -> points
                        else -> -points
                    }
                    results[PLAYER_2] = when (PLAYER_2) {
                        lap.taker -> 2 * points
                        lap.partner -> points
                        else -> -points
                    }
                    results[PLAYER_3] = when (PLAYER_3) {
                        lap.taker -> 2 * points
                        lap.partner -> points
                        else -> -points
                    }
                    results[PLAYER_4] = when (PLAYER_4) {
                        lap.taker -> 2 * points
                        lap.partner -> points
                        else -> -points
                    }
                    results[PLAYER_5] = when (PLAYER_5) {
                        lap.taker -> 2 * points
                        lap.partner -> points
                        else -> -points
                    }
                } else {
                    results[PLAYER_1] = if (PLAYER_1 == lap.taker) 4 * points else -points
                    results[PLAYER_2] = if (PLAYER_2 == lap.taker) 4 * points else -points
                    results[PLAYER_3] = if (PLAYER_3 == lap.taker) 4 * points else -points
                    results[PLAYER_4] = if (PLAYER_4 == lap.taker) 4 * points else -points
                    results[PLAYER_5] = if (PLAYER_5 == lap.taker) 4 * points else -points
                }
            }
        }

        return results.toList() to (points >= 0)
    }

    private fun getPoints(lap: TarotLapData): Int {
        var points: Int = when (lap.oudlers) {
            // Only one
            OUDLER_21_MSK, OUDLER_PETIT_MSK, OUDLER_EXCUSE_MSK -> lap.points - 51
            // Two
            OUDLER_21_MSK or OUDLER_PETIT_MSK,
            OUDLER_21_MSK or OUDLER_EXCUSE_MSK,
            OUDLER_EXCUSE_MSK or OUDLER_PETIT_MSK -> lap.points - 41
            // All
            OUDLER_21_MSK or OUDLER_PETIT_MSK or OUDLER_EXCUSE_MSK -> lap.points - 36
            // None
            else -> lap.points - 56
        }

        // Contrat
        val isWon = points >= 0
        points = (25 + abs(points)) * getCoefficient(lap)

        // Poignée
        points += getPoigneeBonus(lap)
        points = if (isWon) points else -points

        // Petit au bout
        points += getPetitBonus(lap)

        // Chelem
        points += getChelemBonus(lap)
        return points
    }

    fun computeScores(laps: List<TarotLapData>, playerCount: Int): List<Int> {
        val scores = MutableList(playerCount) { 0 }
        laps.map { computeResults(it).first }.forEach { points ->
            for (player in 0 until playerCount) scores[player] += points[player]
        }
        return scores
    }

    private fun getPetitBonus(lap: TarotLapData): Int {
        for ((player, bonus) in lap.bonuses) {
            if (bonus == BONUS_PETIT_AU_BOUT) {
                when (lap.playerCount) {
                    3, 4 -> return (if (lap.taker == player) 10 else -10) * getCoefficient(lap)
                    5 -> return (if (lap.taker == player || lap.partner == player) 10 else -10) * getCoefficient(lap)
                }
            }
        }
        return 0
    }

    private fun getPoigneeBonus(lap: TarotLapData): Int {
        for ((_, bonus) in lap.bonuses) {
            when (bonus) {
                BONUS_POIGNEE_SIMPLE -> return 20
                BONUS_POIGNEE_DOUBLE -> return 30
                BONUS_POIGNEE_TRIPLE -> return 40
            }
        }
        return 0
    }

    private fun getChelemBonus(lap: TarotLapData): Int {
        for ((_, bonus) in lap.bonuses) {
            when (bonus) {
                BONUS_CHELEM_NON_ANNONCE -> return 200
                BONUS_CHELEM_ANNONCE_REALISE -> return 400
                BONUS_CHELEM_ANNONCE_NON_REALISE -> return -200
            }
        }
        return 0
    }

    private fun getCoefficient(lap: TarotLapData): Int = when (lap.bid) {
        BID_GARDE -> 2
        BID_GARDE_SANS -> 4
        BID_GARDE_CONTRE -> 6
        else -> 1 // Prise
    }

    fun getAvailableBonuses(lap: TarotLapData): List<Int> {
        val currentBonuses = lap.bonuses.map { it.second }
        val bonuses = mutableListOf<Int>()
        if (!currentBonuses.contains(BONUS_PETIT_AU_BOUT)) bonuses.add(BONUS_PETIT_AU_BOUT)
        if (!currentBonuses.contains(BONUS_POIGNEE_SIMPLE)
            && !currentBonuses.contains(BONUS_POIGNEE_DOUBLE)
            && !currentBonuses.contains(BONUS_POIGNEE_TRIPLE)
        ) {
            bonuses.add(BONUS_POIGNEE_SIMPLE)
            bonuses.add(BONUS_POIGNEE_DOUBLE)
            bonuses.add(BONUS_POIGNEE_TRIPLE)
        }
        if (!currentBonuses.contains(BONUS_CHELEM_NON_ANNONCE)
            && !currentBonuses.contains(BONUS_CHELEM_ANNONCE_REALISE)
            && !currentBonuses.contains(BONUS_CHELEM_ANNONCE_NON_REALISE)
        ) {
            bonuses.add(BONUS_CHELEM_NON_ANNONCE)
            bonuses.add(BONUS_CHELEM_ANNONCE_REALISE)
            bonuses.add(BONUS_CHELEM_ANNONCE_NON_REALISE)
        }
        return bonuses
    }
}
