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

package com.sbgapps.scoreit.numberpicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;

import com.sbgapps.scoreit.R;

public class NumberPickerDialogFragment extends DialogFragment {

    private static final String REFERENCE_KEY = "NumberPickerDialogFragment_ReferenceKey";

    private int mReference = -1;

    /**
     * Create an instance of the Picker (used internally)
     *
     * @param reference an (optional) user-defined reference, helpful when tracking multiple Pickers
     * @return a Picker!
     */
    public static NumberPickerDialogFragment newInstance(int reference) {
        final NumberPickerDialogFragment frag = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_KEY, reference);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null && args.containsKey(REFERENCE_KEY)) {
            mReference = args.getInt(REFERENCE_KEY);
        }

        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final NumberPicker picker = (NumberPicker) inflater.inflate(R.layout.number_picker_dialog, null);
        ImageButton btn = picker.getSetButton();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Activity activity = getActivity();
                final Fragment fragment = getTargetFragment();
                if (activity instanceof NumberPickerDialogHandler) {
                    final NumberPickerDialogHandler act =
                            (NumberPickerDialogHandler) activity;
                    act.onDialogNumberSet(mReference, picker.getNumber());
                } else if (fragment instanceof NumberPickerDialogHandler) {
                    final NumberPickerDialogHandler frag = (NumberPickerDialogHandler) fragment;
                    frag.onDialogNumberSet(mReference, picker.getNumber());
                }
                dismiss();
            }
        });

        return picker;
    }

    @Override
    public void onResume() {
        super.onResume();
        Window window = getDialog().getWindow();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int width = Math.min(
                getActivity().getResources().getDimensionPixelSize(R.dimen.dialog_width),
                dm.widthPixels * 3 / 4);
        int height = window.getAttributes().height;
        window.setLayout(width, height);
    }

    /**
     * This interface allows objects to register for the Picker's set action.
     */
    public interface NumberPickerDialogHandler {

        void onDialogNumberSet(int reference, int number);
    }
}