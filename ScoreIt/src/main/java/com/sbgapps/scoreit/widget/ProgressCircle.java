package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sbgapps.scoreit.R;

public class ProgressCircle extends View {

    private static final int DEFAULT_STROKE_WIDTH_DIP = 4;
    private static final int DEFAULT_THUMB_RADIUS_DIP = 6;

    private final RectF mRect = new RectF();

    protected final float mStrokeWidth;
    protected final float mThumbRadius;
    protected float mRadius;

    protected int mMaxProgress = 100;
    protected int mProgress = 0;

    protected float mCenterX;
    protected float mCenterY;

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mThumbPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int mColor;
    private final int mInactiveColor;

    public ProgressCircle(Context context) {
        this(context, null);
    }

    public ProgressCircle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressCircle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SeekCircle, 0, 0);
        // Read and clamp max
        int max = attributes.getInteger(R.styleable.SeekCircle_sc_max, 100);
        mMaxProgress = Math.max(max, 1);

        // Read and clamp progress
        int progress = attributes.getInteger(R.styleable.SeekCircle_sc_progress, 0);
        mProgress = Math.max(Math.min(progress, mMaxProgress), 0);

        // Colors
        int color = context.getResources().getColor(R.color.gray_light_pressed);
        mInactiveColor = attributes.getColor(R.styleable.SeekCircle_sc_inactive_color, color);
        color = context.getResources().getColor(R.color.color_primary);
        mColor = attributes.getColor(R.styleable.SeekCircle_sc_active_color, color);

        int dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_STROKE_WIDTH_DIP, getResources().getDisplayMetrics());
        mStrokeWidth = attributes.getDimension(R.styleable.SeekCircle_sc_stroke_width, dim);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);

        dim = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_THUMB_RADIUS_DIP, getResources().getDisplayMetrics());
        mThumbRadius = attributes.getDimension(R.styleable.SeekCircle_sc_thumb_radius, dim);
        color = context.getResources().getColor(R.color.color_accent);
        color = attributes.getColor(R.styleable.SeekCircle_sc_thumb_color, color);
        mThumbPaint.setColor(color);

        attributes.recycle();

        if (isInEditMode()) mProgress = 25;
    }

    private void updateDimensions(int width, int height) {
        // Update center position
        mCenterX = width / 2.0f;
        mCenterY = height / 2.0f;

        // Find shortest dimension
        int diameter = Math.min(width, height);

        mRadius = (diameter / 2) - mStrokeWidth;
        float space = Math.max(mStrokeWidth, mThumbRadius);
        mRect.set(space, space, width - space, height - space);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if (width > height)
            super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        else
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        updateDimensions(getWidth(), getHeight());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        updateDimensions(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale(-1, 1, mRect.centerX(), mRect.centerY());

        float rotation = 360.0f * mProgress / (float) mMaxProgress;
        // Draw progress
        mPaint.setColor(mColor);
        canvas.drawArc(mRect, -90.0f, rotation, false, mPaint);

        // Draw inactive
        mPaint.setColor(mInactiveColor);
        canvas.drawArc(mRect, rotation - 90.0f, 360.0f - rotation, false, mPaint);

        // Draw thumb
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(-90.0f);
        int thumbXPos = (int) ((mRadius - mThumbRadius / 2) * Math.cos(Math.toRadians(rotation)));
        int thumbYPos = (int) ((mRadius - mThumbRadius / 2) * Math.sin(Math.toRadians(rotation)));
        canvas.drawCircle(thumbXPos, thumbYPos, mThumbRadius, mThumbPaint);
    }

    /**
     * Get max progress
     *
     * @return Max progress
     */
    public float getMax() {
        return mMaxProgress;
    }

    /**
     * Set max progress
     *
     * @param max
     */
    public void setMax(int max) {
        int newMax = Math.max(max, 1);
        if (newMax != mMaxProgress)
            mMaxProgress = newMax;

        updateProgress(mProgress);
        invalidate();
    }

    /**
     * Get Progress
     *
     * @return progress
     */
    public int getProgress() {
        return mProgress;
    }

    /**
     * Set progress
     *
     * @param progress
     */
    public void setProgress(int progress) {
        updateProgress(progress);
    }

    /**
     * Update progress internally. Clamp it to a valid range and invalidate the view if necessary
     *
     * @param progress
     * @return true if progress was changed and the view needs an update
     */
    protected boolean updateProgress(int progress) {
        // Clamp progress
        progress = Math.max(0, Math.min(mMaxProgress, progress));

        if (progress != mProgress) {
            mProgress = progress;
            invalidate();

            return true;
        }

        return false;
    }
}
