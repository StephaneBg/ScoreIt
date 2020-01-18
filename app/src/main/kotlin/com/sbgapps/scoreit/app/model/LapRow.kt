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

package com.sbgapps.scoreit.app.model

import com.sbgapps.scoreit.core.utils.string.StringFactory

sealed class LapRow

data class UniversalLapRow(
    val results: List<Int>
) : LapRow()

data class BeloteLapRow(
    val results: List<String>,
    val isWon: Boolean = true
) : LapRow()

data class CoincheLapRow(
    val results: List<String>,
    val isWon: Boolean
) : LapRow()

data class TarotLapRow(
    val results: List<String>,
    val info: StringFactory,
    val isWon: Boolean
) : LapRow()
