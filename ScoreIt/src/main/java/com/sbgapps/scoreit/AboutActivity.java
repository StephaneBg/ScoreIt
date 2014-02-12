package com.sbgapps.scoreit;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.analytics.tracking.android.EasyTracker;
import com.sbgapps.lib.billing.IabHelper;
import com.sbgapps.lib.billing.IabKey;
import com.sbgapps.lib.billing.IabResult;
import com.sbgapps.lib.billing.Inventory;
import com.sbgapps.lib.billing.Purchase;

/**
 * Created by sbaiget on 10/02/14.
 */
public class AboutActivity extends BaseActivity
        implements IabKey {

    static final boolean DEBUG = true;
    static final String TAG = "AboutActivity";
    static final String SKU_PREMIUM = "premium";
    static final int RC_REQUEST = 10001;
    boolean mIsPremium = false;
    IabHelper mHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about);

        debug("Creating IAB helper.");
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                debug("Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    debug("Problem setting up in-app billing: " + result);
                    // TODO: catch service billing unavailable
                    return;
                }

                // Hooray, IAB is fully set up. Now, let's get an inventory of stuff we own.
                debug("Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }

    public void onDonate(View view) {
        if (!mHelper.subscriptionsSupported()) {
            debug("Subscriptions not supported!");
            return;
        }
        debug("Launching purchase flow for premium.");
        mHelper.launchPurchaseFlow(this,
                SKU_PREMIUM, IabHelper.ITEM_TYPE_INAPP,
                RC_REQUEST, mPurchaseFinishedListener, "");
    }

    @Override
    public void onStart() {
        super.onStart();
        EasyTracker.getInstance(this).activityStart(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        debug("onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            debug("onActivityResult handled by IABUtil.");
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            debug("Query inventory finished.");
            if (result.isFailure()) {
                debug("Failed to query inventory: " + result);
                return;
            }
            debug("Query inventory was successful.");
            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            mIsPremium = (premiumPurchase != null);
            updateUi();
            debug("User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
            debug("Initial inventory query finished; enabling main UI.");
        }
    };

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            debug("Purchase finished: " + result + ", purchase: " + purchase);
            if (result.isFailure()) {
                debug("Error purchasing: " + result);
                return;
            }
            debug("Purchase successful.");
            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // Bought the premium upgrade!
                debug("Purchase is premium upgrade. Congratulating user.");
                mIsPremium = true;
                updateUi();
            }
        }
    };

    public void updateUi() {
        //if (mIsPremium);
    }

    private void debug(String msg) {
        if (DEBUG) Log.d(TAG, msg);
    }
}
