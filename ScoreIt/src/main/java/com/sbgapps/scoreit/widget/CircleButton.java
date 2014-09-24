package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 24/09/2014.
 */
public class CircleButton extends Button {

    private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
    private static final int DEFAULT_PRESSED_RING_WIDTH_DIP = 4;

    private int mColor;
    private int mPressedRingWidth;
    private Paint mPaint;

    public CircleButton(Context context) {
        this(context, null);
    }

    public CircleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CircleButton);
        mPressedRingWidth = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_RING_WIDTH_DIP, getResources()
                        .getDisplayMetrics());
        mPressedRingWidth = (int) a.getDimension(R.styleable.CircleButton_cb_margin, mPressedRingWidth);
        mColor = a.getColor(R.styleable.CircleButton_cb_color, Color.BLACK);
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        setColor(mColor);

        setGravity(Gravity.CENTER);
        setBackgroundDrawable(null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float radius = (getHeight() < getWidth()) ? getHeight() : getWidth();
        canvas.drawCircle(
                getWidth() / 2,
                getHeight() / 2,
                radius / 2 - mPressedRingWidth,
                mPaint);

        super.onDraw(canvas);
    }

    public void setColor(int color) {
        mColor = color;
        mPaint.setColor(mColor);
        invalidate();
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        mPaint.setColor(pressed ? getHighlightColor(mColor) : mColor);
        invalidate();
    }

    private int getHighlightColor(int color) {
        return Color.argb(
                Math.min(255, Color.alpha(color)),
                Math.min(255, Color.red(color) + PRESSED_COLOR_LIGHTUP),
                Math.min(255, Color.green(color) + PRESSED_COLOR_LIGHTUP),
                Math.min(255, Color.blue(color) + PRESSED_COLOR_LIGHTUP));
    }
}
