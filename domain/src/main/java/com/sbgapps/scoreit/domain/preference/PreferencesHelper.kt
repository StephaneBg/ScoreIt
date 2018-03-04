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


class PreferencesHelper(context: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun getUniversalGameName(): String? {
        return sharedPreferences.getString("UNIVERSAL_GAME_NAME", null)
    }

    fun setUniversalGame(name: String) {
        sharedPreferences.edit { putString(UNIVERSAL_GAME_NAME, name) }
    }

    fun isTotalDisplayed() = sharedPreferences.getBoolean("UNIVERSAL_SHOW_TOTAL", false)

    companion object {
        const val DEFAULT_GAME_NAME = "ScoreIt"
        const val UNIVERSAL_GAME_NAME = "UNIVERSAL_GAME_NAME"
        const val UNIVERSAL_SHOW_TOTAL = "UNIVERSAL_SHOW_TOTAL"
    }
}