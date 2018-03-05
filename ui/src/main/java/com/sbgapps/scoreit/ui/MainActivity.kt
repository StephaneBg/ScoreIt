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

import android.os.Bundle
import com.sbgapps.scoreit.ui.base.BaseActivity
import com.sbgapps.scoreit.ui.ext.replaceFragment
import com.sbgapps.scoreit.ui.view.HeaderFragment
import com.sbgapps.scoreit.ui.view.LapFragment
import com.sbgapps.scoreit.ui.viewmodel.UniversalViewModel
import kotlinx.coroutines.experimental.launch
import org.koin.android.architecture.ext.getViewModel


class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = getViewModel<UniversalViewModel>()
        launch {
            model.init()
        }.invokeOnCompletion {
            replaceFragment(R.id.headerContainer, HeaderFragment.newInstance())
            replaceFragment(R.id.lapContainer, LapFragment.newInstance())
        }
    }
}