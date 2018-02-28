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
import com.sbgapps.scoreit.cache.model.PlayerEntity
import com.sbgapps.scoreit.cache.model.UniversalLapEntity
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.UniversalLap
import com.sbgapps.scoreit.data.repository.UniversalCache
import io.reactivex.Completable
import io.reactivex.Flowable


class UniversalCacheImpl(val gameDao: UniversalGameDao,
                         val playerDao: PlayerDao,
                         val lapDao: UniversalLapDao
) : UniversalCache {

    override fun clearGame(gameId: Long): Completable {
        return Completable.defer {
            gameDao.deleteGame(gameId)
            Completable.complete()
        }
    }

    override fun getPlayers(gameId: Long): Flowable<List<Player>> {
        return playerDao.getPlayers(gameId).map { it.map { Player(it.id, it.name, it.color) } }
    }

    override fun savePlayer(gameId: Long, player: Player): Completable {
        return Completable.defer {
            playerDao.insertPlayer(PlayerEntity(player.id, gameId, player.name, player.color))
            Completable.complete()
        }
    }

    override fun savePlayers(gameId: Long, players: List<Player>): Completable {
        return Completable.defer {
            playerDao.insertPlayers(players.map { PlayerEntity(it.id, gameId, it.name, it.color) })
            Completable.complete()
        }
    }

    override fun getLaps(gameId: Long): Flowable<List<UniversalLap>> {
        return lapDao.getLaps(gameId).map { it.map { UniversalLap(it.id, it.points) } }
    }

    override fun saveLap(gameId: Long, lap: UniversalLap): Completable {
        return Completable.defer {
            lapDao.insertLap(UniversalLapEntity(lap.id, gameId, lap))
            Completable.complete()
        }
    }

    override fun deleteLap(gameId: Long, lap: UniversalLap): Completable {
        return Completable.defer {
            lapDao.deleteLap(UniversalLapEntity(lap.id, gameId, lap))
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