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

package com.sbgapps.scoreit.core.model.tarot


import com.sbgapps.scoreit.core.model.Lap
import java.util.*

abstract class TarotLap(var taker: Int,
                        var bid: TarotBid,
                        var points: Int,
                        var oudlers: Int,
                        var bonuses: ArrayList<TarotBonus>) : Lap {

    val scores by lazy { IntArray(playerCount) }
    var isDone = true
    abstract var playerCount: Int

    override fun getScore(player: Int, rounded: Boolean): Int = scores[player]

    fun hasBonus(bonus: Int): Boolean = bonuses.any { bonus == it.bonus }

    val result: Int
        get() {
            var points: Int
            when (oudlers) {
                OUDLER_21_MSK, OUDLER_PETIT_MSK, OUDLER_EXCUSE_MSK -> points = this.points - 51
                OUDLER_21_MSK or OUDLER_PETIT_MSK,
                OUDLER_21_MSK or OUDLER_EXCUSE_MSK,
                OUDLER_EXCUSE_MSK or OUDLER_PETIT_MSK -> points = this.points - 41
                OUDLER_21_MSK or OUDLER_PETIT_MSK or OUDLER_EXCUSE_MSK -> points = this.points - 36
                else -> points = this.points - 56
            }
            isDone = points >= 0
            points = (25 + Math.abs(points)) * coefficient
            points += poigneeBonus
            points = if (isDone) points else -points
            points += petitBonus
            points += chelemBonus
            return points
        }

    val coefficient: Int
        get() {
            when (bid.bid) {
                TarotBid.BID_GARDE -> return 2
                TarotBid.BID_GARDE_SANS -> return 4
                TarotBid.BID_GARDE_CONTRE -> return 6
                TarotBid.BID_PRISE -> return 1
                else -> return 1
            }
        }

    val poigneeBonus: Int
        get() {
            var pts = 0
            bonuses.forEach { bonus ->
                when (bonus.bonus) {
                    TarotBonus.BONUS_POIGNEE_SIMPLE -> pts += 20
                    TarotBonus.BONUS_POIGNEE_DOUBLE -> pts += 30
                    TarotBonus.BONUS_POIGNEE_TRIPLE -> pts += 40
                }
            }
            return pts
        }

    open val petitBonus: Int
        get() {
            bonuses.forEach { bonus ->
                if (TarotBonus.BONUS_PETIT_AU_BOUT == bonus.bonus) {
                    var petit = if (taker == bonus.player) 10 else -10
                    petit *= coefficient
                    return petit
                }
            }
            return 0
        }

    val chelemBonus: Int
        get() {
            bonuses.forEach { bonus ->
                when (bonus.bonus) {
                    TarotBonus.BONUS_CHELEM_NON_ANNONCE -> return 200
                    TarotBonus.BONUS_CHELEM_ANNONCE_REALISE -> return 400
                    TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE -> return -200
                }
            }
            return 0
        }

    companion object {

        const val OUDLER_NONE_MSK = 0
        const val OUDLER_PETIT_MSK = 1
        const val OUDLER_EXCUSE_MSK = 2
        const val OUDLER_21_MSK = 4
    }
}
