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

package com.sbgapps.scoreit.cache.model.coinche;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.cache.model.belote.BaseBeloteCoincheLap;

import java.util.ArrayList;
import java.util.List;

import static com.sbgapps.scoreit.data.model.ConstantsKt.COINCHE_NONE;
import static com.sbgapps.scoreit.data.model.ConstantsKt.PLAYER_1;

/**
 * Created by sbaiget on 11/11/13.
 */
public class CoincheLap extends BaseBeloteCoincheLap {

    @SerializedName("bidder")
    protected int mBidder;
    @SerializedName("bid")
    protected int mBid;
    @SerializedName("coinche")
    protected int mCoinche;
    @SerializedName("bonuses")
    protected List<CoincheBonus> mBonuses;

    public CoincheLap(int taker, int points, int bidder, int bid, int coinche, List<CoincheBonus> bonuses) {
        super(taker, points);
        mBidder = bidder;
        mBid = bid;
        mCoinche = coinche;
        mBonuses = bonuses;
    }

    public CoincheLap() {
        this(PLAYER_1, 100, PLAYER_1, 140, COINCHE_NONE, new ArrayList<>());
    }

    public int getBidder() {
        return mBidder;
    }

    public void setBidder(int bidder) {
        mBidder = bidder;
    }

    public int getBid() {
        return mBid;
    }

    public void setBid(int bid) {
        mBid = bid;
    }

    public int getCoinche() {
        return mCoinche;
    }

    public void setCoinche(int coinche) {
        mCoinche = coinche;
    }

    public List<CoincheBonus> getBonuses() {
        return mBonuses;
    }
}
