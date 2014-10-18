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

package com.sbgapps.scoreit;

import android.support.v4.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.sbgapps.scoreit.util.IabKey;

/**
 * Created by sbaiget on 03/03/14.
 */
public class InfoFragment extends Fragment
        implements BillingProcessor.IBillingHandler, IabKey {

    static final String LOG_TAG = "billing";

    private static final String VERSION_UNAVAILABLE = "N/A";
    private static final String PRODUCT_DONATE_COFFEE = "com.sbgapps.scoreit.coffee";
    private static final String PRODUCT_DONATE_BEER = "com.sbgapps.scoreit.beer";
    private BillingProcessor mBillingProcessor;
    private boolean mReadyToPurchase = false;
    private Button mCoffeeBtn;
    private Button mBeerBtn;

    public BillingProcessor getBillingProcessor() {
        return mBillingProcessor;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBillingProcessor = new BillingProcessor(getActivity(), INAPP_KEY, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        PackageManager pm = getActivity().getPackageManager();
        String packageName = getActivity().getPackageName();
        String version;
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            version = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            version = VERSION_UNAVAILABLE;
        }
        TextView nameAndVersionView = (TextView) view.findViewById(R.id.version);
        nameAndVersionView.setText(version);

        Button btn;

        btn = (Button) view.findViewById(R.id.btn_contact);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"stephane@baiget.fr"});
                intent.putExtra(Intent.EXTRA_SUBJECT, "Score It");
                try {
                    startActivity(intent);
                } catch (ActivityNotFoundException e) {
                }
            }
        });

        btn = (Button) view.findViewById(R.id.btn_rate);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(goToMarket);
                } catch (ActivityNotFoundException e) {
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id="
                                    + getActivity().getPackageName())
                    ));
                }
            }
        });

        btn = (Button) view.findViewById(R.id.btn_share);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,
                        "http://play.google.com/store/apps/details?id="
                                + getActivity().getPackageName()
                );
                startActivity(Intent.createChooser(intent, getString(R.string.share)));
            }
        });

        btn = (Button) view.findViewById(R.id.btn_community);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://plus.google.com/u/0/communities/111590957716888215587"));
                startActivity(intent);
            }
        });

        mCoffeeBtn = (Button) view.findViewById(R.id.btn_donate_coffee);
        mCoffeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadyToPurchase) mBillingProcessor.purchase(PRODUCT_DONATE_COFFEE);
            }
        });

        mBeerBtn = (Button) view.findViewById(R.id.btn_donate_beer);
        mBeerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mReadyToPurchase) mBillingProcessor.purchase(PRODUCT_DONATE_BEER);
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        if (null != mBillingProcessor) mBillingProcessor.release();
        super.onDestroy();
    }

    @Override
    public void onProductPurchased(String sku) {
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
