/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.models.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.models.Lap;

import java.util.List;

/**
 * Created by sbaiget on 07/12/13.
 */
public abstract class TarotLap implements Lap {

    public static final int OUDLER_NONE_MSK = 0;
    public static final int OUDLER_PETIT_MSK = 1;
    public static final int OUDLER_EXCUSE_MSK = 2;
    public static final int OUDLER_21_MSK = 4;
    protected transient int[] mScores;
    @SerializedName("taker")
    protected int mTaker;
    @SerializedName("bid")
    protected TarotBid mBid;
    @SerializedName("points")
    protected int mPoints;
    @SerializedName("oudlers")
    protected int mOudlers;
    @SerializedName("bonuses")
    protected List<TarotBonus> mBonuses;
    private transient boolean mIsDone = true;

    protected TarotLap(int taker, TarotBid bid, int points, int oudlers, List<TarotBonus> bonuses) {
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

    public TarotBid getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid.set(bid);
    }

    public void setBid(TarotBid bid) {
        mBid = bid;
    }

    public List<TarotBonus> getBonuses() {
        return mBonuses;
    }

    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public void set(Lap lap) {
        mTaker = ((TarotLap) lap).getTaker();
        mBid = ((TarotLap) lap).getBid();
        mPoints = ((TarotLap) lap).getPoints();
        mOudlers = ((TarotLap) lap).getOudlers();
        mBonuses = ((TarotLap) lap).getBonuses();
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
        mIsDone = (points >= 0);
        points = (25 + Math.abs(points)) * getCoefficient();

        // Poignée
        points += getPoigneeBonus();
        points = (mIsDone ? points : -points);

        // Petit au bout
        points += getPetitBonus();

        // Chelem
        points += getChelemBonus();
        return points;
    }

    public int getPetitBonus() {
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.get()) {
                case TarotBonus.BONUS_PETIT_AU_BOUT:
                    int petit = (mTaker == bonus.getPlayer()) ? 10 : -10;
                    petit *= getCoefficient();
                    return petit;
            }
        }
        return 0;
    }

    public int getPoigneeBonus() {
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.get()) {
                case TarotBonus.BONUS_POIGNEE_SIMPLE:
                    return 20;
                case TarotBonus.BONUS_POIGNEE_DOUBLE:
                    return 30;
                case TarotBonus.BONUS_POIGNEE_TRIPLE:
                    return 40;
            }
        }
        return 0;
    }

    public int getChelemBonus() {
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.get()) {
                case TarotBonus.BONUS_CHELEM_NON_ANNONCE:
                    return 200;
                case TarotBonus.BONUS_CHELEM_ANNONCE_REALISE:
                    return 400;
                case TarotBonus.BONUS_CHELEM_ANNONCE_NON_REALISE:
                    return -200;
            }
        }
        return 0;
    }

    private int getCoefficient() {
        switch (mBid.get()) {
            default:
            case TarotBid.BID_PRISE:
                return 1;
            case TarotBid.BID_GARDE:
                return 2;
            case TarotBid.BID_GARDE_SANS:
                return 4;
            case TarotBid.BID_GARDE_CONTRE:
                return 6;
        }
    }
}
