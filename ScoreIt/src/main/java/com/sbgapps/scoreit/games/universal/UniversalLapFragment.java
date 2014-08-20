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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.doomonafireball.betterpickers.numberpicker.NumberPickerDialogFragment;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.adapter.UniversalLapAdapter;
import com.sbgapps.scoreit.games.LapFragment;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapFragment extends LapFragment
        implements NumberPickerDialogFragment.NumberPickerDialogHandler {

    private UniversalLapAdapter mAdapter;

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_universal, null);

        ListView listView = (ListView) view.findViewById(R.id.list_view);
        ScoreItActivity activity = (ScoreItActivity) getActivity();
        activity.getActionButton().attachToListView(listView);

        mAdapter = new UniversalLapAdapter(this);
        listView.setAdapter(mAdapter);

        return view;
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal,
                                  boolean isNegative, double fullNumber) {
        getLap().setScore(reference, number);
        mAdapter.notifyDataSetChanged();
    }
}
