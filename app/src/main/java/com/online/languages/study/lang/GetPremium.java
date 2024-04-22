package com.online.languages.study.lang;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.presentation.AppStart;
import com.online.languages.study.lang.presentation.BaseActivity;
import com.online.languages.study.lang.util.IabHelper;
import com.online.languages.study.lang.util.IabResult;
import com.online.languages.study.lang.util.Inventory;
import com.online.languages.study.lang.util.Purchase;
import com.online.languages.study.lang.util.SkuDetails;

import java.util.Arrays;

public class GetPremium extends BaseActivity {

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    LinearLayout getPremiumMsg;
    LinearLayout thanksMsg;

    Button purchaseButton;


    IabHelper mHelper;

    OpenActivity openActivity;


    boolean appRestart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, false);
        themeAdapter.getTheme();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_premium);

        appRestart = false;



        String base64EncodedPublicKey;

        base64EncodedPublicKey = getString(R.string.encoded_key);

        mHelper = new IabHelper(this, base64EncodedPublicKey);


        openActivity = new OpenActivity(this);
        openActivity.setOrientation();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_toolbar);


        setTitle("");


        getPremiumMsg = findViewById(R.id.getPremiumMsg);
        thanksMsg = findViewById(R.id.thanksMsg);
        purchaseButton = findViewById(R.id.purchaseButton);

        View fullVersionDesc = findViewById(R.id.fullversionMsg);
        boolean fullVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);


        if (fullVersion) {
            getPremiumMsg.setVisibility(View.GONE);
            thanksMsg.setVisibility(View.GONE);
            fullVersionDesc.setVisibility(View.VISIBLE);
        } else {
            fullVersionDesc.setVisibility(View.GONE);
        }



        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    showFail("Problem setting up In-app Billing: " + result);
                    // Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    // purchaseButton.setEnabled(false);
                }

                if (mHelper == null) {
                    return;
                }

                String[] moreSkus = {MainActivity.SKU_PREMIUM};
                if(mHelper.isSetupDone() && !mHelper.isAsyncInProgress()) {

                    mHelper.queryInventoryAsync(true, Arrays.asList(moreSkus), mGotInventoryListener);

                }
            }
        });

    }


    IabHelper.QueryInventoryFinishedListener mGotInventoryListener
            = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {

            if (result.isFailure()) {
                showRes("Inventory failure");
            }
            else {
                showRes("Inventory received");

                SkuDetails productDetails = inventory.getSkuDetails(MainActivity.SKU_PREMIUM);

                if (productDetails != null){
                    purchaseButton.setText(String.format(getString(R.string.get_premium_buy_price), productDetails.getPrice()));

                    showRes("Price: " + productDetails.getPrice());
                }else{
                    showRes("No Product Detail" );
                }

            }
        }
    };


    public void purchase(View view) {

        if  (Constants.DEBUG) {
            changeVersion(true);
            changeShowAd(false);
            updateAfterPurchase();

        } else {

            if (mHelper != null) mHelper.flagEndAsync();
            {
                assert mHelper != null;

                mHelper.launchPurchaseFlow(this, MainActivity.SKU_PREMIUM, 10001,
                        mPurchaseFinishedListener, "");

            }

        }

    }


    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase)
        {
            if (result.isFailure()) {
                showRes("Error purchasing: " + result);
                return;
            }
            else if (purchase.getSku().equals(MainActivity.SKU_PREMIUM)) {
                showRes("SKU recognized");
            }

            showRes("UI updated");
            updateAfterPurchase();
        }
    };

    public void  continueAfterBuy(View v) {
        finish();
    }


    private void appRestart() {

        Intent i = new Intent(this, AppStart.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(i);
    }


    @Override
    public void finish() {

        if (appRestart) {
            appRestart();
        }

        super.finish();

    }


    private void updateAfterPurchase() {
        showRes("Thank you!");
        getPremiumMsg.setVisibility(View.GONE);
        thanksMsg.setVisibility(View.VISIBLE);
        changeVersion(true);

        appRestart = true;

        Intent returnIntent = new Intent();
        setResult(RESULT_OK,returnIntent);


    }

    public void showRes(String msg) {
        //  Toast.makeText(this, msg, Toast.LENGTH_SHORT ).show();
    }

    public void showFail(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT ).show();
        if (!Constants.DEBUG) purchaseButton.setEnabled(false);
    }


    private void changeVersion(Boolean full_version) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_VERSION_TXT, full_version);
        editor.apply();
    }

    private void changeShowAd(Boolean show_ad) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_SHOW_AD, show_ad);
        editor.apply();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
            showRes("UI developer update.");
        } else {
            showRes("onActivityResult handled by IABUtil.");
        }
    }




}
