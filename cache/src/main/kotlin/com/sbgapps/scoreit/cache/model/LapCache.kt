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

import com.sbgapps.scoreit.data.model.BeloteLapData
import com.sbgapps.scoreit.data.model.CoincheBid
import com.sbgapps.scoreit.data.model.CoincheLapData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.data.model.TarotBid
import com.sbgapps.scoreit.data.model.TarotLapData
import com.sbgapps.scoreit.data.model.TarotOudler
import com.sbgapps.scoreit.data.model.UniversalLapData
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UniversalLapCache(
    val points: List<Int>
) {

    constructor(lap: UniversalLapData) : this(lap.points)

    fun toData(): UniversalLapData = UniversalLapData(points)
}

@JsonClass(generateAdapter = true)
data class TarotLapCache(
    val taker: PlayerPosition = PlayerPosition.ONE,
    val partner: PlayerPosition = PlayerPosition.TWO,
    val bid: TarotBid = TarotBid.SMALL,
    val points: Int = 56,
    val oudlers: List<TarotOudler> = emptyList(),
    val bonuses: List<TarotBonusCache> = emptyList()
) {

    constructor(lap: TarotLapData) : this(
        lap.taker,
        lap.partner,
        lap.bid,
        lap.points,
        lap.oudlers,
        lap.bonuses.map { TarotBonusCache(it) }
    )

    fun toData(playerCount: Int): TarotLapData = TarotLapData(
        playerCount,
        taker,
        partner,
        bid,
        oudlers,
        points,
        bonuses.map { it.toData() }
    )
}

@JsonClass(generateAdapter = true)
data class BeloteLapCache(
    val scorer: PlayerPosition,
    val points: Int,
    val bonuses: List<BeloteBonusCache>
) {

    constructor(lap: BeloteLapData) : this(
        lap.scorer,
        lap.points,
        lap.bonuses.map { BeloteBonusCache(it) }
    )

    fun toData(): BeloteLapData = BeloteLapData(
        scorer,
        points,
        bonuses.map { it.toData() }
    )
}

@JsonClass(generateAdapter = true)
data class CoincheLapCache(
    val scorer: PlayerPosition,
    val bidder: PlayerPosition,
    val bidPoints: Int,
    val coincheBid: CoincheBid,
    val points: Int,
    val bonuses: List<BeloteBonusCache>
) {

    constructor(lap: CoincheLapData) : this(
        lap.scorer,
        lap.bidder,
        lap.bidPoints,
        lap.coincheBid,
        lap.points,
        lap.bonuses.map { BeloteBonusCache(it) }
    )

    fun toData(): CoincheLapData = CoincheLapData(
        scorer,
        bidder,
        bidPoints,
        coincheBid,
        points,
        bonuses.map { it.toData() }
    )
}
