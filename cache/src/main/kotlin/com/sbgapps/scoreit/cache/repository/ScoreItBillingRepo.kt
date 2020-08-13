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
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.sbgapps.scoreit.cache.dao.ScoreItBillingDao
import com.sbgapps.scoreit.data.repository.BillingRepo
import com.sbgapps.scoreit.data.repository.BillingRepo.Companion.BEER
import com.sbgapps.scoreit.data.repository.BillingRepo.Companion.COFFEE
import timber.log.Timber

class ScoreItBillingRepo(
    context: Context,
    private val billingDao: ScoreItBillingDao
) : BillingRepo, PurchasesUpdatedListener, BillingClientStateListener {

    private var purchaseCallback: (() -> Unit)? = null
    private var donations: List<SkuDetails>? = null

    private val playStoreBillingClient: BillingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases()
        .setListener(this)
        .build()

    init {
        connectToPlayBillingService()
    }

    private fun connectToPlayBillingService() {
        if (!playStoreBillingClient.isReady) {
            playStoreBillingClient.startConnection(this)
        }
    }

    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) acknowledgePurchase(purchase.purchaseToken)
        } else {
            Timber.e(billingResult.debugMessage)
        }
    }

    private fun acknowledgePurchase(purchaseToken: String) {
        val params = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchaseToken).build()
        playStoreBillingClient.acknowledgePurchase(params) { billingResult ->
            donations = null
            purchaseCallback?.invoke()
            Timber.d(billingResult.debugMessage)
        }
    }

    override fun onBillingServiceDisconnected() = Unit

    override fun onBillingSetupFinished(billingResult: BillingResult) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
            querySkuDetailsAsync()
        } else {
            Timber.e(billingResult.debugMessage)
        }
    }

    override fun getDonationSkus(): List<SkuDetails>? = donations

    override fun startBillingFlow(activity: Activity, skuDetails: SkuDetails, callback: () -> Unit) {
        purchaseCallback = callback
        val billingFlowParams = BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
        playStoreBillingClient.launchBillingFlow(activity, billingFlowParams)
    }

    private fun querySkuDetailsAsync() {
        val skus = billingDao.loadSkus()
        if (null == skus) {
            val params = SkuDetailsParams.newBuilder().setSkusList(DONATION_SKUS)
                .setType(BillingClient.SkuType.INAPP).build()
            playStoreBillingClient.querySkuDetailsAsync(params) { billingResult, skuDetailsList ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    billingDao.saveSkus(skuDetailsList)
                    queryPurchasesAsync(skuDetailsList)
                } else {
                    Timber.e(billingResult.debugMessage)
                }
            }
        } else {
            queryPurchasesAsync(skus)
        }
    }

    private fun queryPurchasesAsync(skus: List<SkuDetails>?) {
        val hasDonated = playStoreBillingClient.queryPurchases(BillingClient.SkuType.INAPP)
            .purchasesList?.isNotEmpty() == true
        Timber.d("User has ${if (!hasDonated) "not" else ""} donated")
        if (!hasDonated) donations = skus
    }

    companion object {
        private val DONATION_SKUS = listOf(COFFEE, BEER)
    }
}
