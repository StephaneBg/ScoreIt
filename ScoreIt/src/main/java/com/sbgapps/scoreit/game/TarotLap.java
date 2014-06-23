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

package com.sbgapps.scoreit.game;

import android.content.ContentValues;
import android.database.Cursor;

/**
 * Created by sbaiget on 07/12/13.
 */
public abstract class TarotLap implements Lap {

    public static final int OUDLER_PETIT_MSK = 1;
    public static final int OUDLER_FOOL_MSK = 2;
    public static final int OUDLER_21_MSK = 4;
    public static final int DEAL_TAKE = 0;
    public static final int DEAL_GUARD = 1;
    public static final int DEAL_WITHOUT = 2;
    public static final int DEAL_AGAINST = 3;
    protected final int[] mScore = new int[Lap.PLAYER_COUNT_MAX];
    protected long mId;
    protected int mTaker;
    protected int mDeal;
    protected int mPoints;
    protected int mOudlers;

    public TarotLap() {
        mTaker = Lap.PLAYER_1;
        mDeal = DEAL_TAKE;
        mPoints = 41;
        mOudlers = 0;
    }

    public TarotLap(Cursor cursor) {
        mId = cursor.getLong(GameSQLiteHelper.COLUMN_IDX_ID);
        mTaker = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_TAKER);
        mDeal = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_DEAL);
        mPoints = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_POINTS);
        mOudlers = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_OUDLER);
        mScore[Lap.PLAYER_1] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_1);
        mScore[Lap.PLAYER_2] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_2);
        mScore[Lap.PLAYER_3] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_3);
    }

    @Override
    public long getId() {
        return mId;
    }

    @Override
    public void setId(long id) {
        mId = id;
    }

    public int getTaker() {
        return mTaker;
    }

    public void setTaker(int taker) {
        mTaker = taker;
    }

    public int getOudlers() {
        return mOudlers;
    }

    public void setOudlers(int oudlers) {
        mOudlers = oudlers;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getDeal() {
        return mDeal;
    }

    public void setDeal(int deal) {
        mDeal = deal;
    }

    @Override
    public int getScore(int player) {
        return mScore[player];
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = new ContentValues();
        values.put(GameSQLiteHelper.TAROT_COLUMN_TAKER, mTaker);
        values.put(GameSQLiteHelper.TAROT_COLUMN_DEAL, mDeal);
        values.put(GameSQLiteHelper.TAROT_COLUMN_POINTS, mPoints);
        values.put(GameSQLiteHelper.TAROT_COLUMN_OUDLER, mOudlers);
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_1, mScore[Lap.PLAYER_1]);
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_2, mScore[Lap.PLAYER_2]);
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_3, mScore[Lap.PLAYER_3]);
        return values;
    }

    public int computeScore() {
        int result = getDelta();
        result += (result < 0) ? -25 : 25;
        return (result * getCoefficient());
    }

    private int getCoefficient() {
        switch (mDeal) {
            default:
            case DEAL_TAKE:
                return 1;
            case DEAL_GUARD:
                return 2;
            case DEAL_WITHOUT:
                return 4;
            case DEAL_AGAINST:
                return 6;
        }
    }

    private int getDelta() {
        switch (mOudlers) {
            default:
            case 0:
                // None
                return mPoints - 56;
            case OUDLER_21_MSK:
            case OUDLER_PETIT_MSK:
            case OUDLER_FOOL_MSK:
                // Only one
                return mPoints - 51;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK:
            case OUDLER_21_MSK | OUDLER_FOOL_MSK:
            case OUDLER_FOOL_MSK | OUDLER_PETIT_MSK:
                // Two
                return mPoints - 41;
            case OUDLER_21_MSK | OUDLER_PETIT_MSK | OUDLER_FOOL_MSK:
                // Three
                return mPoints - 36;
        }
    }
}
