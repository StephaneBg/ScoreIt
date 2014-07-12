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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.faizmalkani.floatingactionbutton.FloatingActionButton;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.util.TypefaceSpan;
import com.sbgapps.scoreit.util.Utils;
import com.sbgapps.scoreit.view.SwipeListView;
import com.sbgapps.scoreit.widget.PlayerInfo;

import uk.co.chrisjenx.calligraphy.CalligraphyUtils;

public class ScoreItActivity extends BaseActivity
        implements NavigationDrawerFragment.NavigationDrawerListener {

    public static final String EXTRA_LAP = "com.sbgapps.scoreit.lap";
    public static final String EXTRA_EDIT = "com.sbgapps.scoreit.edit";
    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_LAP_ACTIVITY = 2;
    private TypefaceSpan mTypefaceSpan;
    private GameHelper mGameHelper;
    private SpannableString mTitle;
    private boolean mIsTablet;
    private PlayerInfo mEditedName;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private ScoreListFragment mScoreListFragment;
    private GraphFragment mGraphFragment;
    private HeaderFragment mHeaderFragment;
    private FloatingActionButton mFloatingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTranslucentStatusBar();
        mGameHelper = GameHelper.getInstance().init(this);

        setContentView(R.layout.activity_scoreit);
        mIsTablet = (null != findViewById(R.id.fragment_container_large));
        mFloatingButton = (FloatingActionButton) findViewById(R.id.fab);

        final FragmentManager fm = getFragmentManager();

        // Init fragments
        if (null == savedInstanceState) {
            mHeaderFragment = new HeaderFragment();
            if (mIsTablet) {
                mScoreListFragment = new ScoreListFragment();
                mGraphFragment = new GraphFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_header, mHeaderFragment, HeaderFragment.TAG)
                        .add(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG)
                        .add(R.id.fragment_container_large, mGraphFragment, GraphFragment.TAG)
                        .commit();
            } else {
                mScoreListFragment = new ScoreListFragment();
                fm.beginTransaction()
                        .add(R.id.fragment_header, mHeaderFragment, HeaderFragment.TAG)
                        .add(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG)
                        .commit();
            }
        } else {
            mHeaderFragment = (HeaderFragment) fm.findFragmentByTag(HeaderFragment.TAG);
            mGraphFragment = (GraphFragment) fm.findFragmentByTag(GraphFragment.TAG);
            mScoreListFragment = (ScoreListFragment) fm.findFragmentByTag(ScoreListFragment.TAG);
        }

        // Init drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fm.findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), mGameHelper.getPlayedGame());

        mTypefaceSpan = new TypefaceSpan(this, "Lobster.otf");
        setTitle();
    }

    public TypefaceSpan getTypefaceSpan() {
        return mTypefaceSpan;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (null != mScoreListFragment) {
            SwipeListView slv = mScoreListFragment.getListView();
            if (null != slv) slv.closeOpenedItems();
        }
        mGameHelper.saveGame();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mNavigationDrawerFragment.isDrawerOpen()) {
            menu.clear();
            return false;
        } else {
            MenuItem item;
            if (0 == mGameHelper.getLaps().size()) {
                if (!mIsTablet) {
                    item = menu.findItem(R.id.menu_view);
                    item.setVisible(false);
                }
                item = menu.findItem(R.id.menu_clear);
                item.setVisible(false);
            }
            item = menu.findItem(R.id.menu_count);
            item.setVisible(Game.UNIVERSAL == mGameHelper.getPlayedGame());
            getActionBar().setTitle(mTitle);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                showClearDialog();
                return true;

            case R.id.menu_view:
                switchScoreViews();
                return true;

            case R.id.menu_count:
                showPlayerCountDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationDrawerGameSelected(int position) {
        if (-1 == position) {
            // About
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return;
        }

        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        mGameHelper.setPlayedGame(position);
        setTitle();

        reloadFragments();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        if (RESULT_OK != resultCode) return;

        switch (requestCode) {
            case REQ_PICK_CONTACT:
                Cursor cursor = getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    name = cursor.getString(columnIndex);
                    nameEdited(name);
                }
                break;

            case REQ_LAP_ACTIVITY:
                updateFragments();
                invalidateOptionsMenu();
                break;
        }
    }

    public void addLap(View view) {
        addLap();
    }

    public void addLap() {
        showLapActivity(null);
    }

    public void editLap(Lap lap) {
        showLapActivity(lap);
    }

    public void removeLap(Lap lap) {
        mGameHelper.removeLap(lap);
        updateFragments();
    }

    public void editName(View view) {
        mEditedName = (PlayerInfo) view.getParent().getParent();
        showEditNameActionChoices();
    }

    private void reloadFragments() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        mHeaderFragment = new HeaderFragment();
        ft.replace(R.id.fragment_header, mHeaderFragment, HeaderFragment.TAG);
        ft.commit();

        ft = getFragmentManager().beginTransaction();
        if (mIsTablet) {
            ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            mScoreListFragment = new ScoreListFragment();
            mGraphFragment = new GraphFragment();
            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
            ft.replace(R.id.fragment_container_large, mGraphFragment, GraphFragment.TAG);
        } else {
            ft.setCustomAnimations(R.animator.slide_top_in, R.animator.slide_top_out);
            mScoreListFragment = new ScoreListFragment();
            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
        }
        ft.commit();
    }

    private void updateFragments() {
        mHeaderFragment.updateScores();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyDataSetChanged();
        if (null != mGraphFragment && mGraphFragment.isVisible())
            mGraphFragment.traceGraph();
    }

    private void showLapActivity(Lap lap) {
        Intent intent;
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                intent = new Intent(this, UniversalLapActivity.class);
                break;
            case Game.BELOTE_CLASSIC:
                intent = new Intent(this, BeloteLapActivity.class);
                break;
            case Game.BELOTE_COINCHE:
                intent = new Intent(this, CoincheLapActivity.class);
                break;
            case Game.TAROT_3_PLAYERS:
            case Game.TAROT_4_PLAYERS:
            case Game.TAROT_5_PLAYERS:
                intent = new Intent(this, TarotLapActivity.class);
                break;
        }
        if (null == lap) {
            // New lap
            intent.putExtra(EXTRA_EDIT, false);
        } else {
            // Edit lap
            int i = mGameHelper.getLaps().indexOf(lap);
            intent.putExtra(EXTRA_LAP, i);
            intent.putExtra(EXTRA_EDIT, true);
        }

        startActivityForResult(intent, REQ_LAP_ACTIVITY);
    }

    private void switchScoreViews() {
        if (null != mGraphFragment && mGraphFragment.isVisible()) {
            getFragmentManager().popBackStack();
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
    }

    private void setTitle() {
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                mTitle = new SpannableString(getResources().getString(R.string.universal));
                break;
            case Game.BELOTE_CLASSIC:
                mTitle = new SpannableString(getResources().getString(R.string.belote));
                break;
            case Game.BELOTE_COINCHE:
                mTitle = new SpannableString(getResources().getString(R.string.coinche));
                break;
            case Game.TAROT_3_PLAYERS:
            case Game.TAROT_4_PLAYERS:
            case Game.TAROT_5_PLAYERS:
                mTitle = new SpannableString(getResources().getString(R.string.tarot));
                break;
        }
        mTitle.setSpan(mTypefaceSpan, 0, mTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void nameEdited(String name) {
        mGameHelper.setPlayerName(mEditedName.getPlayer(), name);
        mEditedName.setName(name);
    }

    private void showClearDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.clear_game)
                .setMessage(R.string.sure)
                .setPositiveButton(
                        R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameHelper.deleteAll();
                                mHeaderFragment.updateScores();
                                if (null != mScoreListFragment && mScoreListFragment.isVisible())
                                    mScoreListFragment.getListAdapter().removeAll();
                                if (null != mGraphFragment && mGraphFragment.isVisible()) {
                                    mGraphFragment.traceGraph();
                                    if (!mIsTablet) getFragmentManager().popBackStack();
                                }
                                invalidateOptionsMenu();
                            }
                        }
                )
                .setNegativeButton(
                        R.string.no,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing to do!
                            }
                        }
                )
                .create();
        dialog.show();
        Utils.colorizeDialog(dialog);
    }

    private void showPlayerCountDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.player_number)
                .setItems(R.array.player_count,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameHelper.setPlayerCount(which + 2);
                                reloadFragments();
                            }
                        }
                )
                .create();
        dialog.show();
        Utils.colorizeDialog(dialog);
    }

    private void showEditNameActionChoices() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_name)
                .setItems(R.array.edit_name_action,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent;
                                switch (which) {
                                    default:
                                    case 0:
                                        intent = new Intent(Intent.ACTION_PICK,
                                                ContactsContract.Contacts.CONTENT_URI);
                                        startActivityForResult(intent, REQ_PICK_CONTACT);
                                        break;
                                    case 1:
                                        showEditNameDialog();
                                        break;
                                }
                            }
                        }
                )
                .create();
        dialog.show();
        Utils.colorizeDialog(dialog);
    }

    private void showEditNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_name, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_name);
        editText.setText(mEditedName.getName());
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.edit_name)
                .setView(view)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String name = editText.getText().toString();
                                nameEdited(name);
                            }
                        }
                )
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Nothing to do!
                            }
                        }
                )
                .create();
        dialog.show();
        Utils.colorizeDialog(dialog);
    }
}
