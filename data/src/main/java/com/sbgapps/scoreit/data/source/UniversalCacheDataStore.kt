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

package com.sbgapps.scoreit.data.source

import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.UniversalLap
import com.sbgapps.scoreit.data.repository.UniversalCache
import com.sbgapps.scoreit.data.repository.UniversalDataStore
import io.reactivex.Completable
import io.reactivex.Flowable


class UniversalCacheDataStore(val cache: UniversalCache): UniversalDataStore {

    override fun deleteGame(gameId: Long): Completable {
        return cache.deleteGame(gameId)
    }

    override fun getPlayers(gameId: Long): Flowable<List<Player>> {
        return cache.getPlayers(gameId)
    }

    override fun savePlayer(gameId: Long, player: Player): Completable {
        return cache.savePlayer(gameId, player)
    }

    override fun getLaps(gameId: Long): Flowable<List<UniversalLap>> {
        return cache.getLaps(gameId)
    }

    override fun saveLap(gameId: Long, lap: UniversalLap): Completable {
        return cache.saveLap(gameId, lap)
    }

    override fun deleteLap(gameId: Long, lap: UniversalLap): Completable {
        return cache.deleteLap(gameId, lap)
    }

    override fun clearLaps(gameId: Long): Completable {
        return cache.clearLaps(gameId)
    }
}