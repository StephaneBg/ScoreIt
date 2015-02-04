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
    public static final int COINCHE_COINCHE = 1;
    public static final int COINCHE_SURCOINCHE = 2;

    @SerializedName("bidder")
    protected int mBidder;
    @SerializedName("bid")
    protected int mBid;
    @SerializedName("coinche")
    protected int mCoinche;
    @SerializedName("bonuses")
    protected List<CoincheBonus> mBonuses;

    public CoincheLap(int taker, int points, int bidder,
                      int bid, int coinche, List<CoincheBonus> bonuses) {
        super(taker, points);
        mBidder = bidder;
        mBid = bid;
        mCoinche = coinche;
        mBonuses = bonuses;
    }

    public CoincheLap() {
        this(Player.PLAYER_1, 100, Player.PLAYER_1,
                100, COINCHE_NONE, new ArrayList<CoincheBonus>());
    }

    public int getBidder() {
        return mBidder;
    }

    public void setBidder(int bidder) {
        mBidder = bidder;
    }

    public int getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid = bid;
    }

    public int getCoinche() {
        return mCoinche;
    }

    public void setCoinche(int coinche) {
        mCoinche = coinche;
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

        int bidderPts;
        int counterPts;

        if (Player.PLAYER_1 == mBidder) {
            bidderPts = getTeamPoints(Player.PLAYER_1);
            counterPts = getTeamPoints(Player.PLAYER_2);
        } else {
            bidderPts = getTeamPoints(Player.PLAYER_2);
            counterPts = getTeamPoints(Player.PLAYER_1);
        }

        if ((bidderPts >= mBid) && (bidderPts > counterPts)) {
            // Deal succeeded
            mIsDone = true;
            bidderPts += mBid;
            switch (mCoinche) {
                case COINCHE_COINCHE:
                    bidderPts *= 2;
                    break;
                case COINCHE_SURCOINCHE:
                    bidderPts *= 4;
                    break;
            }
        } else {
            // Deal failed
            mIsDone = false;
            bidderPts = 0;
            counterPts = (250 == mBid) ? 500 : (160 + mBid);
            switch (mCoinche) {
                case COINCHE_COINCHE:
                    counterPts *= 2;
                    break;
                case COINCHE_SURCOINCHE:
                    counterPts *= 4;
                    break;
            }
        }

        if (Player.PLAYER_1 == mBidder) {
            mScores[Player.PLAYER_1] = bidderPts;
            mScores[Player.PLAYER_2] = counterPts;
        } else {
            mScores[Player.PLAYER_1] = counterPts;
            mScores[Player.PLAYER_2] = bidderPts;
        }
    }

    @Override
    public Lap copy() {
        return new CoincheLap(mScorer, mPoints, mBidder, mBid, mCoinche, mBonuses);
    }

    private int getTeamPoints(int player) {
        // Points
        int points = getScores()[player];
        // Bonuses
        for (CoincheBonus bonus : mBonuses) {
            if (player == bonus.getPlayer()) {
                switch (bonus.get()) {
                    case CoincheBonus.BONUS_BELOTE:
                    case CoincheBonus.BONUS_RUN_3:
                        points += 20;
                        break;
                    case CoincheBonus.BONUS_RUN_4:
                        points += 50;
                        break;
                    case CoincheBonus.BONUS_RUN_5:
                    case CoincheBonus.BONUS_FOUR_NORMAL:
                        points += 100;
                        break;
                    case CoincheBonus.BONUS_FOUR_NINE:
                        points += 150;
                        break;
                    case CoincheBonus.BONUS_FOUR_JACK:
                        points += 200;
                        break;
                }
            }
        }
        return points;
    }
}
