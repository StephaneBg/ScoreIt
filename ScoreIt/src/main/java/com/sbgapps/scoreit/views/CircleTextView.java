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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.utils.Utils;

public class CircleTextView extends TextView {

    private static final int PRESSED_RING_ALPHA = 48;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 3;

    private int mCenterY;
    private int mCenterX;
    private int mOuterRadius;
    private int mPressedRingRadius;

    private final Paint mCirclePaint;
    private final Paint mFocusPaint;
    private final int mDisabledColor;
    private final int mDefaultColor;
    private final ObjectAnimator mAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f);
    private final int mPressedColor;

    private int mPressedRingWidth;
    private boolean mIsAnimated = false;
    private float mAnimationProgress;


    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFocusable(true);
        setClickable(true);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFocusPaint.setStyle(Paint.Style.STROKE);

        final Resources res = context.getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView);

        mPressedRingWidth = Utils.dpToPx(DEFAULT_PRESSED_RING_WIDTH_DIP, res);
        mPressedRingWidth = (int) a.getDimension(R.styleable.CircleTextView_ct_pressedRingWidth, mPressedRingWidth);

        int color = res.getColor(R.color.color_accent);
        mDefaultColor = a.getColor(R.styleable.CircleTextView_ct_color, color);
        mCirclePaint.setColor(mDefaultColor);

        color = res.getColor(R.color.color_accent_dark);
        mPressedColor = a.getColor(R.styleable.CircleTextView_ct_pressed_color, color);

        color = a.getColor(R.styleable.CircleTextView_ct_shadow_color, color);
        mFocusPaint.setColor(color);
        mFocusPaint.setAlpha(PRESSED_RING_ALPHA);
        mFocusPaint.setStrokeWidth(mPressedRingWidth);

        color = context.getResources().getColor(R.color.gray_light);
        mDisabledColor = a.getColor(R.styleable.CircleTextView_ct_disabled_color, color);

        a.recycle();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);

        if (mCirclePaint != null) {
            mCirclePaint.setColor(pressed ? mPressedColor :
                    isEnabled() ? mDefaultColor : mDisabledColor);
        }

        if (pressed) {
            showPressedRing();
        } else if (mIsAnimated) {
            hidePressedRing();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            mCirclePaint.setColor(mDefaultColor);
        } else {
            mCirclePaint.setColor(mDisabledColor);
        }
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mPressedRingRadius + mAnimationProgress, mFocusPaint);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius - mPressedRingWidth, mCirclePaint);
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mCenterX = w / 2;
        mCenterY = h / 2;
        mOuterRadius = Math.min(w, h) / 2;
        mPressedRingRadius = mOuterRadius - mPressedRingWidth - mPressedRingWidth / 2;
    }

    public float getAnimationProgress() {
        return mAnimationProgress;
    }

    public void setAnimationProgress(float animationProgress) {
        this.mAnimationProgress = animationProgress;
        this.invalidate();
    }

    private void hidePressedRing() {
        mIsAnimated = false;
        mAnimator.setFloatValues(mPressedRingWidth, 0f);
        mAnimator.start();
    }

    private void showPressedRing() {
        mAnimator.setFloatValues(mAnimationProgress, mPressedRingWidth);
        mAnimator.start();
        mIsAnimated = true;
    }
}
