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

package com.sbgapps.scoreit.cache.di

import androidx.preference.PreferenceManager
import com.sbgapps.scoreit.cache.dao.FileStorage
import com.sbgapps.scoreit.cache.dao.ScoreItBillingDao
import com.sbgapps.scoreit.cache.dao.ScoreItFileStorage
import com.sbgapps.scoreit.cache.dao.ScoreItGameDao
import com.sbgapps.scoreit.cache.repository.ScoreItBillingRepo
import com.sbgapps.scoreit.cache.repository.ScoreItCacheRepo
import com.sbgapps.scoreit.cache.repository.ScoreItPreferencesRepo
import com.sbgapps.scoreit.data.model.BeloteGame
import com.sbgapps.scoreit.data.model.CoincheGame
import com.sbgapps.scoreit.data.model.Game
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.model.TarotGame
import com.sbgapps.scoreit.data.model.UniversalGame
import com.sbgapps.scoreit.data.repository.BillingRepo
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {

    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single {
        ScoreItGameDao(
            androidContext(),
            get(),
            get(),
            createMoshi()
        )
    }
    single<FileStorage> {
        ScoreItFileStorage(
            androidContext()
        )
    }
    single { ScoreItBillingDao(get()) }

    single<CacheRepo> { ScoreItCacheRepo(get(), get(), get()) }
    single<PreferencesRepo> { ScoreItPreferencesRepo(get()) }
    single<BillingRepo> { ScoreItBillingRepo(androidContext(), get()) }
}

private fun createMoshi(): Moshi = Moshi.Builder()
    .add(
        PolymorphicJsonAdapterFactory.of(Game::class.java, "type")
            .withSubtype(UniversalGame::class.java, GameType.UNIVERSAL.name)
            .withSubtype(TarotGame::class.java, GameType.TAROT.name)
            .withSubtype(BeloteGame::class.java, GameType.BELOTE.name)
            .withSubtype(CoincheGame::class.java, GameType.COINCHE.name)
    )
    .add(KotlinJsonAdapterFactory())
    .build()
