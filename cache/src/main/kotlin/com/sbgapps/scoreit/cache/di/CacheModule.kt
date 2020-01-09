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

package com.sbgapps.scoreit.cache.di

import androidx.preference.PreferenceManager
import com.sbgapps.scoreit.cache.model.BeloteGameCache
import com.sbgapps.scoreit.cache.model.CoincheGameCache
import com.sbgapps.scoreit.cache.model.GameCache
import com.sbgapps.scoreit.cache.model.TarotGameCache
import com.sbgapps.scoreit.cache.model.UniversalGameCache
import com.sbgapps.scoreit.cache.repository.FileStorage
import com.sbgapps.scoreit.cache.repository.ScoreItCacheRepo
import com.sbgapps.scoreit.cache.repository.ScoreItFileStorage
import com.sbgapps.scoreit.cache.repository.ScoreItGameDao
import com.sbgapps.scoreit.cache.repository.ScoreItPreferencesRepo
import com.sbgapps.scoreit.data.model.GameType
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.PolymorphicJsonAdapterFactory
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val cacheModule = module {

    single { PreferenceManager.getDefaultSharedPreferences(androidContext()) }
    single { createMoshi().adapter(GameCache::class.java) }
    single { ScoreItGameDao(androidContext(), get(), get(), get()) }
    single<FileStorage> { ScoreItFileStorage(androidContext()) }
    single<CacheRepo> { ScoreItCacheRepo(get(), get()) }
    single<PreferencesRepo> { ScoreItPreferencesRepo(get()) }
}

private fun createMoshi(): Moshi = Moshi.Builder()
    .add(
        PolymorphicJsonAdapterFactory.of(GameCache::class.java, "type")
            .withSubtype(UniversalGameCache::class.java, GameType.UNIVERSAL.name)
            .withSubtype(TarotGameCache::class.java, GameType.TAROT.name)
            .withSubtype(BeloteGameCache::class.java, GameType.BELOTE.name)
            .withSubtype(CoincheGameCache::class.java, GameType.COINCHE.name)
    )
    .add(KotlinJsonAdapterFactory())
    .build()
