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

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreListFragment;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class GenericBeloteScoreAdapter extends ScoreListAdapter {

    public GenericBeloteScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return getLayoutInflater().inflate(R.layout.list_item_score_belote, null);
    }

    @Override
    public void fillValues(int position, View convertView) {
        super.fillValues(position, convertView);

        final Resources r = getActivity().getResources();
        View marker = convertView.findViewById(R.id.left_marker);
        marker.setBackgroundColor(getItem(position).isDone() ? r.getColor(R.color.color_player1)
                : r.getColor(R.color.color_player4));
    }

    @Override
    public GenericBeloteLap getItem(int position) {
        return (GenericBeloteLap) super.getItem(position);
    }
}
