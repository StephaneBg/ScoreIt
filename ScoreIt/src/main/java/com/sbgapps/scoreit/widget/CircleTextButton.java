package com.sbgapps.scoreit.widget;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 24/09/2014.
 */
public class CircleTextButton extends View {

    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 6;
    private static final int PRESSED_RING_ALPHA = 75;

    private final float mYTextOffset;

    private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mFocusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final ObjectAnimator mPressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f, 0f);

    private int mCenterY;
    private int mCenterX;
    private int mOuterRadius;
    private int mPressedRingRadius;
    private int mPressedRingWidth;
    private int mColor;

    private float mAnimationProgress;

    private String mText;

    public CircleTextButton(Context context) {
        this(context, null);
    }

    public CircleTextButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleTextButton);
        mPressedRingWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources().getDisplayMetrics());
        mPressedRingWidth = (int) a.getDimension(R.styleable.CircleTextButton_cb_pressedRingWidth, mPressedRingWidth);

        int color = a.getColor(R.styleable.CircleTextButton_cb_color, Color.BLACK);
        mCirclePaint.setStyle(Paint.Style.FILL);
        mFocusPaint.setStyle(Paint.Style.STROKE);
        mFocusPaint.setStrokeWidth(mPressedRingWidth);
        setColor(color);

        color = a.getColor(R.styleable.CircleTextButton_cb_textColor, Color.BLACK);
        float size = a.getDimension(R.styleable.CircleTextButton_cb_textSize, 32);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(size);
        mTextPaint.setColor(color);
        mYTextOffset = (mTextPaint.descent() + mTextPaint.ascent()) / 2;
        mText = a.getString(R.styleable.CircleTextButton_cb_text);

        a.recycle();

        mPressedAnimator.setDuration(200);
    }

    public void setColor(int color) {
        mColor = color;
        mCirclePaint.setColor(mColor);

        mFocusPaint.setColor(mColor);
        mFocusPaint.setAlpha(PRESSED_RING_ALPHA);

        invalidate();
    }

    public void setText(String text) {
        mText = text;
        invalidate();
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
        mAnimationProgress = animationProgress;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawCircle(mCenterX, mCenterY, mPressedRingRadius + mAnimationProgress, mFocusPaint);
        canvas.drawCircle(mCenterX, mCenterY, mOuterRadius - mPressedRingWidth, mCirclePaint);

        if (null != mText) canvas.drawText(mText, mCenterX, (mCenterY - mYTextOffset), mTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                mPressedAnimator.setFloatValues(mAnimationProgress, mPressedRingWidth, 0f);
                mPressedAnimator.start();
                break;
        }
        return super.onTouchEvent(event);
    }
}
