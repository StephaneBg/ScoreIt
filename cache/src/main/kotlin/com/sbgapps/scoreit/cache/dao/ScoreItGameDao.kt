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

package com.sbgapps.scoreit.cache.dao

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.sbgapps.scoreit.cache.R
import com.sbgapps.scoreit.core.ext.color
import com.sbgapps.scoreit.data.model.BeloteGame
import com.sbgapps.scoreit.data.model.CoincheGame
import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.model.ScoreBoard
import com.sbgapps.scoreit.data.model.TarotGame
import com.sbgapps.scoreit.data.model.UniversalGame
import com.squareup.moshi.Moshi

class ScoreItGameDao(
    private val context: Context,
    private val preferences: SharedPreferences,
    private val storage: FileStorage,
    moshi: Moshi
) {

    private val gameCacheJsonAdapter = moshi.adapter(Game::class.java)
    private val scoreboardJsonAdapter = moshi.adapter(ScoreBoard::class.java)

    fun loadGameOrCreate(gameType: GameType, playerCount: Int): Game {
        val directory = getDirectory(gameType, playerCount)
        val fileName = getFileName(gameType, playerCount)
        return try {
            getJson(directory, fileName)
        } catch (e: Exception) {
            createGame(gameType, playerCount, fileName)
        }
    }

    fun loadGame(gameType: GameType, playerCount: Int, fileName: String): Game? {
        val directory = getDirectory(gameType, playerCount)
        return try {
            getJson(directory, fileName)
        } catch (e: Exception) {
            null
        }
    }

    private fun getJson(directory: String, fileName: String): Game {
        val json = storage.loadFile(directory, fileName)
        return gameCacheJsonAdapter.fromJson(json) ?: error("Can't parse json")
    }

    private fun createGame(gameType: GameType, playerCount: Int, fileName: String): Game {
        val players = initPlayers(context, gameType, playerCount)
        val newGame = when (gameType) {
            GameType.UNIVERSAL -> UniversalGame(players)
            GameType.TAROT -> TarotGame(players)
            GameType.BELOTE -> BeloteGame(players)
            GameType.COINCHE -> CoincheGame(players)
        }
        return createGame(newGame, playerCount, fileName)
    }

    fun createGame(currentGame: Game, playerCount: Int, fileName: String): Game {
        preferences.edit { putString(getFileNameKey(currentGame.type, playerCount), fileName) }
        saveGame(currentGame, playerCount)
        return when (currentGame) {
            is UniversalGame -> currentGame.copy(laps = emptyList())
            is TarotGame -> currentGame.copy(laps = emptyList())
            is BeloteGame -> currentGame.copy(laps = emptyList())
            is CoincheGame -> currentGame.copy(laps = emptyList())
        }
    }

    fun saveGame(gameCache: Game, playerCount: Int) {
        val directory = getDirectory(gameCache.type, playerCount)
        val fileName = getFileName(gameCache.type, playerCount)
        val json = gameCacheJsonAdapter.toJson(gameCache)
        storage.saveFile(directory, fileName, json)
    }

    fun setFileName(gameType: GameType, playerCount: Int, fileName: String) {
        preferences.edit {
            putString(getFileNameKey(gameType, playerCount), fileName)
        }
    }

    private fun getFileName(gameType: GameType, playerCount: Int): String =
        preferences.getString(getFileNameKey(gameType, playerCount), getDefaultFileName()) ?: getDefaultFileName()

    private fun getFileNameKey(gameType: GameType, playerCount: Int): String = when (gameType) {
        GameType.UNIVERSAL -> "${UNIVERSAL_KEY}_${playerCount}"
        GameType.TAROT -> "${TAROT_KEY}_${playerCount}"
        GameType.BELOTE -> BELOTE_KEY
        GameType.COINCHE -> COINCHE_KEY
    }

    private fun getDirectory(gameType: GameType, playerCount: Int): String {
        val directory = when (gameType) {
            GameType.UNIVERSAL -> "$UNIVERSAL_PATH/${playerCount}"
            GameType.TAROT -> "$TAROT_PATH/${playerCount}"
            GameType.BELOTE -> BELOTE_PATH
            GameType.COINCHE -> COINCHE_PATH
        }
        storage.createDirectory(directory)
        return directory
    }

    private fun initPlayers(context: Context, gameType: GameType, playerCount: Int): List<Player> {
        val players = mutableListOf<Player>()
        when (gameType) {
            GameType.BELOTE, GameType.COINCHE -> {
                players += Player(
                    context.getString(R.string.belote_first_team_default_name),
                    context.color(R.color.md_green_600)
                )
                players += Player(
                    context.getString(R.string.belote_second_team_default_name),
                    context.color(R.color.md_orange_600)
                )
            }
            else -> {
                val names = context.resources.obtainTypedArray(R.array.player_names)
                val colors = context.resources.obtainTypedArray(R.array.player_colors)
                for (i in 0 until playerCount) {
                    players += Player(names.getString(i)!!, colors.getColor(i, 0))
                }
                names.recycle()
                colors.recycle()
            }
        }
        return players
    }

    fun getSavedFiles(gameType: GameType, playerCount: Int): List<Pair<String, Long>> =
        storage.getSavedFiles(getDirectory(gameType, playerCount))

    fun loadScoreBoard(): ScoreBoard {
        storage.createDirectory(SCOREBOARD_PATH)
        return try {
            val json = storage.loadFile(
                SCOREBOARD_PATH,
                SCOREBOARD_FILENAME
            )
            scoreboardJsonAdapter.fromJson(json) ?: error("Can't parse json")
        } catch (exception: Exception) {
            ScoreBoard(
                nameOne = context.getString(R.string.scoreboard_default_name_one),
                nameTwo = context.getString(R.string.scoreboard_default_name_two)
            )
        }
    }

    fun saveScoreBoard(scoreBoard: ScoreBoard) {
        val json = scoreboardJsonAdapter.toJson(scoreBoard)
        storage.saveFile(
            SCOREBOARD_PATH,
            SCOREBOARD_FILENAME, json
        )
    }

    private fun getDefaultFileName(): String = context.getString(R.string.default_file_name)

    fun removeGame(gameType: GameType, playerCount: Int, fileName: String) {
        val directory = getDirectory(gameType, playerCount)
        storage.removeFile(directory, fileName)
        if (fileName == getFileName(gameType, playerCount)) {
            setFileName(gameType, playerCount, getDefaultFileName())
        }
    }

    fun renameGame(gameType: GameType, playerCount: Int, oldName: String, newName: String) {
        val directory = getDirectory(gameType, playerCount)
        storage.renameFile(directory, oldName, newName)
        if (oldName == getFileName(gameType, playerCount)) {
            setFileName(gameType, playerCount, newName)
        }
    }

    companion object {
        private const val UNIVERSAL_PATH = "universal/v2"
        private const val UNIVERSAL_KEY = "universal_key"
        private const val TAROT_PATH = "tarot/v2"
        private const val TAROT_KEY = "tarot_key"
        private const val BELOTE_PATH = "belote/v2"
        private const val BELOTE_KEY = "belote_key"
        private const val COINCHE_PATH = "coinche/v2"
        private const val COINCHE_KEY = "coinche_key"
        private const val SCOREBOARD_PATH = "scoreboard/v1"
        private const val SCOREBOARD_FILENAME = "data"
    }
}
