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
import com.sbgapps.scoreit.games.Player;

/**
 * Created by sbaiget on 11/10/13.
 */
public abstract class GenericBeloteLap implements Lap {

    protected transient boolean mIsDone;
    protected transient int[] mScores;
    @SerializedName("scorer")
    protected int mScorer;
    @SerializedName("points")
    protected int mPoints;

    public GenericBeloteLap(int scorer, int points) {
        mScorer = scorer;
        mPoints = points;
        mScores = new int[2];
    }

    public int getScorer() {
        return mScorer;
    }

    public void setScorer(int scorer) {
        mScorer = scorer;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public int getScore(int player) {
        return mScores[player];
    }

    @Override
    public void set(Lap lap) {
        mScorer = ((GenericBeloteLap) lap).getScorer();
        mPoints = ((GenericBeloteLap) lap).getPoints();
    }

    public void computePoints() {
        int[] points = new int[2];
        if (160 == mPoints) {
            points[0] = 160;
            points[1] = 0;
        } else if (250 == mPoints) {
            points[0] = 250;
            points[1] = 0;
        } else {
            points[0] = ((mPoints + 5) / 10) * 10;
            points[1] = (((162 - mPoints) + 5) / 10) * 10;
        }

        if (Player.PLAYER_1 == getScorer()) {
            mScores[Player.PLAYER_1] = points[0];
            mScores[Player.PLAYER_2] = points[1];
        } else {
            mScores[Player.PLAYER_1] = points[1];
            mScores[Player.PLAYER_2] = points[0];
        }
    }
}
