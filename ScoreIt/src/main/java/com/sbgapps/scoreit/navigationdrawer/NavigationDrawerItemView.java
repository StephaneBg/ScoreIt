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
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbgapps.scoreit.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NavigationDrawerItemView extends RelativeLayout {

    final Resources mRes;
    @InjectView(R.id.itemRR)
    RelativeLayout mLayout;
    @InjectView(R.id.navigationDrawerItemTitleTV)
    TextView mItemTitle;
    @InjectView(R.id.separator)
    View mSeparator;

    public NavigationDrawerItemView(Context context) {
        this(context, null);
    }

    public NavigationDrawerItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NavigationDrawerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mRes = context.getResources();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        Typeface tf = Typeface.createFromAsset(mRes.getAssets(), "Roboto-Regular.ttf");
        mItemTitle.setTypeface(tf);
    }

    public void bindTo(NavigationDrawerItem item) {
        requestLayout();
        if (item.isSeparator()) {
            mSeparator.setVisibility(VISIBLE);
            mItemTitle.setVisibility(GONE);
            mLayout.setBackgroundColor(Color.WHITE);
        } else {
            mItemTitle.setText(item.getItemName());
        }

        if (item.isSelected()) {
            mItemTitle.setTextColor(mRes.getColor(R.color.color_primary_dark));
        } else {
            mItemTitle.setTextColor(mRes.getColor(R.color.darker_gray));
        }
    }
}
