/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.sbgapps.scoreit.domain.preference.PreferencesHelper
import com.sbgapps.scoreit.ui.base.BaseActivity
import com.sbgapps.scoreit.ui.ext.color
import com.sbgapps.scoreit.ui.ext.replaceFragment
import com.sbgapps.scoreit.ui.view.HeaderFragment
import com.sbgapps.scoreit.ui.view.LapListFragment
import com.sbgapps.scoreit.ui.view.UniversalLapFragment
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel.Mode.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.experimental.launch
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.inject


class MainActivity : BaseActivity() {

    private val model by viewModel<UniversalViewModel>()
    private val prefsHelper by inject<PreferencesHelper>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (null == savedInstanceState)
            launch {
                model.initGame()
            }.invokeOnCompletion {
                replaceFragment(R.id.headerContainer, HeaderFragment.newInstance())
                replaceFragment(R.id.lapContainer, LapListFragment.newInstance())
            }

        setSupportActionBar(toolbar)
        fab.setOnClickListener { onFabClicked() }
        decorFab()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.totals -> {
                if (prefsHelper.isTotalDisplayed) {
                    prefsHelper.isTotalDisplayed = false
                    item.title = getString(R.string.menu_action_show_totals)
                } else {
                    prefsHelper.isTotalDisplayed = true
                    item.title = getString(R.string.menu_action_hide_totals)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (model.isOnHistoryMode()) {
            model.endLapEdition()
            decorFab()
        }
        super.onBackPressed()
    }

    private fun onFabClicked() {
        if (model.isOnHistoryMode()) {
            model.startAdditionMode()
            replaceFragment(R.id.lapContainer, UniversalLapFragment.newInstance(), true)
        } else {
            model.onLapEditionCompleted()
            supportFragmentManager.popBackStack()
        }
        decorFab()
    }

    private fun decorFab() {
        when (model.mode) {
            MODE_HISTORY -> {
                fab.backgroundTintList = ColorStateList.valueOf(color(R.color.color_accent))
                fab.setImageDrawable(getDrawable(R.drawable.ic_add_black_24dp))
            }
            MODE_UPDATE, MODE_ADDITION -> {
                fab.backgroundTintList = ColorStateList.valueOf(color(R.color.color_primary))
                fab.setImageDrawable(getDrawable(R.drawable.ic_done_black_24dp))
            }
        }
    }
}