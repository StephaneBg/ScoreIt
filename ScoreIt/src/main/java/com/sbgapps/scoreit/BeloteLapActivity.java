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

import android.os.Bundle;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sbgapps.scoreit.game.BeloteLap;
import com.sbgapps.scoreit.game.Lap;

/**
 * Created by sbaiget on 01/11/13.
 */
public class BeloteLapActivity extends LapActivity {

    private static final LapHolder HOLDER = new LapHolder();
    private static final int[] PROGRESS2POINTS = {80, 90, 100, 110, 120, 130, 140, 150, 160, 250};

    @Override
    public BeloteLap getLap() {
        return (BeloteLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_belote);

        HOLDER.rb_player1 = (RadioButton) findViewById(R.id.rb_player1);
        HOLDER.rb_player2 = (RadioButton) findViewById(R.id.rb_player2);
        HOLDER.rb_belote_none = (RadioButton) findViewById(R.id.rb_belote_none);
        HOLDER.rb_belote_player1 = (RadioButton) findViewById(R.id.rb_belote_player1);
        HOLDER.rb_belote_player2 = (RadioButton) findViewById(R.id.rb_belote_player2);
        HOLDER.tv_points = (TextView) findViewById(R.id.textview_points);
        HOLDER.sb_points = (SeekBar) findViewById(R.id.seekbar_points);
        HOLDER.btn_less = (ImageButton) findViewById(R.id.btn_less);
        HOLDER.btn_more = (ImageButton) findViewById(R.id.btn_more);

        if (isTablet()) {
            ViewStub stub = (ViewStub) findViewById(R.id.viewstub_buttons);
            View view = stub.inflate();
            view.findViewById(R.id.btn_cancel).setOnClickListener(this);
            view.findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        HOLDER.sb_points.setMax(9);
        HOLDER.sb_points.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                HOLDER.tv_points.setText(Integer.toString(PROGRESS2POINTS[progress]));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        HOLDER.btn_less.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.sb_points.getProgress();
                HOLDER.sb_points.setProgress(points - 1);
            }
        });
        HOLDER.btn_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.sb_points.getProgress();
                HOLDER.sb_points.setProgress(points + 1);
            }
        });

        if (Lap.PLAYER_1 == getLap().getTaker()) {
            HOLDER.rb_player1.setChecked(true);
        } else if (Lap.PLAYER_2 == getLap().getTaker()) {
            HOLDER.rb_player2.setChecked(true);
        } else {
            // Not possible
        }

        int progress = getLap().getPoints() / 10 - 8;
        progress = (17 == progress) ? 9 : progress;
        HOLDER.sb_points.setProgress(progress);

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
    }

    @Override
    public void updateLap() {
        BeloteLap lap = getLap();
        lap.setTaker(HOLDER.rb_player1.isChecked() ? Lap.PLAYER_1 : Lap.PLAYER_2);
        lap.setPoints(PROGRESS2POINTS[HOLDER.sb_points.getProgress()]);
        lap.setBelote(HOLDER.rb_belote_none.isChecked() ? Lap.PLAYER_NONE :
                HOLDER.rb_belote_player1.isChecked() ? Lap.PLAYER_1 : Lap.PLAYER_2);
        lap.setScores();
    }

    static class LapHolder {
        RadioButton rb_player1;
        RadioButton rb_player2;
        RadioButton rb_belote_none;
        RadioButton rb_belote_player1;
        RadioButton rb_belote_player2;
        TextView tv_points;
        SeekBar sb_points;
        ImageButton btn_less;
        ImageButton btn_more;
    }
}
