package com.sbgapps.scoreit.games.tarot;

import android.content.Context;
import android.content.res.Resources;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class TarotThreeGame extends Game<TarotThreeLap> {

    public TarotThreeGame() {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(3);
        mPlayers.add(new Player("Riri"));
        mPlayers.add(new Player("Fifi"));
        mPlayers.add(new Player("Loulou"));
    }
}
