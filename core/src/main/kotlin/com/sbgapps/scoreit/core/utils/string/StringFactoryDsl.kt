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

package com.sbgapps.scoreit.core.utils.string

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes

fun String.toStringFactory(): StringFactory = Pure(this)

private fun Any.toStringFactory() = when (this) {
    is String -> toStringFactory()
    is StringFactory -> this
    is Int -> fromResources(this)
    else -> toString().toStringFactory()
}

private fun <T> Array<T>.toStringFactoryList() = mapNotNull { it?.toStringFactory() }

fun fromResources(@StringRes id: Int, vararg args: Any?): StringFactory =
    FromResources(
        id,
        args.toStringFactoryList()
    )

fun fromQuantityResources(@PluralsRes id: Int, quantity: Int, vararg args: Any?): StringFactory =
    FromQuantityResources(
        id,
        quantity,
        args.toStringFactoryList()
    )

fun concat(vararg parts: Any?): StringFactory =
    Concat(parts.toStringFactoryList())

fun join(separator: Any = "", vararg parts: Any?): StringFactory {
    val buffer = mutableListOf<StringFactory>()

    parts.toStringFactoryList().forEach {
        if (buffer.isNotEmpty()) buffer.add(separator.toStringFactory())
        buffer.add(it)
    }

    return Concat(buffer.toList())
}
