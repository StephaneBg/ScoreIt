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

package com.sbgapps.scoreit.data.model

import com.squareup.moshi.JsonClass

sealed class Lap

@JsonClass(generateAdapter = true)
data class UniversalLap(
    val points: List<Int>
) : Lap() {
    constructor(playerCount: Int) : this(List(playerCount) { 0 })
}

@JsonClass(generateAdapter = true)
data class TarotLap(
    val playerCount: Int,
    val taker: PlayerPosition = PlayerPosition.ONE,
    val partner: PlayerPosition = if (5 == playerCount) PlayerPosition.TWO else PlayerPosition.NONE,
    val bid: TarotBidValue = TarotBidValue.SMALL,
    val oudlers: List<TarotOudlerValue> = emptyList(),
    val points: Int = 56,
    val bonuses: List<TarotBonus> = emptyList()
) : Lap()

@JsonClass(generateAdapter = true)
data class BeloteLap(
    val taker: PlayerPosition = PlayerPosition.ONE,
    val points: Int = 90,
    val bonuses: List<BeloteBonus> = emptyList()
) : Lap()

@JsonClass(generateAdapter = true)
data class CoincheLap(
    val taker: PlayerPosition = PlayerPosition.ONE,
    val bid: Int = 110,
    val coinche: CoincheValue = CoincheValue.NONE,
    val points: Int = 110,
    val bonuses: List<BeloteBonus> = emptyList()
) : Lap()
