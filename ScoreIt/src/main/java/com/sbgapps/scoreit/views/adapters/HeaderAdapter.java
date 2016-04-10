/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.views.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.models.Game;
import com.sbgapps.scoreit.models.GameManager;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.models.Player;
import com.thebluealliance.spectrum.SpectrumDialog;

/**
 * Created by Stéphane on 16/07/2014.
 */
public class HeaderAdapter extends BaseAdapter {

    private final ScoreItActivity mActivity;
    private final LayoutInflater mInflater;
    private final GameManager mGameManager;

    public HeaderAdapter(Activity activity) {
        mActivity = (ScoreItActivity) activity;
        mInflater = LayoutInflater.from(mActivity);
        mGameManager = mActivity.getGameManager();
    }

    @Override
    public int getCount() {
        return mGameManager.getPlayerCount(true);
    }

    @Override
    public Info getItem(int position) {
        int score = 0;
        for (Lap lap : mGameManager.getLaps()) {
            score += lap.getScore(position);
        }
        return new Info(mGameManager.getPlayer(position), score);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder h;

        if (null == convertView) {
            convertView = mInflater.inflate(R.layout.list_item_header, parent, false);

            h = new ViewHolder();
            h.name = (TextView) convertView.findViewById(R.id.name);
            h.score = (TextView) convertView.findViewById(R.id.score);
            h.marker = convertView.findViewById(R.id.marker);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final Info info = getItem(position);

        if (position < mGameManager.getPlayers().size()) {
            h.name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActivity.editName(position);
                }
            });
        }
        h.name.setText(info.mPlayer.getName());

        h.score.setText(Integer.toString(info.mScore));
        h.score.setTextColor(mGameManager.getPlayerColor(position));
        h.score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SpectrumDialog.Builder(mActivity)
                        .setColors(R.array.player_colors)
                        .setOnColorSelectedListener(new SpectrumDialog.OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(boolean positiveResult, @ColorInt int color) {
                                mGameManager.setPlayerColor(position, color);
                                mActivity.updateFragments();
                            }
                        }).build().show(mActivity.getSupportFragmentManager(), null);
            }
        });

        int mod = -1;
        if (Game.BELOTE != mGameManager.getPlayedGame()
                && Game.COINCHE != mGameManager.getPlayedGame())
            mod = mGameManager.getLaps().size() % mGameManager.getPlayerCount();
        h.marker.setVisibility(position == mod ? View.VISIBLE : View.INVISIBLE);

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView score;
        View marker;
    }

    private class Info {

        final Player mPlayer;
        final int mScore;

        private Info(Player player, int score) {
            mPlayer = player;
            mScore = score;
        }
    }
}
