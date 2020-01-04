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

import java.io.Serializable;

/**
 * Created by Stéphane on 29/07/2014.
 */
public class TarotBidCache implements Serializable {

    @SerializedName("bid")
    private int mBid;

    public TarotBidCache(int bid) {
        mBid = bid;
    }

    public int get() {
        return mBid;
    }

    public void set(int bid) {
        mBid = bid;
    }
}
