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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.ext.color
import com.sbgapps.scoreit.ui.ext.inflate
import com.sbgapps.scoreit.ui.ext.sameContentWith
import com.sbgapps.scoreit.ui.model.UniversalLap
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import com.sbgapps.scoreit.ui.widget.LinearListView
import kotlinx.android.synthetic.main.fragment_universal_history.*
import kotlinx.android.synthetic.main.item_universal_history.view.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class UniversalHistoryFragment : BaseFragment() {

    private val model by sharedViewModel<UniversalViewModel>()
    private val adapter = LapListAdapter()
    private var deleteAction: (() -> Unit)? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_universal_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        lapRecycler.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(context)
        lapRecycler.adapter = adapter
        lapRecycler.setHasFixedSize(true)
        lapRecycler.addItemDecoration(androidx.recyclerview.widget.DividerItemDecoration(context, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getLaps().observe(this, Observer {
            it?.let { adapter.updateList(it) }
        })
    }

    override fun onPause() {
        super.onPause()
        deleteAction?.invoke()
    }

    inner class LapListAdapter : androidx.recyclerview.widget.RecyclerView.Adapter<Holder>() {
        private var laps = emptyList<UniversalLap>()

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.bind(laps[position])
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(parent.inflate(R.layout.item_universal_history))
        }

        override fun getItemCount(): Int = laps.size

        fun updateList(newLaps: List<UniversalLap>) {
            val diffResult = DiffUtil.calculateDiff(HistoryDiffCallback(newLaps, laps))
            laps = newLaps
            diffResult.dispatchUpdatesTo(this)
        }
    }

    inner class Holder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view) {
        private val adapter = LapItemAdapter()

        init {
            itemView.points.setAdapter(adapter)
        }

        fun bind(lap: UniversalLap) {
            Timber.d("Lap at position $adapterPosition with id ${lap.id} is updated")
            adapter.items = lap.points

            itemView.delete.setOnClickListener {
                deleteLap(lap)
                itemView.revealLayout.close(true)
            }

            itemView.edit.setOnClickListener {
                model.startUpdateMode(UniversalLap(lap.id, lap.points.toMutableList()))
                with(activity as ScoreItActivity) {
                    displayEdition()
                    switchFab()
                    invalidateOptionsMenu()
                }
                itemView.revealLayout.close(true)
            }

            itemView.points.setOnClickListener {
                if (itemView.revealLayout.isOpen) {
                    itemView.revealLayout.close(true)
                } else {
                    itemView.revealLayout.open(true)
                }
            }
        }

        private fun deleteLap(lap: UniversalLap) {
            val position = adapterPosition
            model.deleteLap(lap)
            deleteAction = { model.deleteLapFromCache() }
            com.google.android.material.snackbar.Snackbar.make(rootContainer, R.string.snackbar_msg_on_lap_deleted, com.google.android.material.snackbar.Snackbar.LENGTH_LONG)
                    .setAction(R.string.snackbar_action_undo, {
                        model.restoreLap(lap, position)
                        deleteAction = null
                    })
                    .setActionTextColor(context!!.color(R.color.orange_500))
                    .addCallback(object : com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback<com.google.android.material.snackbar.Snackbar>() {
                        override fun onDismissed(transientBottomBar: com.google.android.material.snackbar.Snackbar?, event: Int) {
                            when (event) {
                                DISMISS_EVENT_TIMEOUT,
                                DISMISS_EVENT_CONSECUTIVE,
                                DISMISS_EVENT_SWIPE -> deleteAction?.invoke()
                            }
                        }
                    })
                    .show()
        }
    }

    inner class LapItemAdapter : LinearListView.Adapter<Int>() {

        override val layoutId = R.layout.item_point

        override fun bind(position: Int, view: View) {
            (view as TextView).text = getItem(position).toString()
        }
    }

    inner class HistoryDiffCallback(private val newLaps: List<UniversalLap>,
                                    private val oldLaps: List<UniversalLap>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldLaps[oldItemPosition] == newLaps[newItemPosition]
        }

        override fun getOldListSize() = oldLaps.size

        override fun getNewListSize() = newLaps.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldLaps[oldItemPosition].points sameContentWith newLaps[newItemPosition].points
        }
    }

    companion object {
        fun newInstance() = UniversalHistoryFragment()
    }
}