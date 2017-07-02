/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model.tarot

import android.content.Context
import com.sbgapps.scoreit.core.R

class TarotBid(val bid: Int = BID_PRISE) {

    companion object {

        const val BID_PRISE = 0
        const val BID_GARDE = 1
        const val BID_GARDE_SANS = 2
        const val BID_GARDE_CONTRE = 3

        fun getName(context: Context, bid: Int): String? {
            return when (bid) {
                BID_PRISE -> return context.getString(R.string.tarot_bid_take)
                BID_GARDE -> return context.getString(R.string.tarot_bid_guard)
                BID_GARDE_CONTRE -> return context.getString(R.string.tarot_bid_guard_against_kitty)
                BID_GARDE_SANS -> return context.getString(R.string.tarot_bid_guard_without_kitty)
                else -> null
            }
        }
    }
}
