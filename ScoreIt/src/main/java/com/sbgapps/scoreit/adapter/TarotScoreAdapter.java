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
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.linearlistview.LinearListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.tarot.TarotBid;
import com.sbgapps.scoreit.games.tarot.TarotBonus;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class TarotScoreAdapter extends ScoreListAdapter<TarotScoreAdapter.ViewHolder> {

    public final Typeface mTypeface;

    public TarotScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
        mTypeface = Typeface.createFromAsset(getActivity().getResources().getAssets(),
                "Roboto-Medium.ttf");
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mSummary;
        public View mMaker;
        public LinearListView mLinearListView;

        public ViewHolder(View itemView) {
            super(itemView);
            mSummary = (TextView) itemView.findViewById(R.id.summary);
            mMaker = itemView.findViewById(R.id.marker);
            mLinearListView = (LinearListView) itemView.findViewById(R.id.list_score);
        }
    }

    @Override
    public TarotScoreAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_score_tarot, viewGroup, false);
        ViewHolder vh = new ViewHolder(v);
        vh.mSummary.setTypeface(mTypeface);
        return vh;
    }

    @Override
    public void onBindViewHolder(TarotScoreAdapter.ViewHolder viewHolder, int i) {
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
        summary += " • " + TarotBid.getLitteralBid(getActivity(), lap.getBid().get());
        for (TarotBonus bonus : lap.getBonuses()) {
            if (bonus.getPlayer() == taker ||
                    bonus.getPlayer() == partner) {
                summary += " • " + TarotBonus.getLitteralBonus(getActivity(), bonus.get());
            }
        }
        viewHolder.mSummary.setText(summary);
    }
}
