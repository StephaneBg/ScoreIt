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
public class ClassicBeloteLap extends BeloteLap {

    public ClassicBeloteLap(Cursor cursor) {
        super(cursor);
    }

    public ClassicBeloteLap() {
        super();
    }

    @Override
    public void setScores() {
        mScorePlayer1 = (PLAYER_1 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScorePlayer2 = (PLAYER_2 == mTaker) ? mPoints : getCounterPoints(mPoints);
        mScorePlayer1 += (PLAYER_1 == mBelote) ? 20 : 0;
        mScorePlayer2 += (PLAYER_2 == mBelote) ? 20 : 0;
    }
}
