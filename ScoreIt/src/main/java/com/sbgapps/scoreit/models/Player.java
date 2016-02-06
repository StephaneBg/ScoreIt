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

import com.google.gson.annotations.SerializedName;

/**
 * Created by sbaiget on 30/06/2014.
 */
public class Player {

    public static final int PLAYER_NONE = -1;
    public static final int PLAYER_1 = 0;
    public static final int PLAYER_2 = 1;
    public static final int PLAYER_3 = 2;
    public static final int PLAYER_4 = 3;
    public static final int PLAYER_5 = 4;
    public static final int PLAYER_6 = 5;
    public static final int PLAYER_7 = 6;
    public static final int PLAYER_8 = 7;
    public static final int PLAYER_COUNT = 8;

    @SerializedName("name")
    private String mName;

    public Player(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
