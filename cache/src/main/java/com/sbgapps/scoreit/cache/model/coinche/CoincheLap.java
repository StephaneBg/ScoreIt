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
import com.sbgapps.scoreit.cache.model.belote.BeloteBonusCache;

import java.util.List;

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
    protected List<BeloteBonusCache> mBonuses;

    public CoincheLap(int taker, int points, int bidder, int bid, int coinche, List<BeloteBonusCache> bonuses) {
        super(taker, points);
        mBidder = bidder;
        mBid = bid;
        mCoinche = coinche;
        mBonuses = bonuses;
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

    public List<BeloteBonusCache> getBonuses() {
        return mBonuses;
    }
}
