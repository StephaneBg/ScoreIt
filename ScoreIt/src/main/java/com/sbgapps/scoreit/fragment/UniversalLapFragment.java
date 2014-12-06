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

package com.sbgapps.scoreit.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.adapter.UniversalLapAdapter;
import com.sbgapps.scoreit.games.universal.UniversalLap;
import com.sbgapps.scoreit.util.Utils;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapFragment extends LapFragment {

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_universal, null);

        ListView listView = (ListView) view.findViewById(android.R.id.list);

        if (!((ScoreItActivity) getActivity()).isTablet()) {
            ((ScoreItActivity) getActivity()).getActionButton().attachToListView(listView);

            View header = new View(getActivity());
            header.setLayoutParams(
                    new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT,
                            Utils.dpToPx(4, getResources())));
            header.setBackgroundResource(R.drawable.top_shadow);

            listView.addHeaderView(header);
        }

        if(null == getLap()) return view;

        listView.setAdapter(new UniversalLapAdapter(this));

        return view;
    }
}
