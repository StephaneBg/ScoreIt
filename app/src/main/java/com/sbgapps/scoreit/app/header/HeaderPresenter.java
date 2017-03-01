package com.sbgapps.scoreit.app.header;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;

/**
 * Created by sbaiget on 21/12/2016.
 */

class HeaderPresenter extends MvpBasePresenter<HeaderViewActions> {

    final GameManager mGameManager;

    HeaderPresenter(GameManager gameManager) {
        mGameManager = gameManager;
    }

    @SuppressWarnings("WrongConstant")
    void setup() {
        for (int player = 0; player < mGameManager.getPlayerCount(); player++) {
            getView().setName(player, mGameManager.getGame().getPlayer(player).getName());
            getView().setColor(player, mGameManager.getGame().getPlayer(player).getColor());
            getView().setScore(player, mGameManager.getGame().getScore(player, mGameManager.isRounded()));
        }
        getView().setIndicator(Player.PLAYER_2);
    }

    private Game getGame() {
        return mGameManager.getGame();
    }
}
