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

package com.sbgapps.scoreit.app

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.preference.PreferenceManager
import com.sbgapps.scoreit.core.model.Game
import com.sbgapps.scoreit.core.model.Player
import com.sbgapps.scoreit.core.model.universal.UniversalGame
import com.sbgapps.scoreit.core.model.universal.UniversalLap
import java.util.*

class GameManager(private val context: Context) {

    val preferences: SharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }
    val game: Game by lazy {
        val players = ArrayList<Player>(3)
        players.add(Player("Titi", Color.parseColor("#F44336")))
        players.add(Player("Riri", Color.parseColor("#9C27B0")))
        players.add(Player("Fifi", Color.parseColor("#8BC34A")))
        UniversalGame(players, ArrayList<UniversalLap>())
    }

    val playerCount: Int
        get() {
            when (playedGame) {
                Game.BELOTE, Game.COINCHE -> return 2
                Game.UNIVERSAL -> {
                    var count = preferences.getInt(KEY_UNIVERSAL_PLAYER_CNT, 5)
                    if (showTotal()) count++
                    return count
                }
                Game.TAROT -> return preferences.getInt(KEY_TAROT_PLAYER_CNT, 5)
                else -> return 3
            }
        }

    var playedGame: Int
        get() = preferences.getInt(KEY_PLAYED_GAME, Game.UNIVERSAL)
        set(game) = preferences.edit().putInt(KEY_PLAYED_GAME, game).apply()

    fun isRounded(): Boolean {
        return when (playedGame) {
            Game.TAROT, Game.UNIVERSAL -> return false
            Game.BELOTE -> return preferences.getBoolean(KEY_BELOTE_ROUND, false)
            Game.COINCHE -> return preferences.getBoolean(KEY_COINCHE_ROUND, false)
            else -> false
        }
    }

    fun showTotal(): Boolean {
        return when (playedGame) {
            Game.TAROT, Game.BELOTE, Game.COINCHE -> return false
            Game.UNIVERSAL -> return preferences.getBoolean(KEY_UNIVERSAL_TOTAL, false)
            else -> false
        }
    }

    companion object {

        const val KEY_PLAYED_GAME = "selected_game"
        const val KEY_UNIVERSAL_PLAYER_CNT = "universal_player_count"
        const val KEY_TAROT_PLAYER_CNT = "tarot_player_count"
        const val KEY_UNIVERSAL_TOTAL = "universal_show_total"
        const val KEY_BELOTE_ROUND = "belote_round_score"
        const val KEY_COINCHE_ROUND = "coinche_round_score"
    }
}
