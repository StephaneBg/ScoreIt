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

package com.sbgapps.scoreit.cache.model.belote;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import static com.sbgapps.scoreit.data.model.ConstantsKt.PLAYER_1;

/**
 * Created by sbaiget on 11/11/13.
 */
public class BeloteLap extends BaseBeloteCoincheLap {

    @SerializedName("bonuses")
    protected List<BeloteBonus> mBonuses;

    public BeloteLap(int scorer, int points, List<BeloteBonus> bonuses) {
        super(scorer, points);
        mBonuses = bonuses;
    }

    public BeloteLap() {
        this(PLAYER_1, 81, new ArrayList<>());
    }

    public List<BeloteBonus> getBonuses() {
        return mBonuses;
    }
}
