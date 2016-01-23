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
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.utils.GameHelper;
import com.sbgapps.scoreit.utils.Utils;

public class ScoreChartFragment extends Fragment {

    public static final String TAG = ScoreChartFragment.class.getName();

    private ViewGroup mContainer;

    public GameHelper getGameHelper() {
        return ((ScoreItActivity) getActivity()).getGameHelper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContainer = (ViewGroup) inflater.inflate(R.layout.fragment_score_chart, null);
        init();
        return mContainer;
    }

    public void init() {
        GameHelper gameHelper = getGameHelper();
        if (0 == gameHelper.getLaps().size()) return;

        LineChartView chartView = new LineChartView(getActivity());
        chartView.setXLabels(XController.LabelPosition.NONE);
        chartView.setYLabels(YController.LabelPosition.NONE);
        chartView.setXAxis(false);
        chartView.setYAxis(false);

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

            chartView.addData(set);
        }

        mContainer.addView(chartView);

        chartView.show(new Animation()
                .setEasing(new CubicEase())
                .setOverlap(0.8f, null)
                .setStartPoint(-1f, 0f)
                .setAlpha(1)
                .setDuration(getActivity().getResources().getInteger(R.integer.anim_medium_time)));
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }
}
