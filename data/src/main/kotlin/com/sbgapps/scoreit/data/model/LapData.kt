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

package com.sbgapps.scoreit.data.model

import com.sbgapps.scoreit.data.solver.TarotSolver.Companion.OUDLER_NONE

sealed class LapData

data class UniversalLapData(
    val points: List<Int>
) : LapData() {
    constructor(playerCount: Int) : this(MutableList(playerCount) { 0 })
}

data class TarotLapData(
    val playerCount: Int,
    val taker: Int = PLAYER_1,
    val partner: Int = if (5 == playerCount) PLAYER_2 else PLAYER_NONE,
    val bid: TarotBidData = TarotBidData.SMALL,
    val oudlers: Int = OUDLER_NONE,
    val points: Int = 56,
    val bonuses: List<Pair<Int, TarotBonusData>> = emptyList()
) : LapData()

data class BeloteLapData(
    val scorer: Int = PLAYER_1,
    val points: Int = 81,
    val bonuses: List<Pair<Int, Int> /* Player to Bonus */> = emptyList()
) : LapData()

data class CoincheLapData(
    val scorer: Int = PLAYER_1,
    val bidder: Int = PLAYER_1,
    val bidPoints: Int = 110,
    val coincheBid: Int = COINCHE_NONE,
    val points: Int = 110,
    val bonuses: List<Pair<Int, Int> /* Player to Bonus */> = emptyList()
) : LapData()
