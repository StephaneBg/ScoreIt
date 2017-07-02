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


import com.sbgapps.scoreit.core.model.Player
import java.util.*

class TarotThreeLap(taker: Int = Player.PLAYER_1,
                    bid: TarotBid = TarotBid(),
                    points: Int = 41,
                    oudlers: Int = TarotLap.OUDLER_NONE_MSK,
                    bonuses: ArrayList<TarotBonus> = ArrayList<TarotBonus>()) :
        TarotLap(taker, bid, points, oudlers, bonuses) {

    override var playerCount: Int
        get() = TarotThreeGame.NB_PLAYERS
        set(value) {}

    override fun computeScores() {
        val result = result
        scores[Player.PLAYER_1] = if (Player.PLAYER_1 == taker) 2 * result else -result
        scores[Player.PLAYER_2] = if (Player.PLAYER_2 == taker) 2 * result else -result
        scores[Player.PLAYER_3] = if (Player.PLAYER_3 == taker) 2 * result else -result
    }
}
