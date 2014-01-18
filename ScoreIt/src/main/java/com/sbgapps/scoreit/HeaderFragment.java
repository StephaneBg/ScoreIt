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
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;

/**
 * Created by sbaiget on 24/12/13.
 */
public class HeaderFragment extends Fragment {

    public static final String TAG = HeaderFragment.class.getName();
    private static final ViewHolder HOLDER = new ViewHolder();

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
        View view = inflater.inflate(R.layout.fragment_header, null);

        HOLDER.name_player1 = (TextView) view.findViewById(R.id.player1);
        HOLDER.name_player2 = (TextView) view.findViewById(R.id.player2);
        HOLDER.name_player3 = (TextView) view.findViewById(R.id.player3);
        HOLDER.name_player4 = (TextView) view.findViewById(R.id.player4);
        HOLDER.name_player5 = (TextView) view.findViewById(R.id.player5);
        HOLDER.name_player3.setBackgroundResource(R.drawable.background_selector);
        HOLDER.name_player4.setBackgroundResource(R.drawable.background_selector);
        HOLDER.name_player5.setBackgroundResource(R.drawable.background_selector);

        HOLDER.score_player1 = (TextView) view.findViewById(R.id.score_player1);
        HOLDER.score_player2 = (TextView) view.findViewById(R.id.score_player2);
        HOLDER.score_player3 = (TextView) view.findViewById(R.id.score_player3);
        HOLDER.score_player4 = (TextView) view.findViewById(R.id.score_player4);
        HOLDER.score_player5 = (TextView) view.findViewById(R.id.score_player5);

        return view;
    }

    public void init() {
        switch (getGameData().getGame()) {
            case GameData.BELOTE_CLASSIC:
            case GameData.BELOTE_COINCHE:
                HOLDER.name_player1.setBackgroundColor(0);
                HOLDER.name_player2.setBackgroundColor(0);
                HOLDER.name_player3.setVisibility(View.GONE);
                HOLDER.score_player3.setVisibility(View.GONE);
                HOLDER.name_player4.setVisibility(View.GONE);
                HOLDER.score_player4.setVisibility(View.GONE);
                HOLDER.name_player5.setVisibility(View.GONE);
                HOLDER.score_player5.setVisibility(View.GONE);
                break;
            case GameData.TAROT_3_PLAYERS:
                HOLDER.name_player1.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player2.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player3.setVisibility(View.VISIBLE);
                HOLDER.score_player3.setVisibility(View.VISIBLE);
                HOLDER.name_player4.setVisibility(View.GONE);
                HOLDER.score_player4.setVisibility(View.GONE);
                HOLDER.name_player5.setVisibility(View.GONE);
                HOLDER.score_player5.setVisibility(View.GONE);
                break;
            case GameData.TAROT_4_PLAYERS:
                HOLDER.name_player1.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player2.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player3.setVisibility(View.VISIBLE);
                HOLDER.score_player3.setVisibility(View.VISIBLE);
                HOLDER.name_player4.setVisibility(View.VISIBLE);
                HOLDER.score_player4.setVisibility(View.VISIBLE);
                HOLDER.name_player5.setVisibility(View.GONE);
                HOLDER.score_player5.setVisibility(View.GONE);
                break;
            case GameData.TAROT_5_PLAYERS:
                HOLDER.name_player1.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player2.setBackgroundResource(R.drawable.background_selector);
                HOLDER.name_player3.setVisibility(View.VISIBLE);
                HOLDER.score_player3.setVisibility(View.VISIBLE);
                HOLDER.name_player4.setVisibility(View.VISIBLE);
                HOLDER.score_player4.setVisibility(View.VISIBLE);
                HOLDER.name_player5.setVisibility(View.VISIBLE);
                HOLDER.score_player5.setVisibility(View.VISIBLE);
                break;
        }
        updateScores();
        updateNames();
        setColoredPoints(false);
    }

    public GameData getGameData() {
        return GameData.getInstance();
    }

    public void updateScores() {
        HOLDER.score_player1.setText(Integer.toString(getAccumulatedScore(Lap.PLAYER_1)));
        HOLDER.score_player2.setText(Integer.toString(getAccumulatedScore(Lap.PLAYER_2)));
        HOLDER.score_player3.setText(Integer.toString(getAccumulatedScore(Lap.PLAYER_3)));
        HOLDER.score_player4.setText(Integer.toString(getAccumulatedScore(Lap.PLAYER_4)));
        HOLDER.score_player5.setText(Integer.toString(getAccumulatedScore(Lap.PLAYER_5)));
    }

    public void updateNames() {
        final GameData gameData = getGameData();
        HOLDER.name_player5.setText(gameData.getPlayerName(Lap.PLAYER_5));
        HOLDER.name_player4.setText(gameData.getPlayerName(Lap.PLAYER_4));
        HOLDER.name_player3.setText(gameData.getPlayerName(Lap.PLAYER_3));
        HOLDER.name_player2.setText(gameData.getPlayerName(Lap.PLAYER_2));
        HOLDER.name_player1.setText(gameData.getPlayerName(Lap.PLAYER_1));
    }

    public void setColoredPoints(boolean colored) {
        final Resources resources = getActivity().getResources();
        if (colored) {
            HOLDER.score_player1.setTextColor(resources.getColor(R.color.color_player1));
            HOLDER.score_player2.setTextColor(resources.getColor(R.color.color_player2));
            HOLDER.score_player3.setTextColor(resources.getColor(R.color.color_player3));
            HOLDER.score_player4.setTextColor(resources.getColor(R.color.color_player4));
            HOLDER.score_player5.setTextColor(resources.getColor(R.color.color_player5));
        } else {
            int color = resources.getColor(R.color.darker_gray);
            HOLDER.score_player1.setTextColor(color);
            HOLDER.score_player2.setTextColor(color);
            HOLDER.score_player3.setTextColor(color);
            HOLDER.score_player4.setTextColor(color);
            HOLDER.score_player5.setTextColor(color);
        }
    }

    private int getAccumulatedScore(int player) {
        int score = 0;
        for (Lap lap : getGameData().getLaps()) {
            score += lap.getScore(player);
        }
        return score;
    }

    static class ViewHolder {
        TextView name_player1;
        TextView name_player2;
        TextView name_player3;
        TextView name_player4;
        TextView name_player5;
        TextView score_player1;
        TextView score_player2;
        TextView score_player3;
        TextView score_player4;
        TextView score_player5;
    }
}
