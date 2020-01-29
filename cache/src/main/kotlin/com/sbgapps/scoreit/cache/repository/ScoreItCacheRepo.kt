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
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.SavedGameInfo
import com.sbgapps.scoreit.data.model.ScoreBoard
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo

class ScoreItCacheRepo(
    private val gameDao: ScoreItGameDao,
    private val preferencesRepo: PreferencesRepo
) : CacheRepo {

    private val gameType: GameType
        get() = preferencesRepo.getGameType()

    private val playerCount: Int
        get() = preferencesRepo.getPlayerCount()

    override fun loadGame(name: String?): Game {
        name?.let {
            gameDao.setFileName(gameType, playerCount, it)
        }
        return gameDao.loadGameOrCreate(gameType, playerCount)
    }

    override fun createGame(currentGame: Game, name: String): Game = gameDao.createGame(currentGame, playerCount, name)

    override fun saveGame(game: Game) {
        gameDao.saveGame(game, playerCount)
    }

    override fun getSavedGames(): List<SavedGameInfo> {
        val files = gameDao.getSavedFiles(gameType, playerCount)
        return files.mapNotNull { (fileName, timeStamp) ->
            gameDao.loadGame(gameType, playerCount, fileName)?.let { game ->
                val players = game.players.joinToString(" - ") { it.name }
                SavedGameInfo(fileName, players, timeStamp)
            }
        }
    }

    override fun removeGame(fileName: String) {
        gameDao.removeGame(gameType, playerCount, fileName)
    }

    override fun renameGame(oldName: String, newName: String) {
        gameDao.renameGame(gameType, playerCount, oldName, newName)
    }

    override fun loadScoreBoard(): ScoreBoard = gameDao.loadScoreBoard()

    override fun saveScoreBoard(scoreBoard: ScoreBoard) {
        gameDao.saveScoreBoard(scoreBoard)
    }
}
