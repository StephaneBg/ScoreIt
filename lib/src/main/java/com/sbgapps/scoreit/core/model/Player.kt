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

package com.sbgapps.scoreit.core.model


class Player(var name: String, var color: Int) {

    override fun toString(): String = name

    companion object {

        const val PLAYER_NONE = -1
        const val PLAYER_1 = 0
        const val PLAYER_2 = 1
        const val PLAYER_3 = 2
        const val PLAYER_4 = 3
        const val PLAYER_5 = 4
        const val PLAYER_6 = 5
        const val PLAYER_7 = 6
        const val PLAYER_8 = 7
        const val PLAYER_TOTAL = 8
    }
}
