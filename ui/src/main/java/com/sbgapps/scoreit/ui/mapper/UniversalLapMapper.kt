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

import com.sbgapps.scoreit.domain.model.UniversalLapEntity
import com.sbgapps.scoreit.ui.model.UniversalLap


class UniversalLapMapper {

    fun mapFromDomain(entity: UniversalLapEntity) = UniversalLap(entity.id, entity.points)

    fun mapToDomain(lap: UniversalLap) = UniversalLapEntity(lap.id, lap.points)
}