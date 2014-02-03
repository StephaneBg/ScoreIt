/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.widget.FractionFrameLayout;
import com.sbgapps.scoreit.widget.PlayerInfos;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 24/12/13.
 */
public class HeaderFragment extends Fragment {

    public static final String TAG = HeaderFragment.class.getName();
    private final List<PlayerInfos> mPlayers = new ArrayList<>(2);
    private LinearLayout mParent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_score, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        FractionFrameLayout ffl = new FractionFrameLayout(context);
        mParent = new LinearLayout(context);

        for (int i = 0; i < getGameData().getPlayerCount(); i++) {
            PlayerInfos player = new PlayerInfos(context, i);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight = 1.0f;
            player.setLayoutParams(lp);
            player.getName().setText(getGameData().getPlayerName(i));
            player.getScore().setText(Integer.toString(getAccumulatedScore(i)));
            player.getScore().setTextColor(getGameData().getPlayerColor(i));
//            if (getGameData().getGame() != GameData.BELOTE_CLASSIC
//                    && getGameData().getGame() != GameData.BELOTE_COINCHE)
//                player.getName().setBackgroundResource(R.drawable.scoreit_list_selector_action);
            mParent.addView(player);
            mPlayers.add(player);
        }

        ffl.addView(mParent);
        return ffl;
    }

    public GameData getGameData() {
        return GameData.getInstance();
    }

    public void updateScores() {
        for (int i = 0; i < getGameData().getPlayerCount(); i++) {
            mPlayers.get(i).getScore().setText(Integer.toString(getAccumulatedScore(i)));
        }
    }

    private int getAccumulatedScore(int player) {
        int score = 0;
        for (Lap lap : getGameData().getLaps()) {
            score += lap.getScore(player);
        }
        return score;
    }
}
