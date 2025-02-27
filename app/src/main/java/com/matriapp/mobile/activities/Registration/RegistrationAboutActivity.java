package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.matriapp.mobile.R;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationAboutActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private LinearLayout lay_first;
    private RelativeLayout progressBar;
    private ProgressBar pbState;
    private String ragister_id;
    private EditText et_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_about);
        common = new Common(this);
        session = new SessionManager(this);
        ragister_id = getIntent().getStringExtra("ragister_id");
        et_about= findViewById(R.id.et_about);
        lay_first= findViewById(R.id.lay_first);
    }

    public void validFirst(View view) {

        boolean isvalid = true;
        String profile_text = et_about.getText().toString().trim();
        if (TextUtils.isEmpty(profile_text)) {
            et_about.setError("Please enter about yourself");
            isvalid = false;
        }
        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("profile_text", profile_text);
            param.put("id", ragister_id);
            submitRagister(AppConstants.register_step, "", param);
        }
    }
    private void submitRagister(String url, final String tag, HashMap<String, String> param) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",lay_first);
            return;
        }

        Common.hideSoftKeyboard(this);

        common.showProgressRelativeLayout(progressBar);

        common.makePostRequest(url, param, response -> {
            Log.d("resp", response);
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    goToNextActivity();
                } else {
                    common.showToast(object.getString("errmessage"),lay_first);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),lay_first);
            }
        },lay_first);
    }

    private void goToNextActivity() {
        Intent in = new Intent(this, RegistrationHobbyActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);
    }

    public  void  goBack(View view) {
        onBackPressed();
    }
    public  void  onSkipClick(View view) {
        goToNextActivity();
    }
}