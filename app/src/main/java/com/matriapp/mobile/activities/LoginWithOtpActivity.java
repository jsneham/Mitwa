package com.matriapp.mobile.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.tasks.Task;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.Registration.RegistrationReferredByActivity;
import com.matriapp.mobile.broadcastreciever.MySMSBroadcastReceiver;
import com.matriapp.mobile.custom.TimeCompletedListner;
import com.matriapp.mobile.custom.TimerTextView;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
import com.matriapp.mobile.countrycodepicker.CountryCodePicker;
import com.matriapp.mobile.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


public class LoginWithOtpActivity extends AppCompatActivity implements MySMSBroadcastReceiver.OTPReceiveListener, View.OnClickListener, TimeCompletedListner {
    private String LOGIN_TAG = "login";
    private Button btnGenerateOtp, btnVerify;
    private EditText txtMobileNumber;
    private AVLoadingIndicatorView progressBar;
    private Common common;
    private SessionManager session;
    private String country_code = "+91";
    private CountryCodePicker spin_code;
    private LinearLayout layoutFirst, layoutSecond;
    private TextView lblVerifyNumber;
    private OtpView otpView;
    private RelativeLayout lay_first;

    private TimerTextView timerTextView;
    private TextView btnResend;

    private String sendOtp;
    private Toolbar toolbar;

    private MySMSBroadcastReceiver smsReceiver = null;

    private String latitude = "";
    private String longitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_login_with_otp);

        lay_first = findViewById(R.id.lay_first);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login with OTP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> {
            onBackPressedd();
        });

        common = new Common(this);
        session = new SessionManager(this);

        toolbar.setTitle("Verify OTP");

        initData();

        progressBar = findViewById(R.id.progressBar);
    }

    private void initData() {
        startSMSListener();

        timerTextView = findViewById(R.id.timerTextView);
        btnResend = findViewById(R.id.btnResend);
        layoutFirst = findViewById(R.id.layoutFirst);
        layoutSecond = findViewById(R.id.layoutSecond);
        txtMobileNumber = findViewById(R.id.txtMobileNumber);
        spin_code = findViewById(R.id.spin_code);
        btnGenerateOtp = findViewById(R.id.btnGenerateOtp);
        btnVerify = findViewById(R.id.btnVerify);
        lblVerifyNumber = findViewById(R.id.lblVerifyNumber);
        otpView = findViewById(R.id.otpView);

        spin_code.setOnCountryChangeListener(country -> {
            country_code = country.getPhoneCode();
        });

        latitude = getIntent().getStringExtra("lat");
        longitude = getIntent().getStringExtra("log");

        btnGenerateOtp.setOnClickListener(this);
        btnResend.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override public void onOtpCompleted(String otp) {
                otpView.setText(otp);
                timeFinished();
                AppDebugLog.print("otp : " + otp);
                AppDebugLog.print("otpStr : " + sendOtp);
                if (sendOtp.equals(otp)) {
                    sendVerifyOtpRquest();
                }else{
                    common.showToast("Please enter valid otp",lay_first);
                }
            }
        });

        layoutFirst.setVisibility(View.VISIBLE);
        layoutSecond.setVisibility(View.GONE);
    }

    private void setTimerInTextView() {
        btnResend.setVisibility(View.GONE);
        timerTextView.setVisibility(View.VISIBLE);
        long futureTimestamp = System.currentTimeMillis() + (60 * 1000);
        timerTextView.setEndTime(futureTimestamp);
        timerTextView.setListener(this);
    }

    @Override
    public void timeFinished() {
        btnResend.setVisibility(View.VISIBLE);
        timerTextView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnGenerateOtp) {
            validData();
        } else if (id == R.id.btnResend) {
            sendOtpRquest();
        } else if (id == R.id.btnVerify) {
            if (!Common.isTextViewEmpty(otpView.getText()) && sendOtp.equalsIgnoreCase(otpView.getText().toString())) {
                sendVerifyOtpRquest();
            } else {
                common.showToast("You have entered wrong otp",lay_first);
            }
        }
    }

    private void validData() {
        String mobileNumber = txtMobileNumber.getText().toString().trim();

        boolean isvalid = true;
        if (TextUtils.isEmpty(mobileNumber)) {
            txtMobileNumber.setError("Please enter mobile number");
            isvalid = false;
        } else {
            if (mobileNumber.length() < 10) {
                txtMobileNumber.setError("Please enter valid mobile number");
                isvalid = false;
            }
        }

        if (isvalid) {
            sendOtpRquest();
        }
    }

    private void sendOtpRquest() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast( "Please check your internet connection!", lay_first);
            return;
        }

        Common.hideSoftKeyboard(this);
        common.showProgressLayout(progressBar);

        HashMap<String, String> params = new HashMap<>();
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
        params.put("user_agent", AppConstants.USER_AGENT);
        params.put("csrf_new_matrimonial", "70c0313fa86f9b4e960d8bfe24edd7a7");
        params.put("country_code", country_code);
        params.put("mobile_number", txtMobileNumber.getText().toString());

        common.makePostRequestWithTag(AppConstants.generate_otp, params, response -> {
            AppDebugLog.print("login with otp response : " + response);
            common.hideProgressLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (object.has("status") && object.getString("status").equals("success")) {
                    common.showToast(object.getString("errmessage"), lay_first);
                    toolbar.setTitle("Verify OTP");
                    otpView.requestFocus();
                    sendOtp = object.getString("otp_varify");
                    lblVerifyNumber.setText(String.format(getString(R.string.lbl_verify_otp_dialog), country_code + "-" + txtMobileNumber.getText().toString()));

                    layoutFirst.setVisibility(View.GONE);
                    layoutSecond.setVisibility(View.VISIBLE);
                    setTimerInTextView();
                } else {
                    if (object.has("errmessage")) {
                        common.showToast(object.getString("errmessage"), lay_first);
                    }
                    if (!object.has("errmessage") && object.has("error_meessage")) {
                        common.showToast(object.getString("error_meessage"), lay_first);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),lay_first);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.hideProgressLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),lay_first);
            }
        }, LOGIN_TAG,lay_first);

    }

    private void sendVerifyOtpRquest() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast( "Please check your internet connection!", lay_first);
            return;
        }

        Common.hideSoftKeyboard(this);
        common.showProgressLayout(progressBar);

        HashMap<String, String> params = new HashMap<>();
        params.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
        params.put("user_agent", AppConstants.USER_AGENT);
        params.put("csrf_new_matrimonial", "70c0313fa86f9b4e960d8bfe24edd7a7");
        params.put("otp_varify", sendOtp);
        params.put("otp_mobile", otpView.getText().toString());
        params.put("mobile_number", country_code + "-" + txtMobileNumber.getText().toString());
        if(latitude !=null && longitude !=null) {
            params.put("latitude", latitude);
            params.put("longitude", longitude);
        }else{
            params.put("latitude", "");
            params.put("longitude", "");
        }
        common.makePostRequestWithTag(AppConstants.verify_otp, params, response -> {
            AppDebugLog.print("sendVerifyOtpRquest response : " + response);
            common.hideProgressLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("status").equals("success")) {
                    common.showToast(object.getString("errmessage"), lay_first);

                    JSONObject user_data = object.getJSONObject("user_data");
                    session.createLoginSession(user_data.getString("email"), user_data.getString("password"), user_data.getString("id"));
                    session.createLoginSession(user_data.getString("id"));
                    session.setUserData(SessionManager.KEY_EMAIL, user_data.getString("email"));
                    session.setUserData(SessionManager.KEY_username, user_data.getString("username"));
                    session.setUserData(SessionManager.KEY_GENDER, user_data.getString("gender"));
                    session.setUserData(SessionManager.KEY_MATRI_ID, user_data.getString("matri_id"));
                    session.setUserData(SessionManager.KEY_PLAN_STATUS, user_data.getString("plan_status"));
                    session.setUserData(SessionManager.LOGIN_WITH, "local");

                    Intent i = new Intent(this, DashboardActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                }
                if (object.getString("status").equals("error") && object.getString("errmessage").equalsIgnoreCase("register")) {
                    common.showToast("Your mobile number not registered!", lay_first);
                    Intent intent = new Intent(this, RegistrationReferredByActivity.class);
                    intent.putExtra("country_code", spin_code.getSelectedCountryCodeAsInt());
                    intent.putExtra("mobile_number", txtMobileNumber.getText().toString());
                    startActivity(intent);
                    finish();
                }
                if (object.getString("status").equals("error") && object.has("error_meessage")) {
                    common.showToast(object.getString("error_meessage"), lay_first);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),lay_first);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.hideProgressLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),lay_first);
            }
        }, LOGIN_TAG, lay_first);

    }

    @Override
    public void onBackPressed() {
        onBackPressedd();
    }

    private void onBackPressedd() {
        common.hideProgressLayout(progressBar);
        if (layoutSecond.getVisibility() == View.VISIBLE) {
            toolbar.setTitle("Login with OTP");
            layoutFirst.setVisibility(View.VISIBLE);
            layoutSecond.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Starts SmsRetriever, which waits for ONE matching SMS message until timeout
     * (1 minutes). The matching SMS message will be sent via a Broadcast Intent with
     * action SmsRetriever#SMS_RETRIEVED_ACTION.
     */
    private void startSMSListener() {
        try {
            smsReceiver = new MySMSBroadcastReceiver();
            smsReceiver.initOTPListener(this);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);

            this.registerReceiver(smsReceiver, intentFilter);
            SmsRetrieverClient client = SmsRetriever.getClient(this);
            Task task = client.startSmsRetriever();
            task.addOnSuccessListener(o -> {

            });
            task.addOnFailureListener(e -> {

            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override public void onOTPReceived(@NotNull String otp) {
        otpView.setText(otp);
        timeFinished();
        AppDebugLog.print("otp : " + otp);
        AppDebugLog.print("otpStr : " + sendOtp);
        if (sendOtp.equals(otp)) {
            sendVerifyOtpRquest();
        }
    }

    @Override public void onOTPTimeOut() {

    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            if(smsReceiver != null && smsReceiver.isOrderedBroadcast()){
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            AppDebugLog.print("Exception in unregisterReceiver : " + e.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (smsReceiver != null && smsReceiver.isOrderedBroadcast()) {
                unregisterReceiver(smsReceiver);
            }
        } catch (Exception e) {
            AppDebugLog.print("Exception in unregisterReceiver : " + e.getMessage());
        }
    }
}
