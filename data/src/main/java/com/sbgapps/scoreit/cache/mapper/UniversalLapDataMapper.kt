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

import com.sbgapps.scoreit.cache.model.UniversalLapData
import com.sbgapps.scoreit.domain.model.UniversalLapEntity


class UniversalLapDataMapper {

    fun mapFromCache(data: UniversalLapData) = UniversalLapEntity(data.id!!, data.points)

    fun mapToCache(entity: UniversalLapEntity, gameId: Long) = UniversalLapData(entity.id, gameId, entity.points)
}