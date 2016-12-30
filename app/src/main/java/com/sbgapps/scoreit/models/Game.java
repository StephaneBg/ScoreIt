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

package com.sbgapps.scoreit.models;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.R;

import java.util.ArrayList;
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

    public Game(Context context, int playerCount) {
        mLaps = new ArrayList<>();
        mPlayers = new ArrayList<>(playerCount);
        initPlayers(context, playerCount);
    }

    List<T> getLaps() {
        return mLaps;
    }

    List<Player> getPlayers() {
        return mPlayers;
    }

    void initScores() {
        for (Lap lap : mLaps) {
            lap.computeScores();
        }
    }

    protected void initPlayers(Context context, int playerCount) {
        Resources r = context.getResources();
        final TypedArray names = r.obtainTypedArray(R.array.player_names);
        final TypedArray colors = r.obtainTypedArray(R.array.player_colors);
        for (int i = 0; i < playerCount; i++)
            mPlayers.add(new Player(names.getString(i), colors.getColor(i, 0)));

        names.recycle();
        colors.recycle();
    }
}
