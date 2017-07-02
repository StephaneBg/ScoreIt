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

package com.sbgapps.scoreit.core.model

import com.sbgapps.scoreit.core.model.utils.GameHelper

open class Game(val players: ArrayList<Player>, val laps: ArrayList<out Lap>) {

    init {
        initScores()
    }

    private fun initScores() = laps.forEach { lap -> lap.computeScores() }

    fun getPlayer(player: Int): Player = players[player]

    fun getScore(player: Int, rounded: Boolean): Int = GameHelper.getScore(laps, player, rounded)

    companion object {

        const val UNIVERSAL = 0
        const val TAROT = 1
        const val BELOTE = 2
        const val COINCHE = 3
    }
}
