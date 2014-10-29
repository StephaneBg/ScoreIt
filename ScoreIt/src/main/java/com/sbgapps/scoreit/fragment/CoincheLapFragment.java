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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.coinche.CoincheLap;
import com.sbgapps.scoreit.widget.BeloteInputPoints;
import com.sbgapps.scoreit.widget.SeekPoints;
import com.sbgapps.scoreit.widget.ToggleGroup;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by sbaiget on 29/01/14.
 */
public class CoincheLapFragment extends GenericBeloteLapFragment
        implements SeekPoints.OnProgressChangedListener {

    @InjectView(R.id.group_team)
    ToggleGroup mTeamGroup;
    @InjectView(R.id.input_bid)
    SeekPoints mBid;
    @InjectView(R.id.input_points)
    BeloteInputPoints mInputPoints;
    @InjectView(R.id.spinner_coinche)
    Spinner mCoincheSpinner;
    @InjectView(R.id.spinner_belote)
    Spinner mBeloteSpinner;

    @Override
    public CoincheLap getLap() {
        return (CoincheLap) super.getLap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lap_coinche, null);
        ButterKnife.inject(this, view);

        switch (getLap().getScorer()) {
            case Player.PLAYER_1:
                mTeamGroup.check(R.id.btn_player_1);
                break;
            case Player.PLAYER_2:
                mTeamGroup.check(R.id.btn_player_2);
                break;
        }

        GameHelper gh = getGameHelper();
        ToggleButton btn = (ToggleButton) view.findViewById(R.id.btn_player_1);
        String name = gh.getPlayer(Player.PLAYER_1).getName();
        btn.setText(name);
        btn.setTextOn(name);
        btn.setTextOff(name);
        btn = (ToggleButton) view.findViewById(R.id.btn_player_2);
        name = gh.getPlayer(Player.PLAYER_2).getName();
        btn.setText(name);
        btn.setTextOn(name);
        btn.setTextOff(name);

        int bid = getLap().getBid();
        mBid.init(getProgressFromBid(bid), 8, Integer.toString(bid));
        mBid.setOnProgressChangedListener(this);

        mInputPoints.init(this);

        // TODO: spinners

        return view;
    }

    @Override
    public String onProgressChanged(int progress) {
        int bid = getBidFromProgress(progress);
        getLap().setBid(bid);
        return Integer.toString(bid);
    }

    private int getProgressFromBid(int bid) {
        if (250 == bid) {
            return 9;
        } else {
            return (bid - 80) / 10;
        }
    }

    private int getBidFromProgress(int progress) {
        if (8 == progress) {
            return 250;
        } else {
            return (10 * progress + 80);
        }
    }
}
