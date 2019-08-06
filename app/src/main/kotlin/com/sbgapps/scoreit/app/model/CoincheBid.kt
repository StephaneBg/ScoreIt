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

package com.sbgapps.scoreit.app.model

import androidx.annotation.StringRes
import com.sbgapps.scoreit.app.R

enum class CoincheBid(@StringRes val resId: Int) {
    None(R.string.coinche_coinche_none),
    Coinche(R.string.coinche_coinche_coinche),
    Surcoinche(R.string.coinche_coinche_surcoinche)
}

fun Int.toCoincheBid(): CoincheBid = CoincheBid.values()[this]
