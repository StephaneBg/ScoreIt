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
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.daimajia.swipe.SwipeAdapter;
import com.daimajia.swipe.SwipeLayout;
import com.linearlistview.LinearListView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.util.AdapterViewUtil;
import com.sbgapps.scoreit.util.Constants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by sbaiget on 23/11/13.
 */
public abstract class ScoreListAdapter extends SwipeAdapter {

    private final ScoreListFragment mScoreListFragment;
    private final ScoreItActivity mActivity;

    public ScoreListAdapter(ScoreListFragment fragment) {
        mScoreListFragment = fragment;
        mActivity = (ScoreItActivity) fragment.getActivity();
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mActivity);
    }

    public ScoreItActivity getActivity() {
        return mActivity;
    }

    public ListView getListView() {
        return mScoreListFragment.getListView();
    }

    @Override
    public int getSwipeLayoutResourceId(int i) {
        return R.id.swipe;
    }

    @Override
    public void fillValues(int position, View convertView) {
        final Lap lap = getItem(position);
        final int pos = position;

        LinearListView list = (LinearListView) convertView.findViewById(R.id.list_score);
        list.setAdapter(new LapRowAdapter(this, lap));

        final SwipeLayout swipeLayout = (SwipeLayout) convertView.findViewById(R.id.swipe);
        ImageButton button = (ImageButton) convertView.findViewById(R.id.action_discard);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close(false);
                animateDismiss(pos, lap);
            }
        });

        button = (ImageButton) convertView.findViewById(R.id.action_edit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close();
                getActivity().editLap(lap);
            }
        });
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

        for (int i = 0; i < getListView().getChildCount(); i++) {
            View view = getListView().getChildAt(i);
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

    public Animator createAnimatorForView(View view, int idx) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", 0, view.getWidth());
        animator.setDuration(Constants.ANIM_DURATION).setStartDelay(Constants.ANIM_OFFSET * idx);
        return animator;
    }

    public GameHelper getGameHelper() {
        return mActivity.getGameHelper();
    }


    public void animateDismiss(final int position, final Lap lap) {
        animateDismiss(Arrays.asList(position), lap);
    }

    public void animateDismiss(final Collection<Integer> positions, final Lap lap) {
        final List<Integer> positionsCopy = new ArrayList<>(positions);
        List<View> views = getVisibleViewsForPositions(positionsCopy);

        if (!views.isEmpty()) {
            List<Animator> animators = new ArrayList<>();
            for (final View view : views) {
                animators.add(createAnimatorForView(view));
            }

            AnimatorSet animatorSet = new AnimatorSet();

            Animator[] animatorsArray = new Animator[animators.size()];
            for (int i = 0; i < animatorsArray.length; i++) {
                animatorsArray[i] = animators.get(i);
            }

            animatorSet.playTogether(animatorsArray);
            animatorSet.addListener(new AnimatorListenerAdapter() {

                @Override
                public void onAnimationEnd(final Animator animator) {
                    ((ScoreItActivity) mScoreListFragment.getActivity()).removeLap(lap);
                }
            });
            animatorSet.start();
        } else {
            ((ScoreItActivity) mScoreListFragment.getActivity()).removeLap(lap);
        }
    }

    private List<View> getVisibleViewsForPositions(final Collection<Integer> positions) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < getListView().getChildCount(); i++) {
            View child = getListView().getChildAt(i);
            if (positions.contains(AdapterViewUtil.getPositionForView(getListView(), child))) {
                views.add(child);
            }
        }
        return views;
    }

    private Animator createAnimatorForView(final View view) {
        final ViewGroup.LayoutParams lp = view.getLayoutParams();
        final int originalHeight = view.getHeight();

        ValueAnimator animator = ValueAnimator.ofInt(originalHeight, 0);
        animator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(final Animator animator) {
                lp.height = 0;
                view.setLayoutParams(lp);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(final ValueAnimator valueAnimator) {
                lp.height = (Integer) valueAnimator.getAnimatedValue();
                view.setLayoutParams(lp);
            }
        });

        return animator;
    }
}
