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

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import com.sbgapps.scoreit.cache.model.PlayerEntity
import com.sbgapps.scoreit.cache.model.UniversalGameEntity
import com.sbgapps.scoreit.cache.model.UniversalLapEntity

@Database(
        entities = [UniversalGameEntity::class, PlayerEntity::class, UniversalLapEntity::class],
        version = 1)
@TypeConverters(LapConverter::class)
abstract class UniversalDatabase : RoomDatabase() {

    abstract fun gameDao(): UniversalGameDao
    abstract fun playerDao() : PlayerDao
    abstract fun lapDao(): UniversalLapDao
}