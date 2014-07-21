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

package com.sbgapps.scoreit.games.belote;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Lap;

/**
 * Created by sbaiget on 11/10/13.
 */
public abstract class GenericBeloteLap implements Lap {

    public static final int[] PROGRESS2POINTS = {80, 90, 100, 110, 120, 130, 140, 150, 160, 250};

    protected transient int[] mScores;
    @SerializedName("taker")
    protected int mTaker;
    @SerializedName("points")
    protected int mPoints;
    @SerializedName("belote")
    protected int mBelote;

    public GenericBeloteLap(int taker, int points, int belote) {
        mTaker = taker;
        mPoints = points;
        mBelote = belote;
    }

    public int getTaker() {
        return mTaker;
    }

    public void setTaker(int taker) {
        mTaker = taker;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getBelote() {
        return mBelote;
    }

    public void setBelote(int belote) {
        mBelote = belote;
    }

    @Override
    public int getScore(int player) {
        return mScores[player];
    }

    @Override
    public void computeScores() {
        if (null == mScores) {
            mScores = new int[2];
        }
    }
}
