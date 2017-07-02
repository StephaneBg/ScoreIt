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

package com.sbgapps.scoreit.app

import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.view.Menu
import android.view.MenuItem

import com.sbgapps.scoreit.app.base.BaseActivity
import com.sbgapps.scoreit.app.header.HeaderFragment
import com.sbgapps.scoreit.app.utils.ActivityUtils
import com.sbgapps.scoreit.core.model.Game
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupActionBar()
        setupDrawer()

        if (isNewLaunch(savedInstanceState)) {
            ActivityUtils.replaceFragmentToActivity(supportFragmentManager,
                    HeaderFragment.newInstance(), R.id.container_header)
        } else {

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar!!.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_menu_primary_24dp)
        }
    }

    private fun setupDrawer() {
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_universal -> onGameSelected(Game.UNIVERSAL)
                R.id.nav_tarot -> onGameSelected(Game.TAROT)
                R.id.nav_belote -> onGameSelected(Game.BELOTE)
                R.id.nav_coinche -> onGameSelected(Game.COINCHE)
                R.id.nav_donate -> {
                }
                R.id.nav_about -> {
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        navigationView.menu.getItem(ScoreItApp.gameManager.playedGame).isChecked = true
    }

    private fun onGameSelected(game: Int) {

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }
}
