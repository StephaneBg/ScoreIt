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
        this(Player.PLAYER_1, 100, 100, new ArrayList<CoincheBonus>());
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

        final int scores[] = getScores();
        int takerPts = scores[Player.PLAYER_1];
        int counterPts = scores[Player.PLAYER_2];

        // Check belote
        CoincheBonus cb = getBonus(CoincheBonus.BONUS_BELOTE);
        if (null != cb && Player.PLAYER_NONE != cb.getPlayer()) {
            if (((Player.PLAYER_1 == mScorer) && (Player.PLAYER_1 == cb.getPlayer())) ||
                    ((Player.PLAYER_2 == mScorer) && (Player.PLAYER_2 == cb.getPlayer()))) {
                takerPts += 20;
            } else {
                counterPts += 20;
            }
        }

        // Compute scores
        cb = getBonus(CoincheBonus.BONUS_COINCHE);
        if (null == cb) cb = getBonus(CoincheBonus.BONUS_SURCOINCHE);

        if ((takerPts >= mBid) && (takerPts > counterPts)) {
            // Deal succeeded
            takerPts += mBid;
            if (null != cb) {
                if (CoincheBonus.BONUS_COINCHE == cb.get()) {
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
            if (null != cb) {
                if (CoincheBonus.BONUS_COINCHE == cb.get()) {
                    counterPts *= 2;
                } else {
                    counterPts *= 4;
                }
            }
        }

        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mScorer) ? takerPts : counterPts;
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mScorer) ? takerPts : counterPts;
    }

    public CoincheBonus getBonus(int bonus) {
        for (CoincheBonus b : mBonuses) {
            if (bonus == b.get()) {
                return b;
            }
        }
        return null;
    }
}
