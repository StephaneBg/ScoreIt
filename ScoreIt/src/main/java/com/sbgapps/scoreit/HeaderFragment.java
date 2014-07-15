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

package com.sbgapps.scoreit;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.widget.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 24/12/13.
 */
public class HeaderFragment extends Fragment {

    public static final String TAG = HeaderFragment.class.getName();
    private final List<PlayerInfo> mPlayers = new ArrayList<>(2);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getActivity();
        LinearLayout root = new LinearLayout(context);

        for (int player = 0; player < getGame().getPlayerCount(); player++) {
            PlayerInfo pi = new PlayerInfo(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            lp.weight = 1.0f;

            pi.setPlayer(player);
            pi.setLayoutParams(lp);
            pi.setName(getGame().getPlayerName(player));
            pi.setScore(getAccumulatedScore(player));
            pi.setScoreColor(getGame().getPlayerColor(player));
            pi.setNameEditable(getGame().getPlayedGame() != Game.BELOTE
                    && getGame().getPlayedGame() != Game.COINCHE);
            root.addView(pi);
            mPlayers.add(pi);
        }
        return root;
    }

    public GameHelper getGame() {
        return GameHelper.getInstance();
    }

    public void updateScores() {
        for (int player = 0; player < getGame().getPlayerCount(); player++) {
            mPlayers.get(player).setScore(getAccumulatedScore(player));
        }
    }

    private int getAccumulatedScore(int player) {
        int score = 0;
        for (Lap lap : getGame().getLaps()) {
            score += lap.getScore(player);
        }
        return score;
    }
}
