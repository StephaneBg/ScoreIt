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

package com.sbgapps.scoreit.app.ui.edition.universal

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sbgapps.scoreit.R
import com.sbgapps.scoreit.databinding.FragmentUniversalInputScoreBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UniversalInputScore : BottomSheetDialogFragment() {

    private val viewModel by sharedViewModel<UniversalEditionViewModel>()
    private lateinit var binding: FragmentUniversalInputScoreBinding
    private val playerIndex: Int
        get() = arguments?.getInt(ARG_PLAYER_POSITION) ?: error("Use newInstance")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUniversalInputScoreBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initKeys()
        initBackSpace()
        initSign()
        initDone()
    }

    @SuppressLint("SetTextI18n")
    private fun initKeys() {
        val listener = { v: View ->
            val key = v.tag as Int
            val current = with(binding.score.text) { if (startsWith("0")) drop(1) else this }
            binding.score.text = "$current$key"
            onNumberChanged()
        }

        val ids = resources.obtainTypedArray(R.array.input_score_key_ids)
        for (keyIndex in 0 until NB_KEYS) {
            val key = binding.root.findViewById<TextView>(ids.getResourceId(keyIndex, -1))
            key.tag = keyIndex
            key.setOnClickListener(listener)
        }
        ids.recycle()
    }

    private fun initBackSpace() {
        binding.backspace.isEnabled = false
        binding.backspace.setOnClickListener {
            if (binding.score.text.isEmpty()) return@setOnClickListener
            binding.score.text = binding.score.text.dropLast(1)
            onNumberChanged()
        }
        binding.backspace.setOnLongClickListener {
            binding.score.text = ""
            onNumberChanged()
            true
        }
    }

    private fun initDone() {
        binding.done.isEnabled = false
        binding.done.setOnClickListener {
            val score = try {
                Integer.parseInt(binding.score.text.toString())
            } catch (e: Exception) {
                0
            }
            viewModel.setScore(playerIndex, score)
            dismiss()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initSign() {
        binding.keySign.setOnClickListener {
            val number = binding.score.text.toString()
            if (number.startsWith("-")) binding.score.text = number.drop(1)
            else binding.score.text = "-$number"
            onNumberChanged()
        }
    }

    private fun onNumberChanged() {
        binding.backspace.isEnabled = binding.score.text.isNotEmpty()
        binding.done.isEnabled = !(1 == binding.score.text.length && '-' == binding.score.text[0]) &&
            binding.score.text.isNotEmpty()
    }

    companion object {
        private const val NB_KEYS = 10
        private const val ARG_PLAYER_POSITION = "ARG_PLAYER_POSITION"


        fun newInstance(position: Int) = UniversalInputScore().apply {
            arguments = bundleOf(ARG_PLAYER_POSITION to position)
        }
    }
}
