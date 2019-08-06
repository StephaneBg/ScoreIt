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
import com.sbgapps.scoreit.data.model.BELOTE
import com.sbgapps.scoreit.data.model.COINCHE
import com.sbgapps.scoreit.data.model.TAROT
import com.sbgapps.scoreit.data.model.UNIVERSAL
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import kotlin.math.max

class ScoreItPreferencesRepo(context: Context) : PreferencesRepo {

    private val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getCurrentGame(): Int = preferences.getInt(USER_PREF_CURRENT_GAME, UNIVERSAL)

    override fun setCurrentGame(game: Int) = preferences.edit {
        putInt(USER_PREF_CURRENT_GAME, game)
    }

    override fun getPlayerCount(): Int = when (getCurrentGame()) {
        UNIVERSAL -> preferences.getInt(USER_PREF_UNIVERSAL_PLAYER_CNT, 5)
        TAROT -> preferences.getInt(USER_PREF_TAROT_PLAYER_CNT, 5)
        else -> 2
    }

    override fun setPlayerCount(count: Int) = preferences.edit {
        when (getCurrentGame()) {
            UNIVERSAL -> putInt(USER_PREF_UNIVERSAL_PLAYER_CNT, max(2, count))
            TAROT -> putInt(USER_PREF_TAROT_PLAYER_CNT, max(3, count))
        }
    }

    override fun isRounded(game: Int): Boolean = when (game) {
        BELOTE -> preferences.getBoolean(USER_PREF_BELOTE_ROUND, true)
        COINCHE -> preferences.getBoolean(USER_PREF_COINCHE_ROUND, true)
        else -> true
    }

    override fun setRounded(game: Int, rounded: Boolean) = when (game) {
        BELOTE -> preferences.edit { putBoolean(USER_PREF_BELOTE_ROUND, rounded) }
        COINCHE -> preferences.edit { putBoolean(USER_PREF_COINCHE_ROUND, rounded) }
        else -> Unit
    }

    override fun isTotalDisplayed(game: Int): Boolean = when (game) {
        UNIVERSAL -> preferences.getBoolean(USER_PREF_UNIVERSAL_TOTAL, false)
        else -> false
    }

    override fun setTotalDisplayed(game: Int, displayed: Boolean) = preferences.edit {
        when (game) {
            UNIVERSAL -> putBoolean(USER_PREF_UNIVERSAL_TOTAL, displayed)
        }
    }

    override fun getThemeMode(): String = preferences.getString(USER_PREF_THEME, null) ?: THEME_MODE_AUTO

    override fun setThemeMode(mode: String) = preferences.edit {
        putString(USER_PREF_THEME, mode)
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
