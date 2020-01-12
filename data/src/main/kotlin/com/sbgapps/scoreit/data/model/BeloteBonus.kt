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

package com.sbgapps.scoreit.data.model

import androidx.annotation.StringRes
import com.sbgapps.scoreit.data.R

enum class BeloteBonus(val points: Int, @StringRes val resId: Int) {
    BELOTE(20, R.string.belote_bonus_belote),
    RUN_3(20, R.string.belote_bonus_run_3),
    RUN_4(50, R.string.belote_bonus_run_4),
    RUN_5(100, R.string.belote_bonus_run_5),
    FOUR_NORMAL(100, R.string.belote_bonus_normal_four),
    FOUR_NINE(150, R.string.belote_bonus_nine_four),
    FOUR_JACK(200, R.string.belote_bonus_jack_four)
}