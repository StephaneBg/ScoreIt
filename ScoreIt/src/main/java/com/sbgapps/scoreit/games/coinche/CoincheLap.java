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
        CoincheBonus bonus = getBonus(CoincheBonus.BONUS_BELOTE);
        if (null != bonus) {
            if (((Player.PLAYER_1 == mScorer) && (Player.PLAYER_1 == bonus.getPlayer())) ||
                    ((Player.PLAYER_2 == mScorer) && (Player.PLAYER_2 == bonus.getPlayer()))) {
                takerPts += 20;
            } else {
                counterPts += 20;
            }
        }

        // Compute scores
        bonus = getBonus(CoincheBonus.BONUS_COINCHE);
        if (null == bonus) bonus = getBonus(CoincheBonus.BONUS_SURCOINCHE);

        if ((mPoints >= mBid) && (takerPts > counterPts)) {
            // Deal succeeded
            takerPts += mBid;
            if (null != bonus) {
                if (CoincheBonus.BONUS_COINCHE == bonus.get()) {
                    takerPts *= 2;
                } else {
                    takerPts *= 4;
                }
            }
        } else {
            // Deal failed
            mIsDone = false;
            takerPts = 0;
            counterPts = (250 == mBid) ? 500 : 160 + mBid;
            if (null != bonus) {
                if (CoincheBonus.BONUS_COINCHE == bonus.get()) {
                    counterPts *= 2;
                } else {
                    counterPts *= 4;
                }
            }
        }

        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mScorer) ? takerPts : counterPts;
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mScorer) ? takerPts : counterPts;
    }

    private CoincheBonus getBonus(int bonus) {
        for (CoincheBonus b : mBonuses) {
            if (bonus == b.get()) {
                return b;
            }
        }
        return null;
    }
}
