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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import com.sbgapps.scoreit.BaseActivity;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;

/**
 * Created by sbaiget on 08/01/14.
 */
public class LapActivity extends BaseActivity {

    public int mPosition = -1;
    public Lap mLap;
    private GameHelper mGameHelper;
    private boolean mIsDialog = false;

    public LapActivity() {
    }

    public boolean isDialog() {
        return mIsDialog;
    }

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();
        setAccentDecor();

        if (null != savedInstanceState) {
            mLap = (Lap) savedInstanceState.getSerializable(ScoreItActivity.EXTRA_LAP);
        }

        Intent intent = getIntent();
        if (null != intent) {
            mPosition = intent.getIntExtra(ScoreItActivity.EXTRA_POSITION, -1);
        }

        setupFauxDialog();
        setTitle(mGameHelper.getPlayedGame());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ScoreItActivity.EXTRA_LAP, mLap);
    }

    public Lap getLap() {
        return mLap;
    }

    public void onFloatingActionButtonClicked(View view) {
        Intent intent = new Intent();
        intent.putExtra(ScoreItActivity.EXTRA_LAP, mLap);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void setupFauxDialog() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(R.attr.isDialog, tv, true) && tv.data != 0) {
            mIsDialog = true;
            DisplayMetrics dm = getResources().getDisplayMetrics();

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
            params.height = Math.min(
                    getResources().getDimensionPixelSize(R.dimen.dialog_max_height),
                    dm.heightPixels * 3 / 4);
            params.alpha = 1.0f;
            params.dimAmount = 0.5f;
            getWindow().setAttributes(params);
        } else {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }
    }
}
