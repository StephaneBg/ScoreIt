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
public class PlayerInfo extends FrameLayout {

    private final TextView mName;
    private final TextView mScore;
    private int mPlayer;

    public PlayerInfo(Context context) {
        this(context, null);
    }

    public PlayerInfo(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.player_info, this, true);

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

    public String getName() {
        return mName.getText().toString();
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
