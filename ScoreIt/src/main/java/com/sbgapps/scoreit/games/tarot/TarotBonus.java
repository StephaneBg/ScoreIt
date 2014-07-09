/*
 * Copyright (c) 2014 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.games.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Player;

import java.io.Serializable;

/**
 * Created by sbaiget on 23/06/2014.
 */
public class TarotBonus implements Serializable {

    public static final int BONUS_PETIT_AU_BOUT = 0;
    public static final int BONUS_POIGNEE_SIMPLE = 1;
    public static final int BONUS_POIGNEE_DOUBLE = 2;
    public static final int BONUS_POIGNEE_TRIPLE = 3;
    public static final int BONUS_CHELEM_NON_ANNONCE = 4;
    public static final int BONUS_CHELEM_ANNONCE_REALISE = 5;
    public static final int BONUS_CHELEM_ANNONCE_NON_REALISE = 6;

    @SerializedName("bonus")
    private int mBonus;
    @SerializedName("player")
    private int mPlayer;


    public TarotBonus() {
        this(BONUS_PETIT_AU_BOUT, Player.PLAYER_1);
    }

    public TarotBonus(int type, int player) {
        mBonus = type;
        mPlayer = player;
    }

    public int getBonus() {
        return mBonus;
    }

    public void setBonus(int bonus) {
        mBonus = bonus;
    }

    public int getPlayer() {
        return mPlayer;
    }

    public void setPlayer(int player) {
        mPlayer = player;
    }
}
