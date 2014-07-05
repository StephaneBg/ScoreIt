package com.sbgapps.scoreit.games.belote;

import android.content.Context;
import android.content.res.Resources;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 24/06/2014.
 */
public class BeloteClassicGame extends Game<BeloteClassicLap> {

    public BeloteClassicGame(Context context) {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(2);
        Resources r = context.getResources();
        mPlayers.add(new Player(r.getString(R.string.them), r.getColor(R.color.color_player1)));
        mPlayers.add(new Player(r.getString(R.string.us), r.getColor(R.color.color_player2)));
    }
}
