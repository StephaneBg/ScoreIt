/*
 * Copyright (c) 2016 SBG Apps
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sbgapps.scoreit;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

/**
 * Created by sbaiget on 30/01/2015.
 */
public class DonateActivity extends BaseActivity
        implements BillingProcessor.IBillingHandler {

    static final String LOG_TAG = "billing";

    private static final String PRODUCT_DONATE_COFFEE = "com.sbgapps.scoreit.coffee";
    private static final String PRODUCT_DONATE_BEER = "com.sbgapps.scoreit.beer";
    private BillingProcessor mBillingProcessor;
    private boolean mReadyToPurchase = false;
    private Button mCoffeeBtn;
    private Button mBeerBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCoffeeBtn = (Button) findViewById(R.id.btn_donate_coffee);
        mBeerBtn = (Button) findViewById(R.id.btn_donate_beer);

        mBillingProcessor = new BillingProcessor(this, BuildConfig.INAPP_KEY, this);

        setupWindow();
    }

    public void onDonateCoffee(View view) {
        if (mReadyToPurchase)
            mBillingProcessor.purchase(DonateActivity.this, PRODUCT_DONATE_COFFEE);
    }

    public void onDonateBeer(View view) {
        if (mReadyToPurchase)
            mBillingProcessor.purchase(DonateActivity.this, PRODUCT_DONATE_BEER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mBillingProcessor.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_donate;
    }

    @Override
    public void onDestroy() {
        if (null != mBillingProcessor) mBillingProcessor.release();
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String sku, TransactionDetails transactionDetails) {
        manageDonations();
    }

    @Override
    public void onPurchaseHistoryRestored() {
        Log.i(LOG_TAG, "onPurchaseHistoryRestored");
        for (String sku : mBillingProcessor.listOwnedProducts()) {
            Log.d(LOG_TAG, "Owned Managed Product: " + sku);
            manageDonations();
        }
    }

    private void manageDonations() {
        if (mBillingProcessor.isPurchased(PRODUCT_DONATE_COFFEE)) {
            mCoffeeBtn.setText(getString(R.string.button_donate_coffee_donated));
            mCoffeeBtn.setEnabled(false);
        }
        if (mBillingProcessor.isPurchased(PRODUCT_DONATE_BEER)) {
            mBeerBtn.setText(getString(R.string.button_donate_beer_donated));
            mBeerBtn.setEnabled(false);
        }
    }

    @Override
    public void onBillingError(int errorCode, Throwable throwable) {
        Log.e(LOG_TAG, "onBillingError: " + Integer.toString(errorCode));
    }

    @Override
    public void onBillingInitialized() {
        mReadyToPurchase = true;
        manageDonations();
    }

    private void setupWindow() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = Math.min(
                getResources().getDimensionPixelSize(R.dimen.dialog_width),
                dm.widthPixels * 3 / 4);
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.alpha = 1.0f;
        params.dimAmount = 0.5f;
        getWindow().setAttributes(params);
    }
}
