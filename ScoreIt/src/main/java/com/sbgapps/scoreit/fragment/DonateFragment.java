/*
 * Copyright (c) 2014 SBG Apps
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

package com.sbgapps.scoreit.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.util.IabKey;

/**
 * Created by Stéphane on 15/11/2014.
 */
public class DonateFragment extends Fragment
        implements BillingProcessor.IBillingHandler, IabKey {
    static final String LOG_TAG = "billing";

    private static final String PRODUCT_DONATE_COFFEE = "com.sbgapps.scoreit.coffee";
    private static final String PRODUCT_DONATE_BEER = "com.sbgapps.scoreit.beer";
    private BillingProcessor mBillingProcessor;
    private boolean mReadyToPurchase = false;
    private Button mCoffeeBtn;
    private Button mBeerBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_donate, container, false);

        mCoffeeBtn = (Button) view.findViewById(R.id.btn_donate_coffee);
        mCoffeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadyToPurchase) mBillingProcessor.purchase(getActivity(), PRODUCT_DONATE_COFFEE);
            }
        });

        mBeerBtn = (Button) view.findViewById(R.id.btn_donate_beer);
        mBeerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadyToPurchase) mBillingProcessor.purchase(getActivity(), PRODUCT_DONATE_BEER);
            }
        });

        return view;
    }

    public BillingProcessor getBillingProcessor() {
        return mBillingProcessor;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillingProcessor = new BillingProcessor(getActivity(), INAPP_KEY, this);
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
            mCoffeeBtn.setText(getString(R.string.bought_coffee));
            mCoffeeBtn.setClickable(false);
        }
        if (mBillingProcessor.isPurchased(PRODUCT_DONATE_BEER)) {
            mBeerBtn.setText(getString(R.string.bought_beer));
            mBeerBtn.setClickable(false);
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
}
