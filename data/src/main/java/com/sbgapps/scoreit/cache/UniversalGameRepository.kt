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
import com.sbgapps.scoreit.cache.db.DatabaseInitializer
import com.sbgapps.scoreit.cache.mapper.PlayerMapper
import com.sbgapps.scoreit.cache.mapper.UniversalLapMapper
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.repository.GameRepository

class UniversalGameRepository(val gameDao: UniversalGameDao,
                              val playerDao: PlayerDao,
                              val lapDao: UniversalLapDao,
                              val playerMapper: PlayerMapper,
                              val lapMapper: UniversalLapMapper,
                              val dbInit: DatabaseInitializer)
    : GameRepository<UniversalLap> {

    override fun getGameId(name: String): Long {
        return gameDao.getGame(name).id ?: dbInit.createGame()
    }

    override fun deleteGame(gameId: Long) {
        gameDao.deleteGame(gameId)
    }

    override fun getPlayers(gameId: Long): List<Player> = playerDao.getPlayers(gameId).map { playerMapper.mapFromCache(it) }

    override fun savePlayer(gameId: Long, player: Player) {
        playerDao.insertPlayer(playerMapper.mapToCache(player, gameId))
    }

    override fun getLaps(gameId: Long): List<UniversalLap> = lapDao.getLaps(gameId).map { lapMapper.mapFromCache(it) }

    override fun saveLap(gameId: Long, lap: UniversalLap) {
        lapDao.insertLap(lapMapper.mapToCache(lap, gameId))
    }

    override fun deleteLap(gameId: Long, lap: UniversalLap) {
        lapDao.deleteLap(lapMapper.mapToCache(lap, gameId))
    }

    override fun clearLaps(gameId: Long) {
        lapDao.clearLaps(gameId)
    }
}