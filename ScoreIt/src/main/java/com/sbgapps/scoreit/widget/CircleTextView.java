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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

/**
 * Created by Stéphane on 11/10/2014.
 */
public class CircleTextView extends TextView {

    private final Paint mCirclePaint;
    private final float mStrokeWidth;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        final TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CircleTextView, 0, 0);

        mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        int color = ta.getColor(R.styleable.CircleTextView_ct_color, Color.BLACK);
        mCirclePaint.setColor(color);
        final float density = getResources().getDisplayMetrics().density;
        mStrokeWidth = ta.getDimension(R.styleable.CircleTextView_ct_strokeWidth, 2 * density);
        mCirclePaint.setStrokeWidth(mStrokeWidth);

        ta.recycle();
    }

    public void setCircleColor(int color) {
        mCirclePaint.setColor(color);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final float radius = Math.min(getWidth(), getHeight());
        canvas.drawCircle(
                getWidth() / 2,
                getHeight() / 2,
                (radius - mStrokeWidth) / 2,
                mCirclePaint);
        super.onDraw(canvas);
    }
}
