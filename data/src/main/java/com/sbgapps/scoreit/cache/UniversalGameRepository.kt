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

package com.sbgapps.scoreit.cache

import com.sbgapps.scoreit.cache.db.DatabaseInitializer
import com.sbgapps.scoreit.cache.mapper.PlayerDataMapper
import com.sbgapps.scoreit.cache.mapper.UniversalLapDataMapper
import com.sbgapps.scoreit.domain.model.PlayerEntity
import com.sbgapps.scoreit.domain.model.UniversalLapEntity
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import com.sbgapps.scoreit.domain.repository.GameRepository
import timber.log.Timber

class UniversalGameRepository(private val dbRepo: DatabaseRepo,
                              private val playerMapper: PlayerDataMapper,
                              private val lapMapper: UniversalLapDataMapper,
                              private val dbInit: DatabaseInitializer)
    : GameRepository<UniversalLapEntity> {

    override fun getGameId(name: String): Long {
        return dbRepo.universalDb.gameDao().getGame(name).id
                ?: createGame(name, PreferencesHelper.DEFAULT_UNIVERSAL_PLAYER_COUNT)
    }

    override fun createGame(name: String, playerCount: Int): Long {
        return dbInit.createGame(name, playerCount)
    }

    override fun deleteGame(gameId: Long) {
        dbRepo.universalDb.gameDao().deleteGame(gameId)
    }

    override fun getPlayers(gameId: Long): List<PlayerEntity> {
        return dbRepo.universalDb.playerDao().getPlayers(gameId).map { playerMapper.mapFromCache(it) }
    }

    override fun savePlayer(gameId: Long, playerEntity: PlayerEntity) {
        dbRepo.universalDb.playerDao().insertPlayer(playerMapper.mapToCache(playerEntity, gameId))
    }

    override fun getLaps(gameId: Long): List<UniversalLapEntity> {
        return dbRepo.universalDb.lapDao().getLaps(gameId).map { lapMapper.mapFromCache(it) }
    }

    override fun saveLap(gameId: Long, lap: UniversalLapEntity) {
        val lapEntity = lapMapper.mapToCache(lap, gameId)
        Timber.d("Saving " + lapEntity)
        dbRepo.universalDb.lapDao().insertLap(lapEntity)
    }

    override fun deleteLap(gameId: Long, lap: UniversalLapEntity) {
        lap.id?.let { dbRepo.universalDb.lapDao().deleteLap(gameId, it) }
    }

    override fun clearLaps(gameId: Long) {
        dbRepo.universalDb.lapDao().clearLaps(gameId)
    }
}