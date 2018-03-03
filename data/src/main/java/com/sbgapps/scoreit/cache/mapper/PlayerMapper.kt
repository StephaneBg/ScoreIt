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
import com.sbgapps.scoreit.domain.model.Player


class PlayerMapper : Mapper<PlayerEntity, Player> {

    override fun mapFromCache(data: PlayerEntity) = Player(data.id, data.name, data.color)

    override fun mapToCache(domain: Player) = throw(IllegalArgumentException())

    fun mapToCache(domain: Player, gameId: Long) = PlayerEntity(domain.id, gameId, domain.name, domain.color)
}