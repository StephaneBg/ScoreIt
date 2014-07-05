/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit.games.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Lap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 07/12/13.
 */
public abstract class TarotLap implements Lap {

    public static final int OUDLER_PETIT_MSK = 1;
    public static final int OUDLER_FOOL_MSK = 2;
    public static final int OUDLER_21_MSK = 4;
    public static final int DEAL_TAKE = 0;
    public static final int DEAL_GUARD = 1;
    public static final int DEAL_WITHOUT = 2;
    public static final int DEAL_AGAINST = 3;
    protected transient int[] mScores;
    @SerializedName("taker")
    protected int mTaker;
    @SerializedName("deal")
    protected int mDeal;
    @SerializedName("points")
    protected int mPoints;
    @SerializedName("oudlers")
    protected int mOudlers;
    @SerializedName("announces")
    protected List<TarotAnnounce> mAnnounces;

    protected TarotLap(int taker, int deal, int points, int oudlers) {
        mTaker = taker;
        mDeal = deal;
        mPoints = points;
        mOudlers = oudlers;
        mAnnounces = new ArrayList<>();
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

    public int getDeal() {
        return mDeal;
    }

    public void setDeal(int deal) {
        mDeal = deal;
    }

    public List<TarotAnnounce> getAnnounces() {
        return mAnnounces;
    }

    @Override
    public int getScore(int player) {
        return mScores[player];
    }

    @Override
    public void setScores() {
        if (null == mScores) {
            mScores = new int[5];
        }
    }

    public int computeScore() {
        int result = getDelta();
        result += (result < 0) ? -25 : 25;
        return (result * getCoefficient());
    }

    private int getCoefficient() {
        switch (mDeal) {
            default:
            case DEAL_TAKE:
                return 1;
            case DEAL_GUARD:
                return 2;
            case DEAL_AGAINST:
                return 4;
            case DEAL_WITHOUT:
                return 6;
        }
    }

    private int getDelta() {
        switch (mOudlers) {
            default:
            case 0:
                // None
                return mPoints - 56;
            case OUDLER_21_MSK:
            case OUDLER_PETIT_MSK:
            case OUDLER_FOOL_MSK:
                // Only one
                return mPoints - 51;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK:
            case OUDLER_21_MSK | OUDLER_FOOL_MSK:
            case OUDLER_FOOL_MSK | OUDLER_PETIT_MSK:
                // Two
                return mPoints - 41;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK | OUDLER_FOOL_MSK:
                // Three
                return mPoints - 36;
        }
    }
}
