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
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.utils.Utils;

/**
 * Created by Stéphane on 15/03/2015.
 */
public class RingTextView extends TextView {

    private static final int DEFAULT_RING_WIDTH_DIP = 4;

    private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private int mCenterY;
    private int mCenterX;
    private int mOuterRadius;
    private int mRingRadius;

    public RingTextView(Context context) {
        super(context);
        init();
    }

    public RingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mRingRadius = Utils.dpToPx(DEFAULT_RING_WIDTH_DIP, getResources());
        mCirclePaint.setStrokeWidth(mRingRadius);
        mCirclePaint.setColor(getResources().getColor(R.color.color_accent));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius, mCirclePaint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mOuterRadius = (Math.min(w, h) - mRingRadius) / 2;
    }
}
