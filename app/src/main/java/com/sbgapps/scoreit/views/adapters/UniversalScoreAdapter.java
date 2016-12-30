/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.views.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.fragments.ScoreListFragment;

/**
 * Created by sbaiget on 23/11/13.
 */
public class UniversalScoreAdapter extends ScoreListAdapter<ScoreListAdapter.ViewHolder> {

    public UniversalScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    @Override
    public UniversalScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_score_universal, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(UniversalScoreAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);
        viewHolder.mLinearListView.setAdapter(
                new LapRowAdapter(this, getGameHelper().getLaps().get(i)));
    }
}
