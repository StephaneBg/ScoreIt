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

import com.sbgapps.scoreit.data.model.BeloteBonus
import com.sbgapps.scoreit.data.model.BeloteBonusData
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BeloteBonusCache(
    val player: PlayerPosition,
    val bonus: BeloteBonus
) {

    constructor(bonus: BeloteBonusData) : this(
        bonus.player,
        bonus.bonus
    )

    fun toData(): BeloteBonusData = BeloteBonusData(player, bonus)
}
