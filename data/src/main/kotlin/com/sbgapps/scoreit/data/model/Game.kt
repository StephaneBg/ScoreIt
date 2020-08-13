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

sealed class Game(
    val type: GameType,
    open val players: List<Player>,
    open val laps: List<Lap>
)

@JsonClass(generateAdapter = true)
data class UniversalGame(
    override val players: List<Player>,
    override val laps: List<UniversalLap> = emptyList()
) : Game(GameType.UNIVERSAL, players, laps)

@JsonClass(generateAdapter = true)
data class TarotGame(
    override val players: List<Player>,
    override val laps: List<TarotLap> = emptyList()
) : Game(GameType.TAROT, players, laps)

@JsonClass(generateAdapter = true)
data class BeloteGame(
    override val players: List<Player>,
    override val laps: List<BeloteLap> = emptyList()
) : Game(GameType.BELOTE, players, laps)

@JsonClass(generateAdapter = true)
data class CoincheGame(
    override val players: List<Player>,
    override val laps: List<CoincheLap> = emptyList()
) : Game(GameType.COINCHE, players, laps)
