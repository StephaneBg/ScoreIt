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
import com.sbgapps.scoreit.data.solver.TarotBonusData

enum class TarotBonus(@StringRes val resId: Int) {
    PetitAuBout(R.string.tarot_bonus_petit_au_bout),
    PoigneeSimple(R.string.tarot_bonus_simple_poignee),
    PoigneeDouble(R.string.tarot_bonus_double_poignee),
    PoigneeTriple(R.string.tarot_bonus_triple_poignee),
    ChelemNonAnnonce(R.string.tarot_bonus_slam_not_announced),
    ChelemAnnonceRealise(R.string.tarot_bonus_slam_announced_done),
    ChelemAnnoneNonRealise(R.string.tarot_bonus_slam_announced_not_done);

    fun toData(): TarotBonusData = TarotBonusData.values()[ordinal]
}

fun TarotBonusData.toTarotBonus(): TarotBonus = TarotBonus.values()[ordinal]
