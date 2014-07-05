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

package com.sbgapps.scoreit.games.tarot;

import com.google.gson.annotations.SerializedName;
import com.sbgapps.scoreit.games.Player;

/**
 * Created by sbaiget on 07/12/13.
 */
public class TarotFiveLap extends TarotLap {

    @SerializedName("partner")
    private int mPartner;

    public TarotFiveLap(int taker, int deal, int points, int oudlers, int partner) {
        super(taker, deal, points, oudlers);
        mPartner = partner;
        setScores();
    }

    public TarotFiveLap() {
        this(Player.PLAYER_1, DEAL_TAKE, 41, 0, Player.PLAYER_1);
    }

    public int getPartner() {
        return mPartner;
    }

    public void setPartner(int partner) {
        mPartner = partner;
    }

    @Override
    public void setScores() {
        super.setScores();
        final int score = computeScore();
        if (mTaker != mPartner) {
            mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? 2 * score :
                    (Player.PLAYER_1 == mPartner) ? score : -score;
            mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? 2 * score :
                    (Player.PLAYER_2 == mPartner) ? score : -score;
            mScores[Player.PLAYER_3] = (Player.PLAYER_3 == mTaker) ? 2 * score :
                    (Player.PLAYER_3 == mPartner) ? score : -score;
            mScores[Player.PLAYER_4] = (Player.PLAYER_4 == mTaker) ? 2 * score :
                    (Player.PLAYER_4 == mPartner) ? score : -score;
            mScores[Player.PLAYER_5] = (Player.PLAYER_5 == mTaker) ? 2 * score :
                    (Player.PLAYER_5 == mPartner) ? score : -score;
        } else {
            mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_3] = (Player.PLAYER_3 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_4] = (Player.PLAYER_4 == mTaker) ? 4 * score : -score;
            mScores[Player.PLAYER_5] = (Player.PLAYER_5 == mTaker) ? 4 * score : -score;
        }
    }
}
