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
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteLap;

/**
 * Created by sbaiget on 11/11/13.
 */
public class CoincheLap extends BeloteLap {

    public static final int COINCHE_NONE = 0;
    public static final int COINCHE_NORMAL = 1;
    public static final int COINCHE_DOUBLE = 2;

    @SerializedName("bid")
    private int mDeal;
    @SerializedName("coinche")
    private int mCoinche;

    public CoincheLap(int taker, int points, int belote, int deal, int coinche) {
        super(taker, points, belote);
        mDeal = deal;
        mCoinche = coinche;
        computeScores();
    }

    public CoincheLap() {
        this(Player.PLAYER_1, 120, Player.PLAYER_NONE, 120, COINCHE_NONE);
    }

    public int getBid() {
        return mDeal;
    }

    public void setBid(int deal) {
        mDeal = deal;
    }

    public int getCoinche() {
        return mCoinche;
    }

    public void setCoinche(int coinche) {
        mCoinche = coinche;
    }

    @Override
    public void computeScores() {
        super.computeScores();

        int takerPts = mPoints;
        int counterPts = getCounterPoints(mPoints);

        if (Player.PLAYER_NONE != mBelote) {
            if (((Player.PLAYER_1 == mTaker) && (Player.PLAYER_1 == mBelote)) ||
                    ((Player.PLAYER_2 == mTaker) && (Player.PLAYER_2 == mBelote))) {
                takerPts += 20;
            } else {
                counterPts += 20;
            }
        }

        if ((mPoints >= mDeal) && (takerPts > counterPts)) {
            // Deal succeeded
            takerPts += mDeal;
            takerPts = (COINCHE_NORMAL == mCoinche) ? takerPts * 2 :
                    (COINCHE_DOUBLE == mCoinche) ? takerPts * 4 : takerPts;
        } else {
            // Deal failed
            takerPts = 0;
            counterPts = (250 == mDeal) ? 500 : 160 + mDeal;
            counterPts = (COINCHE_NORMAL == mCoinche) ? counterPts * 2 :
                    (COINCHE_DOUBLE == mCoinche) ? counterPts * 4 : counterPts;
        }

        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? takerPts : counterPts;
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? takerPts : counterPts;
    }

    private int getCounterPoints(int points) {
        return (250 == points) ? 0 : 160 - points;
    }
}
