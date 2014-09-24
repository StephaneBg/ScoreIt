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

package com.sbgapps.scoreit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerBuilder;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.universal.UniversalLap;
import com.sbgapps.scoreit.games.universal.UniversalLapFragment;
import com.sbgapps.scoreit.widget.CircleButton;


/**
 * Created by Stéphane on 19/08/2014.
 */
public class UniversalLapAdapter extends BaseAdapter {

    private final UniversalLapFragment mLapFragment;

    public UniversalLapAdapter(UniversalLapFragment fragment) {
        mLapFragment = fragment;
    }

    @Override
    public int getCount() {
        return getGameHelper().getPlayerCount();
    }

    @Override
    public Player getItem(int position) {
        return getGameHelper().getPlayer(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder h;

        if (null == convertView) {
            h = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater)
                    mLapFragment.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_universal_input, parent, false);

            h.name = (TextView) convertView.findViewById(R.id.tv_name);
            h.score = (CircleButton) convertView.findViewById(R.id.points);
            h.plus = (CircleButton) convertView.findViewById(R.id.btn_plus);
            h.plus_5 = (CircleButton) convertView.findViewById(R.id.btn_plus_5);
            h.plus_10 = (CircleButton) convertView.findViewById(R.id.btn_plus_10);
            h.minus = (CircleButton) convertView.findViewById(R.id.btn_minus);
            h.minus_5 = (CircleButton) convertView.findViewById(R.id.btn_minus_5);
            h.minus_10 = (CircleButton) convertView.findViewById(R.id.btn_minus_10);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        Player player = getItem(position);
        h.name.setText(player.getName());

        final UniversalLap lap = mLapFragment.getLap();
        h.score.setText(Integer.toString(lap.getScore(position)));
        h.score.setColor(player.getColor());

        h.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, 1);
                notifyDataSetChanged();
            }
        });
        h.plus_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, 5);
                notifyDataSetChanged();
            }
        });
        h.plus_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, 10);
                notifyDataSetChanged();
            }
        });

        h.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, -1);
                notifyDataSetChanged();
            }
        });
        h.minus_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, -5);
                notifyDataSetChanged();
            }
        });
        h.minus_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lap.stepScore(position, -10);
                notifyDataSetChanged();
            }
        });

        h.score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NumberPickerBuilder()
                        .setFragmentManager(mLapFragment.getFragmentManager())
                        .setStyleResId(R.style.BetterPickerTheme)
                        .setDecimalVisibility(View.INVISIBLE)
                        .setTargetFragment(mLapFragment)
                        .setReference(position)
                        .show();
            }
        });

        return convertView;
    }

    private GameHelper getGameHelper() {
        return mLapFragment.getGameHelper();
    }

    private class ViewHolder {
        TextView name;
        CircleButton score;
        CircleButton plus;
        CircleButton plus_5;
        CircleButton plus_10;
        CircleButton minus;
        CircleButton minus_5;
        CircleButton minus_10;
    }
}
