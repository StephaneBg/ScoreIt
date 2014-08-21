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
import com.sbgapps.scoreit.R;
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

    public BeloteLap(int taker, int points, int belote, List<BeloteBonus> bonuses) {
        super(taker, points, belote);
        mBonuses = bonuses;
    }

    public BeloteLap() {
        this(Player.PLAYER_1, 120, Player.PLAYER_NONE, new ArrayList<BeloteBonus>());
    }

    public List<BeloteBonus> getBonuses() {
        return mBonuses;
    }

    @Override
    public void computeScores() {
        super.computeScores();
        mIsDone = (160 != mPoints);
        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScores[Player.PLAYER_1] += (Player.PLAYER_1 == mBelote) ? 20 : 0;
        mScores[Player.PLAYER_2] += (Player.PLAYER_2 == mBelote) ? 20 : 0;

        // Bonuses
        for(BeloteBonus bonus : mBonuses) {
            switch (bonus.get()) {
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
        computeScores();
    }

    private int getCounterPoints(int points) {
        if (0 == points) {
            return 160;
        } else if (250 == points) {
            return 0;
        } else {
            return 160 - points;
        }
    }
}
