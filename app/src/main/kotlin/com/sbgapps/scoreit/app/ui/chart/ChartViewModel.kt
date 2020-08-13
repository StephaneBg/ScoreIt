/*
 * Copyright 2020 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.app.ui.chart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sbgapps.scoreit.app.ui.widget.LinePoint
import com.sbgapps.scoreit.app.ui.widget.LineSet
import com.sbgapps.scoreit.data.interactor.GameUseCase

class ChartViewModel(private val useCase: GameUseCase) : ViewModel() {

    private val innerState = MutableLiveData<List<LineSet>>()

    fun getLines(): LiveData<List<LineSet>> {
        innerState.value ?: innerState.postValue(getPlayerResults())
        return innerState
    }

    private fun getPlayerResults(): List<LineSet> {
        val lines = mutableListOf<LineSet>()
        val lapResults = mutableListOf<List<Int>>()
        useCase.getGame().laps.forEachIndexed { index, lap ->
            val results = useCase.getResults(lap)
            lapResults += if (0 == index) {
                results
            } else {
                results.zip(lapResults[index - 1]) { current, previous ->
                    previous + current
                }
            }
        }

        useCase.getPlayers().forEachIndexed { playerIndex, player ->
            val points = mutableListOf(LinePoint())
            lapResults.forEachIndexed { lapIndex, results ->
                points += LinePoint(lapIndex + 1, results[playerIndex])
            }
            lines += LineSet(points, player.color)
        }

        return lines
    }
}
