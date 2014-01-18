/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.game;

import android.content.ContentValues;

import java.io.Serializable;

/**
 * Created by sbaiget on 11/11/13.
 */
public interface Lap extends Serializable {

    int PLAYER_NONE = -1;
    int PLAYER_1 = 0;
    int PLAYER_2 = 1;
    int PLAYER_3 = 2;
    int PLAYER_4 = 3;
    int PLAYER_5 = 4;
    int PLAYER_COUNT_MAX = 5;

    public void setId(long id);

    public long getId();

    public void setScores();

    public int getScore(int player);

    public ContentValues getValues();
}
