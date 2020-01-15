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

sealed class LapData

data class UniversalLapData(
    val points: List<Int>
) : LapData() {
    constructor(playerCount: Int) : this(List(playerCount) { 0 })
}

data class TarotLapData(
    val playerCount: Int,
    val taker: PlayerPosition = PlayerPosition.ONE,
    val partner: PlayerPosition = if (5 == playerCount) PlayerPosition.TWO else PlayerPosition.NONE,
    val bid: TarotBid = TarotBid.SMALL,
    val oudlers: List<TarotOudler> = emptyList(),
    val points: Int = 56,
    val bonuses: List<TarotBonusData> = emptyList()
) : LapData()

data class BeloteLapData(
    val taker: PlayerPosition = PlayerPosition.ONE,
    val points: Int = 90,
    val bonuses: List<BeloteBonusData> = emptyList()
) : LapData()

data class CoincheLapData(
    val taker: PlayerPosition = PlayerPosition.ONE,
    val bid: Int = 110,
    val coinche: Coinche = Coinche.NONE,
    val points: Int = 110,
    val bonuses: List<BeloteBonusData> = emptyList()
) : LapData()
