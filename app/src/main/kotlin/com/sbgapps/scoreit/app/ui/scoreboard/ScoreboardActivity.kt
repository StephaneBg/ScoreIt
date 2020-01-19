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

package com.sbgapps.scoreit.app.ui.scoreboard

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.button.MaterialButton
import com.sbgapps.scoreit.app.databinding.ActivityScoreboardBinding
import com.sbgapps.scoreit.app.ui.prefs.PreferencesViewModel
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.data.model.PlayerPosition
import io.uniflow.androidx.flow.onStates
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
        setupButton(binding.minusScoreOne, -1, PlayerPosition.ONE)
        setupButton(binding.plusScoreOne, +1, PlayerPosition.ONE)
        setupButton(binding.minusScoreTwo, -1, PlayerPosition.TWO)
        setupButton(binding.plusScoreTwo, +1, PlayerPosition.TWO)
        binding.reset.setOnClickListener { scoreBoarViewModel.reset() }

        onStates(scoreBoarViewModel) { state ->
            state as Content
            binding.scoreOne.text = state.scoreOne.toString()
            binding.scoreTwo.text = state.scoreTwo.toString()
        }
    }

    private fun setupButton(button: MaterialButton, increment: Int, player: PlayerPosition) {
        button.setOnClickListener { scoreBoarViewModel.incrementScore(increment, player) }
    }
}
