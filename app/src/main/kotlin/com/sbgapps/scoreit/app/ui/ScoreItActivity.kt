/*
 * Copyright 2019 Stéphane Baiget
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

package com.sbgapps.scoreit.app.ui


import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.forEach
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.DialogGameNameBinding
import com.sbgapps.scoreit.app.ui.prefs.PreferencesViewModel
import com.sbgapps.scoreit.core.ui.BaseActivity
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoreItActivity : BaseActivity() {

    private val gameViewModel by viewModel<GameViewModel>()
    private val prefsViewModel by viewModel<PreferencesViewModel>()
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(prefsViewModel.getThemeMode())
        setContentView(R.layout.activity_scoreit)
        navController = findNavController(R.id.navHostFragment)

        onStates(gameViewModel) { state ->
            when (state) {
                is Content -> invalidateOptionsMenu()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val enabledItems = gameViewModel.getEnabledMenuItems()
        menu?.forEach {
            it.isVisible = enabledItems.contains(it.itemId)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        android.R.id.home -> showNavDrawer()
        R.id.menu_count -> showPlayerCountDialog()
        R.id.menu_chart -> showChart()
        R.id.menu_clear -> showClearDialog()
        else -> super.onOptionsItemSelected(item)
    }

    private fun showNavDrawer(): Boolean {
        findNavController(R.id.navHostFragment).navigate(R.id.action_historyFragment_to_navDrawerFragment)
        return true
    }

    private fun showPlayerCountDialog(): Boolean {
        val options = gameViewModel.getPlayerCountOptions()
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_title_player_number)
            .setItems(options.map { it.toString() }.toTypedArray()) { _, which ->
                gameViewModel.setPlayerCount(options[which])
            }
            .show()
        return true
    }

    private fun showClearDialog(): Boolean {
        MaterialAlertDialogBuilder(this)
            .setItems(R.array.dialog_clear_actions) { _, which ->
                when (which) {
                    0 -> gameViewModel.resetGame()
                    1 -> showGameNameDialog()
                }
            }
            .show()
        return true
    }

    private fun showGameNameDialog() {
        val view = DialogGameNameBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(this)
            .setView(view.root)
            .setPositiveButton(R.string.button_action_ok) { _, _ ->
                val name = view.gameName.text.toString()
                gameViewModel.createGame(name)
            }
            .setNegativeButton(R.string.button_action_cancel) { _, _ -> }
            .show()
    }

    private fun showChart(): Boolean {
        navController.navigate(R.id.action_historyFragment_to_chartFragment)
        return true
    }
}
