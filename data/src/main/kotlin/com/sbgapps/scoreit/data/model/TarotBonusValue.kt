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
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
enum class TarotBonusValue(val points: Int, @StringRes val resId: Int) {
    PETIT_AU_BOUT(10, R.string.tarot_bonus_petit_au_bout),
    POIGNEE_SIMPLE(20, R.string.tarot_bonus_simple_poignee),
    POIGNEE_DOUBLE(30, R.string.tarot_bonus_double_poignee),
    POIGNEE_TRIPLE(40, R.string.tarot_bonus_triple_poignee),
    CHELEM_NON_ANNONCE(200, R.string.tarot_bonus_slam_not_announced),
    CHELEM_ANNONCE_REALISE(400, R.string.tarot_bonus_slam_announced_done),
    CHELEM_ANNONCE_NON_REALISE(-200, R.string.tarot_bonus_slam_announced_not_done)
}
