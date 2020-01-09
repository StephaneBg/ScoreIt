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
package com.sbgapps.scoreit.cache.model

import com.sbgapps.scoreit.data.model.BeloteGameData
import com.sbgapps.scoreit.data.model.CoincheGameData
import com.sbgapps.scoreit.data.model.GameData
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.TarotGameData
import com.sbgapps.scoreit.data.model.UniversalGameData
import com.squareup.moshi.JsonClass

sealed class GameCache(val type: GameType) {
    abstract fun toData(): GameData
}

@JsonClass(generateAdapter = true)
data class UniversalGameCache(
    val players: List<PlayerCache>,
    val laps: List<UniversalLapCache> = emptyList()
) : GameCache(GameType.UNIVERSAL) {

    constructor(gameData: UniversalGameData) : this(
        gameData.players.map { PlayerCache(it) },
        gameData.laps.map { UniversalLapCache(it) }
    )

    override fun toData(): GameData = UniversalGameData(
        players.map { it.toData() },
        laps.map { it.toData() }
    )
}

@JsonClass(generateAdapter = true)
data class TarotGameCache(
    val players: List<PlayerCache>,
    val laps: List<TarotLapCache> = emptyList()
) : GameCache(GameType.TAROT) {

    constructor(gameData: TarotGameData) : this(
        gameData.players.map { PlayerCache(it) },
        gameData.laps.map { TarotLapCache(it) }
    )

    override fun toData(): GameData = TarotGameData(
        players.map { it.toData() },
        laps.map { it.toData(players.size) }
    )
}

@JsonClass(generateAdapter = true)
data class BeloteGameCache(
    val players: List<PlayerCache>,
    val laps: List<BeloteLapCache> = emptyList()
) : GameCache(GameType.BELOTE) {

    constructor(gameData: BeloteGameData) : this(
        gameData.players.map { PlayerCache(it) },
        gameData.laps.map { BeloteLapCache(it) }
    )

    override fun toData(): GameData = BeloteGameData(
        players.map { it.toData() },
        laps.map { it.toData() }
    )
}

@JsonClass(generateAdapter = true)
data class CoincheGameCache(
    val players: List<PlayerCache>,
    val laps: List<CoincheLapCache> = emptyList()
) : GameCache(GameType.COINCHE) {

    constructor(gameData: CoincheGameData) : this(
        gameData.players.map { PlayerCache(it) },
        gameData.laps.map { CoincheLapCache(it) }
    )

    override fun toData(): GameData = CoincheGameData(
        players.map { it.toData() },
        laps.map { it.toData() }
    )
}
