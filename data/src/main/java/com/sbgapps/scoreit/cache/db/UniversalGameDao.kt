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

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sbgapps.scoreit.cache.model.UniversalGameEntity

@Dao
interface UniversalGameDao {

    @Query("SELECT * FROM games")
    fun getAllGames(): List<UniversalGameEntity>

    @Query("SELECT * FROM games WHERE id = :id")
    fun getGame(id: Long): UniversalGameEntity

    @Query("SELECT * FROM games WHERE name = :name")
    fun getGame(name: String): UniversalGameEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertGame(game: UniversalGameEntity): Long

    @Query("DELETE FROM games WHERE id = :id")
    fun deleteGame(id: Long)
}