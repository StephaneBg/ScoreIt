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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 14/12/13.
 */
public class RippleLayout extends FrameLayout {

    private final Paint mPaint;
    private final ObjectAnimator mCircleAnimator = ObjectAnimator.ofFloat(this, "radiusFraction", 0f, 0f);
    private final ObjectAnimator mAlphaAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 0f);
    private float mRadius;

    public RippleLayout(Context context) {
        this(context, null);
    }

    public RippleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RippleLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(context.getResources().getColor(R.color.lighter_gray));
    }

    public void start() {
        mCircleAnimator.setFloatValues(0.0f, 1.0f);
        mAlphaAnimator.setFloatValues(1.0f, 0.0f);
        mCircleAnimator.start();
        mAlphaAnimator.start();
    }

    public float getRadiusFraction() {
        final double hyp = Math.hypot(getWidth(), getHeight());
        if (hyp != 0) return mRadius / (float) hyp;
        else return mRadius;
    }

    public void setRadiusFraction(float fraction) {
        final double hyp = Math.hypot(getWidth(), getHeight());
        mRadius = (float) hyp * fraction;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(getWidth(), 0, mRadius, mPaint);
    }
}
