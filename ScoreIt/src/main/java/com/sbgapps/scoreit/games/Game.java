package com.sbgapps.scoreit.games;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sbaiget on 26/06/2014.
 */
public abstract class Game<T extends Lap> {

    public static final int BELOTE_CLASSIC = 0;
    public static final int BELOTE_COINCHE = 1;
    public static final int TAROT_3_PLAYERS = 2;
    public static final int TAROT_4_PLAYERS = 3;
    public static final int TAROT_5_PLAYERS = 4;
    public static final int UNIVERSAL = 5;

    @SerializedName("laps")
    protected List<T> mLaps;
    @SerializedName("players")
    protected List<Player> mPlayers;

    public List<T> getLaps() {
        return mLaps;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }
}
