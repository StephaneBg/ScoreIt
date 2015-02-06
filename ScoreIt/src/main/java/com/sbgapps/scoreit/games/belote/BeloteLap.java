/*
 * Copyright (c) 2014 SBG Apps
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

package com.sbgapps.scoreit.games.belote;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 11/11/13.
 */
public class BeloteLap extends GenericBeloteLap {

    @SerializedName("bonuses")
    protected List<BeloteBonus> mBonuses;

    public BeloteLap(int scorer, int points, List<BeloteBonus> bonuses) {
        super(scorer, points);
        mBonuses = bonuses;
    }

    public BeloteLap() {
        this(Player.PLAYER_1, 81, new ArrayList<BeloteBonus>());
    }

    public List<BeloteBonus> getBonuses() {
        return mBonuses;
    }

    @Override
    public void computeScores() {
        mIsDone = (160 != mPoints);

        computePoints();

        // Bonuses
        for (BeloteBonus bonus : mBonuses) {
            switch (bonus.get()) {
                case BeloteBonus.BONUS_BELOTE:
                    mScores[bonus.getPlayer()] += 20;
                    break;
                case BeloteBonus.BONUS_RUN_3:
                    mScores[bonus.getPlayer()] += 20;
                    break;
                case BeloteBonus.BONUS_RUN_4:
                    mScores[bonus.getPlayer()] += 50;
                    break;
                case BeloteBonus.BONUS_RUN_5:
                case BeloteBonus.BONUS_FOUR_NORMAL:
                    mScores[bonus.getPlayer()] += 100;
                    break;
                case BeloteBonus.BONUS_FOUR_NINE:
                    mScores[bonus.getPlayer()] += 150;
                    break;
                case BeloteBonus.BONUS_FOUR_JACK:
                    mScores[bonus.getPlayer()] += 200;
                    break;
            }
        }
    }

    @Override
    public void set(Lap lap) {
        super.set(lap);
        mBonuses = ((BeloteLap) lap).getBonuses();
        computeScores();
    }

    @Override
    public Lap copy() {
        return new BeloteLap(mScorer, mPoints, mBonuses);
    }
}
