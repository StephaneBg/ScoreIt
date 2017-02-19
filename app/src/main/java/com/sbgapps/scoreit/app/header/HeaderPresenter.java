package com.sbgapps.scoreit.app.header;

import android.support.annotation.ColorInt;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.model.GameModel;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;
import com.sbgapps.scoreit.core.model.utils.GameHelper;

/**
 * Created by sbaiget on 21/12/2016.
 */

class HeaderPresenter extends MvpBasePresenter<HeaderViewActions> {

    @SuppressWarnings("WrongConstant")
    void setup() {
        Game game = GameModel.getGame();
        for (int player = 0; player < GameHelper.getPlayerCount(); player++) {
            getView().setName(player, game.getPlayer(player).getName());
            getView().setColor(player, game.getPlayer(player).getColor());
            getView().setScore(player, game.getScore(player));
        }
        getView().setIndicator(Player.PLAYER_2);
    }

    void onNameChanged(@Player.Players int player, String name) {
        getView().setName(player, name);
    }

    void onColorChanged(@Player.Players int player, @ColorInt int color) {
        getView().setColor(player, color);
    }
}
