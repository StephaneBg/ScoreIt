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

package com.sbgapps.scoreit.app.header

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter
import com.sbgapps.scoreit.app.GameManager
import com.sbgapps.scoreit.app.ScoreItApp
import com.sbgapps.scoreit.core.model.Player

class HeaderPresenter : MvpBasePresenter<HeaderView>() {

    val gameManager: GameManager = ScoreItApp.gameManager
    var editedPlayer = Player.PLAYER_NONE

    fun start() {
        val count = gameManager.playerCount
        view.setupPlayerCount(count)
        for (player in 0..count - 1) {
            view.setName(player, gameManager.game.getPlayer(player).name)
            view.setColor(player, gameManager.game.getPlayer(player).color)
            view.setScore(player, gameManager.game.getScore(player, gameManager.isRounded()))
        }
        view.setIndicator(Player.PLAYER_2)
    }

    fun onNameSelectionStarted(player: Int) {
        editedPlayer = player
        view.showNameActionsDialog()
    }

    fun onNameEdited(name: String) {
        gameManager.game.getPlayer(editedPlayer).name = name
        view.setName(editedPlayer, name)
        editedPlayer = Player.PLAYER_NONE
    }

    fun onColorSelectionStarted(player: Int) {
        editedPlayer = player
        view.showColorSelectorDialog(gameManager.game.getPlayer(player).color)
    }

    fun onColorSelected(color: Int) {
        gameManager.game.getPlayer(editedPlayer).color = color
        view.setColor(editedPlayer, color)
        editedPlayer = Player.PLAYER_NONE
    }
}
