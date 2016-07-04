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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.utils.DimensionsHelper;

/**
 * SeekArc.java
 * <p/>
 * This is a class that functions much like a SeekBar but follows a circle path
 * instead of a straight line.
 *
 * @author Neil Davies
 */
public class SeekArc extends View {

    private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;
    private static final int INVALID_PROGRESS_VALUE = -1;

    /**
     * The Maximum value that this SeekArc can be set to
     */
    private int mMax = 100;

    /**
     * Will the progress increase clockwise or anti-clockwise
     */
    private boolean mClockwise = false;

    private final Paint mArcPaint;
    private final Paint mProgressPaint;
    private final Paint mThumbPaint;
    private final float mThumbRadius;
    private final RectF mArcRect = new RectF();

    private int mArcRadius = 0;
    private float mProgressSweep = 0;
    private float mProgress = 0;
    private int mTranslateX;
    private int mTranslateY;
    private int mThumbXPos;
    private int mThumbYPos;
    private OnSeekArcChangeListener mOnSeekArcChangeListener;
    private boolean mTouchable = true;

    private final ObjectAnimator mAnimator = ObjectAnimator.ofFloat(this, "progressSweep", 0f, 0f);

    public SeekArc(Context context) {
        this(context, null);
    }

    public SeekArc(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SeekArc(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final Resources res = getResources();
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SeekArc, defStyle, 0);

        mMax = a.getInteger(R.styleable.SeekArc_max, mMax);
        mClockwise = a.getBoolean(R.styleable.SeekArc_clockwise, mClockwise);
        mTouchable = a.getBoolean(R.styleable.SeekArc_touchable, mTouchable);
        int arcColor = a.getColor(R.styleable.SeekArc_arcColor,
                res.getColor(R.color.light_grey));
        int progressColor = a.getColor(R.styleable.SeekArc_progressColor,
                res.getColor(R.color.color_primary));
        int thumbColor = a.getColor(R.styleable.SeekArc_thumbColor,
                res.getColor(R.color.color_accent));
        mThumbRadius = a.getDimension(R.styleable.SeekArc_thumbRadius, DimensionsHelper.dpToPx(12, res));

        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setColor(arcColor);
        mArcPaint.setStyle(Paint.Style.STROKE);
        int width = (int) a.getDimension(R.styleable.SeekArc_progressWidth, DimensionsHelper.dpToPx(6, res));
        mArcPaint.setStrokeWidth(width);

        mProgressPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        width = (int) a.getDimension(R.styleable.SeekArc_arcWidth, DimensionsHelper.dpToPx(6, res));
        mProgressPaint.setStrokeWidth(width);

        mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mThumbPaint.setColor(thumbColor);

        final int pressedAnimationTime = getResources().getInteger(ANIMATION_TIME_ID);
        mAnimator.setDuration(pressedAnimationTime);

        a.recycle();
    }

    public int getMax() {
        return mMax;
    }

    public void setMax(int max) {
        mMax = max;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mClockwise) {
            canvas.scale(-1, 1, mArcRect.centerX(), mArcRect.centerY());
        }

        // Draw the arcs
        canvas.drawArc(mArcRect, -90, 360, false, mArcPaint);
        canvas.drawArc(mArcRect, -90, mProgressSweep, false, mProgressPaint);

        // Draw the thumbnail
        canvas.drawCircle(mTranslateX - mThumbXPos, mTranslateY - mThumbYPos, mThumbRadius, mThumbPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        final int width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int min = Math.min(width, height);
        float top, left;
        int arcDiameter;

        mTranslateX = (int) (width * 0.5f);
        mTranslateY = (int) (height * 0.5f);

        arcDiameter = min - getPaddingLeft();
        mArcRadius = arcDiameter / 2;
        top = height / 2 - (arcDiameter / 2);
        left = width / 2 - (arcDiameter / 2);
        mArcRect.set(left, top, left + arcDiameter, top + arcDiameter);

        int arcStart = (int) mProgressSweep + 90;
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(arcStart)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(arcStart)));

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mTouchable) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    getParent().requestDisallowInterceptTouchEvent(true);
                    onStartTrackingTouch();
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateOnTouch(event);
                    break;
                case MotionEvent.ACTION_UP:
                    onStopTrackingTouch();
                    setPressed(false);
                    break;
                case MotionEvent.ACTION_CANCEL:
                    onStopTrackingTouch();
                    setPressed(false);
                    break;
            }
        }
        return true;
    }

    private void onStartTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStartTrackingTouch(this);
        }
    }

    private void onStopTrackingTouch() {
        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onStopTrackingTouch(this);
        }
        getParent().requestDisallowInterceptTouchEvent(false);
    }

    private void updateOnTouch(MotionEvent event) {
        double touchAngle;
        setPressed(true);
        touchAngle = getTouchDegrees(event.getX(), event.getY());
        int progress = getProgressForAngle(touchAngle);
        updateProgress(progress, true, true);
    }

    private double getTouchDegrees(float xPos, float yPos) {
        float x = xPos - mTranslateX;
        float y = yPos - mTranslateY;
        // invert the x-coord if we are rotating anti-clockwise
        x = (mClockwise) ? x : -x;
        // convert to arc Angle
        double angle = Math.toDegrees(Math.atan2(y, x) + (Math.PI / 2));
        if (angle < 0) {
            angle = 360 + angle;
        }
        return angle;
    }

    private int getProgressForAngle(double angle) {
        int touchProgress = (int) Math.round(valuePerDegree() * angle);

        touchProgress = (touchProgress < 0) ? INVALID_PROGRESS_VALUE : touchProgress;
        touchProgress = (touchProgress > mMax) ? INVALID_PROGRESS_VALUE : touchProgress;
        return touchProgress;
    }

    private float valuePerDegree() {
        return (float) mMax / 360;
    }

    private void updateThumbPosition() {
        int thumbAngle = (int) (mProgressSweep + 90);
        mThumbXPos = (int) (mArcRadius * Math.cos(Math.toRadians(thumbAngle)));
        mThumbYPos = (int) (mArcRadius * Math.sin(Math.toRadians(thumbAngle)));
    }

    private void updateProgress(int progress, boolean fromUser, boolean animate) {
        if (progress == INVALID_PROGRESS_VALUE) {
            return;
        }

        if (mOnSeekArcChangeListener != null) {
            mOnSeekArcChangeListener.onProgressChanged(this, progress, fromUser);
        }

        progress = (progress > mMax) ? mMax : progress;
        progress = (progress < 0) ? 0 : progress;

        if (fromUser || !animate) {
            setProgressSweep(progress);
        } else {
            mAnimator.setFloatValues(mProgress, progress);
            mAnimator.start();
        }
        mProgress = progress;
    }

    public void setProgressSweep(float progress) {
        mProgressSweep = progress / mMax * 360;
        updateThumbPosition();
        invalidate();
    }

    public float getProgressSweep() {
        return mProgressSweep;
    }

    /**
     * Sets a listener to receive notifications of changes to the SeekArc's
     * progress level. Also provides notifications of when the user starts and
     * stops a touch gesture within the SeekArc.
     *
     * @param l The seek bar notification listener
     * @see com.sbgapps.scoreit.views.widgets.SeekArc.OnSeekArcChangeListener
     */
    public void setOnSeekArcChangeListener(OnSeekArcChangeListener l) {
        mOnSeekArcChangeListener = l;
    }

    public void setProgress(int progress) {
        setProgress(progress, true);
    }

    public void setProgress(int progress, boolean animate) {
        updateProgress(progress, false, animate);
    }

    public int getProgressColor() {
        return mProgressPaint.getColor();
    }

    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        invalidate();
    }

    public boolean isTouchable() {
        return mTouchable;
    }

    public void setTouchable(boolean mTouchable) {
        this.mTouchable = mTouchable;
    }

    public int getArcColor() {
        return mArcPaint.getColor();
    }

    public void setArcColor(int color) {
        mArcPaint.setColor(color);
        invalidate();
    }

    public void setClockwise(boolean isClockwise) {
        mClockwise = isClockwise;
    }

    public interface OnSeekArcChangeListener {

        /**
         * Notification that the progress level has changed. Clients can use the
         * fromUser parameter to distinguish user-initiated changes from those
         * that occurred programmatically.
         *
         * @param seekArc  The SeekArc whose progress has changed
         * @param progress The current progress level. This will be in the range
         *                 0..max where max was set by
         *                 {@link com.sbgapps.scoreit.views.widgets.SeekArc#setMax(int)}. (The default value for
         *                 max is 100.)
         * @param fromUser True if the progress change was initiated by the user.
         */
        void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser);

        /**
         * Notification that the user has started a touch gesture. Clients may
         * want to use this to disable advancing the seekbar.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStartTrackingTouch(SeekArc seekArc);

        /**
         * Notification that the user has finished a touch gesture. Clients may
         * want to use this to re-enable advancing the seekarc.
         *
         * @param seekArc The SeekArc in which the touch gesture began
         */
        void onStopTrackingTouch(SeekArc seekArc);
    }
}
