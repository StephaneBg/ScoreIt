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

package com.sbgapps.scoreit.core.ui

import androidx.lifecycle.ViewModel
import io.gumil.kaskade.Action
import io.gumil.kaskade.Kaskade
import io.gumil.kaskade.State
import io.gumil.kaskade.livedata.stateLiveData
import kotlinx.coroutines.CoroutineExceptionHandler

abstract class BaseViewModel<ACTION : Action, STATE : State> : ViewModel() {

    protected abstract val exceptionHandler: CoroutineExceptionHandler
    protected abstract val stateContainer: Kaskade<ACTION, STATE>
    val state by lazy { stateContainer.stateLiveData() }

    override fun onCleared() {
        super.onCleared()
        stateContainer.unsubscribe()
    }

    fun process(action: ACTION) = stateContainer.process(action)
}
