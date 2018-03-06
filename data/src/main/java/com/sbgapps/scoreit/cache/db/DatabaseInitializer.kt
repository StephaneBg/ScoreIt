/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.db

import com.sbgapps.scoreit.cache.DatabaseRepo
import com.sbgapps.scoreit.cache.model.PlayerEntity
import com.sbgapps.scoreit.cache.model.UniversalGameEntity
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import kotlin.math.min

class DatabaseInitializer(private val dbRepo: DatabaseRepo) {

    fun createGame(): Long {
        val game = UniversalGameEntity(name = PreferencesHelper.DEFAULT_GAME_NAME)
        val id = dbRepo.universalDb.gameDao().insertGame(game)
        populatePlayers(id, DEFAULT_PLAYER_COUNT)
        return id
    }

    private fun populatePlayers(gameId: Long, count: Int) {
        val _count = min(count, NAMES.size)
        for (i in 0 until _count) {
            dbRepo.universalDb.playerDao().insertPlayer(PlayerEntity(null, gameId, NAMES[i], COLORS[i]))
        }
    }

    companion object {
        private const val DEFAULT_PLAYER_COUNT = 2

        private val NAMES = listOf(
                "Riri",
                "Toto",
                "Fifi",
                "Lulu",
                "Titi",
                "Baba",
                "Lili",
                "Bubu"
        )

        private val COLORS = listOf(
                0xFFF57C00.toInt(),
                0xFF388E3C.toInt(),
                0xFF1976D2.toInt(),
                0xFF7B1FA2.toInt(),
                0xFF00796B.toInt(),
                0xFFD32F2F.toInt(),
                0xFF5D4037.toInt(),
                0xFF303F9F.toInt()
        )
    }
}