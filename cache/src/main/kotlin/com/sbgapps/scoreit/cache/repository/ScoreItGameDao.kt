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
import com.sbgapps.scoreit.data.model.TarotGame
import com.sbgapps.scoreit.data.model.UniversalGame
import com.squareup.moshi.JsonAdapter

class ScoreItGameDao(
    private val context: Context,
    private val preferences: SharedPreferences,
    private val storage: FileStorage,
    private val gameCacheJsonAdapter: JsonAdapter<Game>
) {

    fun getCurrentGame(gameType: GameType, playerCount: Int): Game {
        val directory = getDirectory(gameType, playerCount)
        val fileName = getFileName(gameType, playerCount)
        return try {
            val json = storage.loadFile(directory, fileName)
            gameCacheJsonAdapter.fromJson(json) ?: error("Can't parse json")
        } catch (e: Exception) {
            createGame(gameType, playerCount, fileName)
        }
    }

    fun createGame(gameType: GameType, playerCount: Int, fileName: String): Game {
        preferences.edit { putString(getFileNameKey(gameType, playerCount), fileName) }
        val players = initPlayers(context, gameType, playerCount)
        return when (gameType) {
            GameType.UNIVERSAL -> UniversalGame(players)
            GameType.TAROT -> TarotGame(players)
            GameType.BELOTE -> BeloteGame(players)
            GameType.COINCHE -> CoincheGame(players)
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
        preferences.getString(getFileNameKey(gameType, playerCount), DEFAULT_FILE_NAME) ?: DEFAULT_FILE_NAME

    private fun getFileNameKey(gameType: GameType, playerCount: Int): String = when (gameType) {
        GameType.UNIVERSAL -> "key_universal_${playerCount}"
        GameType.TAROT -> "key_belote"
        GameType.BELOTE -> "key_coinche"
        GameType.COINCHE -> "key_tarot_${playerCount}"
    }

    private fun getDirectory(gameType: GameType, playerCount: Int): String {
        val directory = when (gameType) {
            GameType.UNIVERSAL -> "universal/v2/${playerCount}"
            GameType.TAROT -> "tarot/v2/${playerCount}"
            GameType.BELOTE -> "belote/v2"
            GameType.COINCHE -> "coinche/v2"
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

    companion object {
        const val DEFAULT_FILE_NAME = "ScoreIt"
    }
}
