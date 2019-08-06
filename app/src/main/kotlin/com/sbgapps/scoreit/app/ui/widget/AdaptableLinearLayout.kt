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

package com.sbgapps.scoreit.app.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.sbgapps.scoreit.app.R
import com.sbgapps.scoreit.core.ext.inflate

class AdaptableLinearLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        if (isInEditMode) repeat(4) {
            inflate(R.layout.tools_linearlistview_preview, true)
        }
    }

    var adapter: AdaptableLinearLayoutAdapter? = null
        set(adapter) {
            field = adapter
            setupChildren()
        }

    private fun setupChildren() {
        removeAllViews()
        adapter?.let {
            (0 until it.getCount()).forEach { i ->
                addView(it.getView(i, this))
            }
        }
    }
}

interface AdaptableLinearLayoutAdapter {
    fun getView(position: Int, parent: ViewGroup): View
    fun getCount(): Int
}
