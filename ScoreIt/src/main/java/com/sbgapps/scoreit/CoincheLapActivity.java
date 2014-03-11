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

import android.os.Bundle;
import android.widget.RadioButton;

import com.sbgapps.scoreit.game.CoincheBeloteLap;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.widget.SeekbarInputPoints;

/**
 * Created by sbaiget on 29/01/14.
 */
public class CoincheLapActivity extends LapActivity {

    private static final LapHolder HOLDER = new LapHolder();
    private static final int[] PROGRESS2POINTS = {80, 90, 100, 110, 120, 130, 140, 150, 250};

    @Override
    public CoincheBeloteLap getLap() {
        return (CoincheBeloteLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_coinche);

        HOLDER.rb_player1 = (RadioButton) findViewById(R.id.rb_player1);
        HOLDER.rb_player2 = (RadioButton) findViewById(R.id.rb_player2);
        HOLDER.rb_belote_none = (RadioButton) findViewById(R.id.rb_belote_none);
        HOLDER.rb_belote_player1 = (RadioButton) findViewById(R.id.rb_belote_player1);
        HOLDER.rb_belote_player2 = (RadioButton) findViewById(R.id.rb_belote_player2);
        HOLDER.rb_coinche_none = (RadioButton) findViewById(R.id.rb_no_coinche);
        HOLDER.rb_coinche = (RadioButton) findViewById(R.id.rb_coinche);
        HOLDER.rb_surcoinche = (RadioButton) findViewById(R.id.rb_surcoinche);
        HOLDER.input_deal = (SeekbarInputPoints) findViewById(R.id.input_deal);
        HOLDER.input_points = (SeekbarInputPoints) findViewById(R.id.input_points);

        if (isDialog()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        switch (getLap().getTaker()) {
            default:
            case Lap.PLAYER_1:
                HOLDER.rb_player1.setChecked(true);
                break;
            case Lap.PLAYER_2:
                HOLDER.rb_player2.setChecked(true);
                break;
        }

        HOLDER.input_deal.setMax(8);
        HOLDER.input_deal.setPoints(getLap().getDeal());

        HOLDER.input_points.setMax(8);
        HOLDER.input_points.setPoints(getLap().getPoints());

        switch (getLap().getBelote()) {
            case Lap.PLAYER_1:
                HOLDER.rb_belote_player1.setChecked(true);
                break;
            case Lap.PLAYER_2:
                HOLDER.rb_belote_player2.setChecked(true);
                break;
            default:
            case Lap.PLAYER_NONE:
                HOLDER.rb_belote_none.setChecked(true);
                break;
        }

        switch (getLap().getCoinche()) {
            default:
            case CoincheBeloteLap.COINCHE_NONE:
                HOLDER.rb_coinche_none.setChecked(true);
                break;
            case CoincheBeloteLap.COINCHE_NORMAL:
                HOLDER.rb_coinche.setChecked(true);
                break;
            case CoincheBeloteLap.COINCHE_DOUBLE:
                HOLDER.rb_surcoinche.setChecked(true);
                break;
        }
    }

    @Override
    public void updateLap() {
        CoincheBeloteLap lap = getLap();
        lap.setTaker(HOLDER.rb_player1.isChecked() ? Lap.PLAYER_1 : Lap.PLAYER_2);
        lap.setDeal(HOLDER.input_deal.getPoints());
        lap.setPoints(HOLDER.input_points.getPoints());
        lap.setBelote(HOLDER.rb_belote_none.isChecked() ? Lap.PLAYER_NONE :
                HOLDER.rb_belote_player1.isChecked() ? Lap.PLAYER_1 : Lap.PLAYER_2);
        lap.setCoinche(HOLDER.rb_coinche_none.isChecked() ? CoincheBeloteLap.COINCHE_NONE :
                HOLDER.rb_coinche.isChecked() ? CoincheBeloteLap.COINCHE_NORMAL
                        : CoincheBeloteLap.COINCHE_DOUBLE);
        lap.setScores();
    }

    @Override
    public int progressToPoints(int progress) {
        return PROGRESS2POINTS[progress];
    }

    @Override
    public int pointsToProgress(int points) {
        int progress = points / 10 - 8;
        progress = (17 == progress) ? 8 : progress;
        return progress;
    }

    static class LapHolder {
        RadioButton rb_player1;
        RadioButton rb_player2;
        RadioButton rb_belote_none;
        RadioButton rb_belote_player1;
        RadioButton rb_belote_player2;
        RadioButton rb_coinche_none;
        RadioButton rb_coinche;
        RadioButton rb_surcoinche;
        SeekbarInputPoints input_deal;
        SeekbarInputPoints input_points;
    }
}
