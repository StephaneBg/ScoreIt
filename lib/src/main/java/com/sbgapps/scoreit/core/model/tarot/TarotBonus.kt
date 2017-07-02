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


import com.sbgapps.scoreit.core.model.Player

class TarotBonus(val bonus: Int = BONUS_PETIT_AU_BOUT, val player: Int = Player.PLAYER_1) {

    companion object {

        const val BONUS_PETIT_AU_BOUT = 0
        const val BONUS_POIGNEE_SIMPLE = 1
        const val BONUS_POIGNEE_DOUBLE = 2
        const val BONUS_POIGNEE_TRIPLE = 3
        const val BONUS_CHELEM_NON_ANNONCE = 4
        const val BONUS_CHELEM_ANNONCE_REALISE = 5
        const val BONUS_CHELEM_ANNONCE_NON_REALISE = 6
    }
}
