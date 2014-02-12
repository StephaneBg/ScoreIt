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

package com.sbgapps.scoreit;

import android.app.ActionBar;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;

/**
 * Created by sbaiget on 08/01/14.
 */
public abstract class LapActivity extends BaseActivity
        implements View.OnClickListener {

    private final GameData mGameData;
    private Lap mLap;
    private boolean mIsDialog = false;
    private boolean mIsEdited;

    public LapActivity() {
        mGameData = GameData.getInstance();
    }

    public Lap getLap() {
        return mLap;
    }

    public void setLap(Lap lap) {
        mLap = lap;
    }

    public boolean isEdited() {
        return mIsEdited;
    }

    public boolean isDialog() {
        return mIsDialog;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupFauxDialog();

        Bundle b = getIntent().getExtras();
        mLap = (Lap) b.getSerializable(ScoreItActivity.EXTRA_LAP);
        mIsEdited = b.getBoolean(ScoreItActivity.EXTRA_EDIT);
    }

    private void setupFauxDialog() {
        TypedValue tv = new TypedValue();
        if (!getTheme().resolveAttribute(R.attr.isDialog, tv, true) || tv.data == 0) {
            setupActionBar();
            setAccentDecor();
        } else {
            mIsDialog = true;
            DisplayMetrics dm = getResources().getDisplayMetrics();

            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = getResources().getDimensionPixelSize(R.dimen.lap_dialog_width);
            params.height = Math.min(
                    getResources().getDimensionPixelSize(R.dimen.lap_dialog_max_height),
                    dm.heightPixels * 3 / 4);
            params.alpha = 1.0f;
            params.dimAmount = 0.5f;
            getWindow().setAttributes(params);
        }
    }

    private void setupActionBar() {
        LayoutInflater inflater = (LayoutInflater) getActionBar().getThemedContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View customActionBarView = inflater.inflate(R.layout.ab_cancel_done, null);
        customActionBarView.findViewById(R.id.btn_cancel).setOnClickListener(this);
        customActionBarView.findViewById(R.id.btn_confirm).setOnClickListener(this);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayOptions(
                ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM
                        | ActionBar.DISPLAY_SHOW_HOME
                        | ActionBar.DISPLAY_SHOW_TITLE);
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));
    }

        @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                updateLap();
                if (isEdited()) {
                    mGameData.editLap(mLap);
                } else {
                    mGameData.addLap(mLap);
                }
                setResult(RESULT_OK);
                finish();
                break;

            case R.id.btn_cancel:
                finish();
                break;
        }
    }

    public GameData getGameData() {
        return mGameData;
    }

    abstract public void updateLap();

    abstract public int progressToPoints(int progress);

    abstract public int pointsToProgress(int points);
}
