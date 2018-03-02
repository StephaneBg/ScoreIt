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

package com.sbgapps.scoreit.data.repository

import com.sbgapps.scoreit.data.model.PlayerData
import com.sbgapps.scoreit.domain.model.UniversalLap
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single


interface UniversalDataStore {

    fun getGameId(name: String): Single<Long?>

    fun deleteGame(gameId: Long): Completable

    fun getPlayers(gameId: Long): Flowable<List<PlayerData>>

    fun savePlayer(gameId: Long, player: PlayerData): Completable

    fun getLaps(gameId: Long): Flowable<List<UniversalLap>>

    fun saveLap(gameId: Long, lap: List<Int>): Completable

    fun deleteLap(gameId: Long, lap: List<Int>): Completable

    fun clearLaps(gameId: Long): Completable
}