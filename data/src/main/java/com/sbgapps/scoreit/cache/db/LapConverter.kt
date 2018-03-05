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

package com.sbgapps.scoreit.cache.db

import android.arch.persistence.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import timber.log.Timber


class LapConverter {

    private val adapter = Moshi
            .Builder()
            .build()
            .adapter<List<Int>>(Types.newParameterizedType(List::class.java, Int::class.javaObjectType))

    @TypeConverter
    fun fromString(json: String): List<Int>? {
        Timber.d("Converting json to list: " + json)
        val list = adapter.fromJson(json)
        Timber.d("Converted json is " + list)
        return list
    }

    @TypeConverter
    fun fromList(list: List<Int>?): String? {
        Timber.d("Converting list to json: " + list)
        val json = adapter.toJson(list)
        Timber.d("Converted list is " + json)
        return json
    }
}