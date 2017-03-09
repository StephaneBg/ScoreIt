/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model;

import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

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
    public static final int PLAYER_TOTAL = 8;

    private String mName;
    @ColorInt
    private int mColor;

    public Player(String name, @ColorInt int color) {
        mName = name;
        mColor = color;
    }

    @ColorInt
    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public String toString() {
        return getName();
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @IntDef({PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4, PLAYER_5,
            PLAYER_6, PLAYER_7, PLAYER_8, PLAYER_TOTAL, PLAYER_NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Players {
    }
}
