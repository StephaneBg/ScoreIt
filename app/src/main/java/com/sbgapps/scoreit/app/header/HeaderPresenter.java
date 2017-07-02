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

package com.sbgapps.scoreit.app.header;

import android.support.annotation.ColorInt;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.app.ScoreItApp;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;

class HeaderPresenter extends MvpBasePresenter<HeaderView> {

    final private GameManager mGameManager;
    private int mEditedPlayer = Player.PLAYER_NONE;

    HeaderPresenter() {
        mGameManager = ScoreItApp.getGameManager();
    }

    @SuppressWarnings("WrongConstant")
    void start() {
        int count = mGameManager.getPlayerCount();
        getView().setupPlayerCount(count);
        for (int player = 0; player < count; player++) {
            getView().setName(player, mGameManager.getGame().getPlayer(player).getName());
            getView().setColor(player, mGameManager.getGame().getPlayer(player).getColor());
            getView().setScore(player, mGameManager.getGame().getScore(player, mGameManager.isRounded()));
        }
        getView().setIndicator(Player.PLAYER_2);
    }

    void onNameSelectionStarted(int player) {
        mEditedPlayer = player;
        getView().showNameActionsDialog();
    }

    void onNameSelected(String name) {
        getGame().getPlayer(mEditedPlayer).setName(name);
        getView().setName(mEditedPlayer, name);
        mEditedPlayer = Player.PLAYER_NONE;
    }

    private Game getGame() {
        return mGameManager.getGame();
    }

    void onColorSelectionStarted(int player) {
        mEditedPlayer = player;
        getView().showColorSelectorDialog(getGame().getPlayer(player).getColor());
    }

    void onColorSelected(@ColorInt int color) {
        getGame().getPlayer(mEditedPlayer).setColor(color);
        getView().setColor(mEditedPlayer, color);
        mEditedPlayer = Player.PLAYER_NONE;
    }
}
