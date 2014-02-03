package com.sbgapps.scoreit;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sbgapps.numberpicker.NumberPickerBuilder;
import com.sbgapps.numberpicker.NumberPickerDialogFragment;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapActivity extends LapActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_universal);

        final TextView text = (TextView) findViewById(R.id.edit_points);
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(getFragmentManager())
                        .setDecimalVisibility(View.INVISIBLE)
                        .setPlusMinusVisibility(View.INVISIBLE)
                        .setStyleResId(R.style.NumberPicker)
                        .addNumberPickerDialogHandler(new NumberPickerDialogFragment.NumberPickerDialogHandler() {
                            @Override
                            public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
                                text.setText(Integer.toString(number));
                            }
                        })
                        .show();
            }
        });
    }

    @Override
    public void updateLap() {

    }
}
