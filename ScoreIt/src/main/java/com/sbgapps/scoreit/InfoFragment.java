package com.sbgapps.scoreit;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.IBillingHandler;

/**
 * Created by sbaiget on 03/03/14.
 */
public class InfoFragment extends Fragment
        implements IBillingHandler, IabKey {

    private static final String VERSION_UNAVAILABLE = "N/A";
    private static final String PRODUCT_DONATE_COFFEE = "com.sbgapps.scoreit.coffee";
    private static final String PRODUCT_DONATE_BEER = "com.sbgapps.scoreit.beer";
    private BillingProcessor mBillingProcessor;
    private boolean mReadyToPurchase = false;
    private Button mCoffeeBtn;
    private Button mBeerBtn;

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
                startActivity(intent);
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

        mCoffeeBtn = (Button) view.findViewById(R.id.btn_donate_coffee);
        mCoffeeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mReadyToPurchase) return;
                mBillingProcessor.purchase(PRODUCT_DONATE_COFFEE);
            }
        });

        mBeerBtn = (Button) view.findViewById(R.id.btn_donate_beer);
        mBeerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mReadyToPurchase) return;
                mBillingProcessor.purchase(PRODUCT_DONATE_BEER);
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mBillingProcessor = ((AboutActivity) getActivity()).getBillingProcessor();
        mBillingProcessor.verifyPurchasesWithLicenseKey(INAPP_KEY);
        mBillingProcessor.setBillingHandler(this);
    }

    @Override
    public void onProductPurchased(String sku) {
        manageDonations(sku);
    }

    @Override
    public void onPurchaseHistoryRestored() {
        for (String sku : mBillingProcessor.listOwnedProducts()) {
            manageDonations(sku);
        }
    }

    private void manageDonations(String sku) {
        switch (sku) {
            default:
                break;

            case PRODUCT_DONATE_COFFEE:
                mCoffeeBtn.setText(getString(R.string.bought_coffee));
                mCoffeeBtn.setClickable(false);
                break;

            case PRODUCT_DONATE_BEER:
                mBeerBtn.setText(getString(R.string.bought_beer));
                mBeerBtn.setClickable(false);
                break;
        }
    }

    @Override
    public void onBillingError(int i, Throwable throwable) {
    }

    @Override
    public void onBillingInitialized() {
        mReadyToPurchase = true;
    }
}
