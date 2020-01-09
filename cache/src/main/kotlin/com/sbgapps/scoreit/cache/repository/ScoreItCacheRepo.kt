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

package com.sbgapps.scoreit.cache.repository

import com.sbgapps.scoreit.cache.model.BeloteGameCache
import com.sbgapps.scoreit.cache.model.CoincheGameCache
import com.sbgapps.scoreit.cache.model.TarotGameCache
import com.sbgapps.scoreit.cache.model.UniversalGameCache
import com.sbgapps.scoreit.data.model.BeloteGameData
import com.sbgapps.scoreit.data.model.CoincheGameData
import com.sbgapps.scoreit.data.model.GameData
import com.sbgapps.scoreit.data.model.TarotGameData
import com.sbgapps.scoreit.data.model.UniversalGameData
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo

class ScoreItCacheRepo(
    private val gameDao: ScoreItGameDao,
    private val preferencesRepo: PreferencesRepo
) : CacheRepo {

    override fun loadGame(): GameData = gameDao.getCurrentGame(
        preferencesRepo.getGameType(),
        preferencesRepo.getPlayerCount()
    ).toData()

    override fun createGame(fileName: String): GameData = gameDao.createGame(
        preferencesRepo.getGameType(),
        preferencesRepo.getPlayerCount(),
        fileName
    ).toData()

    override fun saveGame(gameData: GameData) {
        val gameCache = when (gameData) {
            is UniversalGameData -> UniversalGameCache(gameData)
            is TarotGameData -> TarotGameCache(gameData)
            is BeloteGameData -> BeloteGameCache(gameData)
            is CoincheGameData -> CoincheGameCache(gameData)
            else -> error("Unknown game")
        }
        gameDao.saveGame(gameCache, preferencesRepo.getPlayerCount())
    }
}
