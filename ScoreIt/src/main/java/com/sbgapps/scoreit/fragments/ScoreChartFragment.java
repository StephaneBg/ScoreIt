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

package com.sbgapps.scoreit.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.utils.GameHelper;
import com.sbgapps.scoreit.views.widgets.LineChart;

public class ScoreChartFragment extends Fragment {

    public static final String TAG = ScoreChartFragment.class.getName();

    private LineChart mGraph;
    private int[] mScores;
    private int mX;

    public GameHelper getGameHelper() {
        return ((ScoreItActivity) getActivity()).getGameHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_chart, null);
        mGraph = (LineChart) view.findViewById(R.id.line_chart);
        return view;
    }

    public void update() {
        final GameHelper gameHelper = getGameHelper();
        final int lapCnt = gameHelper.getLaps().size();

        mGraph.removeAllLines();
        if (0 == lapCnt) return;

        LineChart.LinePoint p = new LineChart.LinePoint(mX = 0, 0);
        int color;
        for (int i = 0; i < gameHelper.getPlayerCount(); i++) {
            LineChart.LineSet line = new LineChart.LineSet();
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
            LineChart.LinePoint p = new LineChart.LinePoint(mX, mScores[player]);
            mGraph.addPointToLine(player, p);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }
}
