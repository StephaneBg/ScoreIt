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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class GenericBeloteScoreAdapter extends ScoreListAdapter<GenericBeloteScoreAdapter.ViewHolder> {

    public GenericBeloteScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    public static class ViewHolder extends ScoreListAdapter.ViewHolder {
        public View mMaker;

        public ViewHolder(View itemView) {
            super(itemView);
            mMaker = itemView.findViewById(R.id.marker);
        }
    }

    @Override
    public GenericBeloteScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_score_belote, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GenericBeloteScoreAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        final GenericBeloteLap lap = (GenericBeloteLap) getGameHelper().getLaps().get(i);
        final Resources res = getActivity().getResources();

        viewHolder.mLinearListView.setAdapter(new LapRowAdapter(this, lap));
        viewHolder.mMaker.setBackgroundColor(lap.isDone() ? res.getColor(R.color.game_won)
                : res.getColor(R.color.game_lost));
    }
}
