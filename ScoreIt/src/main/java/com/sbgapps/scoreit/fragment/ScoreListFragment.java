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

package com.sbgapps.scoreit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.ScoreItActivity;
import com.sbgapps.scoreit.adapter.GenericBeloteScoreAdapter;
import com.sbgapps.scoreit.adapter.ScoreListAdapter;
import com.sbgapps.scoreit.adapter.TarotScoreAdapter;
import com.sbgapps.scoreit.adapter.UniversalScoreAdapter;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
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
        View view = inflater.inflate(R.layout.fragment_score_list, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(android.R.id.list);
        recyclerView.setHasFixedSize(true);

        mManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mManager);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                closeAllItems();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        ScoreItActivity activity = (ScoreItActivity) getActivity();
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
}
