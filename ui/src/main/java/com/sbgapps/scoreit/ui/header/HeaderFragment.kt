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
import android.widget.BaseAdapter
import android.widget.TextView
import com.sbgapps.scoreit.domain.model.Player
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.inflate
import kotlinx.android.synthetic.main.fragment_header.*
import org.jetbrains.anko.find
import org.koin.android.architecture.ext.viewModel


class HeaderFragment : BaseFragment() {

    private val model: UniversalViewModel by viewModel(true)
    private val adapter = HeaderAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_header)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        header.adapter = adapter
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getPlayersAndScores().observe(this, Observer {
            it?.let { adapter.setItems(it) }
        })
    }

    inner class HeaderAdapter : BaseAdapter() {

        private var items: List<Pair<Player, Int>> = emptyList()

        fun setItems(items: List<Pair<Player, Int>>) {
            this.items = items
            notifyDataSetChanged()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: parent.inflate(R.layout.item_header)

            val (player, score) = getItem(position)
            view.find<TextView>(R.id.name).text = player.name
            view.find<TextView>(R.id.score).text = score.toString()

            return view
        }

        override fun getItem(position: Int) = items[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = items.size
    }

    companion object {
        fun newInstance(): HeaderFragment {
            return HeaderFragment()
        }
    }
}