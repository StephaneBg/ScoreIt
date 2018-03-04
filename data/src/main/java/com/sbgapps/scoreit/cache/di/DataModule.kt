/*
 * Copyright 2018 Stéphane Baiget
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit.cache.di

import com.sbgapps.scoreit.cache.DatabaseRepo
import com.sbgapps.scoreit.cache.UniversalGameRepository
import com.sbgapps.scoreit.cache.db.DatabaseInitializer
import com.sbgapps.scoreit.cache.mapper.PlayerMapper
import com.sbgapps.scoreit.cache.mapper.UniversalLapMapper
import com.sbgapps.scoreit.domain.model.UniversalLap
import com.sbgapps.scoreit.domain.repository.GameRepository
import org.koin.dsl.module.applicationContext

val dataModule = applicationContext {

    bean { PlayerMapper() }
    bean { UniversalLapMapper() }

    provide { DatabaseRepo(get()) }
    provide { DatabaseInitializer(get()) }
    provide { UniversalGameRepository(get(), get(), get(), get()) as GameRepository<UniversalLap> }
}