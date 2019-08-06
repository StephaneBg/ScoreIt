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

package com.sbgapps.scoreit.cache.model;

import androidx.annotation.ColorInt;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.data.model.PlayerData;

import org.jetbrains.annotations.NotNull;

/**
 * Created by sbaiget on 30/06/2014.
 */
public class Player {

    @SerializedName("name")
    private String mName;
    @SerializedName("color")
    private int mColor;

    public Player(String name, @ColorInt int color) {
        mName = name;
        mColor = color;
    }

    public Player(PlayerData player) {
        mName = player.getName();
        mColor = player.getColor();
    }

    public PlayerData toPlayerData() {
        return new PlayerData(mName, mColor);
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    @ColorInt
    public int getColor() {
        return mColor;
    }

    public void setColor(@ColorInt int color) {
        mColor = color;
    }

    @NotNull
    @Override
    public String toString() {
        return getName();
    }
}
