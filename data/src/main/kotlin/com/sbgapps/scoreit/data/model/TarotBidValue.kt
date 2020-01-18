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
enum class TarotBidValue(val coefficient: Int, @StringRes val resId: Int) {
    SMALL(1, R.string.tarot_bid_take),
    GUARD(2, R.string.tarot_bid_guard),
    GUARD_WITHOUT_KITTY(4, R.string.tarot_bid_guard_without_kitty),
    GUARD_AGAINST_KITTY(6, R.string.tarot_bid_guard_against_kitty)
}
