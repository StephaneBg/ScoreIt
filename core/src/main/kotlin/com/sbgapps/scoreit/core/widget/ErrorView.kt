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

package com.sbgapps.scoreit.core.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.sbgapps.scoreit.core.R
import com.sbgapps.scoreit.core.ext.show

class ErrorView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private val image: ImageView
    private val message: TextView
    private val action: Button

    init {
        orientation = VERTICAL
        id = R.id.errorView
        inflate(context, R.layout.widget_error_view, this)

        image = findViewById(R.id.errorImage)
        message = findViewById(R.id.errorMessage)
        action = findViewById(R.id.errorAction)
    }

    fun show(
        @DrawableRes drawable: Int = R.drawable.ic_error_outline_black_24dp,
        @StringRes msg: Int = R.string.error_general,
        retryAction: () -> Unit
    ) {
        image.setImageResource(drawable)
        message.setText(msg)
        action.setOnClickListener { retryAction() }
        show()
    }
}