/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model.belote


import com.sbgapps.scoreit.core.model.Lap

abstract class BaseBeloteLap(var scorer: Int, var points: Int) : Lap {

    var isDone: Boolean = false
    var scores: IntArray = IntArray(2)

    override fun getScore(player: Int, rounded: Boolean): Int {
        return if (rounded) getRoundedScore(scores[player]) else scores[player]
    }

    private fun getRoundedScore(score: Int): Int {
        when (score) {
            162 -> return 160
            250 -> return score
            else -> return (score + 5) / 10 * 10
        }
    }

    abstract fun computePoints()
}
