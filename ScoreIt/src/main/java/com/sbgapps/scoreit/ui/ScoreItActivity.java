/*
 * Copyright (c) 2015 SBG Apps
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

package com.sbgapps.scoreit.ui;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.fragments.BeloteLapFragment;
import com.sbgapps.scoreit.fragments.CoincheLapFragment;
import com.sbgapps.scoreit.fragments.HeaderFragment;
import com.sbgapps.scoreit.fragments.LapFragment;
import com.sbgapps.scoreit.fragments.ScoreGraphFragment;
import com.sbgapps.scoreit.fragments.ScoreListFragment;
import com.sbgapps.scoreit.fragments.TarotLapFragment;
import com.sbgapps.scoreit.fragments.UniversalLapFragment;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteLap;
import com.sbgapps.scoreit.games.coinche.CoincheLap;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotFourLap;
import com.sbgapps.scoreit.games.tarot.TarotThreeLap;
import com.sbgapps.scoreit.games.universal.UniversalLap;

import java.util.List;

public class ScoreItActivity extends BaseActivity {

    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_SAVED_GAME = 2;

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mActionButton;
    private View mGraphContainer;

    private GameHelper mGameHelper;
    private int mEditedPlayer = Player.PLAYER_NONE;
    private Lap mLap;
    private Lap mEditedLap;
    private boolean mIsEdited = false;
    private Snackbar mSnackBar;

    private ScoreListFragment mScoreListFragment;
    private ScoreGraphFragment mScoreGraphFragment;
    private HeaderFragment mHeaderFragment;

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    public Lap getLap() {
        return mLap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGraphContainer = findViewById(R.id.graph_container);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        setTitle();

        reloadFragments(false);

        if (null != savedInstanceState) {
            mLap = (Lap) savedInstanceState.getSerializable("lap");
            mIsEdited = savedInstanceState.getBoolean("edited");
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mEditedLap = mGameHelper.getLaps().get(position);
            }
        }

        if (null != mLap) setActionButtonColor();

        initDrawer();
        initActionButton();
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scoreit;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.menu_clear:
                showClearDialogActionChoices();
                return true;

            case R.id.menu_view:
                switchScoreViews();
                return true;

            case R.id.menu_count:
                showPlayerCountDialog();
                return true;

            case R.id.menu_save:
                if (mGameHelper.getFilesUtil().isDefaultFile()) {
                    showLoadActionChoices();
                } else {
                    startSavedGamesActivity();
                }
                return true;

            case R.id.menu_total:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                boolean show = sp.getBoolean(GameHelper.KEY_UNIVERSAL_TOTAL, false);
                sp.edit().putBoolean(GameHelper.KEY_UNIVERSAL_TOTAL, !show).apply();
                update();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        switch (menuItem.getItemId()) {
                            case R.id.nav_universal:
                                onGameSelected(Game.UNIVERSAL);
                                break;
                            case R.id.nav_tarot:
                                onGameSelected(Game.TAROT);
                                break;
                            case R.id.nav_belote:
                                onGameSelected(Game.BELOTE);
                                break;
                            case R.id.nav_coinche:
                                onGameSelected(Game.COINCHE);
                                break;
                            case R.id.nav_donate:
                                startActivity(new Intent(ScoreItActivity.this, DonateActivity.class));
                                break;
                            case R.id.nav_about:
                                startActivity(new Intent(ScoreItActivity.this, AboutActivity.class));
                                break;
                        }
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });
    }

    private void initActionButton() {
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == mLap) {
                    showLapScene(null);
                } else {
                    showScoreScene();
                }
                invalidateOptionsMenu();
            }
        });
    }

    public boolean isTablet() {
        return (null != mGraphContainer);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEditedPlayer = savedInstanceState.getInt("editedPlayer", Player.PLAYER_NONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        int lapCnt = mGameHelper.getLaps().size();
        int game = mGameHelper.getPlayedGame();

        MenuItem item;
        item = menu.findItem(R.id.menu_view);
        item.setVisible(!isTablet() && 0 != lapCnt);

        item = menu.findItem(R.id.menu_clear);
        item.setVisible(0 != lapCnt);

        item = menu.findItem(R.id.menu_count);
        item.setVisible(Game.UNIVERSAL == game || Game.TAROT == game);

        item = menu.findItem(R.id.menu_save);
        List<String> files = mGameHelper.getFilesUtil().getSavedFiles();
        item.setVisible(0 != files.size());

        item = menu.findItem(R.id.menu_total);
        item.setVisible(Game.UNIVERSAL == game);

        return true;
    }

    private void onGameSelected(int game) {
        if (mGameHelper.getPlayedGame() == game) return;

        mGameHelper.setPlayedGame(game);

        mLap = null;
        mEditedLap = null;
        mIsEdited = false;

        setTitle();
        setActionButtonProperties(true);
        invalidateOptionsMenu();
        reloadFragments(true);
    }

    private void setTitle() {
        String title;
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                title = getString(R.string.universal);
                break;
            case Game.BELOTE:
                title = getString(R.string.belote);
                break;
            case Game.COINCHE:
                title = getString(R.string.coinche);
                break;
            case Game.TAROT:
                title = getString(R.string.tarot);
                break;
        }
        getSupportActionBar().setTitle(title);
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
                    mGameHelper.getPlayer(mEditedPlayer).setName(name.split(" ")[0]);
                    mEditedPlayer = Player.PLAYER_NONE;
                    mHeaderFragment.update();
                }
                cursor.close();
                break;

            case REQ_SAVED_GAME:
                mGameHelper.loadLaps();
                invalidateOptionsMenu();
                update();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        if (null != mLap) {
            mLap = null;
            mEditedLap = null;
            mIsEdited = false;
            setActionButtonProperties(true);
            super.onBackPressed();
        } else if (!isTablet() && null != mScoreGraphFragment) {
            switchScoreViews();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameHelper.saveGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mLap) {
            outState.putBoolean("edited", mIsEdited);
            if (mIsEdited) {
                int idx = mGameHelper.getLaps().indexOf(mEditedLap);
                outState.putInt("position", idx);
            }
            outState.putSerializable("lap", mLap);
        }
        if (Player.PLAYER_NONE != mEditedPlayer) outState.putInt("editedPlayer", mEditedPlayer);
    }

    public void editLap(Lap lap) {
        showLapScene(lap);
    }

    public void removeLap(final Lap lap) {
        final int position = mGameHelper.removeLap(lap);

        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyItemRemoved(position);
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
            mScoreGraphFragment.update();

        invalidateOptionsMenu();

        mSnackBar = Snackbar.make(mDrawerLayout, R.string.deleted_lap, Snackbar.LENGTH_LONG);
        mSnackBar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = position;
                if (p > mGameHelper.getLaps().size())
                    p = mGameHelper.getLaps().size();

                mGameHelper.getLaps().add(p, lap);
                if (null != mHeaderFragment)
                    mHeaderFragment.update();
                if (null != mScoreListFragment && mScoreListFragment.isVisible())
                    mScoreListFragment.getListAdapter().notifyItemInserted(position);
                if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
                    mScoreGraphFragment.update();
                invalidateOptionsMenu();
            }
        })
                .setActionTextColor(getResources().getColorStateList(R.color.sb_text_color))
                .show();
    }

    public void editName(int player) {
        mEditedPlayer = player;
        showEditNameActionChoices();
    }

    public void reloadFragments(boolean anim) {
        mHeaderFragment = null;
        mScoreListFragment = null;
        mScoreGraphFragment = null;

        showHeaderFragment(anim);
        showScoreListFragment(anim);
        if (isTablet()) showScoreGraphFragment(anim);
    }

    public void switchScoreViews() {
        if (null == mScoreGraphFragment) {
            showScoreGraphFragment(true);
        } else {
            showScoreListFragment(true);
            mScoreGraphFragment = null;
        }
    }

    public void update() {
        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.update();
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
            mScoreGraphFragment.update();
        showScoreListFragment(true);
    }

    private void showLapScene(Lap lap) {
        if (null != mSnackBar) mSnackBar.dismiss();

        if (null == lap) {
            switch (mGameHelper.getPlayedGame()) {
                default:
                case Game.UNIVERSAL:
                    mLap = new UniversalLap(mGameHelper.getPlayerCount());
                    break;
                case Game.BELOTE:
                    mLap = new BeloteLap();
                    break;
                case Game.COINCHE:
                    mLap = new CoincheLap();
                    break;
                case Game.TAROT:
                    switch (mGameHelper.getPlayerCount()) {
                        case 3:
                            mLap = new TarotThreeLap();
                            break;
                        case 4:
                            mLap = new TarotFourLap();
                            break;
                        case 5:
                            mLap = new TarotFiveLap();
                            break;
                    }
                    break;
            }
        } else {
            mIsEdited = true;
            mEditedLap = lap;
            mLap = lap.copy();
        }
        showLapFragment();
        setActionButtonProperties(true);
    }

    private void showScoreScene() {
        if (mIsEdited) {
            mEditedLap.set(mLap);
            mEditedLap = null;
            mIsEdited = false;
        } else {
            mLap.computeScores();
            mGameHelper.addLap(mLap);
        }
        mLap = null;
        setActionButtonProperties(true);
        update();
    }

    private void setActionButtonColor() {
        Resources res = getResources();
        if (null == mLap) {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_create_white_24dp));
            mActionButton.setBackgroundTintList(
                    getResources().getColorStateList(R.color.fab_add_selector));
        } else {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_done_white_24dp));
            mActionButton.setBackgroundTintList(
                    getResources().getColorStateList(R.color.fab_confirm_selector));
        }
    }

    public void setActionButtonProperties(boolean animate) {
        if (animate) {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(mActionButton, "scaleX", 0f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(mActionButton, "scaleY", 0f);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setActionButtonColor();
                    ObjectAnimator scaleX = ObjectAnimator.ofFloat(mActionButton, "scaleX", 1f);
                    ObjectAnimator scaleY = ObjectAnimator.ofFloat(mActionButton, "scaleY", 1f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.play(scaleX).with(scaleY);
                    animatorSet.start();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animatorSet.play(scaleX).with(scaleY);
            animatorSet.start();
        }
    }

    private void dismissAll() {
        mGameHelper.deleteAll();
        mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyDataSetChanged();
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible()) {
            mScoreGraphFragment.update();
        }
        invalidateOptionsMenu();
    }

    private void startSavedGamesActivity() {
        Intent intent = new Intent(ScoreItActivity.this, SavedGamesActivity.class);
        startActivityForResult(intent, REQ_SAVED_GAME);
    }

    private void showClearDialogActionChoices() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.current_game)
                .setItems(R.array.clear_actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            default:
                            case 0:
                                dismissAll();
                                break;
                            case 1:
                                if (mGameHelper.getFilesUtil().isDefaultFile()) {
                                    showSaveFileDialog(false);
                                } else {
                                    if (null != mSnackBar) mSnackBar.dismiss();
                                    mGameHelper.saveGame();
                                    mGameHelper.createGame();
                                    invalidateOptionsMenu();
                                    update();
                                }
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void showEditNameActionChoices() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_name)
                .setItems(R.array.edit_name_action, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            default:
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK,
                                        ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent, REQ_PICK_CONTACT);
                                break;
                            case 1:
                                showEditNameDialog();
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    private void showLoadActionChoices() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.current_game)
                .setItems(R.array.load_actions, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            default:
                            case 0:
                                startSavedGamesActivity();
                                break;
                            case 1:
                                showSaveFileDialog(true);
                                break;
                        }
                    }
                })
                .create()
                .show();
    }

    public void showPlayerCountDialog() {
        String[] players;
        switch (mGameHelper.getPlayedGame()) {
            default:
                players = new String[]{"2", "3", "4", "5", "6", "7", "8"};
                break;
            case Game.TAROT:
                players = new String[]{"3", "4", "5"};
                break;
        }

        new AlertDialog.Builder(this)
                .setTitle(R.string.player_number)
                .setItems(players, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mGameHelper.setPlayerCount(which);
                        invalidateOptionsMenu();
                        reloadFragments(true);
                    }
                })
                .create()
                .show();
    }

    private void showEditNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        new AlertDialog.Builder(this)
                .setTitle(R.string.edit_name)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = editText.getText().toString();
                        if (!name.isEmpty()) {
                            mGameHelper.getPlayer(mEditedPlayer).setName(name);
                            mEditedPlayer = Player.PLAYER_NONE;
                            mHeaderFragment.update();
                            if (mScoreListFragment.isVisible()) mScoreListFragment.update();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void showSaveFileDialog(final boolean load) {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        new AlertDialog.Builder(this)
                .setTitle(R.string.filename)
                .setView(view)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String file = editText.getText().toString();
                        mGameHelper.saveGame(file);
                        mGameHelper.createGame();
                        if (load) {
                            startSavedGamesActivity();
                        } else {
                            dismissAll();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void showHeaderFragment(boolean anim) {
        if (null == mHeaderFragment)
            mHeaderFragment = new HeaderFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.header_container, mHeaderFragment, HeaderFragment.TAG);
        ft.commit();
    }

    private void showScoreListFragment(boolean anim) {
        if (null == mScoreListFragment)
            mScoreListFragment = new ScoreListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.score_lap, mScoreListFragment, ScoreListFragment.TAG);
        ft.commit();
    }

    private void showScoreGraphFragment(boolean anim) {
        if (null == mScoreGraphFragment)
            mScoreGraphFragment = new ScoreGraphFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        int id = isTablet() ? R.id.graph_container : R.id.score_lap;
        ft.replace(id, mScoreGraphFragment, ScoreGraphFragment.TAG);
        ft.commit();
    }

    private void showLapFragment() {
        Fragment fragment;
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                fragment = new UniversalLapFragment();
                break;
            case Game.BELOTE:
                fragment = new BeloteLapFragment();
                break;
            case Game.COINCHE:
                fragment = new CoincheLapFragment();
                break;
            case Game.TAROT:
                fragment = new TarotLapFragment();
                break;
        }
        getSupportFragmentManager()
                .beginTransaction()
                .addToBackStack(null)
                .replace(R.id.score_lap, fragment, LapFragment.TAG)
                .commit();
    }
}
