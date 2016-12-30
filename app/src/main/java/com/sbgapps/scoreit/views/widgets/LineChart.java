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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sbgapps.scoreit.R;

import java.util.ArrayList;

public class LineChart extends View {

    private static final int DEFAULT_PADDING = 10;
    private final int mAxisColor;
    private ArrayList<LineSet> mLines = new ArrayList<>();
    private Paint mPaint = new Paint();
    private float mMinY = 0, mMinX = 0;
    private float mMaxY = 0, mMaxX = 0;
    private double mRangeYRatio = 0;
    private double mRangeXRatio = 0;
    private boolean mUserSetMaxX = false;
    private int mStrokeWidth;

    public LineChart(Context context) {
        this(context, null);
    }

    public LineChart(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LineChart, 0, 0);
        mAxisColor = a.getColor(R.styleable.LineChart_lineAxisColor, Color.LTGRAY);
        a.recycle();

        mStrokeWidth = getPixelForDip(2);
    }

    public void removeAllLines() {
        while (mLines.size() > 0) {
            mLines.remove(0);
        }
        postInvalidate();
    }

    public void addLine(LineSet line) {
        mLines.add(line);
        postInvalidate();
    }

    public void addPointToLine(int lineIndex, double x, double y) {
        addPointToLine(lineIndex, (float) x, (float) y);
    }

    public void addPointToLine(int lineIndex, float x, float y) {
        LinePoint p = new LinePoint(x, y);

        addPointToLine(lineIndex, p);
    }

    public double getRangeYRatio() {
        return mRangeYRatio;
    }

    public void setRangeYRatio(double rr) {
        mRangeYRatio = rr;
    }

    public double getRangeXRatio() {
        return mRangeXRatio;
    }

    public void setRangeXRatio(double rr) {
        mRangeXRatio = rr;
    }

    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    public void setStrokeWidth(int strokeWidth) {
        if (strokeWidth < 0) {
            throw new IllegalArgumentException("strokeWidth must not be less than zero");
        }
        mStrokeWidth = getPixelForDip(strokeWidth);
    }

    public void addPointToLine(int lineIndex, LinePoint point) {
        LineSet line = getLine(lineIndex);
        line.addPoint(point);
        mLines.set(lineIndex, line);
        resetLimits();
        postInvalidate();
    }

    public void addPointsToLine(int lineIndex, LinePoint[] points) {
        LineSet line = getLine(lineIndex);
        for (LinePoint point : points) {
            line.addPoint(point);
        }
        mLines.set(lineIndex, line);
        resetLimits();
        postInvalidate();
    }

    public void removeAllPointsAfter(int lineIndex, double x) {
        removeAllPointsBetween(lineIndex, x, getMaxX());
    }

    public void removeAllPointsBefore(int lineIndex, double x) {
        removeAllPointsBetween(lineIndex, getMinX(), x);
    }

    public void removeAllPointsBetween(int lineIndex, double startX, double finishX) {
        LineSet line = getLine(lineIndex);
        LinePoint[] pts = new LinePoint[line.getPoints().size()];
        pts = line.getPoints().toArray(pts);
        for (LinePoint point : pts) {
            if (point.getX() >= startX && point.getX() <= finishX) {
                line.removePoint(point);
            }
        }
        mLines.set(lineIndex, line);
        resetLimits();
        postInvalidate();
    }

    public void removePointsFromLine(int lineIndex, LinePoint[] points) {
        LineSet line = getLine(lineIndex);
        for (LinePoint point : points) {
            line.removePoint(point);
        }
        mLines.set(lineIndex, line);
        resetLimits();
        postInvalidate();
    }

    public void removePointFromLine(int lineIndex, float x, float y) {
        LinePoint p = null;
        LineSet line = getLine(lineIndex);
        p = line.getPoint(x, y);
        removePointFromLine(lineIndex, p);
    }

    public void removePointFromLine(int lineIndex, LinePoint point) {
        LineSet line = getLine(lineIndex);
        line.removePoint(point);
        mLines.set(lineIndex, line);
        resetLimits();
        postInvalidate();
    }

    public void resetYLimits() {
        float range = getMaxY() - getMinY();
        setRangeY(getMinY() - range * getRangeYRatio(), getMaxY() + range * getRangeYRatio());
    }

    public void resetXLimits() {
        float range = getMaxX() - getMinX();
        setRangeX(getMinX() - range * getRangeXRatio(), getMaxX() + range * getRangeXRatio());
    }

    public void resetLimits() {
        resetYLimits();
        resetXLimits();
    }

    public ArrayList<LineSet> getLines() {
        return mLines;
    }

    public void setLines(ArrayList<LineSet> lines) {
        mLines = lines;
    }

    public LineSet getLine(int index) {
        return mLines.get(index);
    }

    public int getSize() {
        return mLines.size();
    }

    public void setRangeY(float min, float max) {
        mMinY = min;
        mMaxY = max;
    }

    private void setRangeY(double min, double max) {
        mMinY = (float) min;
        mMaxY = (float) max;
    }

    public void setRangeX(float min, float max) {
        mMinX = min;
        mMaxX = max;
        mUserSetMaxX = true;
    }

    private void setRangeX(double min, double max) {
        mMinX = (float) min;
        mMaxX = (float) max;
    }

    public float getMaxY() {
        float max = mLines.get(0).getPoint(0).getY();
        for (LineSet line : mLines) {
            for (LinePoint point : line.getPoints()) {
                max = point.getY() > max ? point.getY() : max;
            }
        }
        mMaxY = max;
        return mMaxY;
    }

    public float getMinY() {
        float min = mLines.get(0).getPoint(0).getY();
        for (LineSet line : mLines) {
            for (LinePoint point : line.getPoints()) {
                min = point.getY() < min ? point.getY() : min;
            }
        }
        mMinY = min;
        return mMinY;
    }

    public float getMinLimY() {
        return mMinY;
    }

    public float getMaxLimY() {
        return mMaxY;
    }

    public float getMinLimX() {
        return mMinX;
    }

    public float getMaxLimX() {
        if (mUserSetMaxX) {
            return mMaxX;
        } else {
            return getMaxX();
        }
    }

    public float getMaxX() {
        float max = mLines.size() > 0 ? mLines.get(0).getPoint(0).getX() : 0;
        for (LineSet line : mLines) {
            for (LinePoint point : line.getPoints()) {
                max = point.getX() > max ? point.getX() : max;
            }
        }
        mMaxX = max;
        return mMaxX;

    }

    public float getMinX() {
        float min = mLines.size() > 0 ? mLines.get(0).getPoint(0).getX() : 0;
        for (LineSet line : mLines) {
            for (LinePoint point : line.getPoints()) {
                min = point.getX() < min ? point.getX() : min;
            }
        }
        mMinX = min;
        return mMinX;
    }

    private void resetPaintWithAntiAlias(Paint paint, boolean antiAlias) {
        paint.reset();
        paint.setAntiAlias(antiAlias);
    }

    public void onDraw(Canvas canvas) {
        resetPaintWithAntiAlias(mPaint, true);
        float padding = getPixelForDip(DEFAULT_PADDING);
        float usableHeight = getHeight() - 2 * padding;
        float usableWidth = getWidth() - 2 * padding;

        float maxY = getMaxLimY();
        float minY = getMinLimY();
        float maxX = getMaxLimX();
        float minX = getMinLimX();


        // Draw x-axis line
        resetPaintWithAntiAlias(mPaint, true);
        mPaint.setColor(mAxisColor);
        mPaint.setStrokeWidth(2 * getResources().getDisplayMetrics().density);

        float height;
        if (minY < 0) {
            // Negative values in serie, set x-axis to zero
            height = getHeight() - padding - (usableHeight * (-minY / (maxY - minY)));
        } else {
            height = getHeight() - padding;
        }

        canvas.drawLine(
                padding,
                height,
                getWidth() - padding,
                height,
                mPaint);
        resetPaintWithAntiAlias(mPaint, true);

        // Draw lines
        for (LineSet line : mLines) {
            int count = 0;
            float lastXPixels = 0, newYPixels = 0;
            float lastYPixels = 0, newXPixels = 0;

            mPaint.setColor(line.getColor());
            mPaint.setStrokeWidth(mStrokeWidth);

            for (LinePoint p : line.getPoints()) {
                float yPercent = (p.getY() - minY) / (maxY - minY);
                float xPercent = (p.getX() - minX) / (maxX - minX);
                if (count == 0) {
                    lastXPixels = padding + (xPercent * usableWidth);
                    lastYPixels = getHeight() - padding - (usableHeight * yPercent);
                } else {
                    newXPixels = padding + (xPercent * usableWidth);
                    newYPixels = getHeight() - padding - (usableHeight * yPercent);
                    canvas.drawLine(lastXPixels, lastYPixels, newXPixels, newYPixels, mPaint);
                    lastXPixels = newXPixels;
                    lastYPixels = newYPixels;
                }
                count++;
            }
        }

        // Draw points
        int color;
        for (LineSet line : mLines) {
            color = line.getColor();
            mPaint.setColor(color);
            mPaint.setStrokeWidth(mStrokeWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            if (line.isShowingPoints()) {
                for (LinePoint p : line.getPoints()) {
                    float yPercent = (p.getY() - minY) / (maxY - minY);
                    float xPercent = (p.getX() - minX) / (maxX - minX);
                    float xPixels = padding + (xPercent * usableWidth);
                    float yPixels = getHeight() - padding - (usableHeight * yPercent);

                    int outerRadius = 2 * mStrokeWidth;

                    mPaint.setColor(color);
                    canvas.drawCircle(xPixels, yPixels, outerRadius, mPaint);
                }
            }
        }
    }

    private int getPixelForDip(int dipValue) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dipValue,
                getResources().getDisplayMetrics());
    }

    public static class LinePoint {

        private final Path mPath = new Path();
        private final Region mRegion = new Region();
        private float mX;
        private float mY;

        public LinePoint() {
            this(0, 0);
        }

        public LinePoint(double x, double y) {
            this((float) x, (float) y);
        }

        public LinePoint(float x, float y) {
            mX = x;
            mY = y;
        }

        public float getX() {
            return mX;
        }

        public void setX(float x) {
            mX = x;
        }

        public float getY() {
            return mY;
        }

        public void setY(float y) {
            mY = y;
        }

        public void setX(double x) {
            mX = (float) x;
        }

        public void setY(double y) {
            mY = (float) y;
        }

        public Region getRegion() {
            return mRegion;
        }

        public Path getPath() {
            return mPath;
        }

        @Override
        public String toString() {
            return "x= " + mX + ", y= " + mY;
        }
    }

    public static class LineSet {
        private ArrayList<LinePoint> mPoints = new ArrayList<LinePoint>();
        private int mColor;
        private boolean mShowPoints = true;

        public int getColor() {
            return mColor;
        }

        public void setColor(int color) {
            mColor = color;
        }

        public ArrayList<LinePoint> getPoints() {
            return mPoints;
        }

        public void setPoints(ArrayList<LinePoint> points) {
            mPoints = points;
        }

        public void addPoint(LinePoint point) {
            LinePoint p;
            for (int i = 0; i < mPoints.size(); i++) {
                p = mPoints.get(i);
                if (point.getX() < p.getX()) {
                    mPoints.add(i, point);
                    return;
                }
            }
            mPoints.add(point);
        }

        public void removePoint(LinePoint point) {
            mPoints.remove(point);
        }

        public LinePoint getPoint(int index) {
            return mPoints.get(index);
        }

        public LinePoint getPoint(float x, float y) {
            LinePoint p;
            for (int i = 0; i < mPoints.size(); i++) {
                p = mPoints.get(i);
                if (p.getX() == x && p.getY() == y) {
                    return p;
                }
            }
            return null;
        }

        public int getSize() {
            return mPoints.size();
        }

        public boolean isShowingPoints() {
            return mShowPoints;
        }

        public void setShowingPoints(boolean showPoints) {
            mShowPoints = showPoints;
        }

    }
}
