/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.mapper

import com.sbgapps.scoreit.cache.model.PlayerEntity
import com.sbgapps.scoreit.data.model.PlayerData


class PlayerCacheMapper : CacheMapper<PlayerEntity, PlayerData> {

    override fun mapFromCache(cache: PlayerEntity) = PlayerData(cache.id, cache.name, cache.color)

    override fun mapToCache(domain: PlayerData) = throw(IllegalArgumentException())

    fun mapToCache(dataType: PlayerData, gameId: Long) = PlayerEntity(dataType.id, gameId, dataType.name, dataType.color)
}