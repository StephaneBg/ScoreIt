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

import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo

class ScoreItCacheRepo(
    private val gameDao: ScoreItGameDao,
    private val preferencesRepo: PreferencesRepo
) : CacheRepo {

    override fun loadGame(name: String?): Game {
        val gameType = preferencesRepo.getGameType()
        val count = preferencesRepo.getPlayerCount()
        name?.let {
            gameDao.setFileName(gameType, count, it)
        }
        return gameDao.getCurrentGame(gameType, count)
    }

    override fun createGame(name: String): Game = gameDao.createGame(
        preferencesRepo.getGameType(),
        preferencesRepo.getPlayerCount(),
        name
    )

    override fun saveGame(game: Game) {
        gameDao.saveGame(game, preferencesRepo.getPlayerCount())
    }

    override fun getSavedFiles(): List<Pair<String, Long>> = gameDao.getSavedFiles(
        preferencesRepo.getGameType(),
        preferencesRepo.getPlayerCount()
    )
}
