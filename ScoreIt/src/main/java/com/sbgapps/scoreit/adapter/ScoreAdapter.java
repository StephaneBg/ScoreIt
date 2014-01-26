/*
 * Copyright (C) 2013 SBG Apps
 * http://baiget.fr
 * stephane@baiget.fr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.game.BeloteLap;
import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.game.TarotLap;
import com.sbgapps.scoreit.widget.PlayerScore;
import com.sbgapps.swipelistview.SwipeListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 23/11/13.
 */
public class ScoreAdapter extends BaseAdapter {

    private static final int ANIM_DURATION = 240;
    private static final int ANIM_OFFSET = 60;
    private static final float ALPHA_MAX = 1.0f;
    private static final float ALPHA_MIN = 0.0f;
    private final SwipeListView mSwipeListView;
    private final ScoreItActivity mActivity;

    public ScoreAdapter(Activity activity, SwipeListView listView) {
        mSwipeListView = listView;
        mActivity = (ScoreItActivity) activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Lap lap = getItem(position);
        final int cnt = getGameData().getPlayerCount();
        ViewHolder h;

        if (null == convertView) {
            convertView = LayoutInflater.from(mActivity)
                    .inflate(R.layout.list_item_score, parent, false);
            h = new ViewHolder();

            h.scores = new PlayerScore[cnt];

            LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.score_view);
            for (int i = 0; i < cnt; i++) {
                PlayerScore score = new PlayerScore(mActivity, i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                lp.weight = 1.0f;
                score.setLayoutParams(lp);
                ll.addView(score);
                h.scores[i] = score;
            }
            h.discard = (ImageButton) convertView.findViewById(R.id.action_discard);
            h.edit = (ImageButton) convertView.findViewById(R.id.action_edit);

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final View view = convertView;
        ((SwipeListView) parent).recycle(view, position);

        for (int i = 0; i < cnt; i++) {
            h.scores[i].getScore().setText(Integer.toString(lap.getScore(i)));
            switch (getGameData().getGame()) {
                case GameData.BELOTE_CLASSIC:
                case GameData.BELOTE_COINCHE:
                    if (((BeloteLap) lap).getBelote() == i) {
                        h.scores[i].getImage().setImageResource(R.drawable.ic_star);
                    } else {
                        h.scores[i].getImage().setImageDrawable(null);
                    }
                    break;

                case GameData.TAROT_5_PLAYERS:
                case GameData.TAROT_4_PLAYERS:
                case GameData.TAROT_3_PLAYERS:
                    if (((TarotLap) lap).getTaker() == i) {
                        h.scores[i].getImage().setImageResource(R.drawable.ic_taker);
                    } else {
                        h.scores[i].getImage().setImageDrawable(null);
                    }
                    break;
            }
        }

        h.discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSwipeListView.closeOpenedItems();
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", ALPHA_MAX, ALPHA_MIN);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(ALPHA_MAX);
                        mActivity.removeLap(lap);
                    }
                });
                animator.setDuration(ANIM_DURATION).start();
            }
        });
        h.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.editLap(lap);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return getGameData().getLaps().size();
    }

    @Override
    public Lap getItem(int position) {
        return getGameData().getLaps().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void removeAll() {
        List<View> views = new ArrayList<>();
        List<Animator> animators = new ArrayList<>();

        for (int i = 0; i < mSwipeListView.getChildCount(); i++) {
            View view = mSwipeListView.getChildAt(i);
            views.add(view);
            animators.add(createAnimatorForView(view, i));
        }

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animators);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                notifyDataSetChanged();
            }
        });
        animatorSet.start();
    }

    private Animator createAnimatorForView(View view, int idx) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", 0, view.getWidth());
        animator.setDuration(ANIM_DURATION).setStartDelay(ANIM_OFFSET * idx);
        return animator;
    }

    public GameData getGameData() {
        return GameData.getInstance();
    }

    private static class ViewHolder {
        PlayerScore[] scores;
        ImageButton discard;
        ImageButton edit;
    }
}
