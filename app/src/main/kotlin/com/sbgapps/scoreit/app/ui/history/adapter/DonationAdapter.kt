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

import android.annotation.SuppressLint
import androidx.annotation.CallSuper
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.model.DonationRow
import com.sbgapps.scoreit.core.widget.BaseViewHolder
import com.sbgapps.scoreit.data.repository.Donation
import com.sbgapps.scoreit.databinding.ListItemLapDonationBinding

class DonationAdapter(
    model: DonationRow,
    private val callback: (donation: Donation) -> Unit
) : BaseLapAdapter<DonationRow>(model, R.layout.list_item_lap_donation, {}) {

    @SuppressLint("SetTextI18n")
    @CallSuper
    override fun onBindViewHolder(viewHolder: BaseViewHolder) {
        val binding = ListItemLapDonationBinding.bind(viewHolder.itemView)
        binding.coffee.text = Donation.COFFEE.name
        binding.coffee.setOnClickListener { callback(Donation.COFFEE) }
        binding.beer.text = Donation.BEER.name
        binding.beer.setOnClickListener { callback(Donation.BEER) }
    }
}
