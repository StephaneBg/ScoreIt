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

package com.sbgapps.scoreit.core.model.coinche

import android.content.Context

import com.sbgapps.scoreit.core.R
import com.sbgapps.scoreit.core.model.Player

class CoincheBonus(var bonus: Int, val player: Int = Player.PLAYER_1) {

    companion object {

        const val BONUS_BELOTE = 0
        const val BONUS_RUN_3 = 1
        const val BONUS_RUN_4 = 2
        const val BONUS_RUN_5 = 3
        const val BONUS_FOUR_NORMAL = 4
        const val BONUS_FOUR_NINE = 5
        const val BONUS_FOUR_JACK = 6

        fun getName(context: Context, bonus: Int): String? {
            return when (bonus) {
                BONUS_BELOTE -> return context.getString(R.string.coinche_bonus_belote)
                BONUS_RUN_3 -> return context.getString(R.string.coinche_bonus_run_3)
                BONUS_RUN_4 -> return context.getString(R.string.coinche_bonus_run_4)
                BONUS_RUN_5 -> return context.getString(R.string.coinche_bonus_run_5)
                BONUS_FOUR_NORMAL -> return context.getString(R.string.coinche_bonus_normal_four)
                BONUS_FOUR_NINE -> return context.getString(R.string.coinche_bonus_nine_four)
                BONUS_FOUR_JACK -> return context.getString(R.string.coinche_bonus_jack_four)
                else -> null
            }
        }
    }
}
