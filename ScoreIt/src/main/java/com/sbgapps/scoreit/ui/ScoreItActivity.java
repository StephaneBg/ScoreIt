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
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.mrengineer13.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.adapters.NavigationDrawerAdapter;
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
import com.sbgapps.scoreit.utils.Utils;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ScoreItActivity extends BaseActivity
        implements SnackBar.OnMessageClickListener {

    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_SAVED_GAME = 2;

    @InjectView(R.id.navigation_drawer)
    RecyclerView mNavigationDrawer;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.fab)
    FloatingActionButton mActionButton;
    @InjectView(R.id.lap_container)
    ObservableScrollView mLapContainer;
    FrameLayout mGraphContainer;

    private ActionBarDrawerToggle mDrawerToggle;
    private int mCurrentGame = Game.UNIVERSAL;
    private GameHelper mGameHelper;
    private int mEditedPlayer = Player.PLAYER_NONE;
    private Lap mLap;
    private Lap mEditedLap;
    private boolean mIsEdited = false;
    private SnackBar mSnackBar;

    private ScoreListFragment mScoreListFragment;
    private ScoreGraphFragment mScoreGraphFragment;
    private HeaderFragment mHeaderFragment;

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    public Lap getLap() {
        return mLap;
    }

    public int getCurrentGame() {
        return mCurrentGame;
    }

    @Override
    @SuppressWarnings("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        mGraphContainer = (FrameLayout) findViewById(R.id.graph_container);

        if (Utils.hasLollipopApi())
            getToolbar().setElevation(Utils.dpToPx(4, getResources()));

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();
        mCurrentGame = mGameHelper.getPlayedGame();
        setTitle();

        // Init fragments
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

        initLapContainer();
        initDrawer();
        initActionButton();
    }

    @SuppressWarnings("deprecation")
    private void initLapContainer() {
        final View root = findViewById(R.id.root);
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (null == mLap) {
                    mLapContainer.setTranslationY(mLapContainer.getHeight());
                } else {
                    setActionButtonProperties(false);
                }
                root.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
    }

    private void initDrawer() {
        mNavigationDrawer.setClipToPadding(false);
        mNavigationDrawer.setLayoutManager(new LinearLayoutManager(this));
        mNavigationDrawer.setAdapter(new NavigationDrawerAdapter(this));
        mNavigationDrawer.setHasFixedSize(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (null != mSnackBar) mSnackBar.clear();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        DrawerLayout.LayoutParams lp = (DrawerLayout.LayoutParams) mNavigationDrawer.getLayoutParams();
        lp.width = calculateDrawerWidth();
        mNavigationDrawer.setLayoutParams(lp);
        mDrawerLayout.setStatusBarBackgroundColor(getResources().getColor(R.color.color_primary));
    }

    private void initActionButton() {
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonClicked();
            }
        });
        if (!isTablet()) mActionButton.attachToScrollView(mLapContainer);
    }

    public boolean isTablet() {
        return (null != mGraphContainer);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_scoreit;
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
                outState.putInt("position", mGameHelper.getLaps()
                        .indexOf(mIsEdited ? mEditedLap : mLap));
            }
            outState.putSerializable("lap", mLap);
        }
        if (Player.PLAYER_NONE != mEditedPlayer) outState.putInt("editedPlayer", mEditedPlayer);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mEditedPlayer = savedInstanceState.getInt("editedPlayer", Player.PLAYER_NONE);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer) || (null != mLap)) {
            return false;
        }
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer) || (null != mLap)) {
            return false;
        }

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
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

    public void onGameSelected(int position) {
        mDrawerLayout.closeDrawer(mNavigationDrawer);
        if (mCurrentGame == position) return;

        int before = mCurrentGame;
        mCurrentGame = position;
        mNavigationDrawer.getAdapter().notifyItemChanged(before);
        mNavigationDrawer.getAdapter().notifyItemChanged(mCurrentGame);

        switch (position) {
            default:
                return;
            case Game.UNIVERSAL:
                mGameHelper.setPlayedGame(Game.UNIVERSAL);
                break;
            case Game.BELOTE:
                mGameHelper.setPlayedGame(Game.TAROT);
                break;
            case Game.COINCHE:
                mGameHelper.setPlayedGame(Game.BELOTE);
                break;
            case Game.TAROT:
                mGameHelper.setPlayedGame(Game.COINCHE);
                break;
        }

        mLap = null;
        mEditedLap = null;
        mIsEdited = false;

        setTitle();
        animateLapContainer();
        setActionButtonProperties(true);
        invalidateOptionsMenu();
        reloadFragments(true);
    }

    private void setTitle() {
        String title;
        switch (mCurrentGame) {
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

    public void onDonate() {
        mDrawerLayout.closeDrawer(mNavigationDrawer);
        startActivity(new Intent(this, DonateActivity.class));
    }

    public void onAbout() {
        mDrawerLayout.closeDrawer(mNavigationDrawer);
        startActivity(new Intent(this, AboutActivity.class));
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
                break;

            case REQ_SAVED_GAME:
                mGameHelper.loadLaps();
                invalidateOptionsMenu();
                update();
                break;
        }
    }

    public void editLap(Lap lap) {
        showLapScene(lap);
    }

    public void removeLap(Lap lap) {
        int position = mGameHelper.getLaps().indexOf(lap);
        Bundle token = new Bundle();
        token.putInt("position", position);
        token.putSerializable("lap", lap);

        mGameHelper.removeLap(lap);

        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyItemRemoved(position);
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
            mScoreGraphFragment.update();

        invalidateOptionsMenu();

        mSnackBar = new SnackBar.Builder(this)
                .withOnClickListener(this)
                .withMessageId(R.string.deleted_lap)
                .withActionMessageId(R.string.undo)
                .withTextColorId(R.color.sb_text_color)
                .withToken(token)
                .withDuration(SnackBar.MED_SNACK)
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
    }

    private void onActionButtonClicked() {
        if (null == mLap) {
            showLapScene(null);
        } else {
            showScoreScene();
        }
        invalidateOptionsMenu();
    }

    private void showLapScene(Lap lap) {
        if (null != mSnackBar) mSnackBar.clear();

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
        animateLapContainer();
        setActionButtonProperties(true);
        update();
    }

    private void setActionButtonColor() {
        Resources res = getResources();
        if (null == mLap) {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_content_create));
            mActionButton.setColorNormal(res.getColor(R.color.color_accent));
            mActionButton.setColorPressed(res.getColor(R.color.color_accent_dark));
            mActionButton.setColorRipple(res.getColor(R.color.gray_light));
        } else {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_action_done));
            mActionButton.setColorNormal(res.getColor(R.color.color_primary));
            mActionButton.setColorPressed(res.getColor(R.color.color_primary_dark));
            mActionButton.setColorRipple(res.getColor(R.color.gray_light));
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
                    setActionButtonPosition();
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
        } else {
            setActionButtonPosition();
        }
    }

    private void setActionButtonPosition() {
        if (isTablet()) return;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
                mActionButton.getLayoutParams();
        if (null == mLap) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        mActionButton.setLayoutParams(lp);
        mActionButton.show(false);
    }

    public void animateLapContainer() {
        ObjectAnimator animY;
        float height = mLapContainer.getHeight();
        if (null != mLap) {
            animY = ObjectAnimator.ofFloat(mLapContainer, "y", 0);
        } else {
            mLapContainer.scrollTo(0, 0);
            animY = ObjectAnimator.ofFloat(mLapContainer, "y", height);
        }
        animY.start();
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
        new MaterialDialog.Builder(this)
                .title(getString(R.string.current_game))
                .titleColorRes(R.color.color_primary)
                .items(getResources().getStringArray(R.array.clear_actions))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        switch (which) {
                            default:
                            case 0:
                                dismissAll();
                                break;
                            case 1:
                                if (mGameHelper.getFilesUtil().isDefaultFile()) {
                                    showSaveFileDialog(false);
                                } else {
                                    if (null != mSnackBar) mSnackBar.clear();
                                    mGameHelper.saveGame();
                                    mGameHelper.createGame();
                                    invalidateOptionsMenu();
                                    update();
                                }
                                break;
                        }
                    }
                })
                .show();
    }

    private void showEditNameActionChoices() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.edit_name))
                .titleColorRes(R.color.color_primary)
                .items(getResources().getStringArray(R.array.edit_name_action))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
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
                .show();
    }

    private void showLoadActionChoices() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.current_game))
                .titleColorRes(R.color.color_primary)
                .items(getResources().getStringArray(R.array.load_actions))
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
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

        new MaterialDialog.Builder(this)
                .title(getString(R.string.player_number))
                .titleColorRes(R.color.color_primary)
                .items(players)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        mGameHelper.setPlayerCount(which);
                        invalidateOptionsMenu();
                        reloadFragments(true);
                    }
                })
                .show();
    }

    private void showEditNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        new MaterialDialog.Builder(this)
                .title(R.string.edit_name)
                .titleColorRes(R.color.color_primary)
                .customView(view, false)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.color_primary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.gray_dark)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String name = editText.getText().toString();
                        if (!name.isEmpty()) {
                            mGameHelper.getPlayer(mEditedPlayer).setName(name);
                            mEditedPlayer = Player.PLAYER_NONE;
                            mHeaderFragment.update();
                            if (mScoreListFragment.isVisible()) mScoreListFragment.update();
                        }
                    }
                })
                .build()
                .show();
    }

    private void showSaveFileDialog(final boolean load) {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        new MaterialDialog.Builder(this)
                .title(R.string.filename)
                .titleColorRes(R.color.color_primary)
                .customView(view, false)
                .positiveText(R.string.ok)
                .positiveColorRes(R.color.color_primary)
                .negativeText(R.string.cancel)
                .negativeColorRes(R.color.gray_dark)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
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
                .build()
                .show();
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else if (null != mLap) {
            mLap = null;
            mEditedLap = null;
            mIsEdited = false;
            animateLapContainer();
            setActionButtonProperties(true);
        } else if (!isTablet() && null != mScoreGraphFragment) {
            switchScoreViews();
        } else {
            super.onBackPressed();
        }
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
        ft.replace(R.id.score_container, mScoreListFragment, ScoreListFragment.TAG);
        ft.commit();
    }

    private void showScoreGraphFragment(boolean anim) {
        if (null == mScoreGraphFragment)
            mScoreGraphFragment = new ScoreGraphFragment();

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        int id = isTablet() ? R.id.graph_container : R.id.score_container;
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
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.lap_container, fragment, LapFragment.TAG);
        ft.commit();
        animateLapContainer();
    }

    @Override
    public void onMessageClick(Parcelable token) {
        int position = ((Bundle) token).getInt("position");
        Lap lap = (Lap) ((Bundle) token).getSerializable("lap");

        if (position > mGameHelper.getLaps().size())
            position = mGameHelper.getLaps().size();

        mGameHelper.getLaps().add(position, lap);
        if (null != mHeaderFragment)
            mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().notifyItemInserted(position);
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible())
            mScoreGraphFragment.update();
        invalidateOptionsMenu();
    }
}
