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

package com.sbgapps.scoreit.ui

import android.app.Application
import com.sbgapps.scoreit.cache.di.dataModule
import com.sbgapps.scoreit.domain.di.domainModule
import com.sbgapps.scoreit.ui.di.uiModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class ScoreItApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())

        startKoin(this,
                listOf(
                        dataModule,
                        domainModule,
                        uiModule
                )
        )
    }
}