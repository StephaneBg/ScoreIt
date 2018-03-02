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

package com.sbgapps.scoreit.data

import com.sbgapps.scoreit.data.mapper.PlayerDataMapper
import com.sbgapps.scoreit.data.mapper.UniversalLapDataMapper
import com.sbgapps.scoreit.data.repository.UniversalDataStore
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.repository.UniversalRepository
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


class UniversalDataRepository(val cacheStore: UniversalDataStore,
                              val playerMapper: PlayerDataMapper,
                              val lapMapper: UniversalLapDataMapper)
    : UniversalRepository {

    override fun getGameId(name: String): Single<Long?> = cacheStore.getGameId(name)

    override fun deleteGame(gameId: Long): Completable = cacheStore.deleteGame(gameId)

    override fun getPlayers(gameId: Long): Flowable<List<Player>> = cacheStore.getPlayers(gameId).map { it.map { playerMapper.mapFromData(it) } }

    override fun savePlayer(gameId: Long, player: Player): Completable = cacheStore.savePlayer(gameId, playerMapper.mapToData(player))

    override fun getLaps(gameId: Long): Flowable<List<UniversalLap>> = cacheStore.getLaps(gameId)

    override fun addLap(gameId: Long, lap: UniversalLap): Completable = cacheStore.saveLap(gameId, lap)

    override fun deleteLap(gameId: Long, lap: UniversalLap): Completable = cacheStore.deleteLap(gameId, lap)

    override fun clearLaps(gameId: Long): Completable = cacheStore.clearLaps(gameId)
}