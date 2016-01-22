/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.codetroopers.betterpickers.numberpicker.NumberPickerBuilder;
import com.codetroopers.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.universal.UniversalLap;
import com.sbgapps.scoreit.views.widgets.RingTextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapFragment extends LapFragment
        implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    final List<TextView> mPoints = new ArrayList<>();

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_universal, null);
        if (null == getLap()) return view;

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_players);

        for (int i = 0; i < getGameHelper().getPlayerCount(); i++) {
            View input = inflater.inflate(R.layout.list_item_universal_input, null);
            initView(input, i);
            ll.addView(input);
        }

        return view;
    }

    private void initView(View view, final int position) {
        Player player = getGameHelper().getPlayer(position);

        TextView tv = (TextView) view.findViewById(R.id.tv_name);
        tv.setText(player.getName());

        tv = (RingTextView) view.findViewById(R.id.points);
        tv.setText(Integer.toString(getLap().getScore(position)));
        mPoints.add(tv);

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NumberPickerBuilder npb = new NumberPickerBuilder()
                        .setFragmentManager(UniversalLapFragment.this.getFragmentManager())
                        .setStyleResId(R.style.BetterPickerTheme)
                        .setTargetFragment(UniversalLapFragment.this)
                        .setReference(position);
                npb.show();
            }
        });

        Button btn = (Button) view.findViewById(R.id.btn_plus);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, 1);
                updatePoints(position);
            }
        });
        btn = (Button) view.findViewById(R.id.btn_plus_10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, 10);
                updatePoints(position);
            }
        });
        btn = (Button) view.findViewById(R.id.btn_plus_100);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, 100);
                updatePoints(position);
            }
        });
        btn = (Button) view.findViewById(R.id.btn_minus);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, -1);
                updatePoints(position);
            }
        });
        btn = (Button) view.findViewById(R.id.btn_minus_10);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, -10);
                updatePoints(position);
            }
        });
        btn = (Button) view.findViewById(R.id.btn_minus_100);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLap().stepScore(position, -100);
                updatePoints(position);
            }
        });
    }

    private void updatePoints(int position) {
        mPoints.get(position).setText(Integer.toString(getLap().getScore(position)));
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal, boolean isNegative, double fullNumber) {
        getLap().setScore(reference, number);
        updatePoints(reference);
    }
}
