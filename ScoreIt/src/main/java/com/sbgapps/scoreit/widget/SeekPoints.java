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
import android.widget.TextView;

import com.sbgapps.scoreit.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 26/01/14.
 */
public class SeekPoints extends FrameLayout {

    @InjectView(R.id.points)
    TextView mPointsTv;
    @InjectView(R.id.seekbar_points)
    SeekArc mSeekBarPoints;
    @InjectView(R.id.btn_minus)
    CircleButton mButtonMinus;
    @InjectView(R.id.btn_plus)
    CircleButton mButtonPlus;

    private OnPointsChangedListener mListener;
    private int mPoints;
    private String mTag;

    public SeekPoints(Context context) {
        this(context, null);
    }

    public SeekPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seek_points, this, true);
        ButterKnife.inject(this);
    }

    public void setPoints(int points) {
        mPoints = points;
        updatePoints();
    }

    public void setOnPointsChangedListener(OnPointsChangedListener listener, String tag) {
        mListener = listener;
        mTag = tag;
    }

    public void init(int progress, int max, int points) {
        mPoints = progress;
        mPointsTv.setText(Integer.toString(points));

        mButtonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPoints > 0) {
                    mPoints--;
                    manageProgress();
                }
            }
        });

        mButtonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPoints < mSeekBarPoints.getMax()) {
                    mPoints++;
                    manageProgress();
                }
            }
        });

        mSeekBarPoints.setMax(max);
        mSeekBarPoints.setProgress(progress);
        mSeekBarPoints.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                if (fromUser) {
                    mPoints = progress;
                    manageProgress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {

            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {

            }
        });
    }

    public void manageProgress() {
        mListener.onPointsChanged(mPoints, mTag);
        updatePoints();
        mButtonMinus.setEnabled(mPoints > 0);
        mButtonPlus.setEnabled(mPoints < mSeekBarPoints.getMax());
    }

    private void updatePoints() {
        mPointsTv.setText(Integer.toString(mPoints));
        mSeekBarPoints.setProgress(mPoints);
    }

    public interface OnPointsChangedListener {

        public void onPointsChanged(int points, String tag);
    }
}
