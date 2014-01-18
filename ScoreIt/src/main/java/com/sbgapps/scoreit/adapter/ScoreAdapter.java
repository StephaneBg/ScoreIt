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
import android.widget.ImageView;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.game.BeloteLap;
import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.game.TarotLap;

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
        ViewHolder h;

        if (null == convertView) {
            convertView = LayoutInflater.from(mActivity)
                    .inflate(R.layout.list_item_score, parent, false);
            h = new ViewHolder();

            h.view_player1 = convertView.findViewById(R.id.view_player1);
            h.view_player2 = convertView.findViewById(R.id.view_player2);
            h.view_player3 = convertView.findViewById(R.id.view_player3);
            h.view_player4 = convertView.findViewById(R.id.view_player4);
            h.view_player5 = convertView.findViewById(R.id.view_player5);

            h.score_player1 = (TextView) convertView.findViewById(R.id.score_player1);
            h.score_player2 = (TextView) convertView.findViewById(R.id.score_player2);
            h.score_player3 = (TextView) convertView.findViewById(R.id.score_player3);
            h.score_player4 = (TextView) convertView.findViewById(R.id.score_player4);
            h.score_player5 = (TextView) convertView.findViewById(R.id.score_player5);

            h.img_player1 = (ImageView) convertView.findViewById(R.id.img_player1);
            h.img_player2 = (ImageView) convertView.findViewById(R.id.img_player2);
            h.img_player3 = (ImageView) convertView.findViewById(R.id.img_player3);
            h.img_player4 = (ImageView) convertView.findViewById(R.id.img_player4);
            h.img_player5 = (ImageView) convertView.findViewById(R.id.img_player5);

            h.delete = (ImageButton) convertView.findViewById(R.id.action_discard);
            h.edit = (ImageButton) convertView.findViewById(R.id.action_edit);

            switch (getGameData().getGame()) {
                case GameData.BELOTE_CLASSIC:
                case GameData.BELOTE_COINCHE:
                    h.view_player3.setVisibility(View.GONE);
                case GameData.TAROT_3_PLAYERS:
                    h.view_player4.setVisibility(View.GONE);
                case GameData.TAROT_4_PLAYERS:
                    h.view_player5.setVisibility(View.GONE);
            }

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        final View view = convertView;
        ((SwipeListView) parent).recycle(view, position);

        h.score_player1.setText(Integer.toString(lap.getScore(Lap.PLAYER_1)));
        h.score_player2.setText(Integer.toString(lap.getScore(Lap.PLAYER_2)));
        h.score_player3.setText(Integer.toString(lap.getScore(Lap.PLAYER_3)));
        h.score_player4.setText(Integer.toString(lap.getScore(Lap.PLAYER_4)));
        h.score_player5.setText(Integer.toString(lap.getScore(Lap.PLAYER_5)));

        switch (getGameData().getGame()) {
            case GameData.BELOTE_CLASSIC:
            case GameData.BELOTE_COINCHE:
                switch (((BeloteLap) lap).getBelote()) {
                    case Lap.PLAYER_1:
                        h.img_player1.setImageResource(R.drawable.ic_star);
                        h.img_player2.setImageDrawable(null);
                        break;
                    case Lap.PLAYER_2:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageResource(R.drawable.ic_star);
                        break;
                    default:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageDrawable(null);
                }
                break;

            case GameData.TAROT_5_PLAYERS:
            case GameData.TAROT_4_PLAYERS:
            case GameData.TAROT_3_PLAYERS:
                switch (((TarotLap) lap).getTaker()) {
                    case Lap.PLAYER_1:
                        h.img_player1.setImageResource(R.drawable.ic_taker);
                        h.img_player2.setImageDrawable(null);
                        h.img_player3.setImageDrawable(null);
                        h.img_player4.setImageDrawable(null);
                        h.img_player5.setImageDrawable(null);
                        break;
                    case Lap.PLAYER_2:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageResource(R.drawable.ic_taker);
                        h.img_player3.setImageDrawable(null);
                        h.img_player4.setImageDrawable(null);
                        h.img_player5.setImageDrawable(null);
                        break;
                    case Lap.PLAYER_3:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageDrawable(null);
                        h.img_player3.setImageResource(R.drawable.ic_taker);
                        h.img_player4.setImageDrawable(null);
                        h.img_player5.setImageDrawable(null);
                        break;
                    case Lap.PLAYER_4:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageDrawable(null);
                        h.img_player3.setImageDrawable(null);
                        h.img_player4.setImageResource(R.drawable.ic_taker);
                        h.img_player5.setImageDrawable(null);
                        break;
                    case Lap.PLAYER_5:
                        h.img_player1.setImageDrawable(null);
                        h.img_player2.setImageDrawable(null);
                        h.img_player3.setImageDrawable(null);
                        h.img_player4.setImageDrawable(null);
                        h.img_player5.setImageResource(R.drawable.ic_taker);
                        break;
                }
                break;
        }

        h.delete.setOnClickListener(new View.OnClickListener() {
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
        List<View> views = new ArrayList<View>();
        List<Animator> animators = new ArrayList<Animator>();

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
        View view_player1;
        View view_player2;
        View view_player3;
        View view_player4;
        View view_player5;
        TextView score_player1;
        TextView score_player2;
        TextView score_player3;
        TextView score_player4;
        TextView score_player5;
        ImageView img_player1;
        ImageView img_player2;
        ImageView img_player3;
        ImageView img_player4;
        ImageView img_player5;
        ImageButton delete;
        ImageButton edit;
    }
}
