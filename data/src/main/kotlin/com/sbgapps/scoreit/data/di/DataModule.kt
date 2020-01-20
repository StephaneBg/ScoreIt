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

package com.sbgapps.scoreit.data.di

import android.graphics.Color
import com.sbgapps.scoreit.data.R
import com.sbgapps.scoreit.data.interactor.GameUseCase
import com.sbgapps.scoreit.data.interactor.ScoreBoardUseCase
import com.sbgapps.scoreit.data.model.Player
import com.sbgapps.scoreit.data.solver.BeloteSolver
import com.sbgapps.scoreit.data.solver.CoincheSolver
import com.sbgapps.scoreit.data.solver.TarotSolver
import com.sbgapps.scoreit.data.solver.UniversalSolver
import com.sbgapps.scoreit.data.source.DataStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {

    single { DataStore(get(), get(), Player(androidContext().getString(R.string.universal_total_points), Color.RED)) }

    single { GameUseCase(get(), get(), get(), get(), get()) }
    single { ScoreBoardUseCase(get()) }

    single { UniversalSolver() }
    single { TarotSolver() }
    single { BeloteSolver(get()) }
    single { CoincheSolver(get()) }
}
