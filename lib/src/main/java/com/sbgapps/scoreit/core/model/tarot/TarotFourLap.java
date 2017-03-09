/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.core.model.tarot;


import com.sbgapps.scoreit.core.model.Player;

import java.util.ArrayList;

public class TarotFourLap extends TarotLap {

    public TarotFourLap() {
        this(Player.PLAYER_1, new TarotBid(), 41, OUDLER_NONE_MSK, new ArrayList<TarotBonus>());
    }

    public TarotFourLap(int taker, TarotBid bid, int points, int oudlers, ArrayList<TarotBonus> bonuses) {
        super(taker, bid, points, oudlers, bonuses);
    }

    @Override
    public void computeScores() {
        super.computeScores();
        final int result = getResult();
        mScores[Player.PLAYER_1] = (Player.PLAYER_1 == mTaker) ? 3 * result : -result;
        mScores[Player.PLAYER_2] = (Player.PLAYER_2 == mTaker) ? 3 * result : -result;
        mScores[Player.PLAYER_3] = (Player.PLAYER_3 == mTaker) ? 3 * result : -result;
        mScores[Player.PLAYER_4] = (Player.PLAYER_4 == mTaker) ? 3 * result : -result;
    }

    @Override
    public int getPlayerCount() {
        return TarotFourGame.NB_PLAYERS;
    }
}
