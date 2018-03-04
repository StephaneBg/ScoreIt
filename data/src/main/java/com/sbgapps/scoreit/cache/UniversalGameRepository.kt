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
import com.sbgapps.scoreit.cache.mapper.PlayerMapper
import com.sbgapps.scoreit.cache.mapper.UniversalLapMapper
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.repository.GameRepository

class UniversalGameRepository(val dbRepo: DatabaseRepo,
                              val playerMapper: PlayerMapper,
                              val lapMapper: UniversalLapMapper,
                              val dbInit: DatabaseInitializer)
    : GameRepository<UniversalLap> {

    override fun getGameId(name: String?): Long {
        return name?.let {
            dbRepo.universalDb.gameDao().getGame(name).id
        } ?: run {
            dbInit.createGame()
        }
    }

    override fun deleteGame(gameId: Long) {
        dbRepo.universalDb.gameDao().deleteGame(gameId)
    }

    override fun getPlayers(gameId: Long): List<Player> = dbRepo.universalDb.playerDao().getPlayers(gameId).map { playerMapper.mapFromCache(it) }

    override fun savePlayer(gameId: Long, player: Player) {
        dbRepo.universalDb.playerDao().insertPlayer(playerMapper.mapToCache(player, gameId))
    }

    override fun getLaps(gameId: Long): List<UniversalLap> = dbRepo.universalDb.lapDao().getLaps(gameId).map { lapMapper.mapFromCache(it) }

    override fun saveLap(gameId: Long, lap: UniversalLap) {
        dbRepo.universalDb.lapDao().insertLap(lapMapper.mapToCache(lap, gameId))
    }

    override fun deleteLap(gameId: Long, lap: UniversalLap) {
        dbRepo.universalDb.lapDao().deleteLap(lapMapper.mapToCache(lap, gameId))
    }

    override fun clearLaps(gameId: Long) {
        dbRepo.universalDb.lapDao().clearLaps(gameId)
    }
}