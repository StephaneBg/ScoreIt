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

package com.sbgapps.scoreit.games.universal;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.sbgapps.scoreit.R;

/**
 * Created by Stéphane on 22/07/2014.
 */
public class UniversalInputFragment extends Fragment
        implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    public static final String TAG = UniversalInputFragment.class.getName();

    private TextView mTextViewPoints;
    private int mScore;
    private int mPlayer;

    public static UniversalInputFragment newInstance(int player) {
        UniversalInputFragment fragment = new UniversalInputFragment();

        Bundle bundle = new Bundle();
        bundle.putInt("player", player);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_universal_input, null);

        mPlayer = getArguments().getInt("player");
        TextView textView = (TextView) view.findViewById(R.id.player_name);
        final String name = ((UniversalLapActivity) getActivity())
                .getGameHelper().getPlayer(mPlayer).getName();
        textView.setText(name);

        mTextViewPoints = (TextView) view.findViewById(R.id.edit_points);
        mScore = ((UniversalLapActivity) getActivity()).getLap().getScore(mPlayer);
        mTextViewPoints.setText(Integer.toString(mScore));

        ImageButton button = (ImageButton) view.findViewById(R.id.btn_less);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore--;
                updateScore();
            }
        });

        button = (ImageButton) view.findViewById(R.id.btn_more);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScore++;
                updateScore();
            }
        });

        mTextViewPoints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(getChildFragmentManager())
                        .setStyleResId(R.style.BetterPickerTheme)
                        .setDecimalVisibility(View.INVISIBLE)
                        .setTargetFragment(UniversalInputFragment.this)
                        .show();
            }
        });

        return view;
    }

    private void updateScore() {
        ((UniversalLapActivity) getActivity()).getLap().setScore(mPlayer, mScore);
        mTextViewPoints.setText(Integer.toString(mScore));
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        mScore = number;
        updateScore();
    }
}
