package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 26/01/14.
 */
public class InputPoints extends FrameLayout {

    final ImageButton mButtonLess;
    final ImageButton mButtonMore;
    final TextView mTextViewPoints;
    final SeekBar mSeekBarPoints;

    public InputPoints(Context context) {
        this(context, null);
    }

    public InputPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InputPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.input_points, this, true);

        mButtonLess = (ImageButton) findViewById(R.id.btn_less);
        mButtonMore = (ImageButton) findViewById(R.id.btn_more);
        mTextViewPoints = (TextView) findViewById(R.id.textview_points);
        mSeekBarPoints = (SeekBar) findViewById(R.id.seekbar_points);
    }

    public ImageButton getButtonLess() {
        return mButtonLess;
    }

    public ImageButton getButtonMore() {
        return mButtonMore;
    }

    public TextView getTextViewPoints() {
        return mTextViewPoints;
    }

    public SeekBar getSeekBarPoints() {
        return mSeekBarPoints;
    }
}
