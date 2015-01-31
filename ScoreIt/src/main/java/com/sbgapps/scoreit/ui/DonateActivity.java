package com.sbgapps.scoreit.ui;

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
import com.sbgapps.scoreit.R;
import com.sbgapps.scoreit.utils.IabKey;

/**
 * Created by sbaiget on 30/01/2015.
 */
public class DonateActivity extends BaseActivity
        implements BillingProcessor.IBillingHandler, IabKey {

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

        mBillingProcessor = new BillingProcessor(this, INAPP_KEY, this);

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
