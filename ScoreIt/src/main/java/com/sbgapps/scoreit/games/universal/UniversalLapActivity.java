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

package com.sbgapps.scoreit.games.universal;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.LapActivity;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.widget.NumberPickerDialogFragment;
import com.sbgapps.scoreit.widget.PickerPoints;

import java.util.ArrayList;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapActivity extends LapActivity
        implements NumberPickerDialogFragment.NumberPickerDialogListener {

    private static final String KEY_SCORES = "scores";
    private final ArrayList<PickerPoints> mPoints = new ArrayList<>(2);

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null == savedInstanceState) {
            if (-1 == mPosition) { // New lap
                mLap = new UniversalLap();
            } else { // Edited lap
                mLap = getGameHelper().getLaps().get(mPosition);
            }
        }
        setContentView(R.layout.activity_lap_universal);

        final LinearLayout ll = (LinearLayout) findViewById(R.id.container);
        for (int player = 0; player < getGameHelper().getPlayerCount(); player++) {
            PickerPoints uip = new PickerPoints(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            uip.setLayoutParams(lp);
            uip.setPlayer(player);
            uip.setPoints(getLap().getScore(player));
            ll.addView(uip);
            mPoints.add(uip);
        }

        if (isDialog()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] points = new int[Player.PLAYER_COUNT_MAX];
        for (int player = 0; player < getGameHelper().getPlayerCount(); player++) {
            points[player] = mPoints.get(player).getPoints();
        }
        outState.putIntArray(KEY_SCORES, points);
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal,
                                  boolean isNegative, double fullNumber, int player) {
        mPoints.get(player).setPoints(number);
    }
}
