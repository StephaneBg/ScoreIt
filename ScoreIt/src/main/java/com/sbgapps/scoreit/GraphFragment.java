/*
 * Copyright 2013 SBG Apps
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package com.sbgapps.scoreit;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;
import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;

public class GraphFragment extends Fragment {

    public static final String COLOR = "#FF7D7D7D";
    public static final String TAG = GraphFragment.class.getName();
    private final int[] mColors = new int[Lap.PLAYER_COUNT_MAX];
    private final Line[] mLines = new Line[Lap.PLAYER_COUNT_MAX];
    private LineGraph mGraph;
    private int[] mScores;
    private int mX;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Resources r = activity.getResources();
        mColors[Lap.PLAYER_1] = r.getColor(R.color.color_player1);
        mColors[Lap.PLAYER_2] = r.getColor(R.color.color_player2);
        mColors[Lap.PLAYER_3] = r.getColor(R.color.color_player3);
        mColors[Lap.PLAYER_4] = r.getColor(R.color.color_player4);
        mColors[Lap.PLAYER_5] = r.getColor(R.color.color_player5);
    }

    public GameData getGameData() {
        return GameData.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_graph, null);
        mGraph = (LineGraph) view.findViewById(R.id.line_graph);
        return view;
    }

    public void traceGraph() {
        mGraph.removeAllLines();
        final int lapCnt = getGameData().getLaps().size();
        mGraph.setVisibility((0 == lapCnt) ? View.INVISIBLE : View.VISIBLE);
        if (0 == lapCnt) return;

        final int playerCnt = getGameData().getPlayerCount();
        for (int i = 0; i < playerCnt; i++) {
            mLines[i] = new Line();
            mLines[i].setColor(mColors[i]);
            mGraph.addLine(mLines[i]);
        }

        LinePoint p = new LinePoint(mX = 0, 0);
        p.setColor(COLOR);
        for (int player = 0; player < playerCnt; player++) {
            mGraph.addPointToLine(player, p);
        }

        mScores = new int[Lap.PLAYER_COUNT_MAX];
        for (int i = 0; i < lapCnt; i++) {
            Lap lap = getGameData().getLaps().get(i);
            addLap(lap);
        }
    }

    public void addLap(Lap lap) {
        mX++;
        final int playerCnt = getGameData().getPlayerCount();
        for (int player = 0; player < playerCnt; player++) {
            mScores[player] += lap.getScore(player);
            LinePoint p = new LinePoint(mX, mScores[player]);
            p.setColor(COLOR);
            mGraph.addPointToLine(player, p);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        traceGraph();
    }
}
