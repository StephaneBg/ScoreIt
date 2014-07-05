package com.sbgapps.scoreit.games;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sbaiget on 30/06/2014.
 */
public class Player {

    public static final int PLAYER_NONE = -1;
    public static final int PLAYER_1 = 0;
    public static final int PLAYER_2 = 1;
    public static final int PLAYER_3 = 2;
    public static final int PLAYER_4 = 3;
    public static final int PLAYER_5 = 4;
    public static final int PLAYER_COUNT_MAX = 5;

    @SerializedName("color")
    private final int mColor;
    @SerializedName("name")
    private String mName;

    public Player(String name, int color) {
        mName = name;
        mColor = color;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getColor() {
        return mColor;
    }
}
