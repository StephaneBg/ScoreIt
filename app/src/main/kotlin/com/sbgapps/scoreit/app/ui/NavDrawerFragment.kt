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

package com.sbgapps.scoreit.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.navigation.NavigationView
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.data.model.Game
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class NavDrawerFragment : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<GameViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_nav_drawer, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (view as NavigationView).setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.universal -> viewModel.selectGame(Game.UNIVERSAL)
                R.id.tarot -> viewModel.selectGame(Game.TAROT)
                R.id.belote -> viewModel.selectGame(Game.BELOTE)
//                R.id.coinche -> viewModel.selectGame(Game.COINCHE)

                R.id.preferences -> findNavController().navigate(R.id.action_navDrawerFragment_to_preferencesActivity)
            }
            this@NavDrawerFragment.dismiss()
            true
        }
    }
}
