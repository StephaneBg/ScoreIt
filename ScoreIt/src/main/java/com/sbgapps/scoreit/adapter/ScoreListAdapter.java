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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

import com.fortysevendeg.swipelistview.SwipeListView;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.utils.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 23/11/13.
 */
public abstract class ScoreListAdapter extends BaseAdapter {

    private final SwipeListView mSwipeListView;
    private final ScoreItActivity mActivity;

    public ScoreListAdapter(ScoreItActivity activity, SwipeListView listView) {
        mSwipeListView = listView;
        mActivity = activity;
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(mActivity);
    }

    public ScoreItActivity getActivity() {
        return mActivity;
    }

    public SwipeListView getSwipeListView() {
        return mSwipeListView;
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

    public void discard(final View view, final Lap lap) {
        getSwipeListView().closeOpenedItems();
        ObjectAnimator animator =
                ObjectAnimator.ofFloat(view, "alpha", Constants.ALPHA_MAX, Constants.ALPHA_MIN);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setAlpha(Constants.ALPHA_MAX);
                getActivity().removeLap(lap);
            }
        });
        animator.setDuration(Constants.ANIM_DURATION).start();
    }

    public Animator createAnimatorForView(View view, int idx) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "x", 0, view.getWidth());
        animator.setDuration(Constants.ANIM_DURATION).setStartDelay(Constants.ANIM_OFFSET * idx);
        return animator;
    }

    public GameHelper getGameHelper() {
        return ((ScoreItActivity) mActivity).getGameHelper();
    }
}
