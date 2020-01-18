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

package com.sbgapps.scoreit.app.ui.prefs

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.databinding.ActivityPreferencesBinding
import com.sbgapps.scoreit.app.ui.ScoreItActivity
import com.sbgapps.scoreit.core.ext.start
import com.sbgapps.scoreit.core.ui.BaseActivity
import com.sbgapps.scoreit.core.utils.THEME_MODE_AUTO
import com.sbgapps.scoreit.core.utils.THEME_MODE_DARK
import com.sbgapps.scoreit.core.utils.THEME_MODE_LIGHT
import org.koin.androidx.viewmodel.ext.android.viewModel

class PreferencesActivity : BaseActivity() {

    private val viewModel by viewModel<PreferencesViewModel>()
    private lateinit var binding: ActivityPreferencesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppCompatDelegate.setDefaultNightMode(viewModel.getThemeMode())
        binding = ActivityPreferencesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        setupActionBar(binding.toolbar)

        binding.themeMode.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle(R.string.prefs_select_theme_mode)
                .setSingleChoiceItems(R.array.settings_theme_modes, getCurrentChoice()) { _, which ->
                    saveSelectedChoice(which)
                    start<ScoreItActivity>()
                    finish()
                }
                .create()
                .show()
        }

        binding.showTotal.apply {
            isChecked = viewModel.isUniversalTotalDisplayed()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setUniversalTotalDisplayed(isChecked)
            }
        }

        binding.roundBelote.apply {
            isChecked = viewModel.isBeloteScoreRounded()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setBeloteScoreRounded(isChecked)
            }
        }

        binding.roundCoinche.apply {
            isChecked = viewModel.isCoincheScoreRounded()
            setOnCheckedChangeListener { _, isChecked ->
                viewModel.setCoincheScoreRounded(isChecked)
            }
        }
    }

    private fun getCurrentChoice(): Int = when (viewModel.getPrefThemeMode()) {
        THEME_MODE_LIGHT -> 0
        THEME_MODE_DARK -> 1
        else -> 2
    }

    private fun saveSelectedChoice(which: Int) {
        val mode = when (which) {
            0 -> THEME_MODE_LIGHT
            1 -> THEME_MODE_DARK
            else -> THEME_MODE_AUTO
        }
        viewModel.setPrefThemeMode(mode)
    }
}
