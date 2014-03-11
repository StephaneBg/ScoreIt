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
public class FourPlayerTarotLap extends TarotLap {

    public FourPlayerTarotLap() {
        super();
    }

    public FourPlayerTarotLap(Cursor cursor) {
        super(cursor);
        mScore[Lap.PLAYER_4] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_4);
    }

    @Override
    public void setScores() {
        final int score = computeScore();
        mScore[Lap.PLAYER_1] = (Lap.PLAYER_1 == mTaker) ? 3 * score : -score;
        mScore[Lap.PLAYER_2] = (Lap.PLAYER_2 == mTaker) ? 3 * score : -score;
        mScore[Lap.PLAYER_3] = (Lap.PLAYER_3 == mTaker) ? 3 * score : -score;
        mScore[Lap.PLAYER_4] = (Lap.PLAYER_4 == mTaker) ? 3 * score : -score;
    }

    @Override
    public ContentValues getValues() {
        ContentValues values = super.getValues();
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_4, mScore[Lap.PLAYER_4]);
        return values;
    }
}
