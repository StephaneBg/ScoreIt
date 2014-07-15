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
import com.sbgapps.scoreit.games.Lap;

import java.util.List;

/**
 * Created by sbaiget on 07/12/13.
 */
public abstract class TarotLap implements Lap {

    public static final int OUDLER_PETIT_MSK = 1;
    public static final int OUDLER_EXCUSE_MSK = 2;
    public static final int OUDLER_21_MSK = 4;
    public static final int BID_PRISE = 0;
    public static final int BID_GARDE = 1;
    public static final int BID_GARDE_SANS = 2;
    public static final int BID_GARDE_CONTRE = 3;

    protected transient int[] mScores;
    @SerializedName("taker")
    protected int mTaker;
    @SerializedName("bid")
    protected int mBid;
    @SerializedName("points")
    protected int mPoints;
    @SerializedName("oudlers")
    protected int mOudlers;
    @SerializedName("announces")
    protected List<TarotBonus> mBonuses;

    protected TarotLap(int taker, int bid, int points, int oudlers, List<TarotBonus> bonuses) {
        mTaker = taker;
        mBid = bid;
        mPoints = points;
        mOudlers = oudlers;
        mBonuses = bonuses;
    }

    public int getTaker() {
        return mTaker;
    }

    public void setTaker(int taker) {
        mTaker = taker;
    }

    public int getOudlers() {
        return mOudlers;
    }

    public void setOudlers(int oudlers) {
        mOudlers = oudlers;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid = bid;
    }

    public List<TarotBonus> getBonuses() {
        return mBonuses;
    }

    @Override
    public int getScore(int player) {
        return mScores[player];
    }

    @Override
    public void computeScores() {
        if (null == mScores) {
            mScores = new int[getPlayerCount()];
        }
    }

    abstract public int getPlayerCount();

    public int getPetitBonus() {
        int petit = 0;
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.getBonus()) {
                case TarotBonus.BONUS_PETIT_AU_BOUT:
                    petit = (mTaker == bonus.getPlayer()) ? 10 : -10;
                    petit *= getCoefficient();
                    break;
            }
        }
        return petit;
    }

    private int getCoefficient() {
        switch (mBid) {
            default:
            case BID_PRISE:
                return 1;
            case BID_GARDE:
                return 2;
            case BID_GARDE_SANS:
                return 4;
            case BID_GARDE_CONTRE:
                return 6;
        }
    }

    public int getResult() {
        int points;

        // Bid
        switch (mOudlers) {
            default:
            case 0:
                // None
                points = mPoints - 56;
                break;
            case OUDLER_21_MSK:
            case OUDLER_PETIT_MSK:
            case OUDLER_EXCUSE_MSK:
                // Only one
                points = mPoints - 51;
                break;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK:
            case OUDLER_21_MSK | OUDLER_EXCUSE_MSK:
            case OUDLER_EXCUSE_MSK | OUDLER_PETIT_MSK:
                // Two
                points = mPoints - 41;
                break;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK | OUDLER_EXCUSE_MSK:
                // Three
                points = mPoints - 36;
                break;
        }
        boolean done = (points > 0);
        points = (25 + Math.abs(points)) * getCoefficient();

        // Poignée
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.getBonus()) {
                case TarotBonus.BONUS_POIGNEE_SIMPLE:
                    points += 20;
                    break;
                case TarotBonus.BONUS_POIGNEE_DOUBLE:
                    points += 30;
                    break;
                case TarotBonus.BONUS_POIGNEE_TRIPLE:
                    points += 40;
                    break;
            }
        }

        points = (done ? points : -points);

        // Petit au bout
        points += getPetitBonus();

        // Chelem
        int chelem = 0;
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.getBonus()) {
                case TarotBonus.BONUS_CHELEM_NON_ANNONCE:
                    chelem = 200;
                    break;
                case TarotBonus.BONUS_CHELEM_ANNONCE_REALISE:
                    chelem = 400;
                    break;
                case TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE:
                    chelem = -200;
                    break;
            }
        }
        points += chelem;
        return points;
    }
}
