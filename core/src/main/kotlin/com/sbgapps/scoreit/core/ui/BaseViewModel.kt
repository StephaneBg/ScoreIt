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

package com.sbgapps.scoreit.core.ui

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

open class BaseViewModel(defaultState: State) : ViewModel() {

    private var currentState: State

    private val innerStates: MutableStateFlow<State> = MutableStateFlow(defaultState).apply {
        buffer(0, BufferOverflow.DROP_OLDEST)
    }
    private val innerEffects: MutableSharedFlow<Effect> = MutableSharedFlow<Effect>().apply {
        buffer(0, BufferOverflow.SUSPEND)
    }

    private val states: Flow<State> by ::innerStates
    private val effects: Flow<Effect> by ::innerEffects

    private val stateWithLifeCycleLiveData: LiveData<State> =
        states.asLiveData(viewModelScope.coroutineContext)
    private val effectsWithLifeCycleLiveData: LiveData<Effect> =
        effects.asLiveData(viewModelScope.coroutineContext)

    init {
        currentState = defaultState
    }

    fun observeStates(owner: LifecycleOwner, block: (State) -> Unit) {
        stateWithLifeCycleLiveData.removeObservers(owner)
        stateWithLifeCycleLiveData.observe(owner, block)
    }

    fun observeEffects(owner: LifecycleOwner, block: (Effect) -> Unit) {
        effectsWithLifeCycleLiveData.removeObservers(owner)
        effectsWithLifeCycleLiveData.observe(owner, block)
    }

    protected fun action(
        block: suspend (State) -> Unit
    ): Job = action(
        block = block,
        onError = { Timber.e(it) }
    )

    protected fun action(
        block: suspend (State) -> Unit,
        onError: suspend (Throwable?) -> Unit
    ): Job = viewModelScope.launch {
        try {
            withContext(Dispatchers.IO) {
                block(currentState)
            }
        } catch (e: Exception) {
            onError(e)
        }
    }

    protected suspend fun setState(state: State) {
        Timber.d("Set state: $state")
        currentState = state
        innerStates.emit(state)
    }

    protected suspend fun sendEffect(effect: Effect) {
        Timber.d("Send effect: $effect")
        innerEffects.emit(effect)
    }
}

interface Event
interface State : Event
interface Effect : Event

data object Empty : State
