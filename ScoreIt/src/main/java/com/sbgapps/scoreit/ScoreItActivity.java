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

package com.sbgapps.scoreit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
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

import com.sbgapps.scoreit.fragments.BeloteLapFragment;
import com.sbgapps.scoreit.fragments.CoincheLapFragment;
import com.sbgapps.scoreit.fragments.HeaderFragment;
import com.sbgapps.scoreit.fragments.LapFragment;
import com.sbgapps.scoreit.fragments.ScoreChartFragment;
import com.sbgapps.scoreit.fragments.ScoreListFragment;
import com.sbgapps.scoreit.fragments.TarotLapFragment;
import com.sbgapps.scoreit.fragments.UniversalLapFragment;
import com.sbgapps.scoreit.models.Game;
import com.sbgapps.scoreit.models.GameManager;
import com.sbgapps.scoreit.models.Lap;
import com.sbgapps.scoreit.models.Player;
import com.sbgapps.scoreit.models.belote.BeloteLap;
import com.sbgapps.scoreit.models.coinche.CoincheLap;
import com.sbgapps.scoreit.models.tarot.TarotFiveLap;
import com.sbgapps.scoreit.models.tarot.TarotFourLap;
import com.sbgapps.scoreit.models.tarot.TarotThreeLap;
import com.sbgapps.scoreit.models.universal.UniversalLap;

import java.util.List;

public class ScoreItActivity extends BaseActivity {

    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();

    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_SAVED_GAME = 2;
    private static final int REQ_PERM_READ_CONTACTS = 3;

    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mActionButton;
    private BottomSheetBehavior mBottomSheetBehavior;
    private AppBarLayout mAppBarLayout;

    private GameManager mGameManager;
    private int mEditedPlayer = Player.PLAYER_NONE;
    private Lap mLap;
    private Lap mEditedLap;
    private boolean mIsEdited = false;
    private Snackbar mSnackBar;

    private ScoreListFragment mScoreListFragment;
    private ScoreChartFragment mScoreChartFragment;
    private HeaderFragment mHeaderFragment;

    public GameManager getGameManager() {
        return mGameManager;
    }

    public Lap getLap() {
        return mLap;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appbar);

        mGameManager = new GameManager(this);
        mGameManager.loadGame();

        if (null != savedInstanceState) {
            mLap = (Lap) savedInstanceState.getSerializable("lap");
            mIsEdited = savedInstanceState.getBoolean("edited");
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mEditedLap = mGameManager.getLaps().get(position);
            }

            mHeaderFragment = (HeaderFragment) getSupportFragmentManager()
                    .findFragmentByTag(HeaderFragment.TAG);
            mScoreListFragment = (ScoreListFragment) getSupportFragmentManager()
                    .findFragmentByTag(ScoreListFragment.TAG);
            mScoreChartFragment = (ScoreChartFragment) getSupportFragmentManager()
                    .findFragmentByTag(ScoreChartFragment.TAG);
        } else {
            reloadUi(false);
        }

        setupActionBar();
        setupDrawer();
        setupActionButton();
        setupBottomSheet();
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

            case R.id.menu_chart:
                showChartSheet(true);
                return true;

            case R.id.menu_count:
                showPlayerCountDialog();
                return true;

            case R.id.menu_save:
                if (mGameManager.getFileUtils().isDefaultFile()) {
                    showLoadActionChoices();
                } else {
                    startSavedGamesActivity();
                }
                return true;

            case R.id.menu_total:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                boolean show = sp.getBoolean(GameManager.KEY_UNIVERSAL_TOTAL, false);
                sp.edit().putBoolean(GameManager.KEY_UNIVERSAL_TOTAL, !show).apply();
                updateFragments();
                return true;
        }
        return super.onOptionsItemSelected(item);
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

        int lapCnt = mGameManager.getLaps().size();
        int game = mGameManager.getPlayedGame();

        MenuItem item;
        item = menu.findItem(R.id.menu_chart);
        item.setVisible(isPhone() && 0 != lapCnt);

        item = menu.findItem(R.id.menu_clear);
        item.setVisible(0 != lapCnt);

        item = menu.findItem(R.id.menu_count);
        item.setVisible(Game.UNIVERSAL == game || Game.TAROT == game);

        item = menu.findItem(R.id.menu_save);
        List<String> files = mGameManager.getFileUtils().getSavedFiles();
        item.setVisible(0 != files.size());

        item = menu.findItem(R.id.menu_total);
        item.setVisible(Game.UNIVERSAL == game);

        return true;
    }

    private void setupActionBar() {
        ActionBar ab = getSupportActionBar();
        if (null != ab) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu_24dp);
        }
        setTitle();
    }

    private void setupDrawer() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
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
        navigationView.getMenu().getItem(getGameManager().getPlayedGame()).setChecked(true);
    }

    private void setupActionButton() {
        mActionButton = (FloatingActionButton) findViewById(R.id.fab);
        setActionButtonColor();
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

    private void setupBottomSheet() {
        try {
            mBottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.chart_container));
        } catch (IllegalArgumentException e) {
            // Tablet layout
            return;
        }

        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    mAppBarLayout.setExpanded(true);
                    mActionButton.show();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
    }

    public boolean isPhone() {
        return null != mBottomSheetBehavior;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (null != mLap) {
            outState.putBoolean("edited", mIsEdited);
            if (mIsEdited) {
                int idx = mGameManager.getLaps().indexOf(mEditedLap);
                outState.putInt("position", idx);
            }
            outState.putSerializable("lap", mLap);
        }
        if (Player.PLAYER_NONE != mEditedPlayer) outState.putInt("editedPlayer", mEditedPlayer);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEditedPlayer = savedInstanceState.getInt("editedPlayer", Player.PLAYER_NONE);
    }

    private void onGameSelected(int game) {
        if (mGameManager.getPlayedGame() == game) return;

        mGameManager.setPlayedGame(game);
        mLap = null;
        mEditedLap = null;
        mIsEdited = false;

        showChartSheet(false);
        setTitle();
        animateActionButton();
        invalidateOptionsMenu();
        reloadUi();
    }

    private void setTitle() {
        String title;
        switch (mGameManager.getPlayedGame()) {
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
                    mGameManager.getPlayer(mEditedPlayer).setName(name.split(" ")[0]);
                    mEditedPlayer = Player.PLAYER_NONE;
                    mHeaderFragment.update();
                }
                cursor.close();
                break;

            case REQ_SAVED_GAME:
                mGameManager.loadGame();
                invalidateOptionsMenu();
                updateFragments();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else if (isPhone() && BottomSheetBehavior.STATE_EXPANDED == mBottomSheetBehavior.getState()) {
            showChartSheet(false);
        } else {
            if (null != mLap) {
                mLap = null;
                mEditedLap = null;
                mIsEdited = false;
                animateActionButton();
            }
            invalidateOptionsMenu();
            super.onBackPressed();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameManager.saveGame();
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

    public void removeLap(final Lap lap) {
        final int position = mGameManager.removeLap(lap);

        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyItemRemoved(position);
        if (null != mScoreChartFragment && mScoreChartFragment.isVisible())
            mScoreChartFragment.update();

        invalidateOptionsMenu();

        mSnackBar = Snackbar.make(findViewById(R.id.coordinator),
                R.string.deleted_lap, Snackbar.LENGTH_LONG);
        mSnackBar.setAction(R.string.undo, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = position;
                if (p > mGameManager.getLaps().size())
                    p = mGameManager.getLaps().size();

                mGameManager.getLaps().add(p, lap);
                if (null != mHeaderFragment)
                    mHeaderFragment.update();
                if (null != mScoreListFragment && mScoreListFragment.isVisible())
                    mScoreListFragment.getListAdapter().notifyItemInserted(position);
                if (null != mScoreChartFragment && mScoreChartFragment.isVisible())
                    mScoreChartFragment.update();
                invalidateOptionsMenu();
            }
        })
                .setActionTextColor(ColorStateList.valueOf(
                        ContextCompat.getColor(this, R.color.color_accent)))
                .show();
    }

    public void editName(int player) {
        mEditedPlayer = player;
        showEditNameActionChoices();
    }

    private void reloadUi(boolean anim) {
        initHeaderFragment(anim);
        initScoreListFragment(anim);
        initScoreChartFragment(anim);
    }

    private void reloadUi() {
        reloadUi(true);
    }

    public void updateFragments() {
        if (null != mHeaderFragment) mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.update();
        if (null != mScoreChartFragment && mScoreChartFragment.isVisible())
            mScoreChartFragment.update();

        try {
            getSupportFragmentManager()
                    .popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } catch (IllegalStateException ex) {
            // No fragment was added
        }
    }

    private void showLapScene(Lap lap) {
        if (null != mSnackBar) mSnackBar.dismiss();
        showChartSheet(false);

        if (null == lap) {
            switch (mGameManager.getPlayedGame()) {
                default:
                case Game.UNIVERSAL:
                    mLap = new UniversalLap(mGameManager.getPlayerCount());
                    break;
                case Game.BELOTE:
                    mLap = new BeloteLap();
                    break;
                case Game.COINCHE:
                    mLap = new CoincheLap();
                    break;
                case Game.TAROT:
                    switch (mGameManager.getPlayerCount()) {
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
            mGameManager.addLap(mLap);
        }
        mLap = null;
        animateActionButton();
        updateFragments();
    }

    private void setActionButtonColor() {
        if (null == mLap) {
            mActionButton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_plus_24dp));
            mActionButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_accent)));
        } else {
            mActionButton.setImageDrawable(
                    ContextCompat.getDrawable(this, R.drawable.ic_done_24dp));
            mActionButton.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_primary)));
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
        mGameManager.deleteAll();
        mHeaderFragment.update();
        if (null != mScoreListFragment) mScoreListFragment.update();
        if (null != mScoreChartFragment) mScoreChartFragment.update();
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
                                if (mGameManager.getFileUtils().isDefaultFile()) {
                                    showSaveFileDialog(false);
                                } else {
                                    if (null != mSnackBar) mSnackBar.dismiss();
                                    mGameManager.saveGame();
                                    mGameManager.createGame();
                                    invalidateOptionsMenu();
                                    updateFragments();
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
        switch (mGameManager.getPlayedGame()) {
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
                        mGameManager.setPlayerCount(which);
                        invalidateOptionsMenu();
                        reloadUi();
                    }
                })
                .create()
                .show();
    }

    @SuppressLint("InflateParams")
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
                            mGameManager.getPlayer(mEditedPlayer).setName(name);
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

    @SuppressLint("InflateParams")
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
                        mGameManager.saveGame(file);
                        mGameManager.createGame();
                        if (load) {
                            startSavedGamesActivity();
                        } else {
                            dismissAll();
                            updateFragments();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .create()
                .show();
    }

    private void initHeaderFragment(boolean anim) {
        mHeaderFragment = new HeaderFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.header_container, mHeaderFragment, HeaderFragment.TAG);
        ft.commit();
    }

    private void initScoreListFragment(boolean anim) {
        mScoreListFragment = new ScoreListFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.main_container, mScoreListFragment, ScoreListFragment.TAG);
        ft.commit();
    }

    private void initScoreChartFragment(boolean anim) {
        mScoreChartFragment = new ScoreChartFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.chart_container, mScoreChartFragment, ScoreChartFragment.TAG);
        ft.commit();
    }

    private void showLapFragment() {
        Fragment fragment;
        switch (mGameManager.getPlayedGame()) {
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

    private void showChartSheet(boolean show) {
        if (!isPhone()) return;

        mAppBarLayout.setExpanded(!show);
        if (show) {
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            mActionButton.hide();
        } else {
            if (BottomSheetBehavior.STATE_COLLAPSED != mBottomSheetBehavior.getState())
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            mActionButton.show();
        }
    }
}
