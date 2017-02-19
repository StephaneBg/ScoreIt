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

package com.sbgapps.scoreit.core.model.belote;

import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

/**
 * Created by sbaiget on 11/11/13.
 */
public class BeloteLap extends BaseBeloteLap {

    protected ArrayList<BeloteBonus> mBonuses;

    public BeloteLap(int scorer, int points, ArrayList<BeloteBonus> bonuses) {
        super(scorer, points);
        mBonuses = bonuses;
    }

    public BeloteLap() {
        this(Player.PLAYER_1, 81, new ArrayList<BeloteBonus>());
    }

    public ArrayList<BeloteBonus> getBonuses() {
        return mBonuses;
    }

    @Override
    public void computePoints() {
        int[] points = new int[2];
        if (162 == mPoints) {
            points[0] = 162;
            points[1] = 0;
        } else if (250 == mPoints) {
            points[0] = 250;
            points[1] = 0;
        } else {
            points[0] = mPoints;
            points[1] = (162 - mPoints);
        }

        if (Player.PLAYER_1 == getScorer()) {
            mScores[Player.PLAYER_1] = points[0];
            mScores[Player.PLAYER_2] = points[1];
        } else {
            mScores[Player.PLAYER_1] = points[1];
            mScores[Player.PLAYER_2] = points[0];
        }
    }

    @Override
    public void computeScores() {
        computePoints();
        mIsDone = (160 != mPoints) && (162 != mPoints);

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

    public boolean hasBonus(int bonus) {
        for (BeloteBonus beloteBonus : getBonuses()) {
            if (bonus == beloteBonus.get()) return true;
        }
        return false;
    }

    @Override
    public Object clone() {
        BeloteLap lap = null;
        try {
            lap = ((BeloteLap) super.clone());
            lap.mBonuses = ((ArrayList<BeloteBonus>) mBonuses.clone());
        } catch (CloneNotSupportedException e) {
        }
        return lap;
    }
}
