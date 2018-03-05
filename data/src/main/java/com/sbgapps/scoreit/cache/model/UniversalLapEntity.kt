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

package com.sbgapps.scoreit.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "laps",
        foreignKeys = [(ForeignKey(entity = UniversalGameEntity::class,
                parentColumns = arrayOf("id"),
                childColumns = arrayOf("gameId"),
                onDelete = ForeignKey.CASCADE))])
data class UniversalLapEntity(
        @PrimaryKey(autoGenerate = true) val id: Long? = null,
        val gameId: Long,
        var points: List<Int>
)