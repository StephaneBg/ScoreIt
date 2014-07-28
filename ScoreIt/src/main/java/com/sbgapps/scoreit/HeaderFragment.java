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

import com.linearlistview.LinearListView;
import com.sbgapps.scoreit.adapter.HeaderAdapter;

/**
 * Created by sbaiget on 24/12/13.
 */
public class HeaderFragment extends Fragment {

    public static final String TAG = HeaderFragment.class.getName();

    private HeaderAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_header, null);

        LinearListView listView = (LinearListView) view.findViewById(R.id.list_header);
        mAdapter = new HeaderAdapter(getActivity());
        listView.setAdapter(mAdapter);
        return view;
    }

    public void update() {
        if (null != mAdapter)
            mAdapter.notifyDataSetChanged();
    }
}
