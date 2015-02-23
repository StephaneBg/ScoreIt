/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 26/01/14.
 */
public class SeekPoints extends FrameLayout {

    @InjectView(R.id.tv_points)
    TextView mPointsTv;
    @InjectView(R.id.seekarc_points)
    SeekArc mSeekBarPoints;
    @InjectView(R.id.btn_minus)
    ImageButton mButtonMinus;
    @InjectView(R.id.btn_plus)
    ImageButton mButtonPlus;

    private OnProgressChangedListener mListener;
    private int mProgress;

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

    public void setPoints(int progress, String points) {
        mProgress = progress;
        mSeekBarPoints.setProgress(mProgress);
        mPointsTv.setText(points);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener listener) {
        mListener = listener;
    }

    public void init(int progress, int max, String points) {
        mProgress = progress;
        mPointsTv.setText(points);

        mButtonMinus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress > 0) {
                    mProgress--;
                    manageProgress(true);
                }
            }
        });

        mButtonPlus.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProgress < mSeekBarPoints.getMax()) {
                    mProgress++;
                    manageProgress(true);
                }
            }
        });

        mSeekBarPoints.setMax(max);
        mSeekBarPoints.setProgress(progress, false);
        mSeekBarPoints.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
                if (fromUser) {
                    mProgress = progress;
                    manageProgress(false);
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

    public void manageProgress(boolean fromButton) {
        String points = mListener.onProgressChanged(this, mProgress);
        if (fromButton) mSeekBarPoints.setProgress(mProgress);
        mPointsTv.setText(points);
        mButtonMinus.setEnabled(mProgress > 0);
        mButtonPlus.setEnabled(mProgress < mSeekBarPoints.getMax());
    }

    public interface OnProgressChangedListener {

        public String onProgressChanged(SeekPoints seekPoints, int progress);
    }
}
