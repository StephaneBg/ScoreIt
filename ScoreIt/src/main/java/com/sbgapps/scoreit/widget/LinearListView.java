package com.sbgapps.scoreit.widget;

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
		this(context, null);
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
