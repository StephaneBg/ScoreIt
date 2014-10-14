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

    protected transient boolean mIsDone = true;
    protected transient int[] mScores;
    @SerializedName("scorer")
    protected int mScorer;
    @SerializedName("points")
    protected int mPoints;

    public GenericBeloteLap(int scorer, int points) {
        mScorer = scorer;
        mPoints = points;
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

    @Override
    public void computeScores() {
        if (null == mScores) {
            mScores = new int[2];
        }
    }

    public int[] getScores() {
        int[] scores = new int[2];
        if (160 == mPoints) {
            scores[0] = 160;
            scores[1] = 0;
        } else if (250 == mPoints) {
            scores[0] = 250;
            scores[1] = 0;
        } else {
            int points = mPoints;
            int mod = points % 10;
            scores[0] = points - mod + ((mod >= 5) ? 10 : 0);
            points = 162 - points;
            mod = points % 10;
            scores[1] = points - mod + ((mod >= 5) ? 10 : 0);
        }
        return scores;
    }
}
