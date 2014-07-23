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
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.belote.GenericBeloteLap;
import com.sbgapps.scoreit.games.tarot.TarotLap;
import com.sbgapps.scoreit.utils.Constants;
import com.sbgapps.scoreit.view.PlayerScore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 23/11/13.
 */
public class ScoreAdapter extends BaseAdapter {

    private final SwipeListView mSwipeListView;
    private final Activity mActivity;

    public ScoreAdapter(Activity activity, SwipeListView listView) {
        mSwipeListView = listView;
        mActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Lap lap = getItem(position);
        final int cnt = getGameHelper().getPlayerCount();
        ViewHolder h;

        if (null == convertView) {
            convertView = LayoutInflater.from(mActivity)
                    .inflate(R.layout.list_item_score, parent, false);
            h = new ViewHolder();

            h.scores = new PlayerScore[cnt];

            LinearLayout ll = (LinearLayout) convertView.findViewById(R.id.score_view);
            for (int i = 0; i < cnt; i++) {
                PlayerScore score = new PlayerScore(mActivity);
                score.setPlayer(i);
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
            switch (getGameHelper().getPlayedGame()) {
                case Game.BELOTE:
                case Game.COINCHE:
                    if (((GenericBeloteLap) lap).getBelote() == i) {
                        h.scores[i].getImage().setImageResource(R.drawable.ic_star);
                    } else {
                        h.scores[i].getImage().setImageDrawable(null);
                    }
                    break;

                case Game.TAROT:
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
                ObjectAnimator animator =
                        ObjectAnimator.ofFloat(view, "alpha", Constants.ALPHA_MAX, Constants.ALPHA_MIN);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setAlpha(Constants.ALPHA_MAX);
                        ((ScoreItActivity) mActivity).removeLap(lap);
                    }
                });
                animator.setDuration(Constants.ANIM_DURATION).start();
            }
        });
        h.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ScoreItActivity) mActivity).editLap(lap);
            }
        });

        return view;
    }

    @Override
    public int getCount() {
        return getGameHelper().getLaps().size();
    }

    @Override
    public Lap getItem(int position) {
        return getGameHelper().getLaps().get(position);
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
        animator.setDuration(Constants.ANIM_DURATION).setStartDelay(Constants.ANIM_OFFSET * idx);
        return animator;
    }

    public GameHelper getGameHelper() {
        return ((ScoreItActivity) mActivity).getGameHelper();
    }

    private static class ViewHolder {
        PlayerScore[] scores;
        ImageButton discard;
        ImageButton edit;
    }
}
