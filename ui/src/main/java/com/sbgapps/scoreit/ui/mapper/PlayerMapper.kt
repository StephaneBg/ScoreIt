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

package com.sbgapps.scoreit.ui.mapper

import com.sbgapps.scoreit.domain.model.PlayerEntity
import com.sbgapps.scoreit.ui.model.Player


class PlayerMapper {

    fun mapFromDomain(entity: PlayerEntity, score: Int): Player {
        return Player(entity.id, entity.name, entity.color, score)
    }

    fun mapToDomain(player: Player) = PlayerEntity(player.id, player.name, player.color)
}