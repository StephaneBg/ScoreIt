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

package com.sbgapps.scoreit.cache.db

import androidx.room.*
import com.sbgapps.scoreit.cache.model.UniversalLapData

@Dao
interface UniversalLapDao {

    @Query("SELECT * FROM laps WHERE gameId = :gameId")
    fun getLaps(gameId: Long): List<UniversalLapData>

    @Query("DELETE FROM laps WHERE gameId = :gameId")
    fun clearLaps(gameId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveLap(lap: UniversalLapData): Long

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateLap(lap: UniversalLapData)

    @Query("DELETE FROM laps WHERE id = :id AND gameId = :gameId")
    fun deleteLap(gameId: Long, id: Long)
}