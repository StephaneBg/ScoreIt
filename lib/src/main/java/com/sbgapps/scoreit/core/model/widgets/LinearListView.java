/*
 * Copyright (c) 2016 SBG Apps
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

package com.sbgapps.scoreit.core.model.widgets;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

public class LinearListView extends LinearLayout {

	private ListAdapter mAdapter;
	private DataSetObserver mDataObserver = new DataSetObserver() {

		@Override
		public void onChanged() {
			setupChildren();
		}

		@Override
		public void onInvalidated() {
			setupChildren();
		}

	};

	public LinearListView(Context context) {
        super(context);
	}

	public LinearListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ListAdapter getAdapter() {
		return mAdapter;
	}

	/**
	 * Sets the data behind this LinearListView.
	 *
	 * @param adapter
	 *            The ListAdapter which is responsible for maintaining the data
	 *            backing this list and for producing a view to represent an
	 *            item in that data set.
	 *
	 * @see #getAdapter()
	 */
	public void setAdapter(ListAdapter adapter) {
		if (mAdapter != null) {
			mAdapter.unregisterDataSetObserver(mDataObserver);
		}

		mAdapter = adapter;

		if (mAdapter != null) {
			mAdapter.registerDataSetObserver(mDataObserver);
		}

		setupChildren();
	}

	private void setupChildren() {
		removeAllViews();

		if (mAdapter == null) {
			return;
		}

		for (int i = 0; i < mAdapter.getCount(); i++) {
			View child = mAdapter.getView(i, null, this);
			addViewInLayout(child, -1, child.getLayoutParams(), true);
		}
	}
}
