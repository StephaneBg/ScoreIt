/*
 * Copyright 2019 Stéphane Baiget
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

package com.sbgapps.scoreit.data.repository

import com.sbgapps.scoreit.data.model.Game

interface PreferencesRepo {

    fun getCurrentGame(): Game
    fun setCurrentGame(game: Game)

    fun getPlayerCount(): Int
    fun setPlayerCount(count: Int)

    fun isRounded(game: Game): Boolean
    fun setRounded(game: Game, rounded: Boolean)

    fun isTotalDisplayed(game: Game): Boolean
    fun setTotalDisplayed(game: Game, displayed: Boolean)

    fun getThemeMode(): String
    fun setThemeMode(mode: String)
}
