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
import com.sbgapps.scoreit.app.model.GameModel;
import com.sbgapps.scoreit.app.utils.ActivityUtils;
import com.sbgapps.scoreit.core.model.Game;
import com.sbgapps.scoreit.core.model.utils.GameHelper;

import butterknife.BindView;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view)
    NavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupActionBar();
        setupDrawer();

        GameModel.init();

        if (isFirstRun(savedInstanceState)) {
            GameHelper.init(this);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        mNavigationView.getMenu().getItem(GameHelper.getPlayedGame()).setChecked(true);
    }

    private void onGameSelected(@Game.Games int game) {

    }
}
