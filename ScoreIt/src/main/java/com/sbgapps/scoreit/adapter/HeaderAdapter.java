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

package com.sbgapps.scoreit.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.Player;

/**
 * Created by Stéphane on 16/07/2014.
 */
public class HeaderAdapter extends BaseAdapter {

    private final ScoreItActivity mActivity;
    private final LayoutInflater mInflater;
    private final GameHelper mGameHelper;

    public HeaderAdapter(Activity activity) {
        mActivity = (ScoreItActivity) activity;
        mInflater = LayoutInflater.from(mActivity);
        mGameHelper = mActivity.getGameHelper();
    }

    @Override
    public int getCount() {
        return mGameHelper.getPlayerCount();
    }

    @Override
    public Info getItem(int position) {
        int score = 0;
        for (Lap lap : mGameHelper.getLaps()) {
            score += lap.getScore(position);
        }
        return new Info(mGameHelper.getPlayer(position), score);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (null == convertView) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.list_item_header, parent, false);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.score = (TextView) convertView.findViewById(R.id.score);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Info info = getItem(position);

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.editName(info.mPlayer);
            }
        });

        holder.name.setText(info.mPlayer.getName());
        holder.score.setText(Integer.toString(info.mScore));
        holder.score.setTextColor(info.mPlayer.getColor());

        return convertView;
    }

    private static class ViewHolder {
        TextView name;
        TextView score;
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
