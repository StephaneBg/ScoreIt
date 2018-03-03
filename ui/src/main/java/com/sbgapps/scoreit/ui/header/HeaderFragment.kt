/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.ui.header

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.inflate
import org.koin.android.architecture.ext.viewModel


class HeaderFragment : BaseFragment() {

    private val model: HeaderViewModel by viewModel()
    private lateinit var playerAdapter: ArrayAdapter<Player>
    private lateinit var scoreAdapter: ArrayAdapter<Int>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_header)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        playerAdapter = ArrayAdapter(activity!!, R.layout.item_player, R.id.playerName)
        model.getPlayers().observe(this, Observer {
            playerAdapter.clear()
            playerAdapter.addAll(it)
        })

        scoreAdapter = ArrayAdapter(activity!!, R.layout.item_player, R.id.playerName)
        model.getScores().observe(this, Observer {
            scoreAdapter.clear()
            scoreAdapter.addAll(it)
        })

        model.getPlayers()
        model.getScores()
    }

    companion object {
        fun newInstance(): HeaderFragment {
            return HeaderFragment()
        }
    }
}