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

package com.sbgapps.scoreit.navigationdrawer;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.adapters.NavigationDrawerAdapter;
import com.sbgapps.scoreit.views.BetterViewAnimator;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerView extends BetterViewAnimator {

    private final NavigationDrawerAdapter mAdapter;
    @InjectView(R.id.drawer_list_view)
    ListView mListView;


    public NavigationDrawerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAdapter = new NavigationDrawerAdapter(context);
    }

    public void replaceWith(List<NavigationDrawerItem> items) {
        mAdapter.replaceWith(items);
        setDisplayedChildId(R.id.drawer_list_view);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        mListView.setAdapter(mAdapter);
    }

    public NavigationDrawerAdapter getAdapter() {
        return mAdapter;
    }
}
