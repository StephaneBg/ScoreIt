/*
 * Copyright 2020 Stéphane Baiget
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

package com.sbgapps.scoreit.app.ui.history

import android.app.ActivityOptions
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.Window
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.forEach
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.model.BeloteLapRow
import com.sbgapps.scoreit.app.model.CoincheLapRow
import com.sbgapps.scoreit.app.model.DonationRow
import com.sbgapps.scoreit.app.model.LapRow
import com.sbgapps.scoreit.app.model.TarotLapRow
import com.sbgapps.scoreit.app.model.UniversalLapRow
import com.sbgapps.scoreit.app.ui.Content
import com.sbgapps.scoreit.app.ui.GameEvent
import com.sbgapps.scoreit.app.ui.GameViewModel
import com.sbgapps.scoreit.app.ui.NavDrawerFragment
import com.sbgapps.scoreit.app.ui.chart.ChartFragment
import com.sbgapps.scoreit.app.ui.color.ColorPickerFragment
import com.sbgapps.scoreit.app.ui.edition.belote.BeloteEditionActivity
import com.sbgapps.scoreit.app.ui.edition.coinche.CoincheEditionActivity
import com.sbgapps.scoreit.app.ui.edition.tarot.TarotEditionActivity
import com.sbgapps.scoreit.app.ui.edition.universal.UniversalEditionActivity
import com.sbgapps.scoreit.app.ui.history.adapter.BaseLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.BeloteLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.CoincheLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.DonationAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.HeaderAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.TarotLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.UniversalLapAdapter
import com.sbgapps.scoreit.app.ui.prefs.PreferencesViewModel
import com.sbgapps.scoreit.app.ui.saved.SavedGameActivity
import com.sbgapps.scoreit.core.ext.asListOfType
import com.sbgapps.scoreit.core.ext.onImeActionDone
import com.sbgapps.scoreit.core.ext.start
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.core.widget.DividerItemDecoration
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.repository.BillingRepo
import com.sbgapps.scoreit.data.repository.Donation
import com.sbgapps.scoreit.databinding.ActivityHistoryBinding
import com.sbgapps.scoreit.databinding.DialogEditNameBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryActivity : BaseActivity() {

    private val gameViewModel by viewModel<GameViewModel>()
    private val prefsViewModel by viewModel<PreferencesViewModel>()
    private val billingRepository by inject<BillingRepo>()
    private lateinit var binding: ActivityHistoryBinding
    private val historyAdapter = GenericRecyclerViewAdapter()
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        window.requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS)
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false

        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(prefsViewModel.getThemeMode())

        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.bottomAppBar)
        binding.recyclerView.apply {
            adapter = historyAdapter
            ItemTouchHelper(HistorySwipeCallback(::onEdit, ::onDelete)).attachToRecyclerView(this)
            addItemDecoration(DividerItemDecoration(this@HistoryActivity))
        }
        binding.fab.setOnClickListener {
            gameViewModel.addLap()
        }

        gameViewModel.observeStates(this) { state ->
            when (state) {
                is Content -> {
                    binding.header.adapter =
                        HeaderAdapter(state.header, ::displayPlayerEditionOptions)
                    updateHistory(state.results)
                    invalidateOptionsMenu()
                }
            }
        }

        gameViewModel.observeEffects(this) { event ->
            when (event) {
                is GameEvent.Edition -> startEdition(event.gameType)
                is GameEvent.Deletion -> manageDeletion(event)
            }
        }
    }

    private fun updateHistory(laps: List<LapRow>) {
        val adapters = getItems(laps)
        val diff = DiffUtil.calculateDiff(HistoryDiffCallback(historyAdapter.items.asListOfType(), adapters))
        historyAdapter.updateItems(adapters, diff)
    }

    private fun startEdition(gameType: GameType) {
        val bundle = ActivityOptions.makeSceneTransitionAnimation(
            this,
            binding.fab,
            "shared_element_container"
        ).toBundle()
        when (gameType) {
            GameType.UNIVERSAL -> start<UniversalEditionActivity>(bundle)
            GameType.TAROT -> start<TarotEditionActivity>(bundle)
            GameType.BELOTE -> start<BeloteEditionActivity>(bundle)
            GameType.COINCHE -> start<CoincheEditionActivity>(bundle)
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
        R.id.menu_save -> displaySavedGames()
        else -> super.onOptionsItemSelected(item)
    }

    private fun showNavDrawer(): Boolean {
        NavDrawerFragment().show(supportFragmentManager, null)
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
                    1 -> displayNameEditionDialog()
                }
            }
            .show()
        return true
    }

    private fun displayNameEditionDialog() {
        val action = { name: String ->
            if (name.isNotEmpty()) gameViewModel.createGame(name)
        }
        val view = DialogEditNameBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(view.root)
            .setPositiveButton(R.string.button_action_ok) { _, _ ->
                action(view.name.text.toString())
            }
            .create()
        view.name.apply {
            requestFocus()
            onImeActionDone {
                action(text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun showChart(): Boolean {
        ChartFragment().show(supportFragmentManager, null)
        return true
    }

    private fun displaySavedGames(): Boolean {
        start<SavedGameActivity>()
        return true
    }

    private fun manageDeletion(event: GameEvent.Deletion) {
        snackbar?.apply {
            removeCallback(deleteCallback)
            dismiss()
        }

        Snackbar.make(binding.mainContainer, R.string.snackbar_msg_on_lap_deleted, Snackbar.LENGTH_LONG).apply {
            setAction(R.string.snackbar_action_on_lap_deleted) {
                removeCallback(deleteCallback)
                gameViewModel.undoDeletion()
            }
            addCallback(deleteCallback)
            anchorView = binding.fab
            show()
        }.also { snackbar = it }
        updateHistory(event.results)
    }

    private val deleteCallback = object : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            gameViewModel.confirmDeletion()
            snackbar = null
        }
    }

    override fun onResume() {
        super.onResume()
        gameViewModel.loadGame()
    }

    override fun onPause() {
        super.onPause()
        snackbar?.dismiss()
    }

    private fun getItems(scores: List<LapRow>): List<BaseLapAdapter<out LapRow>> = scores.map { lap ->
        when (lap) {
            is UniversalLapRow -> UniversalLapAdapter(lap, ::onLapClicked)
            is BeloteLapRow -> BeloteLapAdapter(lap, ::onLapClicked)
            is CoincheLapRow -> CoincheLapAdapter(lap, ::onLapClicked)
            is TarotLapRow -> TarotLapAdapter(lap, ::onLapClicked)
            is DonationRow -> DonationAdapter(lap, ::onDonateClicked)
        }
    }

    private fun onLapClicked() {
        Snackbar.make(binding.mainContainer, R.string.history_lap_click_hint, Snackbar.LENGTH_SHORT)
            .setAnchorView(binding.fab)
            .show()
    }

    private fun onDonateClicked(donation: Donation) {
        billingRepository.startBillingFlow(this, donation) { gameViewModel.onDonationPerformed() }
    }

    private fun onEdit(position: Int) {
        gameViewModel.editLap(position)
        historyAdapter.notifyItemChanged(position)
    }

    private fun onDelete(position: Int) {
        gameViewModel.deleteLap(position)
    }

    private fun displayPlayerEditionOptions(position: Int) {
        if (!gameViewModel.canEditPlayer(position)) return
        MaterialAlertDialogBuilder(this)
            .setItems(R.array.dialog_edit_player_actions) { _, which ->
                when (which) {
                    0 -> displayNameEditionDialog(position)
                    1 -> displayColorDialog(position)
                }
            }
            .create()
            .show()
    }

    private fun displayNameEditionDialog(position: Int) {
        val action = { name: String ->
            if (name.isNotEmpty()) gameViewModel.setPlayerName(position, name)
        }
        val view = DialogEditNameBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(view.root)
            .setPositiveButton(R.string.button_action_ok) { _, _ ->
                action(view.name.text.toString())
            }
            .create()
        view.name.apply {
            requestFocus()
            onImeActionDone {
                action(text.toString())
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun displayColorDialog(position: Int) {
        ColorPickerFragment().show(
            resources.getIntArray(R.array.colors),
            supportFragmentManager
        ) { color ->
            gameViewModel.setPlayerColor(position, color)
        }
    }

    private class HistoryDiffCallback(
        private val oldEntries: List<BaseLapAdapter<out LapRow>>,
        private val newEntries: List<BaseLapAdapter<out LapRow>>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldEntries[oldItemPosition].model == newEntries[newItemPosition].model

        override fun getOldListSize(): Int = oldEntries.size

        override fun getNewListSize(): Int = newEntries.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            areItemsTheSame(oldItemPosition, newItemPosition)
    }
}
