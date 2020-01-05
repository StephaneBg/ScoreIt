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

@file:Suppress("DEPRECATION")

package com.sbgapps.scoreit.cache.repository

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.core.content.edit
import com.sbgapps.scoreit.core.utils.THEME_MODE_AUTO
import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import kotlin.math.max

class ScoreItPreferencesRepo(context: Context) : PreferencesRepo {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getCurrentGame(): Game =
        Game.values()[preferences.getInt(USER_PREF_CURRENT_GAME, Game.UNIVERSAL.ordinal)]

    override fun setCurrentGame(game: Game) {
        preferences.edit {
            putInt(USER_PREF_CURRENT_GAME, game.ordinal)
        }
    }

    override fun getPlayerCount(): Int = when (getCurrentGame()) {
        Game.UNIVERSAL -> preferences.getInt(USER_PREF_UNIVERSAL_PLAYER_CNT, 5)
        Game.TAROT -> preferences.getInt(USER_PREF_TAROT_PLAYER_CNT, 5)
        else -> 2
    }

    override fun setPlayerCount(count: Int) {
        preferences.edit {
            when (getCurrentGame()) {
                Game.UNIVERSAL -> putInt(USER_PREF_UNIVERSAL_PLAYER_CNT, max(2, count))
                Game.TAROT -> putInt(USER_PREF_TAROT_PLAYER_CNT, max(3, count))
                else -> error("Cannot set player count for this game")
            }
        }
    }

    override fun isRounded(game: Game): Boolean = when (game) {
        Game.BELOTE -> preferences.getBoolean(USER_PREF_BELOTE_ROUND, true)
        Game.COINCHE -> preferences.getBoolean(USER_PREF_COINCHE_ROUND, true)
        else -> true
    }

    override fun setRounded(game: Game, rounded: Boolean) {
        return when (game) {
            Game.BELOTE -> preferences.edit { putBoolean(USER_PREF_BELOTE_ROUND, rounded) }
            Game.COINCHE -> preferences.edit { putBoolean(USER_PREF_COINCHE_ROUND, rounded) }
            else -> Unit
        }
    }

    override fun isTotalDisplayed(game: Game): Boolean = when (game) {
        Game.UNIVERSAL -> preferences.getBoolean(USER_PREF_UNIVERSAL_TOTAL, false)
        else -> false
    }

    override fun setTotalDisplayed(game: Game, displayed: Boolean) {
        preferences.edit {
            when (game) {
                Game.UNIVERSAL -> putBoolean(USER_PREF_UNIVERSAL_TOTAL, displayed)
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
