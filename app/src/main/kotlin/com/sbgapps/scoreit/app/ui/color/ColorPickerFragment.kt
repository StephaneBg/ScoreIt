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

package com.sbgapps.scoreit.app.ui.color

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sbgapps.scoreit.app.databinding.FragmentColorPickerBinding
import com.sbgapps.scoreit.core.widget.GenericRecyclerViewAdapter

class ColorPickerFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentColorPickerBinding
    private lateinit var adapters: List<ColorAdapter>
    private lateinit var listener: ((color: Int) -> Unit)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentColorPickerBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = GenericRecyclerViewAdapter(adapters)
    }

    fun show(colors: IntArray, fragmentManager: FragmentManager, listener: (color: Int) -> Unit) {
        this.listener = listener
        adapters = colors.map { ColorAdapter(it, ::onColorSelected) }
        show(fragmentManager, null)
    }

    private fun onColorSelected(color: Int) {
        dismiss()
        listener(color)
    }
}
