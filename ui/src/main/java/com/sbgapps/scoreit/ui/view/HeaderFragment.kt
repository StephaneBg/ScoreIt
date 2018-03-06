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
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.inflate
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.android.synthetic.main.fragment_header.*
import kotlinx.android.synthetic.main.item_header.view.*
import org.koin.android.architecture.ext.viewModel


class HeaderFragment : BaseFragment() {

    private val model: UniversalViewModel by viewModel(true)
    private val adapter = HeaderAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_header, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        header.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getScores().observe(this, Observer {
            it?.let { adapter.scores = it }
        })
    }

    inner class HeaderAdapter : BaseAdapter() {

        var scores = emptyList<Pair<Player, Int>>()
            set(value) {
                field = value
                notifyDataSetChanged()
            }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: parent.inflate(R.layout.item_header)

            val (player, _score) = getItem(position)
            with(view) {
                name.text = player.name
                name.setTextColor(player.color)
                score.text = _score.toString()
            }

            return view
        }

        override fun getItem(position: Int) = scores[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = scores.size
    }

    companion object {
        fun newInstance(): HeaderFragment {
            return HeaderFragment()
        }
    }
}