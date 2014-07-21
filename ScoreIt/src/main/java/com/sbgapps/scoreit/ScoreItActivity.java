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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
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
import com.sbgapps.scoreit.games.belote.BeloteLapActivity;
import com.sbgapps.scoreit.games.coinche.CoincheLapActivity;
import com.sbgapps.scoreit.games.tarot.TarotLapActivity;
import com.sbgapps.scoreit.games.universal.UniversalLapActivity;
import com.sbgapps.scoreit.utils.TypefaceSpan;
import com.sbgapps.scoreit.utils.Utils;
import com.sbgapps.scoreit.view.SwipeListView;
import com.sbgapps.scoreit.widget.PlayerInfo;

public class ScoreItActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerListener {

    public static final String EXTRA_LAP = "com.sbgapps.scoreit.lap";
    public static final String EXTRA_POSITION = "com.sbgapps.scoreit.position";
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
    private int mEditedLap = -1;

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameHelper = new GameHelper(this);

        setContentView(R.layout.activity_scoreit);
        //mIsTablet = (null != findViewById(R.id.fragment_container_large));

        final FragmentManager fm = getSupportFragmentManager();

        // Init fragments
        if (null == savedInstanceState) {
            mHeaderFragment = (HeaderFragment) fm.findFragmentByTag(HeaderFragment.TAG);
            mGraphFragment = (GraphFragment) fm.findFragmentByTag(GraphFragment.TAG);
            mScoreListFragment = (ScoreListFragment) fm.findFragmentByTag(ScoreListFragment.TAG);
        }

        // Init drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                fm.findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                (DrawerLayout) findViewById(R.id.drawer_layout),
                mGameHelper.getPlayedGame());

        // Floating ACtion Button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLapActivity();
            }
        });

        mTypefaceSpan = new TypefaceSpan(this, "Lobster.otf");
        setTitle();
    }

    public TypefaceSpan getTypefaceSpan() {
        return mTypefaceSpan;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameHelper.saveGame();
        if (null != mScoreListFragment) {
            SwipeListView slv = mScoreListFragment.getListView();
            if (null != slv) slv.closeOpenedItems();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
            item.setVisible(Game.UNIVERSAL == mGameHelper.getPlayedGame() ||
                    Game.TAROT == mGameHelper.getPlayedGame());
            getSupportActionBar().setTitle(mTitle);
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
    public void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            case 0:
                mGameHelper.setPlayedGame(Game.UNIVERSAL);
                break;
            case 1:
                mGameHelper.setPlayedGame(Game.TAROT);
                break;
            case 2:
                mGameHelper.setPlayedGame(Game.BELOTE);
                break;
            case 3:
                mGameHelper.setPlayedGame(Game.COINCHE);
                break;
            case 4:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return;
        }
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        supportInvalidateOptionsMenu();
        setTitle();
        reload();
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
                if (-1 == mEditedLap) {
                    Lap lap = (Lap) data.getSerializableExtra(EXTRA_LAP);
                    lap.computeScores();
                    mGameHelper.addLap(lap);
                } else {
                    Lap lap = mGameHelper.getLaps().get(mEditedLap);
                    // TODO
                    lap.computeScores();
                    mEditedLap = -1;
                }
                update();
                invalidateOptionsMenu();
                break;
        }
    }

    public void editLap(Lap lap) {
        mEditedLap = mGameHelper.getLaps().indexOf(lap);
        showLapActivity();
    }

    public void removeLap(Lap lap) {
        mGameHelper.removeLap(lap);
        update();
    }

    public void editName(View view) {
        mEditedName = (PlayerInfo) view.getParent().getParent();
        showEditNameActionChoices();
    }

    private void reload() {
        mHeaderFragment = new HeaderFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_header, mHeaderFragment, HeaderFragment.TAG)
                .commit();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (mIsTablet) {
//            ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
//            mScoreListFragment = new ScoreListFragment();
//            mGraphFragment = new GraphFragment();
//            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
//            ft.replace(R.id.fragment_container_large, mGraphFragment, GraphFragment.TAG);
        } else {
//            ft.setCustomAnimations(R.animator.slide_top_in, R.animator.slide_top_out);
            mScoreListFragment = new ScoreListFragment();
            ft.replace(R.id.fragment_container, mScoreListFragment, ScoreListFragment.TAG);
        }
        ft.commit();
    }

    private void update() {
        mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyDataSetChanged();
        if (null != mGraphFragment && mGraphFragment.isVisible())
            mGraphFragment.traceGraph();
    }

    private void showLapActivity() {
        Intent intent;
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                intent = new Intent(this, UniversalLapActivity.class);
                break;
            case Game.BELOTE:
                intent = new Intent(this, BeloteLapActivity.class);
                break;
            case Game.COINCHE:
                intent = new Intent(this, CoincheLapActivity.class);
                break;
            case Game.TAROT:
                intent = new Intent(this, TarotLapActivity.class);
                break;
        }
        intent.putExtra(EXTRA_POSITION, mEditedLap);
        startActivityForResult(intent, REQ_LAP_ACTIVITY);
    }

    private void switchScoreViews() {
        if (null != mGraphFragment && mGraphFragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        if (null == mGraphFragment)
            mGraphFragment = new GraphFragment();

        getSupportFragmentManager()
                .beginTransaction()
//                .setCustomAnimations(
//                        R.animator.slide_bottom_in,
//                        R.animator.slide_top_out,
//                        R.animator.slide_top_in,
//                        R.animator.slide_bottom_out)
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
            case Game.BELOTE:
                mTitle = new SpannableString(getResources().getString(R.string.belote));
                break;
            case Game.COINCHE:
                mTitle = new SpannableString(getResources().getString(R.string.coinche));
                break;
            case Game.TAROT:
                mTitle = new SpannableString(getResources().getString(R.string.tarot));
                break;
        }
        mTitle.setSpan(mTypefaceSpan, 0, mTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void nameEdited(String name) {
        // TODO
        //mGameHelper.setPlayerName(mEditedName.getPlayer(), name);
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
                                mHeaderFragment.update();
                                if (null != mScoreListFragment && mScoreListFragment.isVisible())
                                    mScoreListFragment.getListAdapter().removeAll();
                                if (null != mGraphFragment && mGraphFragment.isVisible()) {
                                    mGraphFragment.traceGraph();
                                    if (!mIsTablet) getFragmentManager().popBackStack();
                                }
                                supportInvalidateOptionsMenu();
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

    public void showPlayerCountDialog() {
        String[] players;
        switch (mGameHelper.getPlayedGame()) {
            default:
                players = new String[]{"2", "3", "4", "5"};
                break;
            case Game.TAROT:
                players = new String[]{"3", "4", "5"};
                break;
        }
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.player_number)
                .setItems(players,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mGameHelper.setPlayerCount(which);
                                mHeaderFragment.update();
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
        //editText.setText(mEditedName.getName());
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

    private void onFloatingActionButtonClicked() {

    }
}
