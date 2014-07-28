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

package com.sbgapps.scoreit.games.belote;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.LapFragment;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.view.SeekbarPoints;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 01/11/13.
 */
public class BeloteLapFragment extends LapFragment
        implements SeekbarPoints.OnPointsChangedListener {

    @InjectView(R.id.radio_group_player)
    RadioGroup mRadioGroupPlayer;
    @InjectView(R.id.radio_group_belote)
    RadioGroup mRadioGroupBelote;
    @InjectView(R.id.input_points)
    SeekbarPoints mPoints;

    @Override
    public BeloteLap getLap() {
        return (BeloteLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_belote, null);
        ButterKnife.inject(this, view);

        switch (getLap().getTaker()) {
            case Player.PLAYER_1:
                mRadioGroupPlayer.check(R.id.rb_player1);
                break;
            case Player.PLAYER_2:
                mRadioGroupPlayer.check(R.id.rb_player2);
                break;
        }
        mRadioGroupPlayer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_player1:
                        getLap().setTaker(Player.PLAYER_1);
                        break;
                    case R.id.rb_player2:
                        getLap().setTaker(Player.PLAYER_2);
                        break;
                }
            }
        });

        mPoints.init(
                pointsToProgress(getLap().getPoints()),
                pointsToProgress(250),
                getLap().getPoints());
        mPoints.setOnPointsChangedListener(this, "points");

        switch (getLap().getBelote()) {
            case Player.PLAYER_NONE:
                mRadioGroupBelote.check(R.id.rb_belote_none);
                break;
            case Player.PLAYER_1:
                mRadioGroupBelote.check(R.id.rb_belote_player1);
                break;
            case Player.PLAYER_2:
                mRadioGroupBelote.check(R.id.rb_belote_player2);
                break;
        }
        mRadioGroupBelote.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_belote_none:
                        getLap().setBelote(Player.PLAYER_NONE);
                        break;
                    case R.id.rb_belote_player1:
                        getLap().setBelote(Player.PLAYER_1);
                        break;
                    case R.id.rb_belote_player2:
                        getLap().setBelote(Player.PLAYER_2);
                        break;
                }
            }
        });
        return view;
    }

    private int progressToPoints(int progress) {
        return GenericBeloteLap.PROGRESS2POINTS[progress];
    }

    private int pointsToProgress(int points) {
        int progress = points / 10 - 8;
        progress = (17 == progress) ? 9 : progress;
        return progress;
    }

    @Override
    public int onPointsChanged(int progress, String tag) {
        int points = progressToPoints(progress);
        getLap().setPoints(points);
        return points;
    }
}
