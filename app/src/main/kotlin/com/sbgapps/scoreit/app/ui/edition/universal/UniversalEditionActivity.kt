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

package com.sbgapps.scoreit.app.ui.edition.universal

import android.os.Bundle
import android.view.MenuItem
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityEditionUniversalBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel

class UniversalEditionActivity : EditionActivity() {

    private val viewModel by viewModel<UniversalEditionViewModel>()
    private lateinit var binding: ActivityEditionUniversalBinding
    private val lapAdapter = GenericRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEditionUniversalBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        binding.recyclerView.adapter = lapAdapter

        onStates(viewModel) { state ->
            when (state) {
                is UniversalEditionState.Content -> {
                    val adapters = state.players.mapIndexed { index, player ->
                        UniversalEditionAdapter(player, state.lap.results[index], ::onScoreEdited)
                    }
                    lapAdapter.updateItems(adapters)
                }

                is UniversalEditionState.Incremented -> {
                    (lapAdapter.items[state.position] as UniversalEditionAdapter).updateScore(state.points)
                }

                is UniversalEditionState.Completed -> finish()
            }
        }
    }

    override fun onUpPressed() {
        viewModel.cancelEdition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done -> {
            viewModel.completeEdition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onScoreEdited(position: Int, increment: Int) {
        viewModel.increment(position, increment)
    }
}
