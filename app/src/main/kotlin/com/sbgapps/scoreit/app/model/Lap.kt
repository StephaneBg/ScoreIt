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
import com.sbgapps.scoreit.data.model.PlayerPosition

sealed class Lap

data class UniversalLap(
    val results: List<Int>
) : Lap()

data class BeloteLap(
    val results: List<Int> = emptyList(),
    val isWon: Boolean = true,
    val hasBelote: PlayerPosition
) : Lap()

data class CoincheLap(
    val results: List<Int>,
    val isWon: Boolean
) : Lap()

data class TarotLap(
    val results: List<Int>,
    val info: StringFactory,
    val isWon: Boolean
) : Lap()
