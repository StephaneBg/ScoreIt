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

package com.sbgapps.scoreit.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sbgapps.scoreit.domain.usecase.UniversalUseCase
import com.sbgapps.scoreit.ui.base.BaseViewModel
import com.sbgapps.scoreit.ui.mapper.PlayerMapper
import com.sbgapps.scoreit.ui.mapper.UniversalLapMapper
import com.sbgapps.scoreit.ui.model.Player
import com.sbgapps.scoreit.ui.model.UniversalLap
import timber.log.Timber


class UniversalViewModel(useCase: UniversalUseCase,
                         private val playerMapper: PlayerMapper,
                         private val lapMapper: UniversalLapMapper)
    : BaseViewModel<UniversalUseCase>(useCase) {

    private val players = MutableLiveData<List<Player>>()
    private val laps = MutableLiveData<List<UniversalLap>>()
    private var editionLap = MutableLiveData<Pair<UniversalLap, List<String>>>()
    private var deletedLap: UniversalLap? = null

    var mode: Mode = Mode.MODE_HISTORY
        private set

    suspend fun init() {
        useCase.initGame()
    }

    fun createGame(name: String, playerCount: Int) {
        launchAsync {
            useCase.createGame(name, playerCount)
            update()
        }
    }

    fun getPlayers(): LiveData<List<Player>> {
        players.value ?: run { update() }
        return players
    }

    private fun internalGetPlayers(laps: List<UniversalLap>): List<Player> {
        Timber.d("Players are updated!")
        val players = mutableListOf<Player>()
        useCase.getPlayers(true).forEachIndexed { index, player ->
            var score = 0
            laps.forEach { score += it.points[index] }
            players.add(playerMapper.mapFromDomain(player, score))
        }
        return players
    }

    fun getPlayerCount(): Int = players.value?.size ?: 0

    fun getLaps(): LiveData<List<UniversalLap>> {
        laps.value ?: run { laps.postValue(internalGetLaps()) }
        return laps
    }

    private fun internalGetLaps(): List<UniversalLap> {
        return useCase.getLaps().map { lapMapper.mapFromDomain(it) }
    }

    fun startUpdateMode(lap: UniversalLap) {
        mode = Mode.MODE_UPDATE
        val names = useCase.getPlayers(false).map { it.name }
        editionLap.value = lap to names
    }

    fun onLapEditionCompleted() {
        val prevMode = mode
        mode = Mode.MODE_HISTORY
        launchAsync {
            editionLap.value?.let {
                val lap = it.first
                when (prevMode) {
                    Mode.MODE_ADDITION -> {
                        Timber.d("Adding lap: $it")
                        useCase.addLap(lapMapper.mapToDomain(lap))
                    }
                    Mode.MODE_UPDATE -> {
                        Timber.d("Updated lap: $it")
                        useCase.updateLap(lapMapper.mapToDomain(lap))
                    }
                    Mode.MODE_HISTORY -> throw IllegalStateException("Cannot be on edition!")
                }
            }
            editionLap.value = null
            update()
        }
    }

    fun isOnHistoryMode() = mode == Mode.MODE_HISTORY

    fun setHistoryMode() {
        mode = Mode.MODE_HISTORY
        editionLap.value = null
    }

    fun getEditedLap(): LiveData<Pair<UniversalLap, List<String>>> {
        editionLap.value ?: run {
            mode = Mode.MODE_ADDITION
            val lap = lapMapper.mapFromDomain(useCase.createLap())
            val names = useCase.getPlayers(false).map { it.name }
            editionLap.postValue(Pair(lap, names))
        }
        return editionLap
    }

    fun toggleShowTotal(): Boolean {
        val isDisplayed = useCase.toggleShowTotal()
        update()
        return isDisplayed
    }

    fun clearLaps() {
        launchAsync {
            useCase.clearLaps()
            update()
        }
    }

    fun restoreLap(lap: UniversalLap, position: Int) {
        useCase.restoreLap(lapMapper.mapToDomain(lap), position)
        update()
    }

    fun deleteLap(lap: UniversalLap) {
        deletedLap = lap
        useCase.deleteLap(lapMapper.mapToDomain(lap))
        update()
    }

    fun deleteLapFromCache() {
        launchAsync { deletedLap?.let { useCase.deleteMapFromCache(lapMapper.mapToDomain(it)) } }
    }

    private fun update() {
        val _laps = internalGetLaps()
        val _players = internalGetPlayers(_laps)
        laps.postValue(_laps)
        players.postValue(_players)
    }

    enum class Mode {
        MODE_HISTORY,
        MODE_UPDATE,
        MODE_ADDITION
    }
}