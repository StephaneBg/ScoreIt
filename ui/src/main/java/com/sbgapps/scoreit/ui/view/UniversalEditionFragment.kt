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
import android.widget.TextView
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import com.sbgapps.scoreit.ui.widget.LinearListView
import kotlinx.android.synthetic.main.fragment_universal_edition.*
import kotlinx.android.synthetic.main.item_universal_edition.view.*
import org.koin.android.architecture.ext.sharedViewModel

class UniversalEditionFragment : BaseFragment() {

    private val model by sharedViewModel<UniversalViewModel>()
    private var points = mutableListOf<Int>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_universal_edition, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getEditedLap().observe(this, Observer {
            it?.let {
                points = it.first.points
                players.setAdapter(PlayerAdapter(it.second.zip(it.first.points)))
            }
        })
    }

    inner class PlayerAdapter(items: List<Pair<String, Int>>) : LinearListView.Adapter<Pair<String, Int>>(items) {

        override val layoutId = R.layout.item_universal_edition

        override fun bind(position: Int, view: View) {
            val (player, _points) = getItem(position)
            with(view) {
                playerName.text = player
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
        }

        private fun addPoints(_points: Int, position: Int, textView: TextView) {
            points[position] += _points
            textView.text = points[position].toString()
        }
    }

    companion object {
        fun newInstance(): UniversalEditionFragment {
            return UniversalEditionFragment()
        }
    }
}