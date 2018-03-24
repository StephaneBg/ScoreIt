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

package com.sbgapps.scoreit.domain.usecase

import com.sbgapps.scoreit.domain.model.PlayerEntity
import com.sbgapps.scoreit.domain.model.UniversalLapEntity
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import com.sbgapps.scoreit.domain.repository.GameRepository
import timber.log.Timber


class UniversalUseCase(private val universalRepo: GameRepository<UniversalLapEntity>,
                       private val prefsHelper: PreferencesHelper)
    : BaseUseCase() {

    private var gameId: Long = 0
    private lateinit var players: MutableList<PlayerEntity>
    private lateinit var laps: MutableList<UniversalLapEntity>

    fun getPlayers(withTotal: Boolean): List<PlayerEntity> {
        val _players = players.toMutableList()
        if (withTotal && prefsHelper.isTotalDisplayed) {
            _players.add(prefsHelper.getTotalPlayer())
        }
        Timber.d("Players: $_players")
        return _players
    }

    fun getLaps(): List<UniversalLapEntity> {
        val _laps = laps.map {
            val points = it.points.toMutableList()
            if (prefsHelper.isTotalDisplayed) points.add(points.sum())
            UniversalLapEntity(it.id, points)
        }
        Timber.d("Laps: $_laps")
        return _laps
    }

    fun createLap(withTotal: Boolean): UniversalLapEntity {
        val points = mutableListOf<Int>()
        for (i in 0 until getPlayers(withTotal).size) points.add(0)
        return UniversalLapEntity(null, points)
    }

    fun deleteLap(lapEntity: UniversalLapEntity) {
        laps.remove(lapEntity)
    }

    suspend fun addLap(lapEntity: UniversalLapEntity) {
        laps.add(lapEntity)
        asyncAwait { universalRepo.saveLap(gameId, lapEntity) }
    }

    suspend fun updateLap(lapEntity: UniversalLapEntity) {
        asyncAwait {
            universalRepo.saveLap(gameId, lapEntity)
            laps.find { it.id == lapEntity.id }
                    .let {
                        val index = laps.indexOf(it)
                        laps.removeAt(index)
                        laps.add(index, lapEntity)
                    }
        }
    }

    suspend fun clearLaps() {
        laps.clear()
        asyncAwait { universalRepo.clearLaps(gameId) }
    }

    suspend fun deleteMapFromCache(lapEntity: UniversalLapEntity) {
        asyncAwait { universalRepo.deleteLap(gameId, lapEntity) }
    }

    fun restoreLap(lapEntity: UniversalLapEntity, position: Int) {
        laps.add(position, lapEntity)
    }

    suspend fun initGame() {
        val name = prefsHelper.getUniversalGameName()
        name?.let {
            gameId = universalRepo.getGameId(name)
            players = universalRepo.getPlayers(gameId).toMutableList()
            laps = universalRepo.getLaps(gameId).toMutableList()
        } ?: run {
            createGame(PreferencesHelper.DEFAULT_GAME_NAME, PreferencesHelper.DEFAULT_UNIVERSAL_PLAYER_COUNT)
        }
    }

    suspend fun createGame(name: String, playerCount: Int) {
        prefsHelper.initUniversalGame(name, playerCount)
        asyncAwait {
            gameId = universalRepo.createGame(name, playerCount)
            players = universalRepo.getPlayers(gameId).toMutableList()
            laps = universalRepo.getLaps(gameId).toMutableList()
        }
    }

    fun toggleShowTotal(): Boolean {
        return if (prefsHelper.isTotalDisplayed) {
            prefsHelper.isTotalDisplayed = false
            false
        } else {
            prefsHelper.isTotalDisplayed = true
            true
        }
    }
}