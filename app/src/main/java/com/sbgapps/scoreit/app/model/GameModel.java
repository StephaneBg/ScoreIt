package com.sbgapps.scoreit.app.model;

import android.graphics.Color;

import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.Player;
import com.sbgapps.scoreit.core.model.universal.UniversalGame;

import java.util.ArrayList;

/**
 * Created by sbaiget on 10/01/2017.
 */

public class GameModel {

    private static Game mGame;

    static public Game getGame() {
        return mGame;
    }

    static public void init() {
        ArrayList<Player> players = new ArrayList<>(3);
        players.add(new Player("Titi", Color.parseColor("#F44336")));
        players.add(new Player("Riri", Color.parseColor("#9C27B0")));
        players.add(new Player("Fifi", Color.parseColor("#8BC34A")));

        mGame = new UniversalGame(players, new ArrayList<>());
    }
}
