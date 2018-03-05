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

package com.sbgapps.scoreit.ui.view

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.inflate
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.android.synthetic.main.fragment_universal_lap.*
import kotlinx.android.synthetic.main.item_universal_lap.view.*
import org.koin.android.architecture.ext.viewModel
import timber.log.Timber
import kotlin.math.min

class UniversalLapFragment : BaseFragment() {

    private val model: UniversalViewModel by viewModel(true)
    private val adapter: PlayerAdapter = PlayerAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_universal_lap, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        players.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getPlayers().observe(this, Observer {
            it?.let { adapter.players = it }
        })
        model.getLap().observe(this, Observer {
            it?.let { adapter.points = it.points.toMutableList() }
        })
    }

    inner class PlayerAdapter : BaseAdapter() {

        var players = emptyList<Player>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }
        var points = mutableListOf<Int>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            Timber.d("Universal lap item $position is updated")
            val view = convertView ?: parent.inflate(R.layout.item_universal_lap)

            val (player, _points) = getItem(position)
            with(view) {
                playerName.text = player.name
                playerPoints.text = _points.toString()

                plusOne.setOnClickListener { addPoints(1, position, playerPoints) }
                plusFive.setOnClickListener { addPoints(5, position, playerPoints) }
                plusTen.setOnClickListener { addPoints(10, position, playerPoints) }
                plusHundred.setOnClickListener { addPoints(100, position, playerPoints) }
                minusOne.setOnClickListener { addPoints(-1, position, playerPoints) }
                minusFive.setOnClickListener { addPoints(-5, position, playerPoints) }
                minusTen.setOnClickListener { addPoints(-10, position, playerPoints) }
                minusHundred.setOnClickListener { addPoints(-100, position, playerPoints) }
            }

            return view
        }

        override fun getItem(position: Int) = Pair(players[position], points[position])

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = min(players.size, points.size)

        private fun addPoints(_points: Int, position: Int, textView: TextView) {
            points[position] += _points
            textView.text = points[position].toString()
            model.setPoints(points)
        }
    }

    companion object {
        fun newInstance(): UniversalLapFragment {
            return UniversalLapFragment()
        }
    }
}