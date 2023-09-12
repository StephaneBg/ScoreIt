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

package com.sbgapps.scoreit.cache.repository

import android.app.Activity
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.sbgapps.scoreit.data.repository.BillingRepo
import com.sbgapps.scoreit.data.repository.Donation

class ScoreItBillingRepo : BillingRepo, PurchasesUpdatedListener, BillingClientStateListener {

    override fun getDonationSkus(): List<Donation>? = null

    override fun startBillingFlow(activity: Activity, donation: Donation, callback: () -> Unit) =
        Unit

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) = Unit

    override fun onBillingServiceDisconnected() = Unit

    override fun onBillingSetupFinished(result: BillingResult) = Unit
}
