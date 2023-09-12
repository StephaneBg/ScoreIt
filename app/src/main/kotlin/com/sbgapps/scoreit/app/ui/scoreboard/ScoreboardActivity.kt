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

package com.sbgapps.scoreit.app.ui.scoreboard

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.ui.prefs.PreferencesViewModel
import com.sbgapps.scoreit.core.ext.onImeActionDone
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.data.model.PlayerPosition
import com.sbgapps.scoreit.databinding.ActivityScoreboardBinding
import com.sbgapps.scoreit.databinding.DialogEditNameBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ScoreboardActivity : BaseActivity() {

    private lateinit var binding: ActivityScoreboardBinding
    private val scoreBoarViewModel by viewModel<ScoreBoardViewModel>()
    private val prefsViewModel by viewModel<PreferencesViewModel>()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(prefsViewModel.getThemeMode())
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        binding = ActivityScoreboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.back.setOnClickListener { onBackPressed() }
        bindButton(binding.minusScoreOne, -1, PlayerPosition.ONE)
        bindButton(binding.plusScoreOne, +1, PlayerPosition.ONE)
        bindButton(binding.minusScoreTwo, -1, PlayerPosition.TWO)
        bindButton(binding.plusScoreTwo, +1, PlayerPosition.TWO)
        binding.reset.setOnClickListener { scoreBoarViewModel.reset() }

        binding.nameOne.setOnClickListener { displayNameDialog(PlayerPosition.ONE) }
        binding.nameTwo.setOnClickListener { displayNameDialog(PlayerPosition.TWO) }

        scoreBoarViewModel.observeStates(this) { state ->
            when (state) {
                is Content -> {
                    binding.scoreOne.text = state.scoreBoard.scoreOne.toString()
                    binding.scoreTwo.text = state.scoreBoard.scoreTwo.toString()
                    binding.nameOne.text = state.scoreBoard.nameOne
                    binding.nameTwo.text = state.scoreBoard.nameTwo
                }
            }
        }
    }

    private fun bindButton(button: MaterialButton, increment: Int, player: PlayerPosition) {
        button.setOnClickListener { scoreBoarViewModel.incrementScore(increment, player) }
    }

    private fun displayNameDialog(position: PlayerPosition) {
        val action = { name: String ->
            if (name.isNotEmpty()) scoreBoarViewModel.setPlayerName(name, position)
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
}
