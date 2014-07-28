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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.sbgapps.scoreit.R;

/**
 * Created by Stéphane on 22/07/2014.
 */
public class UniversalInput extends FrameLayout {

    private final UniversalLapActivity mActivity;
    private final int mPlayer;
    private TextView mTextViewPoints;
    private int mScore;

    public UniversalInput(UniversalLapActivity activity, int player) {
        super(activity);

        mActivity = activity;
        mPlayer = player;

        LayoutInflater inflater = (LayoutInflater) mActivity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.universal_input, this, true);

        TextView textView = (TextView) findViewById(R.id.player_name);
        final String name = mActivity.getGameHelper().getPlayer(mPlayer).getName();
        textView.setText(name);

        mTextViewPoints = (TextView) findViewById(R.id.edit_points);
        mScore = mActivity.getLap().getScore(mPlayer);
        mTextViewPoints.setText(Integer.toString(mScore));

        ImageButton button = (ImageButton) findViewById(R.id.btn_minus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore--;
                updateScore(mScore);
            }
        });

        button = (ImageButton) findViewById(R.id.btn_plus);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore++;
                updateScore(mScore);
            }
        });

        mTextViewPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(mActivity.getSupportFragmentManager())
                        .setStyleResId(R.style.BetterPickerTheme)
                        .setDecimalVisibility(View.INVISIBLE)
                        .setReference(mPlayer)
                        .show();
            }
        });
    }

    public void updateScore(int score) {
        mActivity.getLap().setScore(mPlayer, score);
        mTextViewPoints.setText(Integer.toString(score));
    }
}
