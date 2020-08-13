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

package com.sbgapps.scoreit.data.solver

import com.sbgapps.scoreit.data.model.UniversalLap

class UniversalSolver {

    fun getResults(lap: UniversalLap, withTotal: Boolean): List<Int> =
        lap.points.toMutableList().apply { if (withTotal) add(sum()) }

    fun computeScores(laps: List<UniversalLap>, playerCount: Int, withTotal: Boolean): List<Int> {
        val count = if (withTotal) playerCount + 1 else playerCount
        val scores = MutableList(count) { 0 }
        laps.map { lap ->
            getResults(lap, withTotal)
        }.forEach { points ->
            for (player in 0 until count) scores[player] += points[player]
        }
        return scores
    }
}
