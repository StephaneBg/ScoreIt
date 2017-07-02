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

package com.sbgapps.scoreit.core.model.belote

import com.sbgapps.scoreit.core.model.Player

class BeloteLap(scorer: Int = Player.PLAYER_1,
                points: Int = 81,
                var bonuses: ArrayList<BeloteBonus> = ArrayList<BeloteBonus>()) :
        BaseBeloteLap(scorer, points) {

    override fun computeScores() {
        computePoints()
        isDone = 162 != points

        bonuses.forEach { bonus ->
            when (bonus.bonus) {
                BeloteBonus.BONUS_BELOTE -> scores[bonus.player] += 20
                BeloteBonus.BONUS_RUN_3 -> scores[bonus.player] += 20
                BeloteBonus.BONUS_RUN_4 -> scores[bonus.player] += 50
                BeloteBonus.BONUS_RUN_5, BeloteBonus.BONUS_FOUR_NORMAL -> scores[bonus.player] += 100
                BeloteBonus.BONUS_FOUR_NINE -> scores[bonus.player] += 150
                BeloteBonus.BONUS_FOUR_JACK -> scores[bonus.player] += 200
            }
        }
    }

    override fun computePoints() {
        val scores = IntArray(2)
        if (162 == points) {
            scores[0] = 162
            scores[1] = 0
        } else if (250 == points) {
            scores[0] = 250
            scores[1] = 0
        } else {
            scores[0] = points
            scores[1] = 162 - points
        }

        if (Player.PLAYER_1 === scorer) {
            this.scores[Player.PLAYER_1] = scores[0]
            this.scores[Player.PLAYER_2] = scores[1]
        } else {
            this.scores[Player.PLAYER_1] = scores[1]
            this.scores[Player.PLAYER_2] = scores[0]
        }
    }

    fun hasBonus(bonus: Int): Boolean = bonuses.any { bonus == it.bonus }
}
