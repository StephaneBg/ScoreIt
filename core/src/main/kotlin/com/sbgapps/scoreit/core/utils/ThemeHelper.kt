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

package com.sbgapps.scoreit.core.utils

import androidx.appcompat.app.AppCompatDelegate

const val THEME_MODE_LIGHT = "THEME_MODE_LIGHT"
const val THEME_MODE_DARK = "THEME_MODE_DARK"
const val THEME_MODE_AUTO = "THEME_MODE_AUTO"

fun convertThemeMode(mode: String): Int = when (mode) {
    THEME_MODE_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
    THEME_MODE_DARK -> AppCompatDelegate.MODE_NIGHT_YES
    else -> if (isQOrAbove()) AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM else AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
}
