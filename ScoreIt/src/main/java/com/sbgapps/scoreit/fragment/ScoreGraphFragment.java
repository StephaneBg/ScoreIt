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

package com.sbgapps.scoreit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.widget.linechart.Line;
import com.sbgapps.scoreit.widget.linechart.LineGraph;
import com.sbgapps.scoreit.widget.linechart.LinePoint;

public class ScoreGraphFragment extends Fragment {

    public static final String TAG = ScoreGraphFragment.class.getName();

    private LineGraph mGraph;
    private int[] mScores;
    private int mX;

    public GameHelper getGameHelper() {
        return ((ScoreItActivity) getActivity()).getGameHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_graph, null);
        mGraph = (LineGraph) view.findViewById(R.id.line_graph);
        return view;
    }

    public void update() {
        final GameHelper gameHelper = getGameHelper();
        final int lapCnt = gameHelper.getLaps().size();
        mGraph.removeAllLines();
        mGraph.setVisibility((0 == lapCnt) ? View.INVISIBLE : View.VISIBLE);
        if (0 == lapCnt) return;

        LinePoint p = new LinePoint(mX = 0, 0);
        int color;
        for (int i = 0; i < gameHelper.getPlayerCount(); i++) {
            Line line = new Line();
            color = gameHelper.getPlayerColor(i);
            line.setColor(color);
            line.addPoint(p);
            mGraph.addLine(line);
        }

        mScores = new int[gameHelper.getPlayerCount()];
        for (int i = 0; i < lapCnt; i++) {
            Lap lap = gameHelper.getLaps().get(i);
            addLap(lap);
        }
    }

    public void addLap(Lap lap) {
        mX++;
        GameHelper gh = getGameHelper();
        for (int player = 0; player < gh.getPlayerCount(); player++) {
            mScores[player] += lap.getScore(player);
            LinePoint p = new LinePoint(mX, mScores[player]);
            mGraph.addPointToLine(player, p);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }
}
