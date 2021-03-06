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

package com.sbgapps.scoreit.core.ext

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.annotation.LayoutRes

fun View.show() {
    animate()
        .setDuration(
            context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        )
        .alpha(1f)
        .withStartAction {
            alpha = 0f
            visibility = View.VISIBLE
        }
        .start()
}

fun View.hide() {
    animate()
        .setDuration(
            context.resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
        )
        .alpha(0f)
        .withEndAction { visibility = View.INVISIBLE }
        .start()
}

fun ViewGroup.layoutInflater(): LayoutInflater = LayoutInflater.from(context)

fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    layoutInflater().inflate(layoutRes, this, attachToRoot)

fun EditText.onImeActionDone(function: () -> Unit) {
    setOnEditorActionListener { _, actionId, _ ->
        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                function()
                true
            }
            else -> false
        }
    }
}
