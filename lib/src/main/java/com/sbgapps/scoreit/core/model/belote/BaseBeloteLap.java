/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.core.model.belote;


import com.sbgapps.scoreit.core.model.Lap;
import com.sbgapps.scoreit.core.model.utils.GameHelper;

/**
 * Created by sbaiget on 11/10/13.
 */
public abstract class BaseBeloteLap implements Lap {

    protected boolean mIsDone;
    protected int[] mScores;
    protected int mScorer;
    protected int mPoints;

    public BaseBeloteLap(int scorer, int points) {
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
        return GameHelper.isRounded() ? getRoundedScore(mScores[player]) : mScores[player];
    }

    abstract public void computePoints();

    private int getRoundedScore(int score) {
        if (162 == score) {
            return 160;
        } else if (250 == score) {
            return score;
        } else {
            return ((score + 5) / 10) * 10;
        }
    }
}
