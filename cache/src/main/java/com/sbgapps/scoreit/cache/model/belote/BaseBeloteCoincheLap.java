/*
 * Copyright 2019 Stéphane Baiget
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

package com.sbgapps.scoreit.cache.model.belote;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.cache.model.Lap;

/**
 * Created by sbaiget on 11/10/13.
 */
public abstract class BaseBeloteCoincheLap implements Lap {

    @SerializedName("scorer")
    protected int mScorer;
    @SerializedName("points")
    protected int mPoints;

    public BaseBeloteCoincheLap(int scorer, int points) {
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
}
