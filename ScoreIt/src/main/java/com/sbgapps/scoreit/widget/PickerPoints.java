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

package com.sbgapps.scoreit.widget;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;

/**
 * Created by sbaiget on 06/02/14.
 */
public class PickerPoints extends FrameLayout {

    private final Activity mActivity;
    private final TextView mTextViewPoints;
    private int mPoints;
    private int mPlayer;

    public PickerPoints(Activity activity) {
        this(activity, null);
    }

    public PickerPoints(Activity activity, AttributeSet attrs) {
        this(activity, attrs, 0);
    }

    public PickerPoints(Activity activity, AttributeSet attrs, int defStyle) {
        super(activity, attrs, defStyle);

        mActivity = activity;

        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.universal_input_points, this, true);

        mTextViewPoints = (TextView) findViewById(R.id.edit_points);

        init();
    }

    private void init() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_less);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoints--;
                displayPoints();
            }
        });

        button = (ImageButton) findViewById(R.id.btn_more);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoints++;
                displayPoints();
            }
        });

        mTextViewPoints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(mActivity.getFragmentManager())
                        .setDecimalVisibility(View.INVISIBLE)
                        .setPlayer(mPlayer)
                        .show();
            }
        });
    }

    public void setPlayer(int player) {
        mPlayer = player;
        TextView name = (TextView) findViewById(R.id.player_name);
        name.setText(((ScoreItActivity) mActivity).getGameHelper().getPlayer(player).getName());
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
        displayPoints();
    }

    private void displayPoints() {
        mTextViewPoints.setText(Integer.toString(mPoints));
    }
}
