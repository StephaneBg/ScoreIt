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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.mrengineer13.snackbar.SnackBar;
import com.melnykov.fab.FloatingActionButton;
import com.melnykov.fab.ObservableScrollView;
import com.sbgapps.scoreit.fragment.BeloteLapFragment;
import com.sbgapps.scoreit.fragment.CoincheLapFragment;
import com.sbgapps.scoreit.fragment.HeaderFragment;
import com.sbgapps.scoreit.fragment.LapFragment;
import com.sbgapps.scoreit.fragment.ScoreGraphFragment;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.fragment.TarotLapFragment;
import com.sbgapps.scoreit.fragment.UniversalLapFragment;
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
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerItem;
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerView;
import com.sbgapps.scoreit.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

public class ScoreItActivity extends BaseActivity
        implements SnackBar.OnMessageClickListener {

    private static final int REQ_PICK_CONTACT = 1;
    private static final int REQ_SAVED_GAME = 2;

    @InjectView(R.id.navigation_drawer)
    NavigationDrawerView mNavigationDrawer;
    @InjectView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @InjectView(R.id.drawer_list_view)
    ListView mDrawerListView;
    @InjectView(R.id.fab)
    FloatingActionButton mActionButton;
    ObservableScrollView mLapContainer;

    private List<NavigationDrawerItem> mNavigationItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedPosition = 0;
    private GameHelper mGameHelper;
    private int mEditedPlayer = Player.PLAYER_NONE;
    private Lap mLap;
    private Lap mEditedLap;
    private boolean mIsEdited = false;
    private SnackBar mSnackBar;

    private ScoreListFragment mScoreListFragment;
    private ScoreGraphFragment mScoreGraphFragment;
    private HeaderFragment mHeaderFragment;
    private LapFragment mLapFragment;

    public GameHelper getGameHelper() {
        return mGameHelper;
    }

    public Lap getLap() {
        return mLap;
    }

    @Override
    @SuppressWarnings("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);
        mLapContainer = (ObservableScrollView) findViewById(R.id.lap_container);

        if (!isTablet() && Utils.hasLollipopApi())
            getToolbar().setElevation(Utils.dpToPx(4, getResources()));

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();

        Resources res = getResources();

        // Init fragments
        final FragmentManager fragmentManager = getSupportFragmentManager();
        if (null == savedInstanceState) {
            loadFragments(false);
        } else {
            mLap = (Lap) savedInstanceState.getSerializable("lap");
            mHeaderFragment = (HeaderFragment) fragmentManager
                    .findFragmentByTag(HeaderFragment.TAG);
            mScoreListFragment = (ScoreListFragment) fragmentManager
                    .findFragmentByTag(ScoreListFragment.TAG);
            mScoreGraphFragment = (ScoreGraphFragment) fragmentManager
                    .findFragmentByTag(ScoreGraphFragment.TAG);
            mLapFragment = (LapFragment) fragmentManager
                    .findFragmentByTag(LapFragment.TAG);

            mIsEdited = savedInstanceState.getBoolean("edited");
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mEditedLap = mGameHelper.getLaps().get(position);
            }
        }

        if (null != mLap) setActionButtonColor();

        if (!isTablet()) {
            final View root = findViewById(R.id.root);
            root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (null == mLap) {
                        mLapContainer.setTranslationY(mLapContainer.getHeight());
                    } else {
                        setActionButtonProperties(false);
                    }
                    root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
            });
        }

        // Init drawer
        mNavigationItems = new ArrayList<>();
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.universal), res.getDrawable(R.drawable.ic_universal)));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.tarot), res.getDrawable(R.drawable.ic_tarot)));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.belote), res.getDrawable(R.drawable.ic_belote)));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.coinche), res.getDrawable(R.drawable.ic_coinche)));
        mNavigationItems.add(new NavigationDrawerItem(true));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.about), res.getDrawable(R.drawable.ic_action_info)));
        mNavigationDrawer.replaceWith(mNavigationItems);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close) {
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle();
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mSnackBar.clear();
                setTitle();
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSelectedPosition = mGameHelper.getPlayedGame();
        selectItem(mSelectedPosition);

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mDrawerListView.getLayoutParams();
        lp.width = calculateDrawerWidth();
        mDrawerListView.setLayoutParams(lp);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonClicked();
            }
        });
        if (!isTablet()) mActionButton.attachToScrollView(mLapContainer);

        mSnackBar = new SnackBar(this);
        mSnackBar.setOnClickListener(this);
    }

    public boolean isTablet() {
        return (null == mLapContainer);
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
        outState.putBundle("snackbar", mSnackBar.onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mSnackBar.onRestoreInstanceState(savedInstanceState.getBundle("snackbar"));
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

    @OnItemClick(R.id.drawer_list_view)
    public void onDrawerItemClick(int position, long id) {
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer)) {
            NavigationDrawerItem item = mNavigationDrawer.getAdapter().getItem(position);
            if (item.isSeparator()) return;

            mDrawerLayout.closeDrawer(mNavigationDrawer);
            if (mSelectedPosition == position) return;
            onNavigationDrawerItemSelected(position);
        }
    }

    private void selectItem(int position) {
        if (mDrawerListView != null &&
                position != 4) {
            mDrawerListView.setItemChecked(position, true);

            mNavigationItems.get(mSelectedPosition).setSelected(false);
            mNavigationItems.get(position).setSelected(true);

            mSelectedPosition = position;
            setTitle();
        }
        mDrawerLayout.closeDrawer(mNavigationDrawer);
    }

    private void setTitle() {
        String title;
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer)) {
            title = getString(R.string.app_name);
        } else {
            switch (mSelectedPosition) {
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
        }
        getSupportActionBar().setTitle(title);
    }

    private void onNavigationDrawerItemSelected(int position) {
        switch (position) {
            default:
                return;
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
            case 5:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return;
        }
        mLap = null;
        mEditedLap = null;
        mIsEdited = false;
        animateLapContainer();
        setActionButtonProperties();
        invalidateOptionsMenu();
        loadFragments(true);
        selectItem(position);
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
                    mGameHelper.getPlayer(mEditedPlayer).setName(name);
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
        final int index = mGameHelper.getLaps().indexOf(lap);
        final Bundle token = new Bundle();
        token.putInt("index", index);
        token.putSerializable("lap", lap);

        mGameHelper.removeLap(lap);
        update();
        invalidateOptionsMenu();

        mSnackBar.show(
                R.string.deleted_lap,
                R.string.undo,
                R.color.sb_text_color,
                0,
                token,
                SnackBar.MED_SNACK);
    }

    public void editName(int player) {
        if (null != mLapFragment
                && mLapFragment.isVisible()
                && isTablet()) return;
        mEditedPlayer = player;
        showEditNameActionChoices();
    }

    private void showLapFragment() {
        switch (mGameHelper.getPlayedGame()) {
            default:
            case Game.UNIVERSAL:
                mLapFragment = new UniversalLapFragment();
                break;
            case Game.BELOTE:
                mLapFragment = new BeloteLapFragment();
                break;
            case Game.COINCHE:
                mLapFragment = new CoincheLapFragment();
                break;
            case Game.TAROT:
                mLapFragment = new TarotLapFragment();
                break;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (isTablet()) {
            ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                    .replace(R.id.score_container, mLapFragment, LapFragment.TAG);
        } else {
            ft.replace(R.id.lap_container, mLapFragment, LapFragment.TAG);
            animateLapContainer();
        }
        ft.commit();
    }

    public void loadFragments(boolean anim) {
        mHeaderFragment = new HeaderFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.header_container, mHeaderFragment, HeaderFragment.TAG)
                .commit();

        mScoreListFragment = new ScoreListFragment();
        ft = getSupportFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
        ft.replace(R.id.score_container, mScoreListFragment, ScoreListFragment.TAG)
                .commit();

        if (isTablet()) {
            mScoreGraphFragment = new ScoreGraphFragment();
            ft = getSupportFragmentManager().beginTransaction();
            if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.graph_container, mScoreGraphFragment, ScoreGraphFragment.TAG)
                    .commit();
        }
    }

    public void switchScoreViews() {
        Fragment fragment;
        if (null == mScoreGraphFragment) {
            mScoreGraphFragment = new ScoreGraphFragment();
            fragment = mScoreGraphFragment;
        } else {
            fragment = mScoreListFragment;
            mScoreGraphFragment = null;
        }

        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(isTablet() ? R.id.graph_container : R.id.score_container, fragment, ScoreGraphFragment.TAG)
                .commit();
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
        mSnackBar.clear();

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
        setActionButtonProperties();
    }

    private void showScoreScene() {
        mLap.computeScores();
        if (mIsEdited) {
            mEditedLap.set(mLap);
            mEditedLap = null;
            mIsEdited = false;
        } else {
            mGameHelper.addLap(mLap);
        }
        mLap = null;
        if (isTablet()) {
            showScoreListFragment();
        } else {
            animateLapContainer();
        }
        setActionButtonProperties();
        update();
    }

    private void setActionButtonColor() {
        Resources res = getResources();
        if (null == mLap) {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_content_create));
            mActionButton.setColorNormal(res.getColor(R.color.fab_normal_score));
            mActionButton.setColorPressed(res.getColor(R.color.fab_pressed_score));
            mActionButton.setColorRipple(res.getColor(R.color.fab_ripple_score));
        } else {
            mActionButton.setImageDrawable(res.getDrawable(R.drawable.ic_action_done));
            mActionButton.setColorNormal(res.getColor(R.color.fab_normal_lap));
            mActionButton.setColorPressed(res.getColor(R.color.fab_pressed_lap));
            mActionButton.setColorRipple(res.getColor(R.color.fab_ripple_lap));
        }
    }

    public void setActionButtonProperties() {
        setActionButtonProperties(true);
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
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mActionButton.getLayoutParams();
        if (null == mLap) {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        } else {
            lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        }
        mActionButton.setLayoutParams(lp);
    }

    public void animateLapContainer() {
        if (isTablet()) return;
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
            mScoreListFragment.getListAdapter().removeAll();
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
        CustomListDialog dialog = new CustomListDialog
                .Builder(this,
                getString(R.string.current_game),
                getResources().getStringArray(R.array.clear_actions))
                .itemColorRes(R.color.color_accent)
                .build();

        dialog.setListClickListener(new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int position, String[] items, String item) {
                switch (position) {
                    default:
                    case 0:
                        dismissAll();
                        break;
                    case 1:
                        if (mGameHelper.getFilesUtil().isDefaultFile()) {
                            showSaveFileDialog(false);
                        } else {
                            mSnackBar.clear();
                            mGameHelper.saveGame();
                            mGameHelper.createGame();
                            invalidateOptionsMenu();
                            update();
                        }
                        break;
                }
            }
        });
        dialog.show();
    }

    private void showEditNameActionChoices() {
        CustomListDialog dialog = new CustomListDialog
                .Builder(this,
                getString(R.string.edit_name),
                getResources().getStringArray(R.array.edit_name_action))
                .itemColorRes(R.color.color_accent)
                .build();

        dialog.setListClickListener(new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int position, String[] items, String item) {
                Intent intent;
                switch (position) {
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
        });
        dialog.show();
    }

    private void showLoadActionChoices() {
        CustomListDialog dialog = new CustomListDialog
                .Builder(this,
                getString(R.string.current_game),
                getResources().getStringArray(R.array.load_actions))
                .itemColorRes(R.color.color_accent)
                .build();

        dialog.setListClickListener(new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int position, String[] items, String item) {
                switch (position) {
                    default:
                    case 0:
                        startSavedGamesActivity();
                        break;
                    case 1:
                        showSaveFileDialog(true);
                        break;
                }
            }
        });
        dialog.show();
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

        CustomListDialog dialog = new CustomListDialog
                .Builder(this, getString(R.string.player_number), players)
                .itemColorRes(R.color.color_accent)
                .build();
        dialog.setListClickListener(new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int position, String[] items, String item) {
                mGameHelper.setPlayerCount(position);
                invalidateOptionsMenu();
                loadFragments(true);
            }
        });
        dialog.show();
    }

    private void showEditNameDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        CustomDialog dialog = new CustomDialog
                .Builder(this, R.string.edit_name, R.string.ok)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.color_accent)
                .build();

        dialog.setCustomView(view)
                .setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        String name = editText.getText().toString();
                        if (!name.isEmpty()) {
                            mGameHelper.getPlayer(mEditedPlayer).setName(name);
                            mEditedPlayer = Player.PLAYER_NONE;
                            mHeaderFragment.update();
                            if (mScoreListFragment.isVisible()) mScoreListFragment.update();
                        }
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
        dialog.show();
    }

    private void showSaveFileDialog(final boolean load) {
        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
        final EditText editText = (EditText) view.findViewById(R.id.edit_text);

        CustomDialog dialog = new CustomDialog
                .Builder(this, R.string.filename, R.string.ok)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.color_accent)
                .build();

        dialog.setCustomView(view)
                .setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        String file = editText.getText().toString();
                        mGameHelper.saveGame(file);
                        mGameHelper.createGame();
                        if (load) {
                            startSavedGamesActivity();
                        } else {
                            dismissAll();
                        }
                    }

                    @Override
                    public void onCancelClick() {
                    }
                });
        dialog.show();
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
            if (isTablet()) {
                showScoreListFragment();
            } else {
                animateLapContainer();
            }
            setActionButtonProperties();
        } else if (!isTablet() && null != mScoreGraphFragment) {
            switchScoreViews();
        } else {
            super.onBackPressed();
        }
    }

    private void showScoreListFragment() {
        if (null == mScoreListFragment)
            mScoreListFragment = new ScoreListFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(R.id.score_container,
                        mScoreListFragment, ScoreListFragment.TAG)
                .commit();
    }

    @Override
    public void onMessageClick(Parcelable token) {
        int index = ((Bundle) token).getInt("index");
        Lap lap = (Lap) ((Bundle) token).getSerializable("lap");

        if (index > mGameHelper.getLaps().size())
            index = mGameHelper.getLaps().size();

        mGameHelper.getLaps().add(index, lap);
        update();
        invalidateOptionsMenu();
    }
}
