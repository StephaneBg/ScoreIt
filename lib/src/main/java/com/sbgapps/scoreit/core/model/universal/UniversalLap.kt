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

package com.sbgapps.scoreit.core.model.universal


import com.sbgapps.scoreit.core.model.Lap

class UniversalLap : Lap {

    private val scores: IntArray

    constructor(playerCount: Int) {
        scores = IntArray(playerCount)
    }

    constructor(scores: IntArray) {
        this.scores = scores
    }

    fun setScore(player: Int, score: Int) {
        scores[player] = score
    }

    override fun getScore(player: Int, rounded: Boolean): Int {
        if (player >= scores.size) {
            val total = scores.sum()
            return total
        }
        return scores[player]
    }

    override fun computeScores() {
        // Nothing to do!
    }

    fun stepScore(player: Int, step: Int) {
        scores[player] += step
    }
}
