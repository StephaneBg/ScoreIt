/*
 * Copyright 2020 Stéphane Baiget
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

package com.sbgapps.scoreit.data.repository

import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.model.SavedGameInfo
import com.sbgapps.scoreit.data.model.ScoreBoard

interface CacheRepo {

    fun loadGame(name: String? = null): Game
    fun saveGame(game: Game)
    fun createGame(currentGame: Game, name: String): Game
    fun getSavedGames(): List<SavedGameInfo>

    fun loadScoreBoard(): ScoreBoard
    fun saveScoreBoard(scoreBoard: ScoreBoard)
    fun removeGame(fileName: String)
    fun renameGame(oldName: String, newName: String)
}
