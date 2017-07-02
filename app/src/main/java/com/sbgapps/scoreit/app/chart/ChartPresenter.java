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

package com.sbgapps.scoreit.app.chart;

import com.hannesdorfmann.mosby3.mvp.MvpBasePresenter;
import com.sbgapps.scoreit.app.GameManager;
import com.sbgapps.scoreit.app.ScoreItApp;

class ChartPresenter extends MvpBasePresenter<ChartView> {

    final private GameManager mGameManager;

    public ChartPresenter() {
        mGameManager = ScoreItApp.getGameManager();
    }
}
