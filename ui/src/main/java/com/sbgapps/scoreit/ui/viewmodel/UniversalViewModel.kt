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
import timber.log.Timber


class UniversalViewModel(private val useCase: UniversalUseCase) : BaseViewModel() {

    private val playersScores = MutableLiveData<List<Pair<Player, Int>>>()
    private val laps = MutableLiveData<List<UniversalLap>>()

    suspend fun init() {
        Timber.d("Start of init")
        useCase.execute()
        Timber.d("End of init")
    }

    fun getPlayersAndScores(): MutableLiveData<List<Pair<Player, Int>>> {
        Timber.d("Enter getPlayersAndScores")
        playersScores.value ?: run {
            launchAsync { playersScores.postValue(useCase.getPlayersAndScores()) }
        }
        return playersScores
    }

    fun getLaps(): LiveData<List<UniversalLap>> {
        Timber.d("Enter getLaps")
        laps.value ?: run {
            launchAsync { laps.postValue(useCase.getLaps()) }
        }
        return laps
    }
}