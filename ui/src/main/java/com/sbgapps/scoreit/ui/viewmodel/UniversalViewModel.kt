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
import com.sbgapps.scoreit.ui.ext.combineAndCompute
import com.sbgapps.scoreit.ui.model.Game
import com.sbgapps.scoreit.ui.model.Player
import com.sbgapps.scoreit.ui.model.UniversalLap
import com.sbgapps.scoreit.ui.model.mapFromDomain
import com.sbgapps.scoreit.ui.model.mapToDomain
import timber.log.Timber

class UniversalViewModel(useCase: UniversalUseCase) : BaseViewModel<UniversalUseCase>(useCase) {

    private val players = MutableLiveData<List<Player>>()
    private val laps = MutableLiveData<List<UniversalLap>>()
    private var editionLap = MutableLiveData<Pair<UniversalLap, List<String>>>()
    private val mode = MutableLiveData<Mode>()
    private var deletedLap: UniversalLap? = null

    suspend fun init() = useCase.initGame()

    fun createGame(name: String, playerCount: Int) = launchAsync {
        useCase.createGame(name, playerCount)
        update()
    }

    fun getGame(): LiveData<Game> =
        players.combineAndCompute(laps) { players, laps -> Game(players, laps) }

    fun getPlayers(): LiveData<List<Player>> {
        players.value ?: update()
        return players
    }

    private fun internalGetPlayers(laps: List<UniversalLap>): List<Player> {
        Timber.d("Players are updated!")
        val players = mutableListOf<Player>()
        useCase.getPlayers(true).forEachIndexed { index, player ->
            var score = 0
            laps.forEach { score += it.points[index] }
            players.add(player.mapFromDomain(score))
        }
        return players
    }

    fun getPlayerCount(): Int = players.value?.size ?: 0

    fun getLaps(): LiveData<List<UniversalLap>> {
        laps.value ?: laps.postValue(internalGetLaps())
        return laps
    }

    private fun internalGetLaps(): List<UniversalLap> = useCase.getLaps().map { it.mapFromDomain() }

    fun hasLaps(): Boolean = laps.value?.isNotEmpty() ?: false

    fun getMode(): LiveData<Mode> {
        mode.value ?: mode.postValue(Mode.HISTORY)
        return mode
    }

    fun validate() {
        when (mode.value) {
            Mode.HISTORY -> mode.postValue(Mode.ADDITION)
            Mode.UPDATE, Mode.ADDITION -> onLapEditionCompleted()
            else -> {
            }
        }
    }

    fun startUpdateMode(lap: UniversalLap) {
        mode.postValue(Mode.UPDATE)
        val names = useCase.getPlayers(false).map { it.name }
        editionLap.value = lap to names
    }

    private fun onLapEditionCompleted() {
        val prevMode = mode.value
        launchAsync {
            editionLap.value?.let {
                val lap = it.first
                when (prevMode) {
                    Mode.ADDITION -> {
                        Timber.d("Add lap: $it")
                        useCase.addLap(lap.mapToDomain())
                    }
                    Mode.UPDATE -> {
                        Timber.d("Update lap: $it")
                        useCase.updateLap(lap.mapToDomain())
                    }
                    else -> error("Incorrect mode")
                }
            }
            editionLap.value = null
            update()
            mode.postValue(Mode.HISTORY)
        }
    }

    fun stopEditionMode(): Boolean = if (mode.value == Mode.HISTORY) {
        false
    } else {
        editionLap.value = null
        mode.postValue(Mode.HISTORY)
        true
    }

    fun getEditedLap(): LiveData<Pair<UniversalLap, List<String>>> {
        editionLap.value ?: run {
            val lap = useCase.createLap().mapFromDomain()
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

    fun clearLaps() = launchAsync {
        useCase.clearLaps()
        update()
    }

    fun restoreLap(lap: UniversalLap, position: Int) {
        useCase.restoreLap(lap.mapToDomain(), position)
        update()
    }

    fun deleteLap(lap: UniversalLap) {
        deletedLap = lap
        useCase.deleteLap(lap.mapToDomain())
        update()
    }

    fun deleteLapFromCache() = launchAsync {
        deletedLap?.let { useCase.deleteFromCache(it.mapToDomain()) }
    }

    fun shouldShowMenu(): Boolean = mode.value == Mode.HISTORY

    private fun update() {
        val _laps = internalGetLaps()
        val _players = internalGetPlayers(_laps)
        laps.postValue(_laps)
        players.postValue(_players)
    }

    enum class Mode {
        HISTORY,
        UPDATE,
        ADDITION
    }
}