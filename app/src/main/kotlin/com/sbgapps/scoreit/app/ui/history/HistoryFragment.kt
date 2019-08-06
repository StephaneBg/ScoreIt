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
import com.google.android.material.snackbar.Snackbar
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.FragmentHistoryBinding
import com.sbgapps.scoreit.app.model.*
import com.sbgapps.scoreit.app.ui.Content
import com.sbgapps.scoreit.app.ui.GameEvent
import com.sbgapps.scoreit.app.ui.GameViewModel
import com.sbgapps.scoreit.core.ui.BaseFragment
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import com.sbgapps.scoreit.core.widget.ItemAdapter
import com.sbgapps.scoreit.data.solver.BeloteSolver
import io.uniflow.androidx.flow.onEvents
import io.uniflow.androidx.flow.onStates
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HistoryFragment : BaseFragment() {

    private val viewModel by sharedViewModel<GameViewModel>()
    private val beloteSolver by inject<BeloteSolver>()
    private lateinit var binding: FragmentHistoryBinding
    private val historyAdapter = GenericRecyclerViewAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHistoryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar(binding.bottomAppBar)

        binding.results.apply {
            adapter = historyAdapter
            ItemTouchHelper(SwipeCallback(::onEdit, ::onDelete)).attachToRecyclerView(this)
        }
        binding.fab.setOnClickListener {
            viewModel.addLap()
        }

        onStates(viewModel) { state ->
            when (state) {
                is Content -> {
                    binding.header.adapter = HeaderAdapter(state.header)
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

    private fun getItems(scores: List<Lap>): List<ItemAdapter> = scores.map { lap ->
        when (lap) {
            is UniversalLap -> UniversalLapAdapter(lap)
            is BeloteLap -> BeloteLapAdapter(lap, beloteSolver)
            is CoincheLap -> CoincheLapAdapter(lap)
            is TarotLap -> TarotLapAdapter(lap)
        }
    }

    private fun onEdit(position: Int) {
        viewModel.editLap(position)
    }

    private fun onDelete(position: Int) {
        viewModel.deleteLap(position)
    }
}
