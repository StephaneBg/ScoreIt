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

package com.sbgapps.scoreit.widget.numberpicker;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

public class NumberView extends LinearLayout {

    private TextView mNumber;
    private TextView mMinusLabel;

    /**
     * Instantiate a NumberView
     *
     * @param context the Context in which to inflate the View
     */
    public NumberView(Context context) {
        this(context, null);
    }

    /**
     * Instantiate a NumberView
     *
     * @param context the Context in which to inflate the View
     * @param attrs   attributes that define the title color
     */
    public NumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mNumber = (TextView) findViewById(R.id.number);
        mMinusLabel = (TextView) findViewById(R.id.minus_label);
        mMinusLabel.setText("-");
    }

    /**
     * Set the number shown
     *
     * @param numbersDigit the non-decimal digits
     * @param isNegative   whether it's positive or negative
     */
    public void setNumber(String numbersDigit, boolean isNegative) {
        if (numbersDigit.equals("")) {
            mNumber.setText("");
            mNumber.setEnabled(false);
            mMinusLabel.setVisibility(View.GONE);
        } else {
            mNumber.setText(numbersDigit);
            mNumber.setEnabled(true);
            mMinusLabel.setVisibility(isNegative ? View.VISIBLE : View.GONE);
        }
    }
}