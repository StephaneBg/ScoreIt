package com.sbgapps.scoreit;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.SeekBar;

import com.sbgapps.scoreit.game.CoincheBeloteLap;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.widget.InputPoints;

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
        HOLDER.input_deal = (InputPoints) findViewById(R.id.input_deal);
        HOLDER.input_points = (InputPoints) findViewById(R.id.input_points);

        if (isTablet()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        HOLDER.input_deal.getSeekBar().setMax(8);
        HOLDER.input_deal.getSeekBar().setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        HOLDER.input_deal.getTextView()
                                .setText(Integer.toString(PROGRESS2POINTS[progress]));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        HOLDER.input_deal.getButtonLess().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.input_deal.getSeekBar().getProgress();
                HOLDER.input_deal.getSeekBar().setProgress(points - 1);
            }
        });
        HOLDER.input_deal.getButtonMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.input_deal.getSeekBar().getProgress();
                HOLDER.input_deal.getSeekBar().setProgress(points + 1);
            }
        });

        HOLDER.input_points.getSeekBar().setMax(8);
        HOLDER.input_points.getSeekBar().setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        HOLDER.input_points.getTextView()
                                .setText(Integer.toString(PROGRESS2POINTS[progress]));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

        HOLDER.input_points.getButtonLess().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.input_points.getSeekBar().getProgress();
                HOLDER.input_points.getSeekBar().setProgress(points - 1);
            }
        });
        HOLDER.input_points.getButtonMore().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int points = HOLDER.input_points.getSeekBar().getProgress();
                HOLDER.input_points.getSeekBar().setProgress(points + 1);
            }
        });

        if (Lap.PLAYER_1 == getLap().getTaker()) {
            HOLDER.rb_player1.setChecked(true);
        } else if (Lap.PLAYER_2 == getLap().getTaker()) {
            HOLDER.rb_player2.setChecked(true);
        } else {
            // Not possible
        }

        int progress = getLap().getDeal() / 10 - 8;
        progress = (17 == progress) ? 8 : progress;
        HOLDER.input_deal.getSeekBar().setProgress(progress);

        progress = getLap().getPoints() / 10 - 8;
        progress = (17 == progress) ? 8 : progress;
        HOLDER.input_points.getSeekBar().setProgress(progress);

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
        lap.setDeal(PROGRESS2POINTS[HOLDER.input_deal.getSeekBar().getProgress()]);
        lap.setPoints(PROGRESS2POINTS[HOLDER.input_points.getSeekBar().getProgress()]);
        lap.setBelote(HOLDER.rb_belote_none.isChecked() ? Lap.PLAYER_NONE :
                HOLDER.rb_belote_player1.isChecked() ? Lap.PLAYER_1 : Lap.PLAYER_2);
        lap.setCoinche(HOLDER.rb_coinche_none.isChecked() ? CoincheBeloteLap.COINCHE_NONE :
                HOLDER.rb_coinche.isChecked() ? CoincheBeloteLap.COINCHE_NORMAL
                        : CoincheBeloteLap.COINCHE_DOUBLE);
        lap.setScores();
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
        InputPoints input_deal;
        InputPoints input_points;
    }
}
