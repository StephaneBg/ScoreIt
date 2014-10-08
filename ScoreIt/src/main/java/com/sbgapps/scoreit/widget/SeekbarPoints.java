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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 26/01/14.
 */
public class SeekbarPoints extends FrameLayout {

    @InjectView(R.id.points)
    TextView mPoints;
    @InjectView(R.id.seekbar_points)
    SeekBar mSeekBarPoints;
    @InjectView(R.id.btn_minus)
    CircleButton mButtonMinus;
    @InjectView(R.id.btn_plus)
    CircleButton mButtonPlus;

    private OnPointsChangedListener mListener;
    private int mProgress;
    private String mTag;

    public SeekbarPoints(Context context) {
        this(context, null);
    }

    public SeekbarPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekbarPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seekbar_input_points, this, true);
        ButterKnife.inject(this);
    }

    public void setOnPointsChangedListener(OnPointsChangedListener listener, String tag) {
        mListener = listener;
        mTag = tag;
    }

    public void init(int progress, int max, int points) {
        mProgress = progress;
        mPoints.setText(Integer.toString(points));

        mButtonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress > 0) {
                    mProgress--;
                    manageProgress();
                }
            }
        });

        mButtonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress < mSeekBarPoints.getMax()) {
                    mProgress++;
                    manageProgress();
                }
            }
        });

        mSeekBarPoints.setProgress(progress);
        mSeekBarPoints.setMax(max);
        mSeekBarPoints.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    mProgress = progress;
                    manageProgress();
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

    public void manageProgress() {
        int points = mListener.onPointsChanged(mProgress, mTag);
        mPoints.setText(Integer.toString(points));
        mSeekBarPoints.setProgress(mProgress);
        mButtonMinus.setEnabled(mProgress > 0);
        mButtonPlus.setEnabled(mProgress < mSeekBarPoints.getMax());
    }

    public interface OnPointsChangedListener {

        public int onPointsChanged(int progress, String tag);
    }
}
