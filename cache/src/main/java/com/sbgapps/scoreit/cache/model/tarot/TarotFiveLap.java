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

import java.util.ArrayList;
import java.util.List;

import static com.sbgapps.scoreit.data.model.ConstantsKt.OUDLER_NONE;
import static com.sbgapps.scoreit.data.model.ConstantsKt.PLAYER_1;
import static com.sbgapps.scoreit.data.model.ConstantsKt.PLAYER_2;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotFiveLap extends TarotLap {

    @SerializedName("partner")
    private int mPartner;

    public TarotFiveLap() {
        this(PLAYER_1, new TarotBid(), 41, OUDLER_NONE, new ArrayList<>(), PLAYER_2);
    }

    public TarotFiveLap(int taker, TarotBid bid, int points, int oudlers, List<TarotBonus> bonuses, int partner) {
        super(taker, bid, points, oudlers, bonuses);
        mPartner = partner;
    }

    public int getPartner() {
        return mPartner;
    }

    public void setPartner(int partner) {
        mPartner = partner;
    }
}
