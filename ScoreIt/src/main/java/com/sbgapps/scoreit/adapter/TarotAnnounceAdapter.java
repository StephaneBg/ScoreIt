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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.tarot.TarotAnnounce;

import java.util.List;

/**
 * Created by sbaiget on 04/07/2014.
 */
public class TarotAnnounceAdapter extends BaseAdapter {

    private static final int ANIM_DURATION = 240;
    private static final float ALPHA_MAX = 1.0f;
    private static final float ALPHA_MIN = 0.0f;

    private final Context mContext;
    private final List<TarotAnnounce> mAnnounces;


    public TarotAnnounceAdapter(Context context, List<TarotAnnounce> announces) {
        mContext = context;
        mAnnounces = announces;
    }

    @Override
    public int getCount() {
        return mAnnounces.size();
    }

    @Override
    public TarotAnnounce getItem(int position) {
        return mAnnounces.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final TarotAnnounce ta = getItem(position);
        ViewHolder h;

        if (null == convertView) {
            convertView = LayoutInflater.from(mContext)
                    .inflate(R.layout.list_item_announce, parent, false);

            h = new ViewHolder();
            h.announce = (Spinner) convertView.findViewById(R.id.spinner_announce);
            h.player = (Spinner) convertView.findViewById(R.id.spinner_player);
            h.remove = (ImageButton) convertView.findViewById(R.id.btn_remove_announce);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final View view = convertView;

        h.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", ALPHA_MAX, ALPHA_MIN);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(ALPHA_MAX);
                        mAnnounces.remove(ta);
                        notifyDataSetChanged();
                    }
                });
                animator.setDuration(ANIM_DURATION).start();
            }
        });
        return view;
    }

    private static class ViewHolder {
        Spinner announce;
        Spinner player;
        ImageButton remove;
    }
}
