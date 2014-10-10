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

package com.sbgapps.scoreit;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.sbgapps.scoreit.adapter.GenericBeloteScoreAdapter;
import com.sbgapps.scoreit.adapter.ScoreListAdapter;
import com.sbgapps.scoreit.adapter.TarotScoreAdapter;
import com.sbgapps.scoreit.adapter.UniversalScoreAdapter;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;

/**
 * Created by sbaiget on 11/11/13.
 */
public class ScoreListFragment extends Fragment {

    public static final String TAG = ScoreListFragment.class.getName();
    private ListView mListView;
    private ScoreListAdapter mAdapter;

    public ListView getListView() {
        return mListView;
    }

    public ScoreListAdapter getListAdapter() {
        return mAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_list, null);

        mListView = (ListView) view.findViewById(android.R.id.list);

        ScoreItActivity activity = (ScoreItActivity) getActivity();
//        if (!activity.isTablet())
//            activity.getActionButton().attachToListView(mListView);

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
        mListView.setAdapter(mAdapter);

        return view;
    }

    public void update() {
        getListAdapter().notifyDataSetChanged();
    }
}
