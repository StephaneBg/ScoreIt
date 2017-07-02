/*
 * Copyright 2017 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.app;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.sbgapps.scoreit.app.base.BaseActivity;
import com.sbgapps.scoreit.app.header.HeaderFragment;
import com.sbgapps.scoreit.app.utils.ActivityUtils;
import com.sbgapps.scoreit.core.model.Game;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    GameManager mGameManager;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGameManager = ScoreItApp.getGameManager();

        setupActionBar();
        setupDrawer();

        if (isFirstRun(savedInstanceState)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            ActivityUtils.replaceFragmentToActivity(fragmentManager,
                    HeaderFragment.newInstance(), R.id.container_header);
        } else {

        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_primary_24dp);
    }

    private void setupDrawer() {
        mNavigationView.setNavigationItemSelectedListener(
                menuItem -> {
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
                            //startActivity(new Intent(ScoreItActivity.this, DonateActivity.class));
                            break;
                        case R.id.nav_about:
                            //startActivity(new Intent(ScoreItActivity.this, AboutActivity.class));
                            break;
                    }
                    mDrawerLayout.closeDrawers();
                    return true;
                });
        mNavigationView.getMenu().getItem(mGameManager.getPlayedGame()).setChecked(true);
    }

    private void onGameSelected(int game) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
