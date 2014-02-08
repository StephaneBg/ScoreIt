package com.sbgapps.scoreit.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

/**
 * Created by sbaiget on 21/01/14.
 */
public class PlayerInfos extends FrameLayout {

    private final TextView mName;
    private final TextView mScore;
    private int mPlayer;

    public PlayerInfos(Context context) {
        this(context, null);
    }

    public PlayerInfos(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerInfos(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.player_infos, this, true);

        mName = (TextView) findViewById(R.id.name);
        mScore = (TextView) findViewById(R.id.score);
    }

    public int getPlayer() {
        return mPlayer;
    }

    public void setPlayer(int player) {
        mPlayer = player;
    }

    public void setName(String name) {
        mName.setText(name);
    }

    public void setNameEditable(boolean editable) {
        mName.setClickable(editable);
    }

    public void setScore(int score) {
        mScore.setText(Integer.toString(score));
    }

    public void setScoreColor(int color) {
        mScore.setTextColor(color);
    }
}
