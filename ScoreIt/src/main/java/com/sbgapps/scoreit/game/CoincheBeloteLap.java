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

import android.content.ContentValues;
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
        int takerPts = mPoints;
        int counterPts = getCounterPoints(mPoints);

        if (PLAYER_NONE != mBelote) {
            if (((PLAYER_1 == mTaker) && (PLAYER_1 == mBelote)) ||
                    ((PLAYER_2 == mTaker) && (PLAYER_2 == mBelote))) {
                takerPts += 20;
            } else {
                counterPts += 20;
            }
        }

        if ((mPoints >= mDeal) && (takerPts > counterPts)) {
            // Deal succeeded
            takerPts += mDeal;
            takerPts = (COINCHE_NORMAL == mCoinche) ? takerPts * 2 :
                    (COINCHE_DOUBLE == mCoinche) ? takerPts * 4 : takerPts;
        } else {
            // Deal failed
            takerPts = 0;
            counterPts = (250 == mDeal) ? 500 : 160 + mDeal;
            counterPts = (COINCHE_NORMAL == mCoinche) ? counterPts * 2 :
                    (COINCHE_DOUBLE == mCoinche) ? counterPts * 4 : counterPts;
        }

        mScorePlayer1 = (PLAYER_1 == mTaker) ? takerPts : counterPts;
        mScorePlayer2 = (PLAYER_2 == mTaker) ? takerPts : counterPts;
    }

    @Override
    public int getCounterPoints(int points) {
        return (250 == points) ? 0 : 160 - points;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = super.getValues();
        values.put(GameSQLiteHelper.BELOTE_COLUMN_DEAL, mDeal);
        values.put(GameSQLiteHelper.BELOTE_COLUMN_COINCHE, mCoinche);
        return values;
    }
}
