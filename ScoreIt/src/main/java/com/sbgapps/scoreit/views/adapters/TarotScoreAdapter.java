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

import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.fragments.ScoreListFragment;
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.tarot.TarotBid;
import com.sbgapps.scoreit.models.tarot.TarotBonus;
import com.sbgapps.scoreit.models.tarot.TarotFiveLap;
import com.sbgapps.scoreit.models.tarot.TarotLap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class TarotScoreAdapter extends ScoreListAdapter<TarotScoreAdapter.ViewHolder> {

    public TarotScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    public static class ViewHolder extends ScoreListAdapter.ViewHolder {
        public TextView mSummary;
        public View mMaker;

        public ViewHolder(View itemView) {
            super(itemView);
            mSummary = (TextView) itemView.findViewById(R.id.summary);
            mMaker = itemView.findViewById(R.id.marker);
        }
    }

    @Override
    public TarotScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_score_tarot, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TarotScoreAdapter.ViewHolder viewHolder, int i) {
        super.onBindViewHolder(viewHolder, i);

        final TarotLap lap = (TarotLap) getGameHelper().getLaps().get(i);
        final Resources res = getActivity().getResources();

        viewHolder.mLinearListView.setAdapter(
                new LapRowAdapter(this, getGameHelper().getLaps().get(i)));
        viewHolder.mMaker.setBackgroundColor(lap.isDone() ? res.getColor(R.color.game_won)
                : res.getColor(R.color.game_lost));

        int taker = lap.getTaker();
        int partner = Player.PLAYER_NONE;
        String summary = getGameHelper().getPlayer(taker).getName();
        if (5 == getGameHelper().getPlayerCount()) {
            partner = ((TarotFiveLap) lap).getPartner();
            if (partner != taker)
                summary += " & " + getGameHelper().getPlayer(partner).getName();
        }
        summary += " • " + TarotBid.getLiteralBid(getActivity(), lap.getBid().get());
        for (TarotBonus bonus : lap.getBonuses()) {
            if (bonus.getPlayer() == taker ||
                    bonus.getPlayer() == partner) {
                summary += " • " + TarotBonus.getLiteralBonus(getActivity(), bonus.get());
            }
        }
        viewHolder.mSummary.setText(summary);
    }
}
