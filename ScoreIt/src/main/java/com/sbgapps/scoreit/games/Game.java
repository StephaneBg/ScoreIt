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

package com.sbgapps.scoreit.games;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by sbaiget on 26/06/2014.
 */
public abstract class Game<T extends Lap> {

    public static final int UNIVERSAL = 0;
    public static final int TAROT = 1;
    public static final int BELOTE = 2;
    public static final int COINCHE = 3;

    @SerializedName("laps")
    protected List<T> mLaps;
    @SerializedName("players")
    protected List<Player> mPlayers;

    public List<T> getLaps() {
        return mLaps;
    }

    public List<Player> getPlayers() {
        return mPlayers;
    }

    public void initScores() {
        for (Lap lap : mLaps) {
            lap.computeScores();
        }
    }
}
