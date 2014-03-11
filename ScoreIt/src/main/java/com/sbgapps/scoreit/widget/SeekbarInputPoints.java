/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbgapps.scoreit.LapActivity;
import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 26/01/14.
 */
public class SeekbarInputPoints extends FrameLayout {

    private final LapActivity mContext;
    private final TextView mTextViewPoints;
    private final SeekBar mSeekBarPoints;
    private final ImageButton mButtonPlus;
    private final ImageButton mButtonMinus;
    private int mProgress;

    public SeekbarInputPoints(Context context) {
        this(context, null);
    }

    public SeekbarInputPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekbarInputPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = (LapActivity) context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbar_input_points, this, true);

        mTextViewPoints = (TextView) findViewById(R.id.textview_points);
        mSeekBarPoints = (SeekBar) findViewById(R.id.seekbar_points);
        mButtonMinus = (ImageButton) findViewById(R.id.btn_less);
        mButtonPlus = (ImageButton) findViewById(R.id.btn_more);
        init();
    }

    private void init() {
        mButtonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress > 0) {
                    mProgress--;
                    displayPoints();
                }
            }
        });

        mButtonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress < mSeekBarPoints.getMax()) {
                    mProgress++;
                    displayPoints();
                }
            }
        });

        mSeekBarPoints.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mProgress = progress;
                    displayPoints();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setMax(int max) {
        mSeekBarPoints.setMax(max);
    }

    public int getPoints() {
        return mContext.progressToPoints(mProgress);
    }

    public void setPoints(int points) {
        mProgress = mContext.pointsToProgress(points);
        displayPoints();
    }

    public void displayPoints() {
        int points = mContext.progressToPoints(mProgress);
        mTextViewPoints.setText(Integer.toString(points));
        mSeekBarPoints.setProgress(mProgress);
        mButtonMinus.setEnabled(mProgress > 0);
        mButtonPlus.setEnabled(mProgress < mSeekBarPoints.getMax());
    }
}
