/*
 * Copyright 2020 Stéphane Baiget
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
import com.sbgapps.scoreit.data.model.TarotBonusValue
import com.sbgapps.scoreit.data.model.TarotLap
import kotlin.math.abs

class TarotSolver {

    fun getResults(lap: TarotLap): List<Int> {
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

    fun getDisplayResults(lap: TarotLap): Pair<List<String>, Boolean> =
        getResults(lap).mapIndexed { index, points ->
            listOfNotNull(
                points.toString(),
                "①".takeIf { lap.bonuses.firstOrNull { it.bonus == TarotBonusValue.PETIT_AU_BOUT }?.player?.index == index }
            ).joinToString(" ")
        } to (getPoints(lap) >= 0)

    private fun getPoints(lap: TarotLap): Int {
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

    fun computeScores(laps: List<TarotLap>, playerCount: Int): List<Int> {
        val scores = MutableList(playerCount) { 0 }
        laps.map { getResults(it) }.forEach { points ->
            for (player in 0 until playerCount) scores[player] += points[player]
        }
        return scores
    }

    private fun getPetitBonus(lap: TarotLap): Int {
        for ((player, bonus) in lap.bonuses) {
            if (bonus == TarotBonusValue.PETIT_AU_BOUT) {
                when (lap.playerCount) {
                    3, 4 -> return (if (lap.taker == player) 10 else -10) * lap.bid.coefficient
                    5 -> return (if (lap.taker == player || lap.partner == player) 10 else -10) * lap.bid.coefficient
                }
            }
        }
        return 0
    }

    private fun getPoigneeBonus(lap: TarotLap): Int = lap.bonuses
        .map { it.bonus }
        .firstOrNull {
            it == TarotBonusValue.POIGNEE_SIMPLE || it == TarotBonusValue.POIGNEE_DOUBLE || it == TarotBonusValue.POIGNEE_TRIPLE
        }?.points ?: 0

    private fun getChelemBonus(lap: TarotLap): Int = lap.bonuses
        .map { it.bonus }
        .firstOrNull {
            it == TarotBonusValue.CHELEM_NON_ANNONCE || it == TarotBonusValue.CHELEM_ANNONCE_REALISE || it == TarotBonusValue.CHELEM_ANNONCE_NON_REALISE
        }?.points ?: 0

    fun getAvailableBonuses(lap: TarotLap): List<TarotBonusValue> {
        val currentBonuses = lap.bonuses.map { it.bonus }
        val bonuses = mutableListOf<TarotBonusValue>()
        if (!currentBonuses.contains(TarotBonusValue.PETIT_AU_BOUT)) {
            bonuses.add(TarotBonusValue.PETIT_AU_BOUT)
        }
        if (!currentBonuses.contains(TarotBonusValue.POIGNEE_SIMPLE)
            && !currentBonuses.contains(TarotBonusValue.POIGNEE_DOUBLE)
            && !currentBonuses.contains(TarotBonusValue.POIGNEE_TRIPLE)
        ) {
            bonuses.add(TarotBonusValue.POIGNEE_SIMPLE)
            bonuses.add(TarotBonusValue.POIGNEE_DOUBLE)
            bonuses.add(TarotBonusValue.POIGNEE_TRIPLE)
        }
        if (!currentBonuses.contains(TarotBonusValue.CHELEM_NON_ANNONCE)
            && !currentBonuses.contains(TarotBonusValue.CHELEM_ANNONCE_REALISE)
            && !currentBonuses.contains(TarotBonusValue.CHELEM_ANNONCE_NON_REALISE)
        ) {
            bonuses.add(TarotBonusValue.CHELEM_NON_ANNONCE)
            bonuses.add(TarotBonusValue.CHELEM_ANNONCE_REALISE)
            bonuses.add(TarotBonusValue.CHELEM_ANNONCE_NON_REALISE)
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
