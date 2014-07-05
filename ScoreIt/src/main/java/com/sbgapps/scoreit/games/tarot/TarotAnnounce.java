package com.sbgapps.scoreit.games.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Player;

import java.io.Serializable;

/**
 * Created by sbaiget on 23/06/2014.
 */
public class TarotAnnounce implements Serializable {

    public static final int TYPE_PETIT_AU_BOUT = 0;
    public static final int TYPE_POIGNEE_SIMPLE = 1;
    public static final int TYPE_POIGNEE_DOUBLE = 2;
    public static final int TYPE_MISERE_ATOUT = 3;
    public static final int TYPE_MISERE_TETE = 4;
    @SerializedName("announce")
    private final int mAnnounce;
    @SerializedName("player")
    private final int mPlayer;


    public TarotAnnounce() {
        this(TYPE_PETIT_AU_BOUT, Player.PLAYER_1);
    }

    public TarotAnnounce(int type, int player) {
        mAnnounce = type;
        mPlayer = player;
    }

    public int getAnnounce() {
        return mAnnounce;
    }

    public int getPlayer() {
        return mPlayer;
    }

    @Override
    public String toString() {
        return "Player " + mPlayer;
    }
}
