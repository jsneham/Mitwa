package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.matriapp.mobile.R;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationLastActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar,lay_first;
    private ProgressBar pbState;
    private String ragister_id,country_code;
    private EditText et_phone,et_time_call;
    private CountryCodePicker spin_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_last);
        common = new Common(this);
        session = new SessionManager(this);
        ragister_id = getIntent().getStringExtra("ragister_id");

        lay_first = findViewById(R.id.lay_first);
        et_phone = findViewById(R.id.et_phone);
        et_time_call = findViewById(R.id.et_time_call);

        spin_code = findViewById(R.id.spin_code);
        spin_code.setOnCountryChangeListener(country -> {
            country_code = country.getPhoneCode();
        });
    }

    public void validFirst(View view) {
        boolean isvalid = true;

        String phone = et_phone.getText().toString().trim();
        String time_to_call = et_time_call.getText().toString().trim();

        if (phone.length() > 0 && phone.length() < 8) {
            et_phone.setError("Please enter phone");
            isvalid = false;
        }
//        if (TextUtils.isEmpty(time_to_call)) {
//            et_time_call.setError("Please enter matchmaker's contact number");
//            isvalid = false;
//        }
        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("phone", phone);
//            param.put("time_to_call", time_to_call);
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
        finish();
        Intent in = new Intent(this, RegisterWelcomeActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);

    }
    public  void  goBack(View view) {
        onBackPressed();
    }
}