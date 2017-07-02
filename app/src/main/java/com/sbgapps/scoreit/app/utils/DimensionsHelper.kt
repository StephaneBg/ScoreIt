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

import android.content.res.Resources
import android.util.TypedValue

/**
 * Created by Stéphane on 14/10/2014.
 */
object DimensionsHelper {

    fun dpToPx(dp: Float, resources: Resources): Int {
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, resources.displayMetrics)
        return px.toInt()
    }

    fun spToPx(textSizeSp: Int, resources: Resources): Int {
        val density = resources.displayMetrics.density
        return (0.5f + density * textSizeSp).toInt()
    }
}
