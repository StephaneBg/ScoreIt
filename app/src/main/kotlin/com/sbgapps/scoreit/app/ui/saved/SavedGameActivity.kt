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

package com.sbgapps.scoreit.app.ui.saved

import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivitySavedGamesBinding
import com.sbgapps.scoreit.app.databinding.DialogEditNameBinding
import com.sbgapps.scoreit.core.ext.onImeActionDone
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.core.widget.DividerItemDecoration
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import io.uniflow.androidx.flow.onStates
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SavedGameActivity : BaseActivity() {

    private lateinit var binding: ActivitySavedGamesBinding
    private val viewModel by viewModel<SavedGameViewModel> { parametersOf(getString(R.string.local_date_pattern)) }
    private val savedGamesAdapter = GenericRecyclerViewAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySavedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        binding.recyclerView.apply {
            adapter = savedGamesAdapter
            ItemTouchHelper(SavedGameSwipeCallback(::onEdit, ::onDelete)).attachToRecyclerView(this)
            addItemDecoration(DividerItemDecoration(this@SavedGameActivity))
        }

        onStates(viewModel) { state ->
            when (state) {
                is SavedAction.Content -> {
                    val adapters = state.games.map { (name, players, date) ->
                        SavedGameAdapter(name, players, date, ::onGameSelected)
                    }
                    savedGamesAdapter.updateItems(adapters)
                }
                is SavedAction.Complete -> finish()
            }
        }

        viewModel.getSavedFiles()
    }

    private fun onEdit(position: Int) {
        displayNameEditionDialog(position)
    }

    private fun onDelete(position: Int) {
        viewModel.deleteGame(position)
    }

    private fun onGameSelected(fileName: String) {
        viewModel.loadGame(fileName)
    }

    private fun displayNameEditionDialog(position: Int) {
        val action = { name: String ->
            if (name.isNotEmpty()) viewModel.editName(position, name)
        }
        val view = DialogEditNameBinding.inflate(layoutInflater)
        val dialog = MaterialAlertDialogBuilder(this)
            .setView(view.root)
            .setPositiveButton(R.string.button_action_ok) { _, _ ->
                action(view.name.text.toString())
            }
            .setOnDismissListener {
                viewModel.getSavedFiles()
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
}
