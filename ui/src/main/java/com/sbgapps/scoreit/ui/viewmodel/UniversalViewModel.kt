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

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.usecase.UniversalUseCase
import com.sbgapps.scoreit.ui.base.BaseViewModel


class UniversalViewModel(private val useCase: UniversalUseCase) : BaseViewModel() {

    private val players = MutableLiveData<List<Player>>()
    private val scores = MutableLiveData<List<Pair<Player, Int>>>()
    private val laps = MutableLiveData<List<UniversalLap>>()
    private var lap = MutableLiveData<UniversalLap>()

    var mode: Mode = Mode.MODE_HISTORY
        private set

    suspend fun init() {
        useCase.onUniversalTotalChanged {
            scores.postValue(useCase.getScores())
            laps.postValue(useCase.getLaps())
        }
        useCase.initGame()
    }

    fun getPlayers(): LiveData<List<Player>> {
        players.value ?: run { players.postValue(useCase.getPlayers()) }
        return players
    }

    fun getScores(): LiveData<List<Pair<Player, Int>>> {
        scores.value ?: run { scores.postValue(useCase.getScores()) }
        return scores
    }

    fun getLaps(): LiveData<List<UniversalLap>> {
        laps.value ?: run { laps.postValue(useCase.getLaps()) }
        return laps
    }

    fun startAdditionMode() {
        mode = Mode.MODE_ADDITION
    }

    fun startUpdateMode(_lap: UniversalLap) {
        mode = Mode.MODE_UPDATE
        lap.value = _lap
    }

    fun onLapEditionCompleted() {
        val prevMode = mode
        mode = Mode.MODE_HISTORY
        launchAsync {
            lap.value?.let {
                when (prevMode) {
                    Mode.MODE_ADDITION -> useCase.addLap(it)
                    Mode.MODE_UPDATE -> useCase.updateLap(it)
                }
            }

            laps.postValue(useCase.getLaps())
            scores.postValue(useCase.getScores())
            lap.value = null
        }
    }

    fun isOnHistoryMode() = mode == Mode.MODE_HISTORY

    fun endLapEdition() {
        mode = Mode.MODE_HISTORY
        lap.value = null
    }

    fun getLap(): LiveData<UniversalLap> {
        lap.value ?: run {
            mode = Mode.MODE_ADDITION
            lap.postValue(createLap())
        }
        return lap
    }

    private fun createLap(): UniversalLap {
        val players = useCase.getPlayers()
        val points = mutableListOf<Int>()
        for (i in 0 until players.size) points.add(0)
        return UniversalLap(null, points)
    }

    fun setPoints(points: MutableList<Int>) {
        lap.value?.setPoints(points)
    }

    enum class Mode {
        MODE_HISTORY,
        MODE_UPDATE,
        MODE_ADDITION
    }
}