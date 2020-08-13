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

package com.sbgapps.scoreit.app.ui.history.adapter

import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.sbgapps.scoreit.app.databinding.ListItemHeaderBinding
import com.sbgapps.scoreit.app.model.Header
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.core.ext.layoutInflater

class HeaderAdapter(
    private val model: Header,
    private val editCallback: (position: Int) -> Unit
) : AdaptableLinearLayoutAdapter {

    override fun getView(position: Int, parent: ViewGroup): View {
        val binding = ListItemHeaderBinding.inflate(parent.layoutInflater(), parent, false)
        val player = model.players[position]
        binding.player.apply {
            text = player.name
            setTextColor(player.color)
            setOnClickListener { editCallback(position) }
        }
        binding.score.text = model.scores[position].toString()
        binding.marker.isInvisible = !model.markers[position]
        return binding.root
    }

    override fun getCount(): Int = model.players.size
}
