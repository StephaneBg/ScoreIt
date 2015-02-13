package com.sbgapps.scoreit.games.tarot;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 10/02/2015.
 */
public abstract class TarotGame<E extends TarotLap> extends Game<E> {

    public TarotGame(Context context) {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(getPlayerCount());

        Resources r = context.getResources();
        final TypedArray names = r.obtainTypedArray(R.array.player_names);
        for (int i = 0; i < getPlayerCount(); i++)
            mPlayers.add(new Player(names.getString(i)));

        names.recycle();
    }

    abstract public int getPlayerCount();
}
