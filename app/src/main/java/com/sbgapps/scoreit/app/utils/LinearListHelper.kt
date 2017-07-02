/*
 * Copyright 2017 Stéphane Baiget
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

package com.sbgapps.scoreit.app.utils

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout

import java.util.ArrayList

abstract class LinearListHelper<T> {

    fun populateItems(parent: LinearLayout, @LayoutRes resId: Int, count: Int): ArrayList<T> {
        val items = ArrayList<T>(count)
        val inflater = LayoutInflater.from(parent.context)

        for (i in 0..count - 1) {
            val view = inflater.inflate(resId, parent, false)
            val item = onCreateItem(view, i)
            items.add(item)
            parent.addView(view)
        }

        return items
    }

    abstract fun onCreateItem(view: View, player: Int): T
}
