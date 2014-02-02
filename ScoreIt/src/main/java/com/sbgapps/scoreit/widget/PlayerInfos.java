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

    private final int mPlayer;
    private final TextView mName;
    private final TextView mScore;

    public PlayerInfos(Context context, int player) {
        this(context, null, player);
    }

    public PlayerInfos(Context context, AttributeSet attrs, int player) {
        this(context, attrs, 0, player);
    }

    public PlayerInfos(Context context, AttributeSet attrs, int defStyleAttr, int player) {
        super(context, attrs, defStyleAttr);

        mPlayer = player;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.player_infos, this, true);

        mName = (TextView) findViewById(R.id.name);
        mScore = (TextView) findViewById(R.id.score);
    }

    public int getPlayer() {
        return mPlayer;
    }

    public TextView getScore() {
        return mScore;
    }

    public TextView getName() {
        return mName;
    }
}
