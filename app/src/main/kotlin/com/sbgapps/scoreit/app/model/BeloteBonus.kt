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

import androidx.annotation.StringRes
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.data.model.BeloteBonusData

enum class BeloteBonus(@StringRes val resId: Int) {
    Belote(R.string.belote_bonus_belote),
    RunOfThree(R.string.belote_bonus_run_3),
    RunOfFour(R.string.belote_bonus_run_4),
    RunOfFive(R.string.belote_bonus_run_5),
    FourNormal(R.string.belote_bonus_normal_four),
    FourNine(R.string.belote_bonus_nine_four),
    FourJack(R.string.belote_bonus_jack_four);

    fun toData(): BeloteBonusData = BeloteBonusData.values()[ordinal]
}

fun BeloteBonusData.toBeloteBonus(): BeloteBonus = BeloteBonus.values()[ordinal]
