package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 24/11/2014.
 */
public class UniversalInputPoint extends View {

    private final Resources mResources;
    private final Paint mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mBtnPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final int mPadding;
    private final int mColor;
    private final int mSelectedColor;
    private final RectF mRectF = new RectF();

    private final List<Button> mButtons = new ArrayList<>(6);

    private int mSelectedIndex = -1;
    private OnButtonClickedListener mListener;

    public UniversalInputPoint(Context context) {
        this(context, null);
    }

    public UniversalInputPoint(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UniversalInputPoint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mResources = context.getResources();
        mCirclePaint.setColor(mResources.getColor(R.color.gray_light));
        mCirclePaint.setStyle(Paint.Style.FILL);

        mColor = mResources.getColor(R.color.color_hint);
        mSelectedColor = mResources.getColor(R.color.color_hint_dark);

        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextSize(Utils.spToPx(14, mResources));

        mPadding = Utils.dpToPx(1, mResources);

        mButtons.add(new Button("+10", 10));
        mButtons.add(new Button("+1", 1));
        mButtons.add(new Button("+100", 100));
        mButtons.add(new Button("-100", -100));
        mButtons.add(new Button("-1", -1));
        mButtons.add(new Button("-10", -10));
    }

    public void setOnButtonClickedListener(OnButtonClickedListener listener) {
        mListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float midX, midY, radius;

        midY = getHeight() / 2;
        midX = getWidth() / 2;
        radius = Math.min(midY, midX);
        canvas.drawCircle(midX, midY, radius / 2, mCirclePaint);

        float currentAngle = -67.5f;
        float currentSweep = 45.0f;

        int count = 0;
        for (Button button : mButtons) {
            Path p = button.getPath();
            p.reset();

            if (mSelectedIndex == count && mListener != null) {
                mBtnPaint.setColor(mSelectedColor);
            } else {
                mBtnPaint.setColor(mColor);
            }

            // Draw button
            mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
            createArc(p, mRectF,
                    currentSweep,
                    currentAngle + mPadding,
                    currentSweep - mPadding);
            mRectF.set(midX - radius / 2, midY - radius / 2,
                    midX + radius / 2, midY + radius / 2);
            createArc(p, mRectF,
                    currentSweep,
                    (currentAngle + mPadding) + (currentSweep - mPadding),
                    -(currentSweep - mPadding));
            p.close();

            // Create selection region
            Region r = button.getRegion();
            r.set((int) (midX - radius),
                    (int) (midY - radius),
                    (int) (midX + radius),
                    (int) (midY + radius));
            canvas.drawPath(p, mBtnPaint);

            // Draw text
//        public void drawText(@NonNull String text, float x, float y, @NonNull Paint paint) {
            canvas.drawText(button.getText(), 0, 0, mTextPaint);

            currentAngle = currentAngle + (2 == count++ ? 90.0f : 45.0f);
        }
    }

    private void createArc(Path p, RectF mRectF, float currentSweep, float startAngle, float sweepAngle) {
        if (currentSweep == 360) {
            p.addArc(mRectF, startAngle, sweepAngle);
        } else {
            p.arcTo(mRectF, startAngle, sweepAngle);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Point point = new Point();
        point.x = (int) event.getX();
        point.y = (int) event.getY();

        int count = 0;
        Region r = new Region();
        for (Button button : mButtons) {
            r.setPath(button.getPath(), button.getRegion());
            switch (event.getAction()) {
                default:
                    break;
                case MotionEvent.ACTION_DOWN:
                    if (r.contains(point.x, point.y)) {
                        mSelectedIndex = count;
                        postInvalidate();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (count == mSelectedIndex
                            && mListener != null
                            && r.contains(point.x, point.y)) {
                        mListener.onClick(button.getValue());
                    }
                    break;
            }
            count++;
        }

        // Reset selection
        if (MotionEvent.ACTION_UP == event.getAction()
                || MotionEvent.ACTION_CANCEL == event.getAction()) {
            mSelectedIndex = -1;
            postInvalidate();
        }
        return true;
    }

    private class Button {

        private final Path mPath = new Path();
        private final Region mRegion = new Region();
        private final String mText;
        private final int mValue;

        public Button(String text, int value) {
            mText = text;
            mValue = value;
        }

        public Path getPath() {
            return mPath;
        }

        public Region getRegion() {
            return mRegion;
        }

        public String getText() {
            return mText;
        }

        public int getValue() {
            return mValue;
        }
    }

    public interface OnButtonClickedListener {
        public void onClick(int value);
    }
}
