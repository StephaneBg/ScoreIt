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

package com.sbgapps.scoreit.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.ui.prefs.PreferencesActivity
import com.sbgapps.scoreit.app.ui.scoreboard.ScoreboardActivity
import com.sbgapps.scoreit.core.ext.start
import com.sbgapps.scoreit.data.model.GameType
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NavDrawerFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<GameViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_nav_drawer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view as NavigationView).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.universal -> viewModel.selectGame(GameType.UNIVERSAL)
                R.id.scoreboard -> requireContext().start<ScoreboardActivity>()
                R.id.tarot -> viewModel.selectGame(GameType.TAROT)
                R.id.belote -> viewModel.selectGame(GameType.BELOTE)
                R.id.coinche -> viewModel.selectGame(GameType.COINCHE)

                R.id.preferences -> requireContext().start<PreferencesActivity>()
                R.id.about -> requireContext().start<AboutActivity>()
            }
            this@NavDrawerFragment.dismiss()
            true
        }
    }
}
