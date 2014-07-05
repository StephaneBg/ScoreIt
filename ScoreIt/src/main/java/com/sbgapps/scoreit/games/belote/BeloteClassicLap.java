/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit.games.belote;

import com.sbgapps.scoreit.games.Player;

/**
 * Created by sbaiget on 11/11/13.
 */
public class BeloteClassicLap extends BeloteLap {

    public BeloteClassicLap(int taker, int points, int belote) {
        super(taker, points, belote);
        setScores();
    }

    public BeloteClassicLap() {
        this(Player.PLAYER_1, 120, Player.PLAYER_NONE);
    }

    @Override
    public void setScores() {
        super.setScores();
        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScores[Player.PLAYER_1] += (Player.PLAYER_1 == mBelote) ? 20 : 0;
        mScores[Player.PLAYER_2] += (Player.PLAYER_2 == mBelote) ? 20 : 0;
    }

    private int getCounterPoints(int points) {
        return (0 == points) ? 160 :
                (250 == points) ? 0 :
                        160 - points;
    }
}
