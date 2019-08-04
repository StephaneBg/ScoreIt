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

package com.sbgapps.scoreit.legacy.views.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.data.model.Lap;
import com.sbgapps.scoreit.data.model.belote.GenericBeloteLap;

/**
 * Created by Stéphane on 29/07/2014.
 */
public class BeloteLapRowAdapter extends LapRowAdapter {

    private final boolean mRounded;

    BeloteLapRowAdapter(ScoreListAdapter adapter, Lap lap) {
        super(adapter, lap);
        mRounded = mScoreAdapter.getGameHelper().isRounded();
    }

    @Override
    public int getCount() {
        return mScoreAdapter.getGameHelper().getPlayerCount(true);
    }

    @Override
    public Object getItem(int position) {
        if (mRounded) {
            return GenericBeloteLap.getRoundedScore(mLap.getScore(position));
        } else {
            return mLap.getScore(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;

        if (null == convertView) {
            convertView = mScoreAdapter.getLayoutInflater()
                    .inflate(R.layout.list_item_score, parent, false);

            h = new ViewHolder();
            h.score = convertView.findViewById(R.id.score);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        h.score.setText(getItem(position).toString());
        return convertView;
    }

    private class ViewHolder {
        TextView score;
    }
}
