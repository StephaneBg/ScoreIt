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

package com.sbgapps.scoreit.models.belote;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.models.Lap;

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

    abstract public void computePoints();
}
