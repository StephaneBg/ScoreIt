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

package com.sbgapps.scoreit.app.ui.saved

import android.annotation.SuppressLint
import com.sbgapps.scoreit.app.model.SavedGame
import com.sbgapps.scoreit.core.ui.BaseViewModel
import com.sbgapps.scoreit.core.ui.Empty
import com.sbgapps.scoreit.core.ui.State
import com.sbgapps.scoreit.data.interactor.GameUseCase
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class SavedGameViewModel(
    private val useCase: GameUseCase,
    private val datePattern: String
) : BaseViewModel(Empty) {

    fun loadGame(name: String) {
        action {
            useCase.loadGame(name)
            setState(SavedAction.Complete)
        }
    }

    fun getSavedFiles() {
        action {
            setState(SavedAction.Content(getSavedGames()))
        }
    }

    fun editName(position: Int, newName: String) {
        action {
            val savedGames = getSavedGames().toMutableList()
            useCase.renameGame(savedGames[position].fileName, newName)
            setState(SavedAction.Content(getSavedGames()))
        }
    }

    fun deleteGame(position: Int) {
        action {
            val savedGames = getSavedGames()
            useCase.removeGame(savedGames[position].fileName)
            setState(SavedAction.Content(getSavedGames()))
        }
    }

    @SuppressLint("DefaultLocale")
    private fun getSavedGames(): List<SavedGame> =
        useCase.getSavedFiles().map { (name, players, timeStamp) ->
            val instant = Instant.ofEpochMilli(timeStamp)
            val lastModified = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(datePattern))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            SavedGame(name, players, lastModified)
        }
}

sealed class SavedAction : State {
    data class Content(val games: List<SavedGame>) : SavedAction()
    data object Complete : SavedAction()
}
