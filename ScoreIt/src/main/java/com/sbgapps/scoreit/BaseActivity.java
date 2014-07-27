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

package com.sbgapps.scoreit;

import android.support.v7.app.ActionBarActivity;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sbgapps.scoreit.games.Game;

/**
 * Created by Stéphane on 22/07/2014.
 */
public class BaseActivity extends ActionBarActivity {

    private CharSequence mTitle;

    @Override
    public void setTitle(int position) {
        switch (position) {
            default:
                mTitle = getTitle();
                break;
            case Game.UNIVERSAL:
                mTitle = getResources().getString(R.string.universal);
                break;
            case Game.BELOTE:
                mTitle = getResources().getString(R.string.belote);
                break;
            case Game.COINCHE:
                mTitle = getResources().getString(R.string.coinche);
                break;
            case Game.TAROT:
                mTitle = getResources().getString(R.string.tarot);
                break;
        }
        getSupportActionBar().setTitle(mTitle);
    }


    public void setAccentDecor() {
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        tintManager.setStatusBarTintEnabled(true);
        tintManager.setStatusBarTintResource(R.drawable.background_status_bar);
    }
}
