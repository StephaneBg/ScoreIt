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
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.cocosw.undobar.UndoBarController;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.melnykov.fab.FloatingActionButton;
import com.sbgapps.scoreit.games.Game;
import com.sbgapps.scoreit.games.GameHelper;
import com.sbgapps.scoreit.games.Lap;
import com.sbgapps.scoreit.games.LapFragment;
import com.sbgapps.scoreit.games.Player;
import com.sbgapps.scoreit.games.belote.BeloteLap;
import com.sbgapps.scoreit.games.belote.BeloteLapFragment;
import com.sbgapps.scoreit.games.coinche.CoincheLap;
import com.sbgapps.scoreit.games.coinche.CoincheLapFragment;
import com.sbgapps.scoreit.games.tarot.TarotFiveLap;
import com.sbgapps.scoreit.games.tarot.TarotFourLap;
import com.sbgapps.scoreit.games.tarot.TarotLapFragment;
import com.sbgapps.scoreit.games.tarot.TarotThreeLap;
import com.sbgapps.scoreit.games.universal.UniversalLap;
import com.sbgapps.scoreit.games.universal.UniversalLapFragment;
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerItem;
import com.sbgapps.scoreit.navigationdrawer.NavigationDrawerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.ldialogs.CustomListDialog;

public class ScoreItActivity extends BaseActivity
        implements FragmentManager.OnBackStackChangedListener,
        UndoBarController.UndoListener {

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

    private List<NavigationDrawerItem> mNavigationItems;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mSelectedPosition = 0;
    private boolean mIsTablet;
    private GameHelper mGameHelper;
    private Player mEditedPlayer;
    private Lap mLap;
    private boolean mIsEdited = false;
    private boolean mAnimateFab = false;

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

    public FloatingActionButton getActionButton() {
        return mActionButton;
    }

    public boolean isTablet() {
        return mIsTablet;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameHelper = new GameHelper(this);
        mGameHelper.loadLaps();

        setAccentDecor();
        setContentView(R.layout.activity_scoreit);
        ButterKnife.inject(this);
        mIsTablet = (null != findViewById(R.id.secondary_container));

        final FragmentManager fragmentManager = getFragmentManager();
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
            Resources resources = getResources();
            if (mIsEdited) {
                int position = savedInstanceState.getInt("position");
                mLap = mGameHelper.getLaps().get(position);
                mActionButton.setImageDrawable(
                        getResources().getDrawable(R.drawable.ic_content_edit_fab));
                mActionButton.setColorNormal(resources.getColor(R.color.secondary_accent_translucent));
                mActionButton.setColorPressed(resources.getColor(R.color.secondary_accent_dark_translucent));
            } else {
                mLap = (Lap) savedInstanceState.getSerializable("lap");
                if (null != mLap) {
                    mActionButton.setImageDrawable(
                            getResources().getDrawable(R.drawable.ic_action_accept_fab));
                    mActionButton.setColorNormal(resources.getColor(R.color.secondary_accent_translucent));
                    mActionButton.setColorPressed(resources.getColor(R.color.secondary_accent_dark_translucent));
                }
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

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.navigation_drawer_open,
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
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mSelectedPosition = mGameHelper.getPlayedGame();
        selectItem(mSelectedPosition);

        // Floating Action Button
        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onActionButtonClicked();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        UndoBarController.clear(this);
        mGameHelper.saveGame();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (null != mLap) {
            outState.putBoolean("edited", mIsEdited);
            if (mIsEdited) {
                outState.putInt("position", mGameHelper.getLaps().indexOf(mLap));
            } else {
                outState.putSerializable("lap", mLap);
            }
        }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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

//            case R.id.menu_open:
//                Intent intent = new Intent(this, SavedGameActivity.class);
//                String game;
//                switch (mGameHelper.getPlayedGame()) {
//                    default:
//                    case Game.UNIVERSAL:
//                        game = "universal_" + mGameHelper.getPlayerCount() + "_";
//                        break;
//                    case Game.TAROT:
//                        game = "tarot_" + mGameHelper.getPlayerCount() + "_";
//                        break;
//                    case Game.BELOTE:
//                        game = "belote_";
//                        break;
//                    case Game.COINCHE:
//                        game = "coinche_";
//                        break;
//                }
//                intent.putExtra("game", game);
//                startActivityForResult(intent, REQ_SAVED_GAME);
//                return true;
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
        getActionBar().setTitle(title);
    }

    private void onNavigationDrawerItemSelected(int position) {
        UndoBarController.clear(this);
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
        getFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        invalidateOptionsMenu();
        UndoBarController.clear(this);
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
                    mEditedPlayer.setName(name);
                    mHeaderFragment.update();
                }
                break;

            case REQ_SAVED_GAME:
                String file = data.getStringExtra("file");
                mGameHelper.saveGame(file);
                break;
        }
    }

    public void addLap() {
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
        mScoreListFragment.closeOpenedItems();
        mActionButton.show();
        showLapFragment();
        animateActionButton(R.drawable.ic_content_edit_fab);
    }

    public void removeLap(Lap lap) {
        final int index = mGameHelper.getLaps().indexOf(lap);
        final Bundle b = new Bundle();
        b.putInt("index", index);
        b.putSerializable("lap", lap);

        mGameHelper.removeLap(lap);
        update();
        invalidateOptionsMenu();

        new UndoBarController
                .UndoBar(this)
                .message(getString(R.string.deleted_lap))
                .listener(this)
                .token(b)
                .show();
    }

    public void editName(Player player) {
        if (!mIsTablet
                && null != mLapFragment
                && mLapFragment.isVisible()) {
            return;
        }
        mEditedPlayer = player;
        showEditNameActionChoices();
    }

    public void editColor(Player player) {
        if (!mIsTablet
                && null != mLapFragment
                && mLapFragment.isVisible()) {
            return;
        }
        mEditedPlayer = player;
        showColorPickerDialog();
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

        mAnimateFab = true;
        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.scale_enter_up,
                        R.animator.scale_exit_down,
                        R.animator.scale_enter_down,
                        R.animator.scale_exit_up)
                .replace(R.id.score_container, mLapFragment, LapFragment.TAG)
                .addToBackStack(LapFragment.TAG)
                .commit();
    }

    public void loadFragments(boolean anim) {
        mHeaderFragment = new HeaderFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
        ft.replace(R.id.header_container, mHeaderFragment, HeaderFragment.TAG)
                .commit();

        mScoreListFragment = new ScoreListFragment();
        ft = getFragmentManager().beginTransaction();
        if (anim) ft.setCustomAnimations(R.animator.slide_bottom_in, R.animator.slide_top_out);
        ft.replace(R.id.score_container, mScoreListFragment, ScoreListFragment.TAG)
                .commit();

        if (mIsTablet) {
            mScoreGraphFragment = new ScoreGraphFragment();
            ft = getFragmentManager().beginTransaction();
            if (anim) ft.setCustomAnimations(R.animator.fade_in, R.animator.fade_out);
            ft.replace(R.id.secondary_container, mScoreGraphFragment, ScoreGraphFragment.TAG)
                    .commit();
        }
    }

    public void switchScoreViews() {
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible()) {
            getFragmentManager().popBackStack(ScoreGraphFragment.TAG,
                    android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
            return;
        }

        if (null == mScoreGraphFragment)
            mScoreGraphFragment = new ScoreGraphFragment();

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(
                        R.animator.slide_bottom_in,
                        R.animator.slide_top_out,
                        R.animator.slide_top_in,
                        R.animator.slide_bottom_out)
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
        mScoreListFragment.closeOpenedItems();
        if (null == mLap) {
            addLap();
            animateActionButton(R.drawable.ic_action_accept_fab);
        } else {
            mLap.computeScores();
            if (mIsEdited) {
                mIsEdited = false;
            } else {
                mGameHelper.addLap(mLap);
            }
            getFragmentManager()
                    .popBackStack(LapFragment.TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            update();
            mLap = null;
            animateActionButton(R.drawable.ic_action_new_fab);
        }
    }

    private void showClearDialog() {
        CustomDialog dialog = new CustomDialog
                .Builder(this, R.string.clear_game, R.string.ok)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primary_accent)
                .build();

        dialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                dismissAll();
            }

            @Override
            public void onCancelClick() {

            }
        });
        dialog.show();
    }

    private void dismissAll() {
        mGameHelper.deleteAll();
        mHeaderFragment.update();
        if (null != mScoreListFragment && mScoreListFragment.isVisible())
            mScoreListFragment.getListAdapter().removeAll();
        if (null != mScoreGraphFragment && mScoreGraphFragment.isVisible()) {
            mScoreGraphFragment.update();
            if (!mIsTablet) getFragmentManager().popBackStack();
        }
        invalidateOptionsMenu();
    }

    private void showEditNameActionChoices() {
        CustomListDialog dialog = new CustomListDialog
                .Builder(this,
                getString(R.string.edit_name),
                getResources().getStringArray(R.array.edit_name_action))
                .itemColorRes(R.color.primary_accent)
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

        CustomListDialog dialog = new CustomListDialog
                .Builder(this, getString(R.string.player_number), players)
                .itemColorRes(R.color.primary_accent)
                .build();
        dialog.setListClickListener(new CustomListDialog.ListClickListener() {
            @Override
            public void onListItemSelected(int position, String[] items, String item) {
                mGameHelper.setPlayerCount(position);
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
                .positiveColorRes(R.color.primary_accent)
                .build();

        dialog.setCustomView(view)
                .setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        String name = editText.getText().toString();
                        mEditedPlayer.setName(name);
                        mHeaderFragment.update();
                        if (mScoreListFragment.isVisible()) mScoreListFragment.update();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
        dialog.show();
    }

//    private void showSaveFileDialog() {
//        View view = getLayoutInflater().inflate(R.layout.dialog_input_text, null);
//        final EditText editText = (EditText) view.findViewById(R.id.edit_text);
//
//        CustomDialog dialog = new CustomDialog
//                .Builder(this, R.string.filename, R.string.ok)
//                .negativeText(R.string.cancel)
//                .positiveColorRes(R.color.primary_accent)
//                .build();
//
//        dialog.setCustomView(view)
//                .setClickListener(new CustomDialog.ClickListener() {
//                    @Override
//                    public void onConfirmClick() {
//                        String file = editText.getText().toString();
//                        mGameHelper.saveGame(file);
//                        dismissAll();
//                    }
//
//                    @Override
//                    public void onCancelClick() {
//
//                    }
//                });
//        dialog.show();
//    }

    private void showColorPickerDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_color_picker, null);
        final ColorPicker picker = (ColorPicker) view.findViewById(R.id.picker);
        picker.setColor(mEditedPlayer.getColor());

        CustomDialog dialog = new CustomDialog
                .Builder(this, R.string.edit_color, R.string.ok)
                .negativeText(R.string.cancel)
                .positiveColorRes(R.color.primary_accent)
                .build();

        dialog.setCustomView(view)
                .setClickListener(new CustomDialog.ClickListener() {
                    @Override
                    public void onConfirmClick() {
                        int color = picker.getColor();
                        mEditedPlayer.setColor(color);
                        update();
                    }

                    @Override
                    public void onCancelClick() {

                    }
                });
        dialog.show();
    }

    @Override
    public void onBackStackChanged() {
        if (null != mLapFragment && mLapFragment.isVisible()) {
            return;
        }
        if (null != mLap) mLap.computeScores();
        mLap = null;
        mIsEdited = false;
        if (mAnimateFab) animateActionButton(R.drawable.ic_action_new_fab);
        mAnimateFab = false;
        invalidateOptionsMenu();
        update();
    }

    private void animateActionButton(final int resId) {
        final boolean orange = (null == mLap);
        final AnimatorSet anim1 = (AnimatorSet)
                AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_out);
        final AnimatorSet anim2 = (AnimatorSet)
                AnimatorInflater.loadAnimator(this, R.animator.card_flip_right_in);
        anim1.setTarget(mActionButton);
        anim1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mActionButton.setImageDrawable(getResources().getDrawable(resId));
                int color = ScoreItActivity.this.getResources()
                        .getColor(orange ? R.color.primary_accent
                                : R.color.secondary_accent);
                mActionButton.setColorNormal(color);
                color = ScoreItActivity.this.getResources()
                        .getColor(orange ? R.color.primary_accent_dark
                                : R.color.secondary_accent_dark);
                mActionButton.setColorPressed(color);
                anim2.setTarget(mActionButton);
                anim2.start();
            }
        });
        anim1.start();
    }

    @Override
    public void onUndo(Parcelable parcelable) {
        int index = ((Bundle) parcelable).getInt("index");
        Lap lap = (Lap) ((Bundle) parcelable).getSerializable("lap");

        mGameHelper.getLaps().add(index, lap);
        update();
        invalidateOptionsMenu();
    }
}
