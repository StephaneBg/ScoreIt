/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model.coinche

import com.sbgapps.scoreit.core.model.Player
import com.sbgapps.scoreit.core.model.belote.BaseBeloteLap
import java.util.*

class CoincheLap(taker: Int = Player.PLAYER_1,
                 points: Int = 100,
                 var bidder: Int = Player.PLAYER_1,
                 var bid: Int = 140,
                 var coinche: Int = COINCHE_NONE,
                 var bonuses: ArrayList<CoincheBonus> = ArrayList<CoincheBonus>()) :
        BaseBeloteLap(taker, points) {

    override fun computeScores() {
        computePoints()

        // Add bonuses
        bonuses.forEach { bonus ->
            val player = bonus.player
            when (bonus.bonus) {
                CoincheBonus.BONUS_BELOTE, CoincheBonus.BONUS_RUN_3 -> scores[player] += 20
                CoincheBonus.BONUS_RUN_4 -> scores[player] += 50
                CoincheBonus.BONUS_RUN_5, CoincheBonus.BONUS_FOUR_NORMAL -> scores[player] += 100
                CoincheBonus.BONUS_FOUR_NINE -> scores[player] += 150
                CoincheBonus.BONUS_FOUR_JACK -> scores[player] += 200
            }
        }

        var bidderPts: Int
        var counterPts: Int
        if (Player.PLAYER_1 == bidder) {
            bidderPts = scores[Player.PLAYER_1]
            counterPts = scores[Player.PLAYER_2]
        } else {
            bidderPts = scores[Player.PLAYER_2]
            counterPts = scores[Player.PLAYER_1]
        }

        isDone = bidderPts >= bid && bidderPts > counterPts
        if (isDone) {
            // Deal succeeded
            bidderPts += bid
            when (coinche) {
                COINCHE_COINCHE -> bidderPts *= 2
                COINCHE_SURCOINCHE -> bidderPts *= 4
            }
        } else {
            // Deal failed
            bidderPts = 0
            counterPts = if (250 == bid) 500 else 160 + bid
            when (coinche) {
                COINCHE_COINCHE -> counterPts *= 2
                COINCHE_SURCOINCHE -> counterPts *= 4
            }
        }

        if (Player.PLAYER_1 == bidder) {
            scores[Player.PLAYER_1] = bidderPts
            scores[Player.PLAYER_2] = counterPts
        } else {
            scores[Player.PLAYER_1] = counterPts
            scores[Player.PLAYER_2] = bidderPts
        }
    }

    override fun computePoints() {
        val scores = IntArray(2)
        if (points >= 158) {
            scores[0] = points
            scores[1] = 0
        } else {
            scores[0] = points
            scores[1] = 162 - points
        }

        if (Player.PLAYER_1 == scorer) {
            this.scores[Player.PLAYER_1] = scores[0]
            this.scores[Player.PLAYER_2] = scores[1]
        } else {
            this.scores[Player.PLAYER_1] = scores[1]
            this.scores[Player.PLAYER_2] = scores[0]
        }
    }

    fun hasBonus(bonus: Int): Boolean = bonuses.any { bonus == it.bonus }

    companion object {

        const val COINCHE_NONE = 0
        const val COINCHE_COINCHE = 1
        const val COINCHE_SURCOINCHE = 2
    }
}
