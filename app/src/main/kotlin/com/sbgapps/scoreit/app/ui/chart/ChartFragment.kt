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

package com.sbgapps.scoreit.app.ui.chart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.app.ui.widget.LineChart
import com.sbgapps.scoreit.core.ext.dip
import com.sbgapps.scoreit.core.ext.observe
import com.sbgapps.scoreit.databinding.FragmentChartBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChartFragment : BottomSheetDialogFragment() {

    private val viewModel by viewModel<ChartViewModel>()
    private lateinit var binding: FragmentChartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChartBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observe(viewModel.getLines()) {
            it?.let {
                (view as LineChart).lines = it
            }
        }
    }

    override fun onStart() {
        super.onStart()

        dialog?.findViewById<View>(R.id.design_bottom_sheet)?.updateLayoutParams<ViewGroup.LayoutParams> {
            height = requireContext().dip(resources.configuration.screenHeightDp * 2 / 3)
        }
    }
}
