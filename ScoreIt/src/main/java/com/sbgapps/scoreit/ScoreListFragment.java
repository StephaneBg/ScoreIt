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

package com.sbgapps.scoreit;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbgapps.scoreit.adapter.ScoreAdapter;
import com.sbgapps.scoreit.view.SwipeListView;

/**
 * Created by sbaiget on 11/11/13.
 */
public class ScoreListFragment extends ListFragment {

    public static final String TAG = ScoreListFragment.class.getName();
    private SwipeListView mListView;

    public SwipeListView getListView() {
        return mListView;
    }

    @Override
    public ScoreAdapter getListAdapter() {
        return (ScoreAdapter) super.getListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score, null);

        mListView = (SwipeListView) view.findViewById(android.R.id.list);
        ScoreAdapter adapter = new ScoreAdapter(getActivity(), mListView);
        setListAdapter(adapter);

        return view;
    }
}
