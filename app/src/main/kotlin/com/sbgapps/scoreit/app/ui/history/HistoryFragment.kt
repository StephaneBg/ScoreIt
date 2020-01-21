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

package com.sbgapps.scoreit.app.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.DialogEditNameBinding
import com.sbgapps.scoreit.app.databinding.FragmentHistoryBinding
import com.sbgapps.scoreit.app.model.BeloteLapRow
import com.sbgapps.scoreit.app.model.CoincheLapRow
import com.sbgapps.scoreit.app.model.LapRow
import com.sbgapps.scoreit.app.model.TarotLapRow
import com.sbgapps.scoreit.app.model.UniversalLapRow
import com.sbgapps.scoreit.app.ui.Content
import com.sbgapps.scoreit.app.ui.GameEvent
import com.sbgapps.scoreit.app.ui.GameViewModel
import com.sbgapps.scoreit.app.ui.color.ColorPickerFragment
import com.sbgapps.scoreit.app.ui.history.adapter.BeloteLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.CoincheLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.HeaderAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.TarotLapAdapter
import com.sbgapps.scoreit.app.ui.history.adapter.UniversalLapAdapter
import com.sbgapps.scoreit.core.ext.onImeActionDone
import com.sbgapps.scoreit.core.ui.BaseFragment
import com.sbgapps.scoreit.core.widget.DividerItemDecoration
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import com.sbgapps.scoreit.core.widget.ItemAdapter
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryFragment : BaseFragment() {

    private val viewModel by sharedViewModel<GameViewModel>()
    private lateinit var binding: FragmentHistoryBinding
    private val historyAdapter = GenericRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(binding.bottomAppBar)

        binding.recyclerView.apply {
            adapter = historyAdapter
            ItemTouchHelper(HistorySwipeCallback(::onEdit, ::onDelete)).attachToRecyclerView(this)
            addItemDecoration(DividerItemDecoration(requireContext()))
        }
        binding.fab.setOnClickListener {
            viewModel.addLap()
        }

        onStates(viewModel) { state ->
            when (state) {
                is Content -> {
                    binding.header.adapter = HeaderAdapter(state.header, ::displayPlayerEditionOptions)
                    historyAdapter.updateItems(getItems(state.results))
                }
            }
        }

        onEvents(viewModel) { event ->
            when (val data = event.take()) {
                is GameEvent.Edition -> findNavController().navigate(data.actionId)
                is GameEvent.Deletion -> manageDeletion(data)
            }
        }
    }

    private fun manageDeletion(data: GameEvent.Deletion) {
        val callback = DeleteCallback(data.position)
        val snackbar = Snackbar.make(binding.mainContainer, R.string.snackbar_msg_on_lap_deleted, Snackbar.LENGTH_LONG)
        snackbar
            .setAction(R.string.snackbar_action_on_lap_deleted) {
                snackbar.removeCallback(callback)
                viewModel.loadGame()
            }
            .addCallback(callback)
            .show()
        historyAdapter.updateItems(getItems(data.results))
    }

    private inner class DeleteCallback(private val position: Int) : Snackbar.Callback() {
        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            viewModel.confirmLapDeletion(position)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadGame()
    }

    private fun getItems(scores: List<LapRow>): List<ItemAdapter> = scores.map { lap ->
        when (lap) {
            is UniversalLapRow -> UniversalLapAdapter(lap, ::onLapClicked)
            is BeloteLapRow -> BeloteLapAdapter(lap, ::onLapClicked)
            is CoincheLapRow -> CoincheLapAdapter(lap, ::onLapClicked)
            is TarotLapRow -> TarotLapAdapter(lap, ::onLapClicked)
        }
    }

    private fun onLapClicked() {
        Snackbar.make(binding.root, R.string.history_lap_click_hint, Snackbar.LENGTH_SHORT).show()
    }

    private fun onEdit(position: Int) {
        viewModel.editLap(position)
    }

    private fun onDelete(position: Int) {
        viewModel.deleteLap(position)
    }

    private fun displayPlayerEditionOptions(position: Int) {
        if (!viewModel.canEditPlayer(position)) return
        MaterialAlertDialogBuilder(requireContext())
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
            if (name.isNotEmpty()) viewModel.setPlayerName(position, name)
        }
        val view = DialogEditNameBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(requireContext())
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
            childFragmentManager
        ) { color ->
            viewModel.setPlayerColor(position, color)
        }
    }
}
