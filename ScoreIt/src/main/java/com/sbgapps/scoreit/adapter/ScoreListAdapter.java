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

import android.content.res.Resources;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.widget.LinearListView;

/**
 * Created by sbaiget on 23/11/13.
 */
public abstract class ScoreListAdapter<E extends ScoreListAdapter.ViewHolder> extends RecyclerView.Adapter<E> {

    private final ScoreListFragment mScoreListFragment;

    public ScoreListAdapter(ScoreListFragment fragment) {
        mScoreListFragment = fragment;
    }

    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(getActivity());
    }

    public ScoreItActivity getActivity() {
        return (ScoreItActivity) mScoreListFragment.getActivity();
    }

    public ScoreListFragment getFragment() {
        return mScoreListFragment;
    }

    @Override
    public void onBindViewHolder(final E viewHolder, int i) {
        final Lap lap = getGameHelper().getLaps().get(i);
        viewHolder.mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().removeLap(lap);
                viewHolder.mViewPager.setCurrentItem(0);
            }
        });

        viewHolder.mEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().editLap(lap);
                viewHolder.mViewPager.setCurrentItem(0);
            }
        });
    }

    @Override
    public int getItemCount() {
        return getGameHelper().getLaps().size();
    }

    public GameHelper getGameHelper() {
        return getActivity().getGameHelper();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ViewPager mViewPager;
        public LinearListView mLinearListView;
        public ImageButton mDeleteBtn;
        public ImageButton mEditBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mViewPager = (ViewPager) itemView.findViewById(R.id.viewpager);
            mLinearListView = (LinearListView) itemView.findViewById(R.id.list_score);
            mDeleteBtn = (ImageButton) itemView.findViewById(R.id.action_discard);
            mEditBtn = (ImageButton) itemView.findViewById(R.id.action_edit);

            mViewPager.setAdapter(new ViewPagerAdapter());
            DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
            mViewPager.getLayoutParams().width = displayMetrics.widthPixels;
            mViewPager.requestLayout();
        }
    }

    public static class ViewPagerAdapter extends PagerAdapter {

        public Object instantiateItem(ViewGroup collection, int position) {
            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.primary_content;
                    break;
                case 1:
                    resId = R.id.secondary_content;
                    break;
            }
            return collection.findViewById(resId);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }
    }
}
