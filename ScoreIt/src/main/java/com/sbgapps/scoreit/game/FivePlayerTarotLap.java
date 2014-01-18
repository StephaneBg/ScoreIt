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
public class FivePlayerTarotLap extends TarotLap {

    private int mPartner;

    public FivePlayerTarotLap() {
        super();
        mPartner = Lap.PLAYER_2;
    }

    public FivePlayerTarotLap(Cursor cursor) {
        super(cursor);
        mScore[Lap.PLAYER_4] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_4);
        mScore[Lap.PLAYER_5] = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_SCORE_5);
        mPartner = cursor.getInt(GameSQLiteHelper.TAROT_COLUMN_IDX_PARTNER);
    }

    public int getPartner() {
        return mPartner;
    }

    public void setPartner(int partner) {
        mPartner = partner;
    }

    @Override
    public void setScores() {
        final int score = computeScore();
        if (mTaker != mPartner) {
            mScore[Lap.PLAYER_1] = (Lap.PLAYER_1 == mTaker) ? 2 * score :
                    (Lap.PLAYER_1 == mPartner) ? score : -score;
            mScore[Lap.PLAYER_2] = (Lap.PLAYER_2 == mTaker) ? 2 * score :
                    (Lap.PLAYER_2 == mPartner) ? score : -score;
            mScore[Lap.PLAYER_3] = (Lap.PLAYER_3 == mTaker) ? 2 * score :
                    (Lap.PLAYER_3 == mPartner) ? score : -score;
            mScore[Lap.PLAYER_4] = (Lap.PLAYER_4 == mTaker) ? 2 * score :
                    (Lap.PLAYER_4 == mPartner) ? score : -score;
            mScore[Lap.PLAYER_5] = (Lap.PLAYER_5 == mTaker) ? 2 * score :
                    (Lap.PLAYER_5 == mPartner) ? score : -score;
        } else {
            mScore[Lap.PLAYER_1] = (Lap.PLAYER_1 == mTaker) ? 4 * score : -score;
            mScore[Lap.PLAYER_2] = (Lap.PLAYER_2 == mTaker) ? 4 * score : -score;
            mScore[Lap.PLAYER_3] = (Lap.PLAYER_3 == mTaker) ? 4 * score : -score;
            mScore[Lap.PLAYER_4] = (Lap.PLAYER_4 == mTaker) ? 4 * score : -score;
            mScore[Lap.PLAYER_5] = (Lap.PLAYER_5 == mTaker) ? 4 * score : -score;
        }
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
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_4, mScore[Lap.PLAYER_4]);
        values.put(GameSQLiteHelper.TAROT_COLUMN_SCORE_5, mScore[Lap.PLAYER_5]);
        values.put(GameSQLiteHelper.TAROT_COLUMN_PARTNER, mPartner);
        return values;
    }
}
