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

package com.sbgapps.scoreit.models.tarot;

import android.content.Context;
import android.support.annotation.IntDef;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.R;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Stéphane on 29/07/2014.
 */
public class TarotBid implements Serializable {

    public static final int BID_PRISE = 0;
    public static final int BID_GARDE = 1;
    public static final int BID_GARDE_SANS = 2;
    public static final int BID_GARDE_CONTRE = 3;

    @TarotBidValues
    @SerializedName("bid")
    private int mBid;

    public TarotBid() {
        this(BID_PRISE);
    }

    public TarotBid(@TarotBidValues int bid) {
        mBid = bid;
    }

    public static String getLiteralBid(Context context, @TarotBidValues int bid) {
        switch (bid) {
            case BID_PRISE:
                return context.getString(R.string.take);
            case BID_GARDE:
                return context.getString(R.string.guard);
            case BID_GARDE_CONTRE:
                return context.getString(R.string.guard_against);
            case BID_GARDE_SANS:
                return context.getString(R.string.guard_without);
        }
        return null;
    }

    @TarotBidValues
    public int get() {
        return mBid;
    }

    public void set(@TarotBidValues int bid) {
        mBid = bid;
    }

    @IntDef({BID_PRISE, BID_GARDE, BID_GARDE_SANS, BID_GARDE_CONTRE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TarotBidValues {
    }
}
