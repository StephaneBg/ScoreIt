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

import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBonus
import com.sbgapps.scoreit.data.model.TarotLapData
import kotlin.math.abs

class TarotSolver {

    fun getResults(lap: TarotLapData): List<Int> {
        val results = IntArray(lap.playerCount)
        val points = getPoints(lap)

        when (lap.playerCount) {
            3 -> for (player in PlayerPosition.ONE.index..PlayerPosition.THREE.index)
                results[player] = (if (player == lap.taker.index) 2 else -1) * points

            4 -> for (player in PlayerPosition.ONE.index..PlayerPosition.FOUR.index)
                results[player] = (if (player == lap.taker.index) 3 else -1) * points

            5 -> {
                if (lap.taker != lap.partner) {
                    for (player in PlayerPosition.ONE.index..PlayerPosition.FIVE.index) {
                        results[player] = when (player) {
                            lap.taker.index -> 2
                            lap.partner.index -> 1
                            else -> -1
                        } * points
                    }
                } else {
                    for (player in PlayerPosition.ONE.index..PlayerPosition.FIVE.index)
                        results[player] = (if (player == lap.taker.index) 4 else -1) * points
                }
            }
        }
        return results.toList()
    }

    fun getDisplayResults(lap: TarotLapData): Pair<List<String>, Boolean> =
        getResults(lap).mapIndexed { index, points ->
            listOfNotNull(
                points.toString(),
                "①".takeIf { lap.bonuses.firstOrNull { it.bonus == TarotBonus.PETIT_AU_BOUT }?.player?.index == index }
            ).joinToString(" ")
        } to (getPoints(lap) >= 0)

    private fun getPoints(lap: TarotLapData): Int {
        var points: Int = lap.points - when (lap.oudlers.size) {
            0 -> POINTS_WITH_NO_OUDLER
            1 -> POINTS_WITH_ONE_OUDLER
            2 -> POINTS_WITH_TWO_OUDLERS
            3 -> POINTS_WITH_THREE_OUDLERS
            else -> error("Not possible")
        }

        // Contrat
        val isWon = points >= 0
        points = (POINTS_CONTRACT + abs(points)) * lap.bid.coefficient

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
        laps.map { getResults(it) }.forEach { points ->
            for (player in 0 until playerCount) scores[player] += points[player]
        }
        return scores
    }

    private fun getPetitBonus(lap: TarotLapData): Int {
        for ((player, bonus) in lap.bonuses) {
            if (bonus == TarotBonus.PETIT_AU_BOUT) {
                when (lap.playerCount) {
                    3, 4 -> return (if (lap.taker == player) 10 else -10) * lap.bid.coefficient
                    5 -> return (if (lap.taker == player || lap.partner == player) 10 else -10) * lap.bid.coefficient
                }
            }
        }
        return 0
    }

    private fun getPoigneeBonus(lap: TarotLapData): Int = lap.bonuses
        .map { it.bonus }
        .firstOrNull {
            it == TarotBonus.POIGNEE_SIMPLE || it == TarotBonus.POIGNEE_DOUBLE || it == TarotBonus.POIGNEE_TRIPLE
        }?.points ?: 0

    private fun getChelemBonus(lap: TarotLapData): Int = lap.bonuses
        .map { it.bonus }
        .firstOrNull {
            it == TarotBonus.CHELEM_NON_ANNONCE || it == TarotBonus.CHELEM_ANNONCE_REALISE || it == TarotBonus.CHELEM_ANNONCE_NON_REALISE
        }?.points ?: 0

    fun getAvailableBonuses(lap: TarotLapData): List<TarotBonus> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        val bonuses = mutableListOf<TarotBonus>()
        if (!currentBonuses.contains(TarotBonus.PETIT_AU_BOUT)) {
            bonuses.add(TarotBonus.PETIT_AU_BOUT)
        }
        if (!currentBonuses.contains(TarotBonus.POIGNEE_SIMPLE)
            && !currentBonuses.contains(TarotBonus.POIGNEE_DOUBLE)
            && !currentBonuses.contains(TarotBonus.POIGNEE_TRIPLE)
        ) {
            bonuses.add(TarotBonus.POIGNEE_SIMPLE)
            bonuses.add(TarotBonus.POIGNEE_DOUBLE)
            bonuses.add(TarotBonus.POIGNEE_TRIPLE)
        }
        if (!currentBonuses.contains(TarotBonus.CHELEM_NON_ANNONCE)
            && !currentBonuses.contains(TarotBonus.CHELEM_ANNONCE_REALISE)
            && !currentBonuses.contains(TarotBonus.CHELEM_ANNONCE_NON_REALISE)
        ) {
            bonuses.add(TarotBonus.CHELEM_NON_ANNONCE)
            bonuses.add(TarotBonus.CHELEM_ANNONCE_REALISE)
            bonuses.add(TarotBonus.CHELEM_ANNONCE_NON_REALISE)
        }
        return bonuses
    }

    companion object {
        const val POINTS_CONTRACT = 25
        const val POINTS_TOTAL = 91
        const val POINTS_WITH_NO_OUDLER = 56
        const val POINTS_WITH_ONE_OUDLER = 51
        const val POINTS_WITH_TWO_OUDLERS = 41
        const val POINTS_WITH_THREE_OUDLERS = 36
    }
}
