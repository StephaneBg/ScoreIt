/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.adapters.GenericBeloteScoreAdapter;
import com.sbgapps.scoreit.adapters.ScoreListAdapter;
import com.sbgapps.scoreit.adapters.TarotScoreAdapter;
import com.sbgapps.scoreit.adapters.UniversalScoreAdapter;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.ui.ScoreItActivity;
import com.sbgapps.scoreit.widget.RevealView;

/**
 * Created by sbaiget on 11/11/13.
 */
public class ScoreListFragment extends Fragment {

    public static final String TAG = ScoreListFragment.class.getName();

    private ScoreListAdapter mAdapter;
    private LinearLayoutManager mManager;

    public ScoreListAdapter getListAdapter() {
        return mAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ScoreItActivity activity = (ScoreItActivity) getActivity();

        View view = inflater.inflate(R.layout.fragment_score_list, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        mManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mManager);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                closeAllItems();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        GameHelper gameHelper = activity.getGameHelper();
        switch (gameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                mAdapter = new UniversalScoreAdapter(this);
                break;
            case Game.BELOTE:
            case Game.COINCHE:
                mAdapter = new GenericBeloteScoreAdapter(this);
                break;
            case Game.TAROT:
                mAdapter = new TarotScoreAdapter(this);
                break;
        }
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    public void closeAllItems() {
        closeOthers(null);
    }

    public void closeOthers(RevealView revealView) {
        for (int i = 0; i < mManager.getChildCount(); i++) {
            View view = mManager.getChildAt(i);
            if (null != view) {
                RevealView rv = (RevealView) view.findViewById(R.id.reveal);
                if (!rv.equals(revealView)) rv.hide();
            }
        }
    }

    public void update() {
        closeAllItems();
        getListAdapter().notifyDataSetChanged();
    }

    class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = ContextCompat.getDrawable(context, R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }
}
