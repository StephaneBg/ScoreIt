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
import com.sbgapps.scoreit.ui.R
import com.sbgapps.scoreit.ui.base.BaseFragment
import com.sbgapps.scoreit.ui.model.Player
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import com.sbgapps.scoreit.ui.widget.LinearListView
import kotlinx.android.synthetic.main.fragment_score.*
import kotlinx.android.synthetic.main.item_score.view.*
import org.koin.android.architecture.ext.sharedViewModel


class ScoreFragment : BaseFragment() {

    private val model by sharedViewModel<UniversalViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_score, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        model.getPlayers().observe(this, Observer {
            it?.let { scoreContainer.setAdapter(ScoreAdapter(it)) }
        })
    }

    inner class ScoreAdapter(items: List<Player>) : LinearListView.Adapter<Player>(items) {

        override val layoutId = R.layout.item_score

        override fun bind(position: Int, view: View) {
            val player = getItem(position)
            with(view) {
                name.text = player.name
                name.setTextColor(player.color)
                score.text = player.score.toString()
            }
        }
    }

    companion object {
        fun newInstance(): ScoreFragment {
            return ScoreFragment()
        }
    }
}