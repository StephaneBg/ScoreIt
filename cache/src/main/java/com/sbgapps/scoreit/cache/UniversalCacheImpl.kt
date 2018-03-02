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

import com.sbgapps.scoreit.cache.db.PlayerDao
import com.sbgapps.scoreit.cache.db.UniversalGameDao
import com.sbgapps.scoreit.cache.db.UniversalLapDao
import com.sbgapps.scoreit.cache.db.initializer.DatabaseInitializer
import com.sbgapps.scoreit.cache.mapper.PlayerCacheMapper
import com.sbgapps.scoreit.cache.mapper.UniversalLapCacheMapper
import com.sbgapps.scoreit.data.model.PlayerData
import com.sbgapps.scoreit.data.model.UniversalLapData
import com.sbgapps.scoreit.data.repository.UniversalCache
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class UniversalCacheImpl(val gameDao: UniversalGameDao,
                         val playerDao: PlayerDao,
                         val lapDao: UniversalLapDao,
                         val playerMapper: PlayerCacheMapper,
                         val lapMapper: UniversalLapCacheMapper,
                         val dbInit: DatabaseInitializer)
    : UniversalCache {

    override fun getGameId(name: String): Single<Long?> {
        return gameDao.getGame(name).map { it.id ?: dbInit.createGame() }
    }

    override fun deleteGame(gameId: Long): Completable {
        return Completable.defer {
            gameDao.deleteGame(gameId)
            Completable.complete()
        }
    }

    override fun getPlayers(gameId: Long): Flowable<List<PlayerData>> {
        return playerDao.getPlayers(gameId).map { it.map { playerMapper.mapFromCache(it) } }
    }

    override fun savePlayer(gameId: Long, player: PlayerData): Completable {
        return Completable.defer {
            playerDao.insertPlayer(playerMapper.mapToCache(player, gameId))
            Completable.complete()
        }
    }

    override fun getLaps(gameId: Long): Flowable<List<UniversalLapData>> {
        return lapDao.getLaps(gameId).map { it.map { lapMapper.mapFromCache(it) } }
    }

    override fun saveLap(gameId: Long, lap: UniversalLapData): Completable {
        return Completable.defer {
            lapDao.insertLap(lapMapper.mapToCache(lap, gameId))
            Completable.complete()
        }
    }

    override fun deleteLap(gameId: Long, lap: UniversalLapData): Completable {
        return Completable.defer {
            lapDao.deleteLap(lapMapper.mapToCache(lap, gameId))
            Completable.complete()
        }
    }

    override fun clearLaps(gameId: Long): Completable {
        return Completable.defer {
            lapDao.clearLaps(gameId)
            Completable.complete()
        }
    }
}