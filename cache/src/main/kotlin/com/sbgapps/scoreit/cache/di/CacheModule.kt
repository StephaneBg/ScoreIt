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

import com.sbgapps.scoreit.cache.manager.GameManager
import com.sbgapps.scoreit.cache.manager.StorageManager
import com.sbgapps.scoreit.cache.repository.ScoreItCacheRepo
import com.sbgapps.scoreit.cache.repository.ScoreItPreferencesRepo
import com.sbgapps.scoreit.data.repository.CacheRepo
import com.sbgapps.scoreit.data.repository.PreferencesRepo
import org.koin.dsl.module

val cacheModule = module {

    single { GameManager(get(), get(), get()) }
    single { StorageManager(get(), get()) }
    single<CacheRepo> { ScoreItCacheRepo(get()) }
    single<PreferencesRepo> { ScoreItPreferencesRepo(get()) }
}
