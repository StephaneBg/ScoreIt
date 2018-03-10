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
import android.support.v7.util.DiffUtil
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.inflate
import com.sbgapps.scoreit.ui.ext.sameContentWith
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.android.synthetic.main.fragment_universal_history.*
import kotlinx.android.synthetic.main.item_lap.view.*
import org.koin.android.architecture.ext.viewModel

class UniversalHistoryFragment : BaseFragment() {

    private val model: UniversalViewModel by viewModel(true)
    private val adapter = LapListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_universal_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lapRecycler.layoutManager = LinearLayoutManager(context)
        lapRecycler.adapter = adapter
        lapRecycler.setHasFixedSize(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getLaps().observe(this, Observer {
            it?.let { adapter.updateList(it.map { it.getPoints().toMutableList() }) }
        })
    }

    inner class LapListAdapter : RecyclerView.Adapter<Holder>() {
        private var laps = emptyList<List<Int>>()

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(laps[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(parent.inflate(R.layout.item_lap))
        }

        override fun getItemCount(): Int = laps.size

        fun updateList(newLaps: List<List<Int>>) {
            val diffResult = DiffUtil.calculateDiff(NoteDiffCallback(newLaps, laps))
            laps = newLaps
            diffResult.dispatchUpdatesTo(this)
        }
    }

    inner class Holder(view: View?) : RecyclerView.ViewHolder(view) {
        fun bind(points: List<Int>) {
            itemView.lapItem.adapter = LapItemAdapter(points)
        }
    }

    inner class LapItemAdapter(private val points: List<Int>) : BaseAdapter() {

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: parent.inflate(R.layout.item_point)
            (view as TextView).text = getItem(position).toString()
            return view
        }

        override fun getItem(position: Int) = points[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = points.size
    }

    inner class NoteDiffCallback(private val newLaps: List<List<Int>>,
                                 private val oldLaps: List<List<Int>>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldLaps[oldItemPosition] == newLaps[newItemPosition]
        }

        override fun getOldListSize() = oldLaps.size

        override fun getNewListSize() = newLaps.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldLaps[oldItemPosition] sameContentWith newLaps[newItemPosition]
        }
    }

    companion object {
        fun newInstance(): UniversalHistoryFragment {
            return UniversalHistoryFragment()
        }
    }
}