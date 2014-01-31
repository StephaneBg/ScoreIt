/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.game;

import android.database.Cursor;

/**
 * Created by sbaiget on 11/11/13.
 */
public class CoincheBeloteLap extends BeloteLap {

    public static final int COINCHE_NONE = 0;
    public static final int COINCHE_NORMAL = 1;
    public static final int COINCHE_DOUBLE = 2;

    private int mDeal;
    private int mCoinche;

    public CoincheBeloteLap(Cursor cursor) {
        super(cursor);
        mDeal = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_DEAL);
        mCoinche = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_COINCHE);
    }

    public CoincheBeloteLap() {
        super();
        mDeal = 120;
        mCoinche = COINCHE_NONE;
    }

    public int getDeal() {
        return mDeal;
    }

    public void setDeal(int deal) {
        mDeal = deal;
    }

    public int getCoinche() {
        return mCoinche;
    }

    public void setCoinche(int coinche) {
        mCoinche = coinche;
    }

    @Override
    public void setScores() {
        mScorePlayer1 = (PLAYER_1 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScorePlayer2 = (PLAYER_2 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScorePlayer1 += (PLAYER_1 == mBelote) ? 20 : 0;
        mScorePlayer2 += (PLAYER_2 == mBelote) ? 20 : 0;
    }
}
