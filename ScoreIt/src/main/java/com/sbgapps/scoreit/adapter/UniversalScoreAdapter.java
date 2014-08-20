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

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.linearlistview.LinearListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.ScoreListFragment;
import com.sbgapps.scoreit.games.Lap;

/**
 * Created by sbaiget on 23/11/13.
 */
public class UniversalScoreAdapter extends ScoreListAdapter {

    public UniversalScoreAdapter(ScoreListFragment fragment) {
        super(fragment);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Lap lap = getItem(position);
        ViewHolder h;

        if (null == convertView) {
            convertView = getLayoutInflater().inflate(R.layout.list_item_score_universal, parent, false);
            h = new ViewHolder();

            h.list = (LinearListView) convertView.findViewById(R.id.score_view);
            h.discard = (ImageButton) convertView.findViewById(R.id.action_discard);
            h.edit = (ImageButton) convertView.findViewById(R.id.action_edit);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final View view = convertView;
        ((SwipeListView) parent).recycle(view, position);

        h.list.setAdapter(new LinearListAdapter(this, lap));

        h.discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateDismiss(position, lap);
            }
        });
        h.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().editLap(lap);
            }
        });

        return view;
    }

    private static class ViewHolder {
        LinearListView list;
        ImageButton discard;
        ImageButton edit;
    }
}
