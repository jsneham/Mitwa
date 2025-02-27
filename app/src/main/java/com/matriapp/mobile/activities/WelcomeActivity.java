package com.matriapp.mobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.Registration.RegistrationReferredByActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.retrofit.ApiRequestResponse;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class WelcomeActivity extends AppCompatActivity {

    private TextView btnLogin,btnRegister,btnWeddingVendor,btnContactUs,btnAboutUs,btnPrivacyPolicy;
    boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout llProfileCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        getList();
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnWeddingVendor = findViewById(R.id.btnWeddingVendor);
        btnContactUs = findViewById(R.id.btnContactUs);
        btnAboutUs = findViewById(R.id.btnAboutUs);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
        llProfileCreate = findViewById(R.id.llProfileCreate);


        btnLogin.setOnClickListener(v-> startActivity(new Intent(this,LoginActivity.class)));
        btnRegister.setOnClickListener(v-> startActivity(new Intent(this, RegistrationReferredByActivity.class)));
        btnWeddingVendor.setOnClickListener(v-> startActivity(new Intent(this,FirstVendorCategoryListActivity.class)));
        btnContactUs.setOnClickListener(v-> startActivity(new Intent(this,ContactUsActivity.class)));
        btnAboutUs.setOnClickListener(v-> {
            Intent intent = new Intent(this, AllCmsActivity.class);
            intent.putExtra(AppConstants.KEY_INTENT, "about");
            startActivity(intent);
        });
        btnPrivacyPolicy.setOnClickListener(v-> {
            Intent intent1 = new Intent(this, AllCmsActivity.class);
            intent1.putExtra(AppConstants.KEY_INTENT, "privacy");
            startActivity(intent1);
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
            startActivity(intent);
            finish();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Common.showToast("Please click BACK again to exit", llProfileCreate);

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }


    private void getList() {
        Common  common = new Common(this);
        SessionManager session = new SessionManager(this);
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",llProfileCreate);
            return;
        }

        common.makePostRequest(AppConstants.common_list, new HashMap<String, String>(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                MyApplication.setSpinData(object);
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llProfileCreate);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            if(error.networkResponse!=null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);
    }
}