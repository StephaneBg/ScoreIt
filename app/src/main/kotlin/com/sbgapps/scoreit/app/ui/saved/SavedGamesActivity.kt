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
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivitySavedGamesBinding
import com.sbgapps.scoreit.app.ui.GameViewModel
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.core.widget.DividerItemDecoration
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter

class SavedGamesActivity : BaseActivity() {

    private lateinit var binding: ActivitySavedGamesBinding
    private val gameViewModel by viewModel<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySavedGamesBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupActionBar(binding.toolbar)

        val adapters = gameViewModel.getSavedFiles().map { (name, duration, players) ->
            val instant = Instant.ofEpochMilli(duration)
            val lastModified = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern(getString(R.string.local_date_pattern)))
                .capitalize()
            SavedGameAdapter(
                name,
                lastModified,
                players,
                ::onGameSelected
            )
        }
        binding.recyclerView.apply {
            adapter = GenericRecyclerViewAdapter(adapters)
            addItemDecoration(DividerItemDecoration(this@SavedGamesActivity))
        }
    }

    private fun onGameSelected(fileName: String) {
        gameViewModel.loadGame(fileName)
        finish()
    }
}
