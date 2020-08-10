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

package com.sbgapps.scoreit.cache.dao

import android.content.SharedPreferences
import androidx.core.content.edit
import com.android.billingclient.api.SkuDetails

class ScoreItBillingDao(private val preferences: SharedPreferences) {

    fun loadSkus(): List<SkuDetails>? =
        preferences.getStringSet(KEY_SKUS, null)?.map { SkuDetails(it) }?.toList()

    fun saveSkus(skus: List<SkuDetails>?) {
        preferences.edit {
            putStringSet(KEY_SKUS, skus?.map { it.originalJson }?.toSet())
        }
    }

    companion object {
        private const val KEY_SKUS = "KEY_SKUS"
    }
}
