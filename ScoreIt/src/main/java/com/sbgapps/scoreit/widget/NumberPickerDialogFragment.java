/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit.widget;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.sbgapps.scoreit.R;

/**
 * Dialog to set alarm time.
 */
public class NumberPickerDialogFragment extends DialogFragment {

    private static final String REFERENCE_KEY = "NumberPickerDialogFragment_ReferenceKey";
    private static final String MIN_NUMBER_KEY = "NumberPickerDialogFragment_MinNumberKey";
    private static final String MAX_NUMBER_KEY = "NumberPickerDialogFragment_MaxNumberKey";
    private static final String PLAYER_KEY = "NumberPickerDialogFragment_Player";
    private static final String PLUS_MINUS_VISIBILITY_KEY = "NumberPickerDialogFragment_PlusMinusVisibilityKey";
    private static final String DECIMAL_VISIBILITY_KEY = "NumberPickerDialogFragment_DecimalVisibilityKey";

    private Button mSet, mCancel;
    private NumberPicker mPicker;

    private int mReference = -1;

    private Integer mMinNumber = null;
    private Integer mMaxNumber = null;
    private int mPlusMinusVisibility = View.VISIBLE;
    private int mDecimalVisibility = View.VISIBLE;
    private int mPlayer;
    private NumberPickerDialogListener mListener;

    /**
     * Create an instance of the Picker (used internally)
     *
     * @param reference           an (optional) user-defined reference, helpful when tracking multiple Pickers
     * @param minNumber           (optional) the minimum possible number
     * @param maxNumber           (optional) the maximum possible number
     * @param plusMinusVisibility (optional) View.VISIBLE, View.INVISIBLE, or View.GONE
     * @param decimalVisibility   (optional) View.VISIBLE, View.INVISIBLE, or View.GONE
     * @return a Picker!
     */
    public static NumberPickerDialogFragment newInstance(int reference,
                                                         Integer minNumber,
                                                         Integer maxNumber,
                                                         Integer plusMinusVisibility,
                                                         Integer decimalVisibility,
                                                         Integer player) {
        final NumberPickerDialogFragment frag = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, reference);
        if (minNumber != null) {
            args.putInt(MIN_NUMBER_KEY, minNumber);
        }
        if (maxNumber != null) {
            args.putInt(MAX_NUMBER_KEY, maxNumber);
        }
        if (plusMinusVisibility != null) {
            args.putInt(PLUS_MINUS_VISIBILITY_KEY, plusMinusVisibility);
        }
        if (decimalVisibility != null) {
            args.putInt(DECIMAL_VISIBILITY_KEY, decimalVisibility);
        }
        if (player != null) {
            args.putInt(PLAYER_KEY, player);
        }
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(REFERENCE_KEY)) {
            mReference = args.getInt(REFERENCE_KEY);
        }
        if (args != null && args.containsKey(PLUS_MINUS_VISIBILITY_KEY)) {
            mPlusMinusVisibility = args.getInt(PLUS_MINUS_VISIBILITY_KEY);
        }
        if (args != null && args.containsKey(DECIMAL_VISIBILITY_KEY)) {
            mDecimalVisibility = args.getInt(DECIMAL_VISIBILITY_KEY);
        }
        if (args != null && args.containsKey(MIN_NUMBER_KEY)) {
            mMinNumber = args.getInt(MIN_NUMBER_KEY);
        }
        if (args != null && args.containsKey(MAX_NUMBER_KEY)) {
            mMaxNumber = args.getInt(MAX_NUMBER_KEY);
        }
        if (args != null && args.containsKey(PLAYER_KEY)) {
            mPlayer = args.getInt(PLAYER_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.number_picker_dialog, null);
        mSet = (Button) v.findViewById(R.id.set_button);
        mCancel = (Button) v.findViewById(R.id.cancel_button);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mPicker = (NumberPicker) v.findViewById(R.id.number_picker);
        mPicker.setSetButton(mSet);
        mSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double number = mPicker.getEnteredNumber();
                if (mMinNumber != null && mMaxNumber != null && (number < mMinNumber || number > mMaxNumber)) {
                    String errorText = String.format(getString(R.string.min_max_error), mMinNumber, mMaxNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                } else if (mMinNumber != null && number < mMinNumber) {
                    String errorText = String.format(getString(R.string.min_error), mMinNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                } else if (mMaxNumber != null && number > mMaxNumber) {
                    String errorText = String.format(getString(R.string.max_error), mMaxNumber);
                    mPicker.getErrorView().setText(errorText);
                    mPicker.getErrorView().show();
                    return;
                }
                mListener.onDialogNumberSet(mReference, mPicker.getNumber(),
                        mPicker.getDecimal(), mPicker.getIsNegative(), number, mPlayer);
                dismiss();
            }
        });

        mPicker.setDecimalVisibility(mDecimalVisibility);
        mPicker.setPlusMinusVisibility(mPlusMinusVisibility);
        return v;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NumberPickerDialogListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    /**
     * This interface allows objects to register for the Picker's set action.
     */
    public interface NumberPickerDialogListener {

        void onDialogNumberSet(int reference, int number, double decimal,
                               boolean isNegative, double fullNumber, int player);
    }
}