package com.sbgapps.scoreit;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.game.UniversalLap;
import com.sbgapps.scoreit.widget.UniversalInputPoints;

import java.util.ArrayList;

/**
 * Created by sbaiget on 02/02/14.
 */
public class UniversalLapActivity extends LapActivity {

    private final ArrayList<UniversalInputPoints> mPoints = new ArrayList<>(2);

    @Override
    public UniversalLap getLap() {
        return (UniversalLap) super.getLap();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_lap_universal);
        final LinearLayout ll = (LinearLayout) findViewById(R.id.container);

        for (int player = 0; player < getGameData().getPlayerCount(); player++) {
            UniversalInputPoints uip = new UniversalInputPoints(this);
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
}
