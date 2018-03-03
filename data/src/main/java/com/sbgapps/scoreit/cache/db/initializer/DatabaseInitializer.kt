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

package com.sbgapps.scoreit.cache.db.initializer

import com.sbgapps.scoreit.cache.db.UniversalDatabase
import com.sbgapps.scoreit.cache.model.PlayerEntity
import com.sbgapps.scoreit.cache.model.UniversalGameEntity


class DatabaseInitializer(val db: UniversalDatabase) {

    fun createGame(): Long {
        val game = UniversalGameEntity(null, "Default")
        db.gameDao().insertGame(game)
        game.id?.let { populatePlayers(it, 4) } ?: run { throw (Throwable("Cannot create game")) }
        return game.id
    }

    fun populatePlayers(gameId: Long, count: Int) {
        val _count = if (count > NAMES.size) NAMES.size else count
        for (i in 0 until _count) {
            db.playerDao().insertPlayer(PlayerEntity(null, gameId, NAMES[i], 0x424242))
        }
    }

    companion object {
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
    }
}