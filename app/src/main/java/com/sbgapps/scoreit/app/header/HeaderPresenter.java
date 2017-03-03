package com.sbgapps.scoreit.app.header;

import android.support.annotation.ColorInt;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.app.ScoreItApp;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;

/**
 * Created by sbaiget on 21/12/2016.
 */

class HeaderPresenter extends MvpBasePresenter<HeaderViewActions> {

    final private GameManager mGameManager;
    @Player.Players
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

    void onNameSelectionStarted(@Player.Players int player) {
        mEditedPlayer = player;
        getView().showNameActionsDialog();
    }

    void onNameSelected(String name) {
        getGame().getPlayer(mEditedPlayer).setName(name);
        getView().setName(mEditedPlayer, name);
        mEditedPlayer = Player.PLAYER_NONE;
    }

    void onColorSelectionStarted(@Player.Players int player) {
        mEditedPlayer = player;
        getView().showColorSelectorDialog(getGame().getPlayer(player).getColor());
    }

    void onColorSelected(@ColorInt int color) {
        getGame().getPlayer(mEditedPlayer).setColor(color);
        getView().setColor(mEditedPlayer, color);
        mEditedPlayer = Player.PLAYER_NONE;
    }

    private Game getGame() {
        return mGameManager.getGame();
    }
}
