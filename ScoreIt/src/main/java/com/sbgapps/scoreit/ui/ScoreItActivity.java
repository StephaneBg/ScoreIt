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

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Interpolator;
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

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_SAVED_GAME = 2;
    private static final int REQ_PERM_READ_CONTACTS = 3;

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mActionButton;
    private View mGraphContainer;
    private View mMainContainer;

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
        mMainContainer = findViewById(R.id.main_container);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadGame();

        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        }
        setTitle();

        if (null == savedInstanceState) {
            loadFragments(false);
        } else {
            mLap = (Lap) savedInstanceState.getSerializable("lap");
            mIsEdited = savedInstanceState.getBoolean("edited");
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mEditedLap = mGameHelper.getLaps().get(position);
            }
            mHeaderFragment = (HeaderFragment) getSupportFragmentManager()
                    .findFragmentByTag(HeaderFragment.TAG);
            mScoreListFragment = (ScoreListFragment) getSupportFragmentManager()
                    .findFragmentByTag(ScoreListFragment.TAG);
            mScoreGraphFragment = (ScoreGraphFragment) getSupportFragmentManager()
                    .findFragmentByTag(ScoreGraphFragment.TAG);
        }

        setupDrawer();
        setupActionButton();
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

    private void setupDrawer() {
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
        navigationView.getMenu().getItem(getGameHelper().getPlayedGame()).setChecked(true);
    }

    private void setupActionButton() {
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        if (null != mLap) setActionButtonColor();
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
        if (null == mLap) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (null != mLap) return true;

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
        animateActionButton();
        invalidateOptionsMenu();
        loadFragments(true);
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
        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setTitle(title);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String name;
        if (RESULT_OK != resultCode) return;

        switch (requestCode) {
            case REQ_PICK_CONTACT:
                Cursor cursor = getContentResolver().query(data.getData(),
                        new String[]{ContactsContract.Contacts.DISPLAY_NAME}, null, null, null);
                if (null == cursor) break;
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
                mGameHelper.loadGame();
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
            animateActionButton();
        }
        super.onBackPressed();
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

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQ_PERM_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickContact();
                }
            }
        }
    }

    public void editLap(Lap lap) {
        showLapScene(lap);
    }

    @SuppressLint("deprecation")
    public void removeLap(final Lap lap) {
        final int position = mGameHelper.removeLap(lap);

        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyItemRemoved(position);
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
            mScoreGraphFragment.update();

        invalidateOptionsMenu();

        mSnackBar = Snackbar.make(mMainContainer, R.string.deleted_lap, Snackbar.LENGTH_LONG);
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
                .setActionTextColor(ColorStateList.valueOf(
                        getResources().getColor(R.color.color_accent)))
                .show();
    }

    public void editName(int player) {
        mEditedPlayer = player;
        showEditNameActionChoices();
    }

    private void loadFragments(boolean anim) {
        mHeaderFragment = null;
        mScoreListFragment = null;
        mScoreGraphFragment = null;

        showHeaderFragment(anim);
        showScoreListFragment(anim);
        if (isTablet()) showScoreGraphFragment(anim);
    }

    private void switchScoreViews() {
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
        if (null != mScoreListFragment)
            mScoreListFragment.update();
        if (isTablet()) {
            if (null != mScoreGraphFragment)
                mScoreGraphFragment.update();
        } else {
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
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
        animateActionButton();
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
        animateActionButton();
        update();
    }

    @SuppressWarnings("deprecation")
    private void setActionButtonColor() {
        Resources res = getResources();
        if (null == mLap) {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_create_24dp));
            mActionButton.setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.color_accent)));
        } else {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_done_24dp));
            mActionButton.setBackgroundTintList(
                    ColorStateList.valueOf(getResources().getColor(R.color.color_primary)));
        }
    }

    public void animateActionButton() {
        if (View.VISIBLE == mActionButton.getVisibility()) {
            ViewCompat.animate(mActionButton).scaleX(0.0F).scaleY(0.0F).alpha(0.0F)
                    .setInterpolator(INTERPOLATOR).withLayer()
                    .setListener(new ViewPropertyAnimatorListenerAdapter() {
                        public void onAnimationEnd(View view) {
                            mActionButton.setVisibility(View.GONE);
                            animateActionButton();
                        }
                    })
                    .start();
        } else {
            setActionButtonColor();
            mActionButton.setVisibility(View.VISIBLE);
            ViewCompat.animate(mActionButton).scaleX(1.0F).scaleY(1.0F).alpha(1.0F)
                    .setInterpolator(INTERPOLATOR).withLayer().setListener(null)
                    .start();
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
                                if (ContextCompat.checkSelfPermission(ScoreItActivity.this,
                                        Manifest.permission.READ_CONTACTS)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(ScoreItActivity.this,
                                            new String[]{Manifest.permission.READ_CONTACTS},
                                            REQ_PERM_READ_CONTACTS);
                                } else {
                                    pickContact();
                                }
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

    private void pickContact() {
        Intent intent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQ_PICK_CONTACT);
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
                        loadFragments(true);
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
        ft.replace(R.id.main_container, mScoreListFragment, ScoreListFragment.TAG);
        ft.commit();
    }

    private void showScoreGraphFragment(boolean anim) {
        if (null == mScoreGraphFragment)
            mScoreGraphFragment = new ScoreGraphFragment();

        int id = isTablet() ? R.id.graph_container : R.id.main_container;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
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
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.main_container, fragment, LapFragment.TAG)
                .commit();
    }
}
