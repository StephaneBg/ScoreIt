package com.sbgapps.scoreit;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.game.UniversalLap;
import com.sbgapps.scoreit.widget.NumberPickerDialogFragment;
import com.sbgapps.scoreit.widget.PickerInputPoints;

import java.util.ArrayList;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapActivity extends LapActivity
        implements NumberPickerDialogFragment.NumberPickerDialogListener {

    private static final String KEY_SCORES = "scores";
    private final ArrayList<PickerInputPoints> mPoints = new ArrayList<>(2);

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_universal);

        if (isDialog()) {
            findViewById(R.id.btn_cancel).setOnClickListener(this);
            findViewById(R.id.btn_confirm).setOnClickListener(this);
        }

        if (null != savedInstanceState) {
            for (int player = 0; player < getGameData().getPlayerCount(); player++) {
                getLap().setScore(player, savedInstanceState.getIntArray(KEY_SCORES)[player]);
            }
        }

        final LinearLayout ll = (LinearLayout) findViewById(R.id.container);
        for (int player = 0; player < getGameData().getPlayerCount(); player++) {
            PickerInputPoints uip = new PickerInputPoints(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            uip.setLayoutParams(lp);
            uip.setPlayer(player);
            uip.setPoints(getLap().getScore(player));
            ll.addView(uip);
            mPoints.add(uip);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] points = new int[Lap.PLAYER_COUNT_MAX];
        for (int player = 0; player < getGameData().getPlayerCount(); player++) {
            points[player] = mPoints.get(player).getPoints();
        }
        outState.putIntArray(KEY_SCORES, points);
    }

    @Override
    public void updateLap() {
        UniversalLap lap = getLap();
        for (int player = 0; player < getGameData().getPlayerCount(); player++) {
            lap.setScore(player, mPoints.get(player).getPoints());
        }
    }

    @Override
    public int progressToPoints(int progress) {
        return progress;
    }

    @Override
    public int pointsToProgress(int points) {
        return points;
    }

    @Override
    public void onDialogNumberSet(int reference, int number, double decimal,
                                  boolean isNegative, double fullNumber, int player) {
        mPoints.get(player).setPoints(number);
    }
}
