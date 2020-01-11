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

import android.content.SharedPreferences
import androidx.core.content.edit
import com.sbgapps.scoreit.core.utils.THEME_MODE_AUTO
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import kotlin.math.max

class ScoreItPreferencesRepo(private val preferences: SharedPreferences) : PreferencesRepo {

    override fun getGameType(): GameType =
        GameType.values()[preferences.getInt(USER_PREF_CURRENT_GAME, GameType.UNIVERSAL.ordinal)]

    override fun setGameType(gameType: GameType) {
        preferences.edit {
            putInt(USER_PREF_CURRENT_GAME, gameType.ordinal)
        }
    }

    override fun getPlayerCount(): Int = when (getGameType()) {
        GameType.UNIVERSAL -> preferences.getInt(USER_PREF_UNIVERSAL_PLAYER_CNT, 5)
        GameType.TAROT -> preferences.getInt(USER_PREF_TAROT_PLAYER_CNT, 5)
        else -> 2
    }

    override fun setPlayerCount(count: Int) {
        preferences.edit {
            when (getGameType()) {
                GameType.UNIVERSAL -> putInt(USER_PREF_UNIVERSAL_PLAYER_CNT, max(2, count))
                GameType.TAROT -> putInt(USER_PREF_TAROT_PLAYER_CNT, max(3, count))
                else -> error("Cannot set player count for this game")
            }
        }
    }

    override fun isRounded(gameType: GameType): Boolean = when (gameType) {
        GameType.BELOTE -> preferences.getBoolean(USER_PREF_BELOTE_ROUND, true)
        GameType.COINCHE -> preferences.getBoolean(USER_PREF_COINCHE_ROUND, true)
        else -> true
    }

    override fun setRounded(gameType: GameType, rounded: Boolean) {
        return when (gameType) {
            GameType.BELOTE -> preferences.edit { putBoolean(USER_PREF_BELOTE_ROUND, rounded) }
            GameType.COINCHE -> preferences.edit { putBoolean(USER_PREF_COINCHE_ROUND, rounded) }
            else -> Unit
        }
    }

    override fun isTotalDisplayed(gameType: GameType): Boolean = when (gameType) {
        GameType.UNIVERSAL -> preferences.getBoolean(USER_PREF_UNIVERSAL_TOTAL, false)
        else -> false
    }

    override fun setTotalDisplayed(gameType: GameType, displayed: Boolean) {
        preferences.edit {
            when (gameType) {
                GameType.UNIVERSAL -> putBoolean(USER_PREF_UNIVERSAL_TOTAL, displayed)
                else -> error("Cannot display total for this game")
            }
        }
    }

    override fun getThemeMode(): String = preferences.getString(USER_PREF_THEME, null) ?: THEME_MODE_AUTO

    override fun setThemeMode(mode: String) {
        preferences.edit {
            putString(USER_PREF_THEME, mode)
        }
    }

    companion object {
        private const val USER_PREF_CURRENT_GAME = "selected_game"
        private const val USER_PREF_UNIVERSAL_PLAYER_CNT = "universal_player_count"
        private const val USER_PREF_TAROT_PLAYER_CNT = "tarot_player_count"
        private const val USER_PREF_UNIVERSAL_TOTAL = "universal_show_total"
        private const val USER_PREF_BELOTE_ROUND = "belote_round_score"
        private const val USER_PREF_COINCHE_ROUND = "coinche_round_score"
        private const val USER_PREF_THEME = "preferred_theme"
    }
}
