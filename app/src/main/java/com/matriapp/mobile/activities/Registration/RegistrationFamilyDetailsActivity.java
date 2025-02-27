package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegistrationFamilyDetailsActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar,lay_first;
    private ProgressBar pbState;
    private String ragister_id;
    private EditText et_father_name,et_father_ocu,et_mother_name,et_mother_ocu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_family_details);
        common = new Common(this);
        session = new SessionManager(this);
        ragister_id = getIntent().getStringExtra("ragister_id");
        et_mother_ocu=findViewById(R.id.et_mother_ocu);
        et_father_ocu=findViewById(R.id.et_father_ocu);
        et_mother_name=findViewById(R.id.et_mother_name);
        et_father_name=findViewById(R.id.et_father_name);
        lay_first=findViewById(R.id.lay_first);
    }

    public void validFirst(View view) {

        boolean isValid = true;
        String father_name = et_father_name.getText().toString().trim();
        String father_occupation = et_father_ocu.getText().toString().trim();
        String mother_name = et_mother_name.getText().toString().trim();
        String mother_occupation = et_mother_ocu.getText().toString().trim();

        if (TextUtils.isEmpty(father_name)) {
            et_father_name.setError("Please enter father name");
            isValid = false;
        }
//        else if (TextUtils.isEmpty(father_occupation)) {
//            et_father_ocu.setError("Please enter father's occupation");
//            isValid = false;
//        }
        else if (TextUtils.isEmpty(mother_name)) {
            et_mother_name.setError("Please enter mother name");
            isValid = false;
        }
//        else if (TextUtils.isEmpty(mother_occupation)) {
//            et_mother_ocu.setError("Please enter mother's occupation");
//            isValid = false;
//        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("father_name", father_name);
            param.put("father_occupation", father_occupation);
            param.put("mother_name", mother_name);
            param.put("mother_occupation", mother_occupation);
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
        Intent in = new Intent(this, RegistrationNoOFBrothersActivity.class);
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