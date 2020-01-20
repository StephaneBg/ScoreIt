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
import com.sbgapps.scoreit.data.model.SavedGameInfo
import com.sbgapps.scoreit.data.model.ScoreBoard
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
        return gameDao.loadGameOrCreate(gameType, count)
    }

    override fun createGame(currentGame: Game, name: String): Game = gameDao.createGame(
        currentGame,
        preferencesRepo.getPlayerCount(),
        name
    )

    override fun saveGame(game: Game) {
        gameDao.saveGame(game, preferencesRepo.getPlayerCount())
    }

    override fun getSavedGames(): List<SavedGameInfo> {
        val gameType = preferencesRepo.getGameType()
        val playerCount = preferencesRepo.getPlayerCount()
        val files = gameDao.getSavedFiles(gameType, playerCount)
        return files.mapNotNull { (fileName, date) ->
            gameDao.loadGame(gameType, playerCount, fileName)?.let { game ->
                val players = game.players.joinToString(" - ") { it.name }
                SavedGameInfo(fileName, date, players)
            }
        }
    }

    override fun loadScoreBoard(): ScoreBoard = gameDao.loadScoreBoard()

    override fun saveScoreBoard(scoreBoard: ScoreBoard) {
        gameDao.saveScoreBoard(scoreBoard)
    }
}
