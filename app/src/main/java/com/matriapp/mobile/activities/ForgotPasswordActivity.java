package com.matriapp.mobile.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.android.material.textfield.TextInputEditText;
import com.matriapp.mobile.activities.Registration.RegistrationMainActivity;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText et_email, et_new_password, et_con_password, et_id, et_old_password;
    private Button btn_reset;
    private static EditText et_dob;
    private Common common;
    private SessionManager session;
    private RelativeLayout loader,container;
    private int age = 0;
    private Button btn_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        common = new Common(this);
        common.setGradient(getWindow());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Forgot Password");
        toolbar.setNavigationOnClickListener(v -> finish());

        session = new SessionManager(this);

        container = findViewById(R.id.container);
        loader = findViewById(R.id.loader);
        et_id = findViewById(R.id.et_id);
        et_email = findViewById(R.id.et_email);
        btn_reset = findViewById(R.id.btn_reset);
        btn_reset.setOnClickListener(view -> validData());
        et_dob = findViewById(R.id.et_dob);
        et_old_password = findViewById(R.id.et_old_password);
        et_new_password = findViewById(R.id.et_new_password);
        et_con_password = findViewById(R.id.et_con_password);
        btn_submit = findViewById(R.id.btn_id);
        btn_submit.setOnClickListener(view -> validPasswordData());


        et_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    et_new_password.setError("Please enter password");
                    return;
                }
            }
        });

        et_con_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable)) {
                    et_con_password.setError("Please enter password");
                    return;
                }
            }
        });

        et_dob.setOnClickListener(v -> {
            selectDate();
        });

        et_dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!et_dob.getText().toString().equals("")) {
                    et_dob.setError(null);
                }
            }
        });
    }

    private void validPasswordData() {
        try {
            String id = et_id.getText().toString().trim();
            String dob = et_dob.getText().toString().trim();

            String old = et_old_password.getText().toString().trim();
            String newpass = et_new_password.getText().toString().trim();
            String con = et_con_password.getText().toString().trim();
            boolean isvalid =true;

            if (TextUtils.isEmpty(id)) {
                et_id.setError("Please enter valid profile id");
                return;
            }

            if (TextUtils.isEmpty(dob)) {
                et_dob.setError("Please enter date of birth");
                return;
            } else {
                age = calculateAge(et_dob.getText().toString());
            }
            if (TextUtils.isEmpty(old)) {
                et_old_password.setError("Please enter old password");
                isvalid = false;
            }
            if (TextUtils.isEmpty(newpass)) {
                et_new_password.setError("Please enter new password");
                isvalid = false;
            }
            if (newpass.length() < 6) {
                et_new_password.setError("Please enter atleast 6 character");
                isvalid = false;
            }
            if (TextUtils.isEmpty(con)) {
                et_con_password.setError("Please enter confirm password");
                isvalid = false;
            }
            if (con.length() < 6) {
                et_con_password.setError("Please enter atleast 6 character");
                isvalid = false;
            }
            if (!newpass.equals(con)) {
                et_con_password.setError("New password and confirm password not match");
                isvalid = false;
            }

            if (isvalid) {
                HashMap<String, String> param = new HashMap<>();
                param.put("old_pass", old);
                param.put("new_pass", newpass);
                param.put("cnfm_pass", con);
                param.put("member_id", id.substring(4));
                changeApi(param);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectDate() {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    private int calculateAge(String age) {
        try {
            String birthDateString = age; // example birth date string
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = format.parse(birthDateString); // parse the birth date string to a Date object
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate); // set the birth date to the calendar
            Calendar todayCalendar = Calendar.getInstance(); // get the current calendar

            return todayCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }

    private void validData() {
        String email = et_email.getText().toString().trim();
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please enter email");
            return;
        }
        if (!isValidEmail(email)) {
            et_email.setError("Please enter valid email");
            return;
        }


        forgotApi(email);

//        openPassword();
    }


    private void forgotApi(String email) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!", container);
            return;
        }

        common.showProgressRelativeLayout(loader);

        Common.hideSoftKeyboard(this);

        HashMap<String, String> params = new HashMap<>();
        params.put("username", email);

        common.makePostRequest(AppConstants.forgot, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                common.hideProgressRelativeLayout(loader);
                try {
                    JSONObject object = new JSONObject(response);
                    session.setUserData(SessionManager.TOKEN, object.getString("token"));
                    if (!object.getString("status").equals("error")) {
                        et_email.setText("");
//                        finish();
                        openMainLayout();
                    }
                    common.showToast(object.getString("errmessage"), container);
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),container);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.hideProgressRelativeLayout(loader);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),container);
                }
            }
        },container);
    }

    private void openMainLayout() {
        findViewById(R.id.llMain).setVisibility(View.VISIBLE);
        findViewById(R.id.llTop).setVisibility(View.GONE);

    }

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }



    private void changeApi(final HashMap<String, String> param) {

        common.showProgressRelativeLayout(loader);

        common.makePostRequest(AppConstants.change_password, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),container);
                if (!object.getString("status").equals("error")) {
                    session.setUserData(SessionManager.KEY_PASSWORD, param.get("cnfm_pass"));
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),container);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),container);
            }
        },container);
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR)-18;
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);


            DatePickerDialog dialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, yy, mm, dd);
//            dialog.getDatePicker().setMaxDate(new Date().getTime());
            calendar.set(yy, mm, dd);
            dialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
            return dialog;

        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }
    }
    public static void populateSetDate(int year, int month, int day) {
        et_dob.setText(day + "-" + month + "-" + year);
    }

}
