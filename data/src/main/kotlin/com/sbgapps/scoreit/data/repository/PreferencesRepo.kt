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

package com.sbgapps.scoreit.data.repository

import com.sbgapps.scoreit.data.model.GameType

interface PreferencesRepo {

    fun getGameType(): GameType
    fun setGameType(gameType: GameType)

    fun getPlayerCount(): Int
    fun setPlayerCount(count: Int)

    fun isRounded(gameType: GameType): Boolean
    fun setRounded(gameType: GameType, rounded: Boolean)

    fun isTotalDisplayed(gameType: GameType): Boolean
    fun setTotalDisplayed(gameType: GameType, displayed: Boolean)

    fun getThemeMode(): String
    fun setThemeMode(mode: String)
}
