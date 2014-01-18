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
 * Created by sbaiget on 11/10/13.
 */
public abstract class BeloteLap implements Lap {

    protected long mId;
    protected int mTaker;
    protected int mPoints;
    protected int mBelote;
    protected int mScorePlayer1;
    protected int mScorePlayer2;

    public BeloteLap(Cursor cursor) {
        mId = cursor.getLong(GameSQLiteHelper.COLUMN_IDX_ID);
        mTaker = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_TAKER);
        mPoints = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_POINTS);
        mBelote = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_BELOTE);
        mScorePlayer1 = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_SCORE_1);
        mScorePlayer2 = cursor.getInt(GameSQLiteHelper.BELOTE_COLUMN_IDX_SCORE_2);
    }

    public BeloteLap() {
        mTaker = Lap.PLAYER_1;
        mPoints = 120;
        mBelote = Lap.PLAYER_NONE;
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

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
    }

    public int getBelote() {
        return mBelote;
    }

    public void setBelote(int belote) {
        mBelote = belote;
    }

    @Override
    public int getScore(int player) {
        switch (player) {
            default:
                return 0;
            case PLAYER_1:
                return mScorePlayer1;
            case PLAYER_2:
                return mScorePlayer2;
        }
    }

    @Override
    public ContentValues getValues() {
        ContentValues val = new ContentValues();
        val.put(GameSQLiteHelper.BELOTE_COLUMN_TAKER, mTaker);
        val.put(GameSQLiteHelper.BELOTE_COLUMN_POINTS, mPoints);
        val.put(GameSQLiteHelper.BELOTE_COLUMN_BELOTE, mBelote);
        val.put(GameSQLiteHelper.BELOTE_COLUMN_SCORE_1, mScorePlayer1);
        val.put(GameSQLiteHelper.BELOTE_COLUMN_SCORE_2, mScorePlayer2);
        return val;
    }
}
