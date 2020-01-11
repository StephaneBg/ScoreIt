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

package com.sbgapps.scoreit.data.source

import com.sbgapps.scoreit.data.model.GameData
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo

class DataStore(
    private val cacheRepo: CacheRepo,
    private val prefsRepo: PreferencesRepo
) {
    private var game: GameData? = null

    fun getGame(): GameData = game ?: cacheRepo.loadGame().also { game = it }

    fun createGame(name: String) {
        game = cacheRepo.createGame(name)
    }

    fun saveGame(game: GameData) {
        this.game = game
        cacheRepo.saveGame(game)
    }

    fun setCurrentGame(gameType: GameType) {
        this.game = null
        prefsRepo.setGameType(gameType)
    }

    fun setPlayerCount(count: Int) {
        this.game = null
        prefsRepo.setPlayerCount(count)
    }

    fun isUniversalTotalDisplayed(): Boolean = prefsRepo.isTotalDisplayed(GameType.UNIVERSAL)

    fun setUniversalTotalDisplayed(displayed: Boolean) {
        prefsRepo.setTotalDisplayed(GameType.UNIVERSAL, displayed)
    }

    fun isBeloteScoreRounded(): Boolean = prefsRepo.isRounded(GameType.BELOTE)

    fun setBeloteScoreRounded(rounded: Boolean) {
        prefsRepo.setRounded(GameType.BELOTE, rounded)
    }

    fun isCoincheScoreRounded(): Boolean = prefsRepo.isRounded(GameType.COINCHE)

    fun setCoincheScoreRounded(rounded: Boolean) {
        prefsRepo.setRounded(GameType.COINCHE, rounded)
    }

    fun getPrefThemeMode(): String = prefsRepo.getThemeMode()

    fun setPrefThemeMode(mode: String) {
        prefsRepo.setThemeMode(mode)
    }
}
