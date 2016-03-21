/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.views.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 26/01/14.
 */
public class SeekPoints extends FrameLayout {

    TextView mPointsTv;
    SeekArc mSeekArc;
    ImageView mButtonMinus;
    ImageView mButtonPlus;

    private OnProgressChangedListener mListener;
    private int mProgress;

    public SeekPoints(Context context) {
        super(context);
        init();
    }

    public SeekPoints(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeekPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seek_points, this, true);

        mPointsTv = (TextView) findViewById(R.id.tv_points);
        mSeekArc = (SeekArc) findViewById(R.id.seekarc_points);
        mButtonMinus = (ImageView) findViewById(R.id.btn_minus);
        mButtonPlus = (ImageView) findViewById(R.id.btn_plus);

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
                if (mProgress < mSeekArc.getMax()) {
                    mProgress++;
                    manageProgress(true);
                }
            }
        });

        mSeekArc.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
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

    public SeekPoints setPoints(String points) {
        mPointsTv.setText(points);
        return this;
    }

    public SeekPoints setOnProgressChangedListener(OnProgressChangedListener listener) {
        mListener = listener;
        return this;
    }

    public SeekPoints setProgress(int progress) {
        mProgress = progress;
        mSeekArc.setProgress(progress, false);
        return this;
    }

    public SeekPoints setProgress(int progress, boolean animate) {
        mProgress = progress;
        mSeekArc.setProgress(progress, animate);
        return this;
    }

    public SeekPoints setMax(int max) {
        mSeekArc.setMax(max);
        return this;
    }

    private void manageProgress(boolean fromButton) {
        String points = mListener.onProgressChanged(this, mProgress);
        if (fromButton) mSeekArc.setProgress(mProgress);
        mPointsTv.setText(points);
        mButtonMinus.setEnabled(mProgress > 0);
        mButtonPlus.setEnabled(mProgress < mSeekArc.getMax());
    }

    public interface OnProgressChangedListener {

        String onProgressChanged(SeekPoints seekPoints, int progress);
    }
}
