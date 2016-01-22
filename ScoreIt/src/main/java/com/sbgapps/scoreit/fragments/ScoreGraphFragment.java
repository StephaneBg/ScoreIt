/*
 * Copyright (c) 2015 SBG Apps
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

import com.db.chart.model.LineSet;
import com.db.chart.view.LineChartView;
import com.db.chart.view.XController;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.CubicEase;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.utils.Constants;
import com.sbgapps.scoreit.utils.Utils;

public class ScoreGraphFragment extends Fragment {

    public static final String TAG = ScoreGraphFragment.class.getName();

    private LineChartView mChartView;

    public Constants.GameHelper getGameHelper() {
        return ((ScoreItActivity) getActivity()).getGameHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_graph, null);
        mChartView = (LineChartView) view.findViewById(R.id.line_chart);
        mChartView.setXLabels(XController.LabelPosition.NONE);
        mChartView.setYLabels(YController.LabelPosition.NONE);
        mChartView.setXAxis(false);
        mChartView.setYAxis(false);

        return view;
    }

    public void update() {
        Constants.GameHelper gameHelper = getGameHelper();
        int lapCnt = gameHelper.getLaps().size();
        mChartView.setVisibility((0 == lapCnt) ? View.INVISIBLE : View.VISIBLE);
        if (0 == lapCnt) return;

        mChartView.reset();
        int score;
        for (int player = 0; player < gameHelper.getPlayerCount(); player++) {
            LineSet set = new LineSet();
            set.addPoint("", 0);
            score = 0;
            for (Lap lap : gameHelper.getLaps()) {
                score += lap.getScore(player);
                set.addPoint("", score);
            }

            set.setColor(gameHelper.getPlayerColor(player))
                    .setThickness(Utils.dpToPx(2, getResources()))
                    .setDotsStrokeThickness(Utils.dpToPx(2, getResources()))
                    .setDotsStrokeColor(gameHelper.getPlayerColor(player))
                    .setDotsColor(getResources().getColor(R.color.white))
                    .setDotsRadius(Utils.dpToPx(4, getResources()));

            mChartView.addData(set);
        }

        mChartView.show(new Animation()
                .setEasing(new CubicEase())
                .setOverlap(0.8f, null)
                .setStartPoint(-1f, 0f)
                .setAlpha(1)
                .setDuration(getActivity().getResources().getInteger(R.integer.anim_medium_time)));
    }

    @Override
    public void onResume() {
        super.onResume();
        update();
    }
}
