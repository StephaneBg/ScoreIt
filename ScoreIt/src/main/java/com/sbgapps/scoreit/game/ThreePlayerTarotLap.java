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
 * Created by sbaiget on 07/12/13.
 */
public class ThreePlayerTarotLap extends TarotLap {

    public ThreePlayerTarotLap() {
        super();
    }

    public ThreePlayerTarotLap(Cursor cursor) {
        super(cursor);
    }

    @Override
    public void setScores() {
        final int score = computeScore();
        mScore[Lap.PLAYER_1] = (Lap.PLAYER_1 == mTaker) ? 2 * score : -score;
        mScore[Lap.PLAYER_2] = (Lap.PLAYER_2 == mTaker) ? 2 * score : -score;
        mScore[Lap.PLAYER_3] = (Lap.PLAYER_3 == mTaker) ? 2 * score : -score;
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
}
