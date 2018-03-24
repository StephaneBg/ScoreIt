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

package com.sbgapps.scoreit.domain.repository

import com.sbgapps.scoreit.domain.model.PlayerEntity


interface GameRepository<Lap> {

    fun getGameId(name: String): Long

    fun createGame(name: String, playerCount: Int): Long

    fun deleteGame(gameId: Long)

    fun getPlayers(gameId: Long): List<PlayerEntity>

    fun savePlayer(gameId: Long, playerEntity: PlayerEntity)

    fun getLaps(gameId: Long): List<Lap>

    fun saveLap(gameId: Long, lap: Lap)

    fun deleteLap(gameId: Long, lap: Lap)

    fun clearLaps(gameId: Long)
}