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

package com.sbgapps.scoreit.data.model

import androidx.annotation.StringRes
import com.sbgapps.scoreit.data.R

enum class Coinche(val coefficient: Int, @StringRes val resId: Int) {
    NONE(1, R.string.coinche_coinche_none),
    COINCHE(2, R.string.coinche_coinche_coinche),
    SURCOINCHE(4, R.string.coinche_coinche_surcoinche);
}
