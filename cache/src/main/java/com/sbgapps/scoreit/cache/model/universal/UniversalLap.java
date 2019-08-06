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

package com.sbgapps.scoreit.cache.model.universal;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.cache.model.Lap;

/**
 * Created by sbaiget on 06/02/14.
 */
public class UniversalLap implements Lap {

    @SerializedName("scores")
    private final int[] mScores;

    public UniversalLap(int playerCount) {
        mScores = new int[playerCount];
    }

    public UniversalLap(int[] scores) {
        mScores = scores;
    }

    public void setScore(int player, int score) {
        mScores[player] = score;
    }

    public int[] getScores() {
        return mScores;
    }
}
