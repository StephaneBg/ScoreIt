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
import android.widget.ImageButton;

import com.devspark.robototextview.widget.RobotoTextView;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.linearlistview.LinearListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.ScoreListFragment;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.tarot.TarotBid;
import com.sbgapps.scoreit.games.tarot.TarotBonus;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class TarotScoreAdapter extends ScoreListAdapter {

    public TarotScoreAdapter(ScoreItActivity activity, ScoreListFragment fragment) {
        super(activity, fragment);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TarotLap lap = getItem(position);
        ViewHolder h;

        if (null == convertView) {
            convertView = getLayoutInflater().inflate(R.layout.list_item_score_tarot, parent, false);
            h = new ViewHolder();

            h.marker = convertView.findViewById(R.id.left_marker);
            h.summary = (RobotoTextView) convertView.findViewById(R.id.summary);
            h.list = (LinearListView) convertView.findViewById(R.id.list_score);
            h.discard = (ImageButton) convertView.findViewById(R.id.action_discard);
            h.edit = (ImageButton) convertView.findViewById(R.id.action_edit);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final View view = convertView;
        ((SwipeListView) parent).recycle(view, position);

        Resources r = getActivity().getResources();
        h.marker.setBackgroundColor(lap.isDone() ? r.getColor(R.color.color_player1)
                : r.getColor(R.color.color_player4));

        final GameHelper gameHelper = getGameHelper();
        int taker = lap.getTaker();
        int partner = Player.PLAYER_NONE;
        String summary = gameHelper.getPlayer(taker).getName();
        if (5 == gameHelper.getPlayerCount()) {
            partner = ((TarotFiveLap) lap).getPartner();
            if (partner != taker)
                summary += " & " + gameHelper.getPlayer(partner).getName();
        }
        summary += " • " + TarotBid.getLitteralBid(getActivity(), lap.getBid().get());
        for (TarotBonus bonus : lap.getBonuses()) {
            if (bonus.getPlayer() == taker ||
                    bonus.getPlayer() == partner) {
                summary += " • " + TarotBonus.getLitteralBonus(getActivity(), bonus.get());
            }
        }
        h.summary.setText(summary);

        h.list.setAdapter(new LinearListAdapter(this, lap));

        h.discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDismiss(position, lap);
            }
        });
        h.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().editLap(lap);
            }
        });

        return view;
    }

    @Override
    public TarotLap getItem(int position) {
        return (TarotLap) super.getItem(position);
    }

    private static class ViewHolder {
        View marker;
        RobotoTextView summary;
        LinearListView list;
        ImageButton discard;
        ImageButton edit;
    }
}
