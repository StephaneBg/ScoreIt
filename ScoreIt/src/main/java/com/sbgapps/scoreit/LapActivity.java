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

import android.app.ActionBar;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;

/**
 * Created by sbaiget on 08/01/14.
 */
public abstract class LapActivity extends BaseActivity
        implements View.OnClickListener {

    private final GameHelper mGameData;
    private Lap mLap;
    private boolean mIsEdited;

    public LapActivity() {
        mGameData = GameHelper.getInstance();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTranslucentStatusBar();
        if (!isDialog()) setupActionBar();

        Bundle b = getIntent().getExtras();
        mIsEdited = b.getBoolean(ScoreItActivity.EXTRA_EDIT);
        if (mIsEdited) {
            int i = b.getInt(ScoreItActivity.EXTRA_LAP, -1);
            mLap = getGameHelper().getLaps().get(i);
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
                        | ActionBar.DISPLAY_SHOW_TITLE
        );
        actionBar.setCustomView(customActionBarView,
                new ActionBar.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT)
        );
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_confirm:
                updateLap();
                if (!isEdited()) {
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

    public GameHelper getGameHelper() {
        return mGameData;
    }

    abstract public void updateLap();

    abstract public int progressToPoints(int progress);

    abstract public int pointsToProgress(int points);
}
