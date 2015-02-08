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

package com.sbgapps.scoreit.adapters;

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.ui.ScoreItActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Michal Bialas on 19/07/14.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.DrawerViewHolder>
        implements View.OnClickListener {

    private final ScoreItActivity mActivity;
    private final List<NavigationDrawerGameItem> mNavigationItems;
    private final Typeface mTypeface;

    public NavigationDrawerAdapter(ScoreItActivity activity) {
        mActivity = activity;

        mTypeface = Typeface.createFromAsset(activity.getAssets(), "Roboto-Medium.ttf");

        mNavigationItems = new ArrayList<>();
        mNavigationItems.add(new NavigationDrawerGameItem(activity.getString(R.string.universal), Game.UNIVERSAL));
        mNavigationItems.add(new NavigationDrawerGameItem(activity.getString(R.string.tarot), Game.TAROT));
        mNavigationItems.add(new NavigationDrawerGameItem(activity.getString(R.string.belote), Game.BELOTE));
        mNavigationItems.add(new NavigationDrawerGameItem(activity.getString(R.string.coinche), Game.COINCHE));
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_nav_drawer, parent, false);
        return new DrawerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(DrawerViewHolder holder, int position) {
        if (position == mNavigationItems.size()) {
            holder.divider.setVisibility(View.VISIBLE);
            holder.title.setText(mActivity.getString(R.string.donate));
            holder.view.setTag(position);
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(R.drawable.ic_drawer_donate);
        } else if (position == mNavigationItems.size() + 1) {
            holder.title.setText(mActivity.getString(R.string.about));
            holder.view.setTag(position);
            holder.icon.setVisibility(View.VISIBLE);
            holder.icon.setImageResource(R.drawable.ic_drawer_about);
        } else {
            NavigationDrawerGameItem item = mNavigationItems.get(position);
            holder.view.setTag(item.getGameIndex());
            holder.title.setText(item.getGameName());
        }

        holder.title.setTypeface(mTypeface);
        holder.view.setOnClickListener(this);
        if (mActivity.getCurrentGame() == position) {
            holder.view.setActivated(true);
            holder.title.setTextColor(mActivity.getResources().getColor(R.color.color_primary));
        } else {
            holder.view.setActivated(false);
            holder.title.setTextColor(mActivity.getResources().getColor(R.color.nav_drawer_item));
        }
    }

    @Override
    public int getItemCount() {
        return mNavigationItems.size() + 2;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (position == mNavigationItems.size()) {
            mActivity.onDonate();
        } else if (position == mNavigationItems.size() + 1) {
            mActivity.onAbout();
        } else {
            mActivity.onGameSelected(position);
        }
    }

    public static class DrawerViewHolder extends RecyclerView.ViewHolder {

        public DrawerViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            divider = itemView.findViewById(R.id.divider);
            background = itemView.findViewById(R.id.bg_title);
            title = (TextView) itemView.findViewById(R.id.title);
            icon = (ImageView) itemView.findViewById(R.id.icon);
        }

        View view;
        View divider;
        View background;
        TextView title;
        ImageView icon;
    }

    public static class NavigationDrawerGameItem {

        private final int mGameIndex;
        private final String mGameName;

        public NavigationDrawerGameItem(String name, int index) {
            mGameName = name;
            mGameIndex = index;
        }

        public String getGameName() {
            return mGameName;
        }

        public int getGameIndex() {
            return mGameIndex;
        }
    }
}
