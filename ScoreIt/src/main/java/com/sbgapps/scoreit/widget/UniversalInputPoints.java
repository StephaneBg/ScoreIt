package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.sbgapps.numberpicker.NumberPickerBuilder;
import com.sbgapps.numberpicker.NumberPickerDialogFragment;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.UniversalLapActivity;

/**
 * Created by sbaiget on 06/02/14.
 */
public class UniversalInputPoints extends FrameLayout {

    private final UniversalLapActivity mContext;
    private final TextView mTextViewPoints;
    private int mPoints;

    public UniversalInputPoints(Context context) {
        this(context, null);
    }

    public UniversalInputPoints(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UniversalInputPoints(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = (UniversalLapActivity) context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.universal_input_points, this, true);

        mTextViewPoints = (TextView) findViewById(R.id.edit_points);

        init();
    }

    private void init() {
        ImageButton button = (ImageButton) findViewById(R.id.btn_less);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoints--;
                displayPoints();
            }
        });

        button = (ImageButton) findViewById(R.id.btn_more);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mPoints++;
                displayPoints();
            }
        });

        mTextViewPoints.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(mContext.getFragmentManager())
                        .setDecimalVisibility(View.INVISIBLE)
                        .addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
                            @Override
                            public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                                mPoints = number;
                                displayPoints();
                            }
                        })
                        .show();
            }
        });
    }

    public void setPlayer(int player) {
        TextView name = (TextView) findViewById(R.id.player_name);
        name.setText(mContext.getGameData().getPlayerName(player));
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        mPoints = points;
        displayPoints();
    }

    private void displayPoints() {
        mTextViewPoints.setText(Integer.toString(mPoints));
    }
}
