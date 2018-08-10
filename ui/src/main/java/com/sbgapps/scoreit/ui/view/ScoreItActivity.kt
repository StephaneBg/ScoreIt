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

package com.sbgapps.scoreit.ui.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseActivity
import com.sbgapps.scoreit.ui.ext.addFragment
import com.sbgapps.scoreit.ui.ext.onImeActionDone
import com.sbgapps.scoreit.ui.ext.replaceFragment
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.android.synthetic.main.activity_scoreit.*
import kotlinx.coroutines.experimental.launch
import org.jetbrains.anko.find
import org.koin.androidx.viewmodel.ext.android.viewModel


class ScoreItActivity : BaseActivity() {

    private val model by viewModel<UniversalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreit)

        savedInstanceState ?: launch {
            model.init()
        }.invokeOnCompletion {
            replaceFragment(R.id.headerContainer, ScoreFragment.newInstance())
            replaceFragment(R.id.lapContainer, UniversalHistoryFragment.newInstance())
        }

        setSupportActionBar(bottomBar)
        fab.setOnClickListener { onFabClicked() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (model.isOnHistoryMode()) menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.playerCount -> {
            showPlayerCountDialog()
            true
        }

        R.id.totals -> {
            val isShown = model.toggleShowTotal()
            val res = if (isShown) R.string.menu_action_hide_totals
            else R.string.menu_action_show_totals
            item.title = getString(res)
            true
        }

        R.id.clear -> {
            showClearLapDialog()
            true
        }

        else -> super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (!model.isOnHistoryMode()) {
            model.setHistoryMode()
            switchFab()
        }
        invalidateOptionsMenu()
        super.onBackPressed()
    }

    private fun onFabClicked() {
        if (model.isOnHistoryMode()) {
            displayEdition()
        } else {
            model.onLapEditionCompleted()
            supportFragmentManager.popBackStack()
        }
        switchFab()
        invalidateOptionsMenu()
    }

    fun displayEdition() {
        addFragment(
                R.id.lapContainer,
                UniversalEditionFragment.newInstance(),
                true,
                R.anim.slide_in_up,
                R.anim.slide_out_down
        )
    }

    fun switchFab() {
        fab.hide(object : FloatingActionButton.OnVisibilityChangedListener() {
            override fun onHidden(fab: FloatingActionButton) {
                decorFab()
                fab.show()
            }
        })
    }

    private fun decorFab() {
        when (model.mode) {
            UniversalViewModel.Mode.MODE_HISTORY -> {
                fab.setImageDrawable(getDrawable(R.drawable.ic_add))
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_CENTER
            }
            UniversalViewModel.Mode.MODE_UPDATE, UniversalViewModel.Mode.MODE_ADDITION -> {
                fab.setImageDrawable(getDrawable(R.drawable.ic_done))
                bottomBar.fabAlignmentMode = BottomAppBar.FAB_ALIGNMENT_MODE_END
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showPlayerCountDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_player_number)
                .setItems(arrayOf("2", "3", "4", "5", "6", "7", "8")) { _, which ->
                    showGameNameDialog(which + 2)
                }
                .create()
                .show()
    }

    @SuppressLint("InflateParams")
    private fun showGameNameDialog(playerCount: Int) {
        val view = layoutInflater.inflate(R.layout.dialog_game_name, null)
        val editText = view.find<EditText>(R.id.gameName)
        val dialog = AlertDialog.Builder(this)
                .setView(view)
                .setTitle(R.string.dialog_title_game_name)
                .setPositiveButton(R.string.button_action_ok) { _, _ ->
                    model.createGame(editText.text.toString(), playerCount)
                }
                .setNeutralButton(R.string.button_action_cancel, null)
                .create()

        with(editText) {
            requestFocus()
            onImeActionDone {
                model.createGame(text.toString(), playerCount)
                dialog.dismiss()
            }
        }

        dialog.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        dialog.show()
    }

    private fun showClearLapDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_current_game)
                .setItems(R.array.dialog_clear_actions) { _, which ->
                    when (which) {
                        0 -> model.clearLaps()
                        1 -> showGameNameDialog(model.getPlayerCount())
                    }
                }
                .create()
                .show()
    }
}