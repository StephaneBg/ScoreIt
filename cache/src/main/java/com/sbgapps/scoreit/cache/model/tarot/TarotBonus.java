/*
 * Copyright 2019 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.model.tarot;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import static com.sbgapps.scoreit.data.model.ConstantsKt.BONUS_PETIT_AU_BOUT;
import static com.sbgapps.scoreit.data.model.ConstantsKt.PLAYER_1;

/**
 * Created by sbaiget on 23/06/2014.
 */
public class TarotBonus implements Serializable {

    @SerializedName("bonus")
    private int mBonus;
    @SerializedName("player")
    private int mPlayer;

    public TarotBonus() {
        this(BONUS_PETIT_AU_BOUT);
    }

    public TarotBonus(int bonus) {
        this(bonus, PLAYER_1);
    }

    public TarotBonus(int bonus, int player) {
        mBonus = bonus;
        mPlayer = player;
    }

//    public static String getLiteralBonus(Context context, @TarotBonusValues int bonus) {
//        switch (bonus) {
//            case TarotBonus.BONUS_PETIT_AU_BOUT:
//                return context.getString(R.string.tarot_bonus_petit_au_bout);
//            case TarotBonus.BONUS_POIGNEE_SIMPLE:
//                return context.getString(R.string.tarot_bonus_simple_poignee);
//            case TarotBonus.BONUS_POIGNEE_DOUBLE:
//                return context.getString(R.string.tarot_bonus_double_poignee);
//            case TarotBonus.BONUS_POIGNEE_TRIPLE:
//                return context.getString(R.string.tarot_bonus_triple_poignee);
//            case TarotBonus.BONUS_CHELEM_NON_ANNONCE:
//                return context.getString(R.string.tarot_bonus_slam_not_announced);
//            case TarotBonus.BONUS_CHELEM_ANNONCE_REALISE:
//                return context.getString(R.string.tarot_bonus_slam_announced_not_done);
//            case TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE:
//                return context.getString(R.string.tarot_bonus_slam_announced_done);
//        }
//        return null;
//    }

    public int get() {
        return mBonus;
    }

    public void set(int bonus) {
        mBonus = bonus;
    }

    public int getPlayer() {
        return mPlayer;
    }

    public void setPlayer(int player) {
        mPlayer = player;
    }
}
