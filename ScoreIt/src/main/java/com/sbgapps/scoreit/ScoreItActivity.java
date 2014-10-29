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

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.github.mrengineer13.snackbar.SnackBar;
import com.sbgapps.scoreit.fragment.HeaderFragment;
import com.sbgapps.scoreit.fragment.ScoreGraphFragment;
import com.sbgapps.scoreit.fragment.ScoreListFragment;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.fragment.LapFragment;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteLap;
import com.sbgapps.scoreit.fragment.BeloteLapFragment;
import com.sbgapps.scoreit.games.coinche.CoincheLap;
import com.sbgapps.scoreit.fragment.CoincheLapFragment;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotFourLap;
import com.sbgapps.scoreit.fragment.TarotLapFragment;
import com.sbgapps.scoreit.games.tarot.TarotThreeLap;
import com.sbgapps.scoreit.games.universal.UniversalLap;
import com.sbgapps.scoreit.fragment.UniversalLapFragment;
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerItem;
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerView;
import com.sbgapps.scoreit.widget.FloatingActionButton;
import com.sbgapps.scoreit.widget.RippleLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

public class ScoreItActivity extends BaseActivity
        implements FragmentManager.OnBackStackChangedListener,
        SnackBar.OnMessageClickListener {

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
    @InjectView(R.id.ripple_layout)
    RippleLayout mRippleLayout;

    private List<NavigationDrawerItem> mNavigationItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedPosition = 0;
    private boolean mIsTablet;
    private GameHelper mGameHelper;
    private int mEditedPlayer = Player.PLAYER_NONE;
    private Lap mLap;
    private boolean mIsEdited = false;
    private boolean mUpdateFab = false;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ButterKnife.inject(this);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();

        mIsTablet = (null != findViewById(R.id.secondary_container));

        final FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);

        // Init fragments
        if (null == savedInstanceState) {
            loadFragments(false);
        } else {
            mHeaderFragment = (HeaderFragment) fragmentManager
                    .findFragmentByTag(HeaderFragment.TAG);
            mScoreListFragment = (ScoreListFragment) fragmentManager
                    .findFragmentByTag(ScoreListFragment.TAG);
            mScoreGraphFragment = (ScoreGraphFragment) fragmentManager
                    .findFragmentByTag(ScoreGraphFragment.TAG);
            mLapFragment = (LapFragment) fragmentManager
                    .findFragmentByTag(LapFragment.TAG);

            mIsEdited = savedInstanceState.getBoolean("edited");
            mUpdateFab = savedInstanceState.getBoolean("updateFab");
            Resources resources = getResources();
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mLap = mGameHelper.getLaps().get(position);
            } else {
                mLap = (Lap) savedInstanceState.getSerializable("lap");
            }
            if (null != mLap) {
                mActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_action_done));
                mActionButton.setBackgroundColor(resources.getColor(R.color.color_hint));
            }
        }

        // Init drawer
        mNavigationItems = new ArrayList<>();
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.universal), false));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.tarot), false));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.belote), false));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.coinche), false));
        mNavigationItems.add(new NavigationDrawerItem(true));
        mNavigationItems.add(new NavigationDrawerItem(getString(R.string.about), false));
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
                setTitle();
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mSelectedPosition = mGameHelper.getPlayedGame();
        selectItem(mSelectedPosition);

        // Floating Action Button
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonClicked();
            }
        });
        mActionButton.setTouchAnimationListener(new FloatingActionButton.TouchAnimationListener() {
            @Override
            public void onAnimationEnd() {
                Resources resources = getResources();
                if (null == mLap) {
                    mActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_content_create));
                    mActionButton.setBackgroundColor(resources.getColor(R.color.color_accent));
                } else {
                    mActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_action_done));
                    mActionButton.setBackgroundColor(resources.getColor(R.color.color_hint));
                }
            }
        });

        mSnackBar = new SnackBar(this);
        mSnackBar.setOnClickListener(this);
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
            outState.putBoolean("updateFab", mUpdateFab);
            if (mIsEdited) {
                outState.putInt("position", mGameHelper.getLaps().indexOf(mLap));
            } else {
                outState.putSerializable("lap", mLap);
            }
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
        item.setVisible(!mIsTablet && 0 != lapCnt);

        item = menu.findItem(R.id.menu_clear);
        item.setVisible(0 != lapCnt);

        item = menu.findItem(R.id.menu_count);
        item.setVisible(Game.UNIVERSAL == game || Game.TAROT == game);

        item = menu.findItem(R.id.menu_save);
        List<String> files = mGameHelper.getFilesUtil().getSavedFiles();
        item.setVisible(0 != files.size());

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
        SpannableString title;

        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer)) {
            title = new SpannableString(getResources().getString(R.string.app_name));
        } else {
            switch (mSelectedPosition) {
                default:
                case Game.UNIVERSAL:
                    title = new SpannableString(getResources().getString(R.string.universal));
                    break;
                case Game.BELOTE:
                    title = new SpannableString(getResources().getString(R.string.belote));
                    break;
                case Game.COINCHE:
                    title = new SpannableString(getResources().getString(R.string.coinche));
                    break;
                case Game.TAROT:
                    title = new SpannableString(getResources().getString(R.string.tarot));
                    break;
            }
        }

        title.setSpan(getTypefaceSpan(), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        getSupportActionBar().setTitle(title);
    }

    private void onNavigationDrawerItemSelected(int position) {
        mSnackBar.clear(false);
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
        getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
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

    public void addLap() {
        mSnackBar.clear(false);
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
        showLapFragment();
    }

    public void editLap(Lap lap) {
        mIsEdited = true;
        mLap = lap;
        Resources resources = getResources();
        mActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_action_done));
        mActionButton.setBackgroundColor(resources.getColor(R.color.color_hint));
        showLapFragment();
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
                R.color.color_primary,
                0,
                token,
                SnackBar.MED_SNACK);
    }

    public void editName(int player) {
        if (!mIsTablet
                && null != mLapFragment
                && mLapFragment.isVisible()) {
            return;
        }
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
        mUpdateFab = true;
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
                .replace(R.id.score_container, mLapFragment, LapFragment.TAG)
                .addToBackStack(LapFragment.TAG)
                .commit();
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

        if (mIsTablet) {
            mScoreGraphFragment = new ScoreGraphFragment();
            ft = getSupportFragmentManager().beginTransaction();
            if (anim) ft.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            ft.replace(R.id.secondary_container, mScoreGraphFragment, ScoreGraphFragment.TAG)
                    .commit();
        }
    }

    public void switchScoreViews() {
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible()) {
            getSupportFragmentManager().popBackStack(ScoreGraphFragment.TAG,
                    android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (null == mScoreGraphFragment)
            mScoreGraphFragment = new ScoreGraphFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.fade_in,
                        R.anim.fade_out,
                        R.anim.fade_in,
                        R.anim.fade_out)
                .addToBackStack(ScoreGraphFragment.TAG)
                .replace(R.id.score_container, mScoreGraphFragment, ScoreGraphFragment.TAG)
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
            addLap();
        } else {
            mLap.computeScores();
            if (mIsEdited) {
                mIsEdited = false;
            } else {
                mGameHelper.addLap(mLap);
            }
            mUpdateFab = false;
            getSupportFragmentManager()
                    .popBackStack(LapFragment.TAG,
                            FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mLap = null;
        }
    }

    private void dismissAll() {
        mGameHelper.deleteAll();
        mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().removeAll();
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible()) {
            mScoreGraphFragment.update();
            if (!mIsTablet) getSupportFragmentManager().popBackStack();
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
    public void onBackStackChanged() {
        mRippleLayout.start();
        if (null != mLapFragment && mLapFragment.isVisible()) {
            return;
        }
        if (null != mLap) mLap.computeScores();
        mLap = null;
        mIsEdited = false;
        if (mUpdateFab) {
            Resources resources = getResources();
            mActionButton.setImageDrawable(resources.getDrawable(R.drawable.ic_content_create));
            mActionButton.setBackgroundColor(resources.getColor(R.color.color_accent));
        }
        mUpdateFab = false;
        invalidateOptionsMenu();
        update();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(mNavigationDrawer)) {
            mDrawerLayout.closeDrawer(Gravity.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onMessageClick(Parcelable token) {
        int index = ((Bundle) token).getInt("index");
        Lap lap = (Lap) ((Bundle) token).getSerializable("lap");

        mGameHelper.getLaps().add(index, lap);
        update();
        invalidateOptionsMenu();
    }
}
