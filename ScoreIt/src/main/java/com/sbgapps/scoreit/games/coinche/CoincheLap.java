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

package com.sbgapps.scoreit.games.coinche;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 11/11/13.
 */
public class CoincheLap extends GenericBeloteLap {

    public static final int COINCHE_NONE = 0;
    public static final int COINCHE_NORMAL = 1;
    public static final int COINCHE_DOUBLE = 2;

    @SerializedName("bid")
    private int mBid;
    @SerializedName("bonuses")
    protected List<CoincheBonus> mBonuses;

    public CoincheLap(int taker, int points, int bid, List<CoincheBonus> bonuses) {
        super(taker, points);
        mBid = bid;
        mBonuses = bonuses;
    }

    public CoincheLap() {
        this(Player.PLAYER_1, 110, 110, new ArrayList<CoincheBonus>());
    }

    public int getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid = bid;
    }

    public List<CoincheBonus> getBonuses() {
        return mBonuses;
    }

    @Override
    public void set(Lap lap) {
        super.set(lap);
        mBid = ((CoincheLap) lap).getBid();
        mBonuses = ((CoincheLap) lap).getBonuses();
        computeScores();
    }

    @Override
    public void computeScores() {
        super.computeScores();

        int takerPts = mScores[0];
        int counterPts = mScores[1];

        // Check belote
        for (CoincheBonus bonus : mBonuses) {
            if (CoincheBonus.BONUS_BELOTE == bonus.get()) {
                if (((Player.PLAYER_1 == mScorer) && (Player.PLAYER_1 == bonus.getPlayer())) ||
                        ((Player.PLAYER_2 == mScorer) && (Player.PLAYER_2 == bonus.getPlayer()))) {
                    takerPts += 20;
                } else {
                    counterPts += 20;
                }
            }
        }

//        if ((mPoints >= mBid) && (takerPts > counterPts)) {
//            // Deal succeeded
//            takerPts += mBid;
//            takerPts = (COINCHE_NORMAL == mCoinche) ? takerPts * 2 :
//                    (COINCHE_DOUBLE == mCoinche) ? takerPts * 4 : takerPts;
//        } else {
//            // Deal failed
//            mIsDone = false;
//            takerPts = 0;
//            counterPts = (250 == mBid) ? 500 : 160 + mBid;
//            counterPts = (COINCHE_NORMAL == mCoinche) ? counterPts * 2 :
//                    (COINCHE_DOUBLE == mCoinche) ? counterPts * 4 : counterPts;
//        }

        // Check coinche
        for (CoincheBonus bonus : mBonuses) {
            if (CoincheBonus.BONUS_COINCHE == bonus.get()) {

            } else if (CoincheBonus.BONUS_SURCOINCHE == bonus.get()) {

            }
        }

        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mScorer) ? takerPts : counterPts;
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mScorer) ? takerPts : counterPts;
    }
}
