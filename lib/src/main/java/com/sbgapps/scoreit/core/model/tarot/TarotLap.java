/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model.tarot;


import com.sbgapps.scoreit.core.model.Lap;

import java.util.ArrayList;
import java.util.List;

public abstract class TarotLap implements Lap {

    public static final int OUDLER_NONE_MSK = 0;
    public static final int OUDLER_PETIT_MSK = 1;
    public static final int OUDLER_EXCUSE_MSK = 2;
    public static final int OUDLER_21_MSK = 4;

    int mTaker;
    int mPoints;
    int mOudlers;
    int[] mScores;
    TarotBid mBid;
    ArrayList<TarotBonus> mBonuses;

    private boolean mIsDone = true;

    protected TarotLap(int taker, TarotBid bid, int points, int oudlers, ArrayList<TarotBonus> bonuses) {
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

    public void setBid(TarotBid bid) {
        mBid = bid;
    }

    public void setBid(int bid) {
        mBid.set(bid);
    }

    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public int getScore(int player, boolean rounded) {
        return mScores[player];
    }

    @Override
    public void computeScores() {
        if (null == mScores) {
            mScores = new int[getPlayerCount()];
        }
    }

    abstract public int getPlayerCount();

    public boolean hasBonus(int bonus) {
        for (TarotBonus tarotBonus : getBonuses()) {
            if (bonus == tarotBonus.get()) return true;
        }
        return false;
    }

    public List<TarotBonus> getBonuses() {
        return mBonuses;
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

    public int getPoigneeBonus() {
        int pts = 0;
        for (TarotBonus bonus : getBonuses()) {
            switch (bonus.get()) {
                case TarotBonus.BONUS_POIGNEE_SIMPLE:
                    pts += 20;
                    break;
                case TarotBonus.BONUS_POIGNEE_DOUBLE:
                    pts += 30;
                    break;
                case TarotBonus.BONUS_POIGNEE_TRIPLE:
                    pts += 40;
                    break;
            }
        }
        return pts;
    }

    public int getPetitBonus() {
        for (TarotBonus bonus : getBonuses()) {
            if (TarotBonus.BONUS_PETIT_AU_BOUT == bonus.get()) {
                int petit = (mTaker == bonus.getPlayer()) ? 10 : -10;
                petit *= getCoefficient();
                return petit;
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

    @Override
    public Object clone() {
        TarotLap lap = null;
        try {
            lap = ((TarotLap) super.clone());
            lap.mBid = new TarotBid(mBid.get());
            lap.mBonuses = ((ArrayList<TarotBonus>) mBonuses.clone());
        } catch (CloneNotSupportedException e) {
        }
        return lap;
    }
}