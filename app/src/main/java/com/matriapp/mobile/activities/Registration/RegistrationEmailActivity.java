package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.matriapp.mobile.R;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class RegistrationEmailActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private EditText et_email;
    private  String ragister_id="",fb_id="" ,fname,lname,mobile,password,country_code,dob,gender,reference_id,created_id;
    private RelativeLayout progressBar;
    private LinearLayout lay_first;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_email);

        common = new Common(this);
        session = new SessionManager(this);

        lay_first= findViewById(R.id.lay_first);
        progressBar= findViewById(R.id.progressBar);
        et_email= findViewById(R.id.et_email);

        reference_id = getIntent().getStringExtra("reference_id");
        created_id = getIntent().getStringExtra("created_id");
    }


    public void validFirst(View view) {
        String email = et_email.getText().toString().trim();

        boolean isvalid = true;
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please enter email");
            isvalid = false;
        } else {
            if (!common.isValidEmail(email)) {
                et_email.setError("Please enter valid email");
                isvalid = false;
            }
        }


        fname = getIntent().getStringExtra("firstname");
        lname = getIntent().getStringExtra("lastname");
        password = getIntent().getStringExtra("password");
        country_code = getIntent().getStringExtra("country_code");
        mobile = getIntent().getStringExtra("mobile_number");
        dob = getIntent().getStringExtra("birthdate");
        gender = getIntent().getStringExtra("gender");

        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("firstname", fname);
            param.put("lastname", lname);
            param.put("email", email);
            param.put("password", password);
            param.put("country_code", country_code);
            param.put("mobile_number", mobile);
            param.put("birthdate", changeDate(dob));
            param.put("gender", gender);
            param.put("id", ragister_id);
            param.put("fb_id", fb_id);
            param.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));

            submitRagister(AppConstants.register_first, param);
        }
    }

    public String changeDate(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "yyyy-M-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }



    private void submitRagister(String url, HashMap<String, String> param) {
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
                    ragister_id = object.getString("id");
                    HashMap<String, String> params = new HashMap<>();
                    params.put("profileby", created_id);
                    params.put("member_id", ragister_id);
                    params.put("reference", getValidId(reference_id));
                    submitData(params);
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


    private String getValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return "";
        }
        return val;
    }

    private void submitData(HashMap<String, String> param) {


        common.showProgressRelativeLayout(progressBar);

        common.makePostRequest(AppConstants.edit_profile, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    common.showToast(getString(R.string.profile_details_added),lay_first);
                   goToNextActivity();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),lay_first);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),lay_first);
                }
            }
        },lay_first);
    }

    private void goToNextActivity() {
        Intent in = new Intent(this, RegistrationMaritalStatusActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);
    }

    public  void  goBack(View view) {
        onBackPressed();
    }

}