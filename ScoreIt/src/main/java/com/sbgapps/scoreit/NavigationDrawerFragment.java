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

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sbgapps.scoreit.adapter.DrawerArrayAdapter;
import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.util.TypefaceSpan;
import com.sbgapps.scoreit.view.DrawerEntry;
import com.sbgapps.scoreit.view.DrawerHeader;
import com.sbgapps.scoreit.view.DrawerItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sbaiget on 19/10/13.
 */
public class NavigationDrawerFragment extends Fragment {

    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final int[] GAME_TO_POSITION = new int[]{2, 3, 5, 6, 7, 0};
    private NavigationDrawerListener mListener;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private DrawerArrayAdapter mAdapter;
    private View mFragmentContainerView;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserLearnedDrawer = PreferenceManager.getDefaultSharedPreferences(getActivity())
                .getBoolean(PREF_USER_LEARNED_DRAWER, false);

        final Context context = getActivity();
        final List<DrawerItem> items = new ArrayList<DrawerItem>(8);
        items.add(new DrawerEntry(R.string.universal, GameData.UNIVERSAL));
        items.add(new DrawerHeader(R.string.belote));
        items.add(new DrawerEntry(R.string.classic, GameData.BELOTE_CLASSIC));
        items.add(new DrawerEntry(R.string.coinche, GameData.BELOTE_COINCHE));
        items.add(new DrawerHeader(R.string.tarot));
        items.add(new DrawerEntry(R.string.three_players, GameData.TAROT_3_PLAYERS));
        items.add(new DrawerEntry(R.string.four_players, GameData.TAROT_4_PLAYERS));
        items.add(new DrawerEntry(R.string.five_players, GameData.TAROT_5_PLAYERS));
        mAdapter = new DrawerArrayAdapter(context, items);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });
        mDrawerListView.setAdapter(mAdapter);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, int game) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_navigation_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) return;
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) return;

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).commit();
                }
                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        if (!mUserLearnedDrawer) mDrawerLayout.openDrawer(mFragmentContainerView);

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        mDrawerListView.setItemChecked(GAME_TO_POSITION[game], true);
    }

    private void selectItem(int position) {
        DrawerItem item = mAdapter.getItem(position);
        final int game = item.getGame();
        if (game <= GameData.TAROT_5_PLAYERS)
            mDrawerListView.setItemChecked(position, true);
        if (mDrawerLayout != null) mDrawerLayout.closeDrawer(mFragmentContainerView);
        if (mListener != null) mListener.onNavigationDrawerGameSelected(game);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NavigationDrawerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (mDrawerLayout != null && isDrawerOpen()) {
            menu.clear();
            SpannableString title = new SpannableString(getActivity()
                    .getResources().getString(R.string.app_name));
            TypefaceSpan ts = ((ScoreItActivity) getActivity()).getTypefaceSpan();
            title.setSpan(ts, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getActionBar().setTitle(title);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    private ActionBar getActionBar() {
        return getActivity().getActionBar();
    }

    public static interface NavigationDrawerListener {
        void onNavigationDrawerGameSelected(int game);
    }
}
