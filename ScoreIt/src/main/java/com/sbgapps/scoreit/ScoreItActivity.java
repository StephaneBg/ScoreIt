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

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.negusoft.holoaccent.dialog.AccentAlertDialog;
import com.sbgapps.scoreit.game.ClassicBeloteLap;
import com.sbgapps.scoreit.game.CoincheBeloteLap;
import com.sbgapps.scoreit.game.FivePlayerTarotLap;
import com.sbgapps.scoreit.game.FourPlayerTarotLap;
import com.sbgapps.scoreit.game.GameData;
import com.sbgapps.scoreit.game.Lap;
import com.sbgapps.scoreit.game.ThreePlayerTarotLap;
import com.sbgapps.scoreit.util.TypefaceSpan;

public class ScoreItActivity extends AccentActivity
        implements NavigationDrawerFragment.NavigationDrawerListener,
        FragmentManager.OnBackStackChangedListener {

    public static final String KEY_SELECTED_GAME = "selected_game";
    public static final String EXTRA_LAP = "com.sbgapps.scoreit.lap";
    public static final String EXTRA_TABLET = "com.sbgapps.scoreit.tablet";
    public static final String EXTRA_EDIT = "com.sbgapps.scoreit.edit";
    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_LAP_ACTIVITY = 2;
    public static final float DEFAULT_ALPHA = 0.85f;
    private TypefaceSpan mTypefaceSpan;
    private GameData mGameData;
    private SharedPreferences mPreferences;
    private SpannableString mTitle;
    private boolean mIsTablet;
    private int mNameViewId;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ScoreListFragment mScoreListFragment;
    private GraphFragment mGraphFragment;
    private HeaderFragment mHeaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setAccentDecor();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final int game = mPreferences.getInt(KEY_SELECTED_GAME, GameData.BELOTE_CLASSIC);
        mGameData = GameData.getInstance();
        mGameData.init(this, game);

        setContentView(R.layout.activity_scoreit);
        mIsTablet = (null != findViewById(R.id.fragment_container_large));

        final FragmentManager fm = getFragmentManager();
        fm.addOnBackStackChangedListener(this);

        // Init header
        mHeaderFragment = (HeaderFragment) fm.findFragmentById(R.id.fragment_header);
        mHeaderFragment.init();

        // Init fragments
        if (null == savedInstanceState) {
            if (mIsTablet) {
                mScoreListFragment = new ScoreListFragment();
                mGraphFragment = new GraphFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG)
                        .add(R.id.fragment_container_large, mGraphFragment, GraphFragment.TAG)
                        .commit();
                mHeaderFragment.setColoredPoints(true);
            } else {
                mScoreListFragment = new ScoreListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG)
                        .commit();
            }
        } else {
            mGraphFragment = (GraphFragment) fm.findFragmentByTag(GraphFragment.TAG);
            if (null != mGraphFragment) mHeaderFragment.setColoredPoints(true);
            mScoreListFragment = (ScoreListFragment) fm.findFragmentByTag(ScoreListFragment.TAG);
        }

        // Init drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fm.findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), game);

        mTypefaceSpan = new TypefaceSpan(this, "Lobster.otf");
        setTitle();
    }

    public TypefaceSpan getTypefaceSpan() {
        return mTypefaceSpan;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            menu.clear();
            return false;
        } else {
            if (!mIsTablet && 0 == mGameData.getLaps().size()) {
                MenuItem item = menu.findItem(R.id.menu_view);
                item.setVisible(false);
            }
            getActionBar().setTitle(mTitle);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                new AccentAlertDialog.Builder(this)
                        .setMessage(R.string.new_game)
                        .setPositiveButton(
                                R.string.clear,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mGameData.deleteAll();
                                        mHeaderFragment.updateScores();
                                        if (null != mScoreListFragment && mScoreListFragment.isVisible())
                                            mScoreListFragment.getListAdapter().removeAll();
                                        if (null != mGraphFragment && mGraphFragment.isVisible()) {
                                            mGraphFragment.traceGraph();
                                            if (!mIsTablet) getFragmentManager().popBackStack();
                                        }
                                        invalidateOptionsMenu();
                                    }
                                })
                        .setNegativeButton(
                                R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Nothing to do!
                                    }
                                })
                        .create()
                        .show();
                return true;

            case R.id.menu_new:
                Lap lap;
                switch (mGameData.getGame()) {
                    default:
                    case GameData.BELOTE_CLASSIC:
                        lap = new ClassicBeloteLap();
                        break;
                    case GameData.BELOTE_COINCHE:
                        lap = new CoincheBeloteLap();
                        break;
                    case GameData.TAROT_3_PLAYERS:
                        lap = new ThreePlayerTarotLap();
                        break;
                    case GameData.TAROT_4_PLAYERS:
                        lap = new FourPlayerTarotLap();
                        break;
                    case GameData.TAROT_5_PLAYERS:
                        lap = new FivePlayerTarotLap();
                        break;
                }
                showLapActivity(lap, false);
                return true;

            case R.id.menu_view:
                switchScoreViews();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerGameSelected(int game) {
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        mPreferences.edit().putInt(KEY_SELECTED_GAME, game).commit();
        mGameData.setGame(game);
        mHeaderFragment.init();
        setTitle();

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (mIsTablet) {
            mScoreListFragment = new ScoreListFragment();
            mGraphFragment = new GraphFragment();
            ft.setCustomAnimations(R.animator.slide_top_in, R.animator.slide_bottom_out);
            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
            ft.replace(R.id.fragment_container_large, mGraphFragment, GraphFragment.TAG);
            mHeaderFragment.setColoredPoints(true);
        } else {
            mScoreListFragment = new ScoreListFragment();
            ft.setCustomAnimations(R.animator.slide_top_in, R.animator.slide_bottom_out);
            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
        }
        ft.commit();
    }

    @Override
    public void onNavigationDrawerMove(float offset) {
        offset = DEFAULT_ALPHA + (1.0f - DEFAULT_ALPHA) * offset;
        setDecorAlpha(offset);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (RESULT_OK != resultCode) return;

        switch (requestCode) {
            case REQ_PICK_CONTACT:
                Cursor cursor = getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    String name = cursor.getString(columnIndex);
                    switch (mNameViewId) {
                        case R.id.player1:
                            mGameData.setPlayerName(Lap.PLAYER_1, name);
                            break;
                        case R.id.player2:
                            mGameData.setPlayerName(Lap.PLAYER_2, name);
                            break;
                        case R.id.player3:
                            mGameData.setPlayerName(Lap.PLAYER_3, name);
                            break;
                        case R.id.player4:
                            mGameData.setPlayerName(Lap.PLAYER_4, name);
                            break;
                        case R.id.player5:
                            mGameData.setPlayerName(Lap.PLAYER_5, name);
                            break;
                    }
                    TextView textView = (TextView) findViewById(mNameViewId);
                    textView.setText(name);
                }
                mHeaderFragment.updateNames();
                break;

            case REQ_LAP_ACTIVITY:
                updateFragments();
                invalidateOptionsMenu();
                break;
        }
    }

    public void editLap(Lap lap) {
        showLapActivity(lap, true);
    }

    public void removeLap(Lap lap) {
        mGameData.removeLap(lap);
        updateFragments();
    }

    public void editName(View view) {
        if (GameData.BELOTE_CLASSIC == mGameData.getGame()
                || GameData.BELOTE_COINCHE == mGameData.getGame()) {
            // Do not edit names!
            return;
        }

        mNameViewId = view.getId();
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_CONTACT);
    }

    private void updateFragments() {
        mHeaderFragment.updateScores();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyDataSetChanged();
        if (null != mGraphFragment && mGraphFragment.isVisible())
            mGraphFragment.traceGraph();
    }

    private void showLapActivity(Lap lap, boolean edit) {
        Intent intent;
        switch (mGameData.getGame()) {
            default:
            case GameData.BELOTE_CLASSIC:
            case GameData.BELOTE_COINCHE:
                intent = new Intent(this, BeloteLapActivity.class);
                break;
            case GameData.TAROT_3_PLAYERS:
            case GameData.TAROT_4_PLAYERS:
            case GameData.TAROT_5_PLAYERS:
                intent = new Intent(this, TarotLapActivity.class);
                break;
        }
        intent.putExtra(EXTRA_LAP, lap);
        intent.putExtra(EXTRA_TABLET, mIsTablet);
        intent.putExtra(EXTRA_EDIT, edit);

        startActivityForResult(intent, REQ_LAP_ACTIVITY);
        if (null != mScoreListFragment) mScoreListFragment.getListView().closeOpenedItems();
    }

    private void switchScoreViews() {
        if (null != mGraphFragment && mGraphFragment.isVisible()) {
            getFragmentManager().popBackStack();
            mHeaderFragment.setColoredPoints(false);
            return;
        }

        if (null == mGraphFragment)
            mGraphFragment = new GraphFragment();

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_bottom_in,
                        R.animator.slide_top_out,
                        R.animator.slide_top_in,
                        R.animator.slide_bottom_out)
                .addToBackStack(null)
                .replace(R.id.fragment_container, mGraphFragment, GraphFragment.TAG)
                .commit();
        mHeaderFragment.setColoredPoints(true);
    }

    private void setTitle() {
        switch (mGameData.getGame()) {
            default:
            case GameData.BELOTE_CLASSIC:
                mTitle = new SpannableString(getResources().getString(R.string.belote));
                break;
            case GameData.BELOTE_COINCHE:
                mTitle = new SpannableString(getResources().getString(R.string.coinche));
                break;
            case GameData.TAROT_3_PLAYERS:
            case GameData.TAROT_4_PLAYERS:
            case GameData.TAROT_5_PLAYERS:
                mTitle = new SpannableString(getResources().getString(R.string.tarot));
                break;
        }
        mTitle.setSpan(mTypefaceSpan, 0, mTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    @Override
    public void onBackStackChanged() {
        if (mIsTablet) return;
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mHeaderFragment.setColoredPoints(false);
    }
}
