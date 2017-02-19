/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.core.model.tarot;


import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotFiveLap extends TarotLap {

    private int mPartner;

    public TarotFiveLap() {
        this(Player.PLAYER_1, new TarotBid(), 41, OUDLER_NONE_MSK, new ArrayList<TarotBonus>(), Player.PLAYER_2);
    }

    public TarotFiveLap(int taker, TarotBid bid, int points, int oudlers, ArrayList<TarotBonus> bonuses, int partner) {
        super(taker, bid, points, oudlers, bonuses);
        mPartner = partner;
    }

    public int getPartner() {
        return mPartner;
    }

    public void setPartner(int partner) {
        mPartner = partner;
    }

    @Override
    public int getPlayerCount() {
        return TarotFiveGame.NB_PLAYERS;
    }

    @Override
    public void computeScores() {
        super.computeScores();
        final int score = getResult();
        if (mTaker != mPartner) {
            mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? 2 * score :
                    (Player.PLAYER_1 == mPartner) ? score : -score;
            mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? 2 * score :
                    (Player.PLAYER_2 == mPartner) ? score : -score;
            mScores[Player.PLAYER_3] = (Player.PLAYER_3 == mTaker) ? 2 * score :
                    (Player.PLAYER_3 == mPartner) ? score : -score;
            mScores[Player.PLAYER_4] = (Player.PLAYER_4 == mTaker) ? 2 * score :
                    (Player.PLAYER_4 == mPartner) ? score : -score;
            mScores[Player.PLAYER_5] = (Player.PLAYER_5 == mTaker) ? 2 * score :
                    (Player.PLAYER_5 == mPartner) ? score : -score;
        } else {
            mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_3] = (Player.PLAYER_3 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_4] = (Player.PLAYER_4 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_5] = (Player.PLAYER_5 == mTaker) ? 4 * score : -score;
        }
    }

    @Override
    public int getPetitBonus() {
        for (TarotBonus bonus : getBonuses())
            if (TarotBonus.BONUS_PETIT_AU_BOUT == bonus.get())
                return (mTaker == bonus.getPlayer() || mPartner == bonus.getPlayer()) ? 10 : -10;
        return 0;
    }

    @Override
    public Object clone() {
        TarotFiveLap lap = ((TarotFiveLap) super.clone());
        return lap;
    }
}
