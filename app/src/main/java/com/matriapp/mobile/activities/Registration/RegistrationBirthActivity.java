package com.matriapp.mobile.activities.Registration;

import android.app.TimePickerDialog;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class RegistrationBirthActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar,lay_first;
    private ProgressBar pbState;
    private String ragister_id;
    private EditText et_birth_place,et_birth_time,et_address,et_gothra;
    private final Calendar mcurrentTime = Calendar.getInstance();
    private SimpleDateFormat mFormat = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_birth);
        common = new Common(this);
        session = new SessionManager(this);
        ragister_id = getIntent().getStringExtra("ragister_id");

        lay_first = findViewById(R.id.lay_first);
        et_birth_place = findViewById(R.id.et_birth_place);
        et_birth_time = findViewById(R.id.et_birth_time);
        et_address = findViewById(R.id.et_address);
        et_gothra = findViewById(R.id.et_gothra);

        et_birth_time.setOnClickListener(v -> {
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(RegistrationBirthActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                mcurrentTime.set(Calendar.HOUR, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);

                if (mFormat == null)
                    mFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                et_birth_time.setText(Common.get12HrTime(selectedHour, selectedMinute));
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });
    }

    public void validFirst(View view) {
        boolean isValid = true;
        String location = et_birth_place.getText().toString().trim();
        String time = et_birth_time.getText().toString().trim();
        String gothra = et_gothra.getText().toString().trim();
        String add = et_address.getText().toString().trim();

//         if (TextUtils.isEmpty(time)) {
//             et_birth_time.setError("Please enter birth time");
//            isValid = false;
//        }
//        if (TextUtils.isEmpty(location)) {
//            et_birth_place.setError("Please enter birth place");
//            isValid = false;
//        }
//        if (TextUtils.isEmpty(add)) {
//            et_address.setError("Please enter Ancestral Origin (Native Place)");
//            isValid = false;
//        }
//        if (TextUtils.isEmpty(gothra)) {
//            et_gothra.setError("Please enter gothra");
//            isValid = false;
//        }
        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("gothra", gothra);
            param.put("birthplace", location);
            param.put("birthtime", time);
            param.put("address", add);
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
        Intent in = new Intent(this, RegistrationResidentActivity.class);
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