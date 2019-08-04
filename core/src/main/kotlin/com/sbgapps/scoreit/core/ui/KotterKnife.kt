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

@file:Suppress("unused")

package com.sbgapps.scoreit.core.ui

import android.app.Activity
import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import androidx.fragment.app.DialogFragment as SupportDialogFragment
import androidx.fragment.app.Fragment as SupportFragment

fun <V : View> View.bindView(id: Int)
        : ReadOnlyProperty<View, V> = required(id, viewFinder)

fun <V : View> Activity.bindView(id: Int)
        : ReadOnlyProperty<Activity, V> = required(id, viewFinder)

fun <V : View> SupportDialogFragment.bindView(id: Int)
        : ReadOnlyProperty<SupportDialogFragment, V> = required(id, viewFinder)

fun <V : View> SupportFragment.bindView(id: Int)
        : ReadOnlyProperty<SupportFragment, V> = required(id, viewFinder)

private val View.viewFinder: View.(Int) -> View?
    get() = { findViewById(it) }
private val Activity.viewFinder: Activity.(Int) -> View?
    get() = { findViewById(it) }
private val SupportDialogFragment.viewFinder: SupportDialogFragment.(Int) -> View?
    get() = { dialog!!.findViewById(it) }
private val SupportFragment.viewFinder: SupportFragment.(Int) -> View?
    get() = { view!!.findViewById(it) }

private fun viewNotFound(id: Int, desc: KProperty<*>): Nothing =
    error("View ID $id for '${desc.name}' not found.")

@Suppress("UNCHECKED_CAST")
private fun <T, V : View> required(id: Int, finder: T.(Int) -> View?) =
    Lazy { t: T, desc -> t.finder(id) as V? ?: viewNotFound(id, desc) }

private class Lazy<T, V>(private val initializer: (T, KProperty<*>) -> V) :
    ReadOnlyProperty<T, V>, LifecycleObserver {

    private object EMPTY

    private var value: Any? = EMPTY
    private var attachedToLifecycleOwner = false

    override fun getValue(thisRef: T, property: KProperty<*>): V {
        checkAddToLifecycleOwner(thisRef)
        if (value == EMPTY) {
            value = initializer(thisRef, property)
        }
        @Suppress("UNCHECKED_CAST")
        return value as V
    }

    private fun checkAddToLifecycleOwner(thisRef: T) {
        if (!attachedToLifecycleOwner && thisRef is LifecycleOwner) {
            thisRef.lifecycle.addObserver(this)
            attachedToLifecycleOwner = true
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun destroy() {
        value = EMPTY
    }
}
