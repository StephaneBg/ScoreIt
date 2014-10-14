package com.sbgapps.scoreit.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.util.Util;

public class CircleButton extends ImageView {

    private static final int PRESSED_RING_ALPHA = 75;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;
    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

    private int mCenterY;
    private int mCenterX;
    private int mOuterRadius;
    private int mPressedRingRadius;

    private final Paint mCirclePaint;
    private final Paint mFocusPaint;
    private final int mDisabledColor;
    private final int mDefaultColor;
    private final ObjectAnimator mAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);
    private final int mPressedColor;

    private int mPressedRingWidth;
    private boolean mIsAnimated = false;
    private float mAnimationProgress;


    public CircleButton(Context context) {
        this(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setFocusable(true);
        this.setScaleType(ScaleType.CENTER_INSIDE);
        setClickable(true);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.FILL);

        mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFocusPaint.setStyle(Paint.Style.STROKE);

        mPressedRingWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                .getDisplayMetrics());

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
        mDefaultColor = a.getColor(R.styleable.CircleButton_cb_color, Color.BLACK);
        mPressedColor = Util.getDarkerColor(mDefaultColor);
        mDisabledColor = context.getResources().getColor(R.color.lighter_gray);
        mPressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, mPressedRingWidth);
        a.recycle();

        mFocusPaint.setStrokeWidth(mPressedRingWidth);
        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        mAnimator.setDuration(pressedAnimationTime);

        mCirclePaint.setColor(mDefaultColor);
        mFocusPaint.setColor(mDefaultColor);
        mFocusPaint.setAlpha(PRESSED_RING_ALPHA);
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
