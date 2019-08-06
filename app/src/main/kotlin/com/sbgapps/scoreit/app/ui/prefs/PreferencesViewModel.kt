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

package com.sbgapps.scoreit.app.ui.prefs

import androidx.lifecycle.ViewModel
import com.sbgapps.scoreit.core.utils.convertThemeMode
import com.sbgapps.scoreit.data.source.DataStore

class PreferencesViewModel(private val dataStore: DataStore) : ViewModel() {

    fun isUniversalTotalDisplayed(): Boolean = dataStore.isUniversalTotalDisplayed()

    fun setUniversalTotalDisplayed(displayed: Boolean) {
        dataStore.setUniversalTotalDisplayed(displayed)
    }

    fun isBeloteScoreRounded(): Boolean = dataStore.isBeloteScoreRounded()

    fun setBeloteScoreRounded(rounded: Boolean) {
        dataStore.setBeloteScoreRounded(rounded)
    }

    fun isCoincheScoreRounded(): Boolean = dataStore.isCoincheScoreRounded()

    fun setCoincheScoreRounded(rounded: Boolean) {
        dataStore.setCoincheScoreRounded(rounded)
    }

    fun getPrefThemeMode(): String = dataStore.getPrefThemeMode()

    fun setPrefThemeMode(mode: String) {
        dataStore.setPrefThemeMode(mode)
    }

    fun getThemeMode(): Int = convertThemeMode(getPrefThemeMode())
}
