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

package com.sbgapps.scoreit.data.repository

import android.app.Activity
import com.android.billingclient.api.SkuDetails

interface BillingRepo {
    fun getDonationSkus(): List<SkuDetails>?

    fun startBillingFlow(activity: Activity, skuDetails: SkuDetails, callback: () -> Unit)

    companion object {
        const val COFFEE = "com.sbgapps.scoreit.coffee"
        const val BEER = "com.sbgapps.scoreit.beer"
    }
}

fun List<SkuDetails>.getBeerSku(): SkuDetails? = firstOrNull { it.sku == BillingRepo.BEER }
fun List<SkuDetails>.getCoffeeSku(): SkuDetails? = firstOrNull { it.sku == BillingRepo.COFFEE }
