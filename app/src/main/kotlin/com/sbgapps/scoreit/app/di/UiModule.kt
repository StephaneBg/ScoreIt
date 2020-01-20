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

package com.sbgapps.scoreit.app.di

import com.sbgapps.scoreit.app.ui.GameViewModel
import com.sbgapps.scoreit.app.ui.chart.ChartViewModel
import com.sbgapps.scoreit.app.ui.edition.belote.BeloteEditionViewModel
import com.sbgapps.scoreit.app.ui.edition.coinche.CoincheEditionViewModel
import com.sbgapps.scoreit.app.ui.edition.tarot.TarotEditionViewModel
import com.sbgapps.scoreit.app.ui.edition.universal.UniversalEditionViewModel
import com.sbgapps.scoreit.app.ui.prefs.PreferencesViewModel
import com.sbgapps.scoreit.app.ui.scoreboard.ScoreBoardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val uiModule = module {

    viewModel { GameViewModel(get()) }
    viewModel { ChartViewModel(get()) }
    viewModel { PreferencesViewModel(get()) }
    viewModel { UniversalEditionViewModel(get()) }
    viewModel { TarotEditionViewModel(get(), get()) }
    viewModel { BeloteEditionViewModel(get(), get()) }
    viewModel { CoincheEditionViewModel(get(), get()) }
    viewModel { ScoreBoardViewModel(get()) }
}
