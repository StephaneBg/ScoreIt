package com.sbgapps.numberpicker;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sbgapps.numberpicker.widget.ZeroTopPaddingTextView;

public class NumberView extends LinearLayout {

    private ZeroTopPaddingTextView mNumber, mDecimal;
    private ZeroTopPaddingTextView mDecimalSeperator;
    private ZeroTopPaddingTextView mMinusLabel;
    private Typeface mOriginalNumberTypeface;

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

        mNumber = (ZeroTopPaddingTextView) findViewById(R.id.number);
        mDecimal = (ZeroTopPaddingTextView) findViewById(R.id.decimal);
        mDecimalSeperator = (ZeroTopPaddingTextView) findViewById(R.id.decimal_separator);
        mMinusLabel = (ZeroTopPaddingTextView) findViewById(R.id.minus_label);
        if (mNumber != null) {
            mOriginalNumberTypeface = mNumber.getTypeface();
        }
        // Set the lowest time unit with thin font
        if (mNumber != null) {
            mNumber.updatePadding();
        }
        if (mDecimal != null) {
            mDecimal.updatePadding();
        }
    }

    /**
     * Set the number shown
     *
     * @param numbersDigit the non-decimal digits
     * @param decimalDigit the decimal digits
     * @param showDecimal  whether it's a decimal or not
     * @param isNegative   whether it's positive or negative
     */
    public void setNumber(String numbersDigit, String decimalDigit, boolean showDecimal,
                          boolean isNegative) {
        mMinusLabel.setVisibility(isNegative ? View.VISIBLE : View.GONE);
        if (mNumber != null) {
            if (numbersDigit.equals("")) {
                // Set to -
                mNumber.setText("-");
                mNumber.setEnabled(false);
                mNumber.updatePadding();
                mNumber.setVisibility(View.VISIBLE);
            } else if (showDecimal) {
                // Set to bold
                mNumber.setText(numbersDigit);
                mNumber.setTypeface(mOriginalNumberTypeface);
                mNumber.setEnabled(true);
                mNumber.updatePaddingForBoldDate();
                mNumber.setVisibility(View.VISIBLE);
            } else {
                // Set to thin
                mNumber.setText(numbersDigit);
                mNumber.setEnabled(true);
                mNumber.updatePadding();
                mNumber.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimal != null) {
            // Hide digit
            if (decimalDigit.equals("")) {
                mDecimal.setVisibility(View.GONE);
            } else {
                mDecimal.setText(decimalDigit);
                mDecimal.setEnabled(true);
                mDecimal.updatePadding();
                mDecimal.setVisibility(View.VISIBLE);
            }
        }
        if (mDecimalSeperator != null) {
            // Hide separator
            mDecimalSeperator.setVisibility(showDecimal ? View.VISIBLE : View.GONE);
        }
    }
}