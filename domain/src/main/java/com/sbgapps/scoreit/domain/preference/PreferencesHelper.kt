/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.domain.preference

import android.content.Context
import android.preference.PreferenceManager
import androidx.content.edit
import com.sbgapps.scoreit.domain.R
import com.sbgapps.scoreit.domain.model.Player
import timber.log.Timber


class PreferencesHelper(private val context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    var isTotalDisplayed: Boolean
        get() {
            return sharedPreferences.getBoolean(KEY_UNIVERSAL_SHOW_TOTAL, false)
        }
        set(value) {
            sharedPreferences.edit { putBoolean(KEY_UNIVERSAL_SHOW_TOTAL, value) }
        }

    fun getUniversalGameName(): String? {
        return sharedPreferences.getString(KEY_UNIVERSAL_GAME_NAME, null)
    }

    fun setUniversalGame(name: String) {
        sharedPreferences.edit { putString(KEY_UNIVERSAL_GAME_NAME, name) }
    }

    fun getTotalPlayer() = Player(null, context.getString(R.string.universal_total_points), 0xFF757575.toInt())

    fun onUniversalTotalChanged(block: () -> Unit) {
        sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
            Timber.d("Show is total was updated")
            if (key == KEY_UNIVERSAL_SHOW_TOTAL) block.invoke()
        }
    }

    companion object {
        const val DEFAULT_GAME_NAME = "ScoreIt"
        private const val KEY_UNIVERSAL_GAME_NAME = "KEY_UNIVERSAL_GAME_NAME"
        private const val KEY_UNIVERSAL_SHOW_TOTAL = "KEY_UNIVERSAL_SHOW_TOTAL"
    }
}