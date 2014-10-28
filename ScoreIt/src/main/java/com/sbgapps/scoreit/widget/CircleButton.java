package com.sbgapps.scoreit.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.util.Util;

public class CircleButton extends ImageView {

    private static final int PRESSED_RING_ALPHA = 48;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 2;
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

        final float density = getResources().getDisplayMetrics().density;
        mPressedRingWidth = (int) (DEFAULT_PRESSED_RING_WIDTH_DIP * density);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);

        int color = context.getResources().getColor(R.color.color_hint);
        mDefaultColor = a.getColor(R.styleable.CircleButton_cb_color, color);
        mPressedColor = Util.getHighlightColor(mDefaultColor, 0.8f);

        mCirclePaint.setColor(mDefaultColor);
        color = context.getResources().getColor(R.color.gray_light);
        mDisabledColor = a.getColor(R.styleable.CircleButton_cb_disabled_color, color);

        mPressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_pressedRingWidth, mPressedRingWidth);
        color = context.getResources().getColor(R.color.gray_dark);
        color = a.getColor(R.styleable.CircleButton_cb_shadow_color, color);
        mFocusPaint.setColor(color);
        mFocusPaint.setAlpha(PRESSED_RING_ALPHA);
        mFocusPaint.setStrokeWidth(mPressedRingWidth);

        a.recycle();

        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        mAnimator.setDuration(pressedAnimationTime);
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
