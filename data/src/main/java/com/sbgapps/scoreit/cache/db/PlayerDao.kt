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

import android.arch.persistence.room.*
import com.sbgapps.scoreit.cache.model.PlayerData

@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE gameId = :gameId")
    fun getPlayers(gameId: Long): List<PlayerData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePlayer(player: PlayerData)

    @Update
    fun updatePlayer(player: PlayerData)
}