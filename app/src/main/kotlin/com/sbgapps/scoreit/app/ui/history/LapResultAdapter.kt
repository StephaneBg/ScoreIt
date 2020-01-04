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

package com.sbgapps.scoreit.app.ui.history

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayoutAdapter
import com.sbgapps.scoreit.core.ext.inflate

class LapResultAdapter(private val model: List<String>) : AdaptableLinearLayoutAdapter {

    override fun getView(position: Int, parent: ViewGroup): View {
        val view = parent.inflate(R.layout.list_item_lap_result) as TextView
        view.text = model[position]
        return view
    }

    override fun getCount(): Int = model.size
}
