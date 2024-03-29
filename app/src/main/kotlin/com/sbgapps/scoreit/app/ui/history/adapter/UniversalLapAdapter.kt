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

import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.model.UniversalLapRow
import com.sbgapps.scoreit.app.ui.widget.AdaptableLinearLayout
import com.sbgapps.scoreit.core.widget.BaseViewHolder

class UniversalLapAdapter(
    model: UniversalLapRow,
    clickCallback: () -> Unit
) : BaseLapAdapter<UniversalLapRow>(model, R.layout.list_item_lap_universal, clickCallback) {

    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        super.onBindViewHolder(viewHolder)
        (viewHolder.itemView as AdaptableLinearLayout).adapter = LapResultAdapter(model.results.map { it.toString() })
    }
}
