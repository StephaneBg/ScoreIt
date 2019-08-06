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

package com.sbgapps.scoreit.cache.model.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.cache.model.Lap;

import java.util.List;

/**
 * Created by sbaiget on 07/12/13.
 */
public abstract class TarotLap implements Lap {

    @SerializedName("taker")
    protected int mTaker;
    @SerializedName("bid")
    protected TarotBid mBid;
    @SerializedName("points")
    protected int mPoints;
    @SerializedName("oudlers")
    protected int mOudlers;
    @SerializedName("bonuses")
    protected List<TarotBonus> mBonuses;

    protected TarotLap(int taker, TarotBid bid, int points, int oudlers, List<TarotBonus> bonuses) {
        mTaker = taker;
        mBid = bid;
        mPoints = points;
        mOudlers = oudlers;
        mBonuses = bonuses;
    }

    public int getTaker() {
        return mTaker;
    }

    public void setTaker(int taker) {
        mTaker = taker;
    }

    public int getOudlers() {
        return mOudlers;
    }

    public void setOudlers(int oudlers) {
        mOudlers = oudlers;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public TarotBid getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid.set(bid);
    }

    public void setBid(TarotBid bid) {
        mBid = bid;
    }

    public List<TarotBonus> getBonuses() {
        return mBonuses;
    }
}
