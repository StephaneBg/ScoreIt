/*
 * Copyright (c) 2014 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.games;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by sbaiget on 08/01/14.
 */
public class LapActivity extends ActionBarActivity {

    public int mPosition = -1;
    public Lap mLap;
    private GameHelper mGameHelper;

    public LapActivity() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGameHelper = new GameHelper(this);
    }

    public Lap getLap() {
        return mLap;
    }

    public GameHelper getGameHelper() {
        return mGameHelper;
    }
}
