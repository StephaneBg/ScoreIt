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

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Window;
import android.view.WindowManager;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.utils.TypefaceSpan;

/**
 * Created by Stéphane on 22/07/2014.
 */
public class BaseActivity extends ActionBarActivity {

    protected GameHelper mGameHelper;
    private TypefaceSpan mTypefaceSpan;
    private SpannableString mTitle;

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();

        mTypefaceSpan = new TypefaceSpan(this, "Lobster.otf");
    }

    public void setTitle() {
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                mTitle = new SpannableString(getResources().getString(R.string.universal));
                break;
            case Game.BELOTE:
                mTitle = new SpannableString(getResources().getString(R.string.belote));
                break;
            case Game.COINCHE:
                mTitle = new SpannableString(getResources().getString(R.string.coinche));
                break;
            case Game.TAROT:
                mTitle = new SpannableString(getResources().getString(R.string.tarot));
                break;
        }
        mTitle.setSpan(mTypefaceSpan, 0, mTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(mTitle);
    }


    public void setAccentDecor() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            win.setAttributes(winParams);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(getResources().getColor(android.R.color.black));
            tintManager.setStatusBarAlpha(0.6f);
        }
    }
}
