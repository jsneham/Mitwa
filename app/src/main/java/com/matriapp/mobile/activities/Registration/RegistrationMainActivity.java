package com.matriapp.mobile.activities.Registration;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.material.textfield.TextInputLayout;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.AllCmsActivity;
import com.matriapp.mobile.activities.LoginActivity;
import com.matriapp.mobile.activities.RegisterFirstActivity;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.countrycodepicker.CountryCodePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class RegistrationMainActivity extends AppCompatActivity implements View.OnClickListener {

    private Common common;
    private SessionManager session;
    private EditText et_f_name, et_l_name, et_password, et_mobile,et_email;
    private static EditText et_dob;
    private String reference_id = "", created_id = "", gender = "", country_code = "+91", ragister_id="",fb_id="" , staff_assign_id="";
    private Button btn_male, btn_female;
    private DatePickerDialog.OnDateSetListener date;
    private TextInputLayout pass_input;
    private CountryCodePicker spin_code;
    private int age = 0;
    private RelativeLayout progressBar;
            private LinearLayout lay_first;

    public void onHaveAccountClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_main);


        common = new Common(this);
        session = new SessionManager(this);
        lay_first= findViewById(R.id.lay_first);
        progressBar= findViewById(R.id.progressBar);
        staff_assign_id = getIntent().getStringExtra("staff_assign_id");
        created_id = getIntent().getStringExtra("created_id");
        btn_male = findViewById(R.id.btn_male);
        btn_female = findViewById(R.id.btn_female);
        et_f_name = findViewById(R.id.et_f_name);
        et_f_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_l_name = findViewById(R.id.et_l_name);
        et_l_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_password = findViewById(R.id.et_password);
        et_dob = findViewById(R.id.et_dob);
        et_email = findViewById(R.id.et_email);
        btn_male = findViewById(R.id.btn_male);
        btn_female = findViewById(R.id.btn_female);
        et_mobile = findViewById(R.id.et_mobile);
        pass_input = findViewById(R.id.pass_input);
        spin_code = findViewById(R.id.spin_code);
        spin_code.setOnCountryChangeListener(country -> {
            country_code = country.getPhoneCode();
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

        btn_male.setOnClickListener(this);
        btn_female.setOnClickListener(this);
        common.setDrawableLeftButton(R.drawable.male_inactive, btn_male);
        common.setDrawableLeftButton(R.drawable.female_inactive, btn_female);

        singleTextView1(findViewById(R.id.lblTerms));
    }

    private void singleTextView1(TextView textView) {
        String clickableTextStr = "terms";
        String clickableTextStr1 = "privacy policy";
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("By continuing, you accept the " + clickableTextStr+ " and " +clickableTextStr1);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openCMSDataDialog("term");
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(RegistrationMainActivity.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 30, 35, 0);

        SpannableStringBuilder spanText1 = new SpannableStringBuilder();
        spanText1.append(" and " +clickableTextStr1);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openCMSDataDialog("privacy");
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(RegistrationMainActivity.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 40, 54, 0);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(RegistrationMainActivity.this, R.color.transparent));
        textView.setText(spanText, TextView.BufferType.SPANNABLE);
        //  lblTerms.setText(Html.fromHtml(getString(R.string.lbl_service_request)), TextView.BufferType.SPANNABLE);

    }
    private void openCMSDataDialog(String tag) {
        Intent intent = new Intent(this, AllCmsActivity.class);
        intent.putExtra(AppConstants.KEY_INTENT, tag);
        startActivity(intent);
    }

    public void selectDate() {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    public void validFirst1(View view) {
        Intent in = new Intent(this, RegistrationReligionActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);
    }

    public void validFirst(View view) {

        String fname = et_f_name.getText().toString().trim();
        String lname = et_l_name.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String dob = et_dob.getText().toString().trim();
        String mobile = et_mobile.getText().toString().trim();
        country_code = spin_code.getSelectedCountryCodeWithPlus();
        String email = et_email.getText().toString().trim();



        boolean isvalid = true;
        if (gender.equals("")) {
            common.showToast("Please Select your gender",lay_first);
            isvalid = false;
        }

        if (TextUtils.isEmpty(fname)) {
            et_f_name.setError("Please enter first name");
            common.showToast("Please enter first name",lay_first);
            isvalid = false;
        } else if (!common.isValidName(fname)) {

            et_f_name.setError("Please enter valid first name");
            common.showToast("Please enter valid first name",lay_first);
            isvalid = false;

        }
        if (fname.contains(" ") || fname.contains(".")) {
            et_f_name.setError("Please enter valid first name");
            common.showToast("Please enter valid first name",lay_first);
            isvalid = false;
        }

        if (TextUtils.isEmpty(lname)) {
            et_l_name.setError("Please enter last name");
            common.showToast("Please enter last name",lay_first);
            isvalid = false;
        } else if (!common.isValidName(lname)) {

            et_l_name.setError("Please enter valid last name");
            common.showToast("Please enter valid last name",lay_first);
            isvalid = false;

        }

        if (lname.contains(" ")) {
            et_l_name.setError("Please enter valid last name");
            common.showToast("Please enter valid last name",lay_first);
            isvalid = false;
        }




        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter password");
            common.showToast("Please enter password",lay_first);
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (password.length() < 6) {
            et_password.setError("Please enter atleast 6 characters");
            common.showToast("Please enter atleast 6 characters",lay_first);
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (TextUtils.isEmpty(mobile)) {
            et_mobile.setError("Please enter mobile number");
            common.showToast("Please enter mobile number",lay_first);
            isvalid = false;
        } else {

            if(!common.isValidMobileNumberMatch(mobile)){
                et_mobile.setError("Please enter valid mobile number");
                common.showToast("Please enter valid mobile number",lay_first);
                isvalid = false;
            }
            else if (mobile.length() < 10 || mobile.contains(".")) {
                et_mobile.setError("Please enter valid mobile number");
                common.showToast("Please enter valid mobile number",lay_first);
                isvalid = false;
            }
        }
        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please enter email");
            common.showToast("Please enter email",lay_first);
            isvalid = false;
        } else {
            if (!common.isValidEmail(email)) {
                et_email.setError("Please enter valid email");
                common.showToast("Please enter email",lay_first);

                isvalid = false;
            }
        }
        if (TextUtils.isEmpty(dob)) {
            et_dob.setError("Please enter date of birth");
            common.showToast("Please enter date of birth",lay_first);

            isvalid = false;
        } else {
            age = calculateAge(et_dob.getText().toString());
        }
        if (gender.equals("Male")) {
            if (!(age >= 21)) {
                et_dob.setError("Minimum age should be 21 years");
                common.showToast("Minimum age should be 21 years",lay_first);
                isvalid = false;
            }
        } else if (gender.equals("Female")) {
            if (!(age >= 18)) {
                et_dob.setError("Minimum age should be 18 years");
                common.showToast("Minimum age should be 18 years",lay_first);
                isvalid = false;
            }
        }



        if (isvalid) {
//            Intent in = new Intent(this, RegistrationEmailActivity.class);
//            in.putExtra("firstname", fname);
//            in.putExtra("lastname", lname);
//            in.putExtra("password", password);
//            in.putExtra("country_code", country_code);
//            in.putExtra("mobile_number", mobile);
//            in.putExtra("birthdate", dob);
//            in.putExtra("gender", gender);
//            param.put("email", email);
//            in.putExtra("created_id", created_id);
//            startActivity(in);

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
                param.put("staff_assign_id", getValidId(staff_assign_id));

                submitRagister(AppConstants.register_first, param);
            }
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

    private boolean isValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return false;
        }
        return true;
    }

    private int calculateAge(String age) {
        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//            Date d = sdf.parse(age);
//            Calendar c = Calendar.getInstance();
//            c.setTime(d);
//            int year = c.get(Calendar.YEAR);
//            int month = c.get(Calendar.MONTH) + 1;
//            int date = c.get(Calendar.DATE);
//            LocalDate l1 = LocalDate.of(year, month, date);
//            LocalDate now1 = LocalDate.now();
//            Period diff1 = Period.between(l1, now1);
//            return diff1.getYears();
            // System.out.println("age:" + diff1.getYears() + "years");

            String birthDateString = age; // example birth date string
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            Date birthDate = format.parse(birthDateString); // parse the birth date string to a Date object
            Calendar birthCalendar = Calendar.getInstance();
            birthCalendar.setTime(birthDate); // set the birth date to the calendar
            Calendar todayCalendar = Calendar.getInstance(); // get the current calendar

            return todayCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR);


// age variable now holds the calculated age

        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_male) {
            gender = "Male";
            btn_male.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            btn_female.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.registration_hint_color)));
            common.setDrawableLeftButton(R.drawable.male_active, btn_male);
            common.setDrawableLeftButton(R.drawable.female_inactive, btn_female);
            btn_male.setTextColor(getResources().getColor(R.color.colorAccent));
            btn_female.setTextColor(getResources().getColor(R.color.registration_hint_color));


        } else if (id == R.id.btn_female) {
            gender = "Female";
            btn_female.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            btn_male.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.registration_hint_color)));
            common.setDrawableLeftButton(R.drawable.male_inactive, btn_male);
            common.setDrawableLeftButton(R.drawable.female_active, btn_female);
            btn_female.setTextColor(getResources().getColor(R.color.colorAccent));
            btn_male.setTextColor(getResources().getColor(R.color.registration_hint_color));

        }
    }




    public  void  goBack(View view) {
        onBackPressed();
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
//                    params.put("reference", getValidId(reference_id));
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