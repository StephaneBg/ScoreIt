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
import androidx.recyclerview.widget.DiffUtil
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityEditionUniversalBinding
import com.sbgapps.scoreit.app.ui.edition.EditionActivity
import com.sbgapps.scoreit.core.ext.asListOfType
import com.sbgapps.scoreit.core.widget.DividerItemDecoration
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

        binding.recyclerView.apply {
            adapter = lapAdapter
            itemAnimator = null
            addItemDecoration(DividerItemDecoration(this@UniversalEditionActivity))
        }

        onStates(viewModel) { state ->
            when (state) {
                is UniversalEditionState.Content -> {
                    val adapters = state.players.mapIndexed { index, player ->
                        UniversalEditionAdapter(player, state.results[index], ::onScoreIncremented, ::onScoreEntered)
                    }
                    val diff = DiffUtil.calculateDiff(DiffCallback(lapAdapter.items.asListOfType(), adapters))
                    lapAdapter.updateItems(adapters, diff)
                }
                is UniversalEditionState.Completed -> finish()
            }
        }
        viewModel.loadContent()
    }

    override fun onBackPressed() {
        viewModel.cancelEdition()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.done -> {
            viewModel.completeEdition()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun onScoreIncremented(position: Int, increment: Int) {
        viewModel.incrementScore(increment, position)
    }

    private fun onScoreEntered(position: Int) {
        UniversalInputScore.newInstance(position).show(supportFragmentManager, null)
    }

    inner class DiffCallback(
        private val oldEntries: List<UniversalEditionAdapter>,
        private val newEntries: List<UniversalEditionAdapter>
    ) : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldEntries[oldItemPosition].score == newEntries[newItemPosition].score &&
                    oldEntries[oldItemPosition].player == newEntries[newItemPosition].player

        override fun getOldListSize(): Int = oldEntries.size

        override fun getNewListSize(): Int = newEntries.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldEntries[oldItemPosition].score == newEntries[newItemPosition].score
    }
}
