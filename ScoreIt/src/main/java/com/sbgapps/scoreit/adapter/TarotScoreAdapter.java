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

import com.daimajia.swipe.SwipeLayout;
import com.devspark.robototextview.widget.RobotoTextView;
import com.sbgapps.scoreit.R;
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

    public TarotScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View view = getLayoutInflater().inflate(R.layout.list_item_score_tarot, null);
        setSwipeLaout((SwipeLayout) view.findViewById(R.id.swipe));
        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        super.fillValues(position, convertView);

        final Resources r = getActivity().getResources();

        View marker = convertView.findViewById(R.id.left_marker);
        marker.setBackgroundColor(getItem(position).isDone() ? r.getColor(R.color.color_player1)
                : r.getColor(R.color.color_player4));

        final GameHelper gameHelper = getGameHelper();
        final TarotLap lap = getItem(position);
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
        RobotoTextView textView = (RobotoTextView) convertView.findViewById(R.id.summary);
        textView.setText(summary);
    }

    @Override
    public TarotLap getItem(int position) {
        return (TarotLap) super.getItem(position);
    }
}
