package com.matriapp.mobile.activities.Registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.multispinnerfilter.MultiSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SingleSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SpinnerListener;
import com.matriapp.mobile.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class RegistrationPartnerActivity extends AppCompatActivity implements View.OnClickListener, SpinnerListener {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private LinearLayout lay_first;
    private ProgressBar pbState;
    private String ragister_id, fage_id,tage_id, fhite_id,thite_id,reli_id,mari_id, height_from = "",
            height_to = "", age_from = "", age_to = "";
    private SingleSpinnerSearch spin_height_from, spin_height_to, spin_age_from, spin_age_to;
    private MultiSpinnerSearch spin_religion, spin_martial;
    private TextView tv_min_height, tv_max_height, tv_min_age, tv_max_age;
    private CrystalRangeSeekbar range_height, range_age;
    private HashMap<String, String> height_map = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_partner);
        common = new Common(this);
        session = new SessionManager(this);
        ragister_id = getIntent().getStringExtra("ragister_id");


        spin_martial = findViewById(R.id.spin_martial);
        setupSearchDropDown(spin_martial, "Marital Status", "marital_status");


        lay_first = findViewById(R.id.lay_first);
        tv_min_height = findViewById(R.id.search_tv_min_height);
        tv_max_height = findViewById(R.id.search_tv_max_height);
        range_height = findViewById(R.id.search_range_height);

        tv_min_age = findViewById(R.id.search_tv_min_age);
        tv_max_age = findViewById(R.id.search_tv_max_age);
        range_age = findViewById(R.id.search_range_age);

//        spin_height_from = findViewById(R.id.spin_height_from);
//        setupSearchDropDown(spin_height_from, "From Height", "height_list");
//        spin_height_to = findViewById(R.id.spin_height_to);
//        setupSearchDropDown(spin_height_to, "To Height", "height_list");

//        spin_age_from = findViewById(R.id.spin_age_from);
//        setupSearchDropDown(spin_age_from, "From Age", "age_rang");
//
//        spin_age_to = findViewById(R.id.spin_age_to);
//        setupSearchDropDown(spin_age_to, "To Age", "age_rang");

        spin_religion = findViewById(R.id.spin_religion);
        setupSearchDropDown(spin_religion, "Religion", "religion_list");

//        spin_caste = findViewById(R.id.spin_caste);
//        setupInitializeSearchDropDown(spin_caste, "Caste");

        setHeightAndAgeRange();
    }

    private void setHeightAndAgeRange() {
        try {
            JSONArray arr = MyApplication.getSpinData().getJSONArray("height_list");
            JSONObject obj = arr.getJSONObject(0);
            JSONObject obj1 = arr.getJSONObject(arr.length() - 1);
            range_height.setMinStartValue(Float.parseFloat(obj.getString("id"))).setMaxStartValue(Float.parseFloat(obj1.getString("id"))).apply();
            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                if (object.getString("id").equals("48")) {
                    height_map.put(object.getString("id"), "Below 4ft");
                } else if (object.getString("id").equals("85")) {
                    height_map.put(object.getString("id"), "Above 7ft");
                } else {
                    height_map.put(object.getString("id"), object.getString("val"));
    }
            }

            range_height.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                height_from = String.valueOf(minValue);
                height_to = String.valueOf(maxValue);
                tv_min_height.setText(disHeight(height_from));
                tv_max_height.setText(disHeight(height_to));
            });


            range_age.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                age_from = String.valueOf(minValue);
                age_to = String.valueOf(maxValue);
                tv_min_age.setText(minValue + " Years");
                tv_max_age.setText(maxValue + " Years");
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String disHeight(String val) {
        return height_map.get(val);
    }

    private void setupSearchDropDown(MultiSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void setupSearchDropDown(SingleSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void setupInitializeSearchDropDown(MultiSpinnerSearch spinner, String hint) {
        spinner.setItems(spinner, new ArrayList<>(), -1, this, hint);
    }

    public void validFirst(View view) {
        boolean isValid = true;
        if (!isValidId(age_from)) {
            tv_min_age.setError("Please select from age");
            isValid = false;
        }
        if (!isValidId(age_to)) {
            tv_max_age.setError("Please select to age");
            isValid = false;
        }
        if (!isValidId(height_from)) {
            tv_min_height.setError("Please select from height");
            isValid = false;
        }
        if (!isValidId(height_to)) {
            tv_max_height.setError("Please select to height");
            isValid = false;
        }
        if (!isValidId(reli_id)) {
            common.spinnerSetError(spin_religion, "Please select religion");
            return;
        }
        if (!isValidId(mari_id)) {
            common.spinnerSetError(spin_martial, "Please select martial status");
            return;
        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("part_frm_age", getValidId(age_from));
            param.put("part_to_age", getValidId(age_to));
            param.put("part_height", getValidId(height_from));
            param.put("part_height_to", getValidId(height_to));
            param.put("part_religion", getValidId(reli_id));
            param.put("looking_for", getValidId(mari_id));
            param.put("member_id", ragister_id);
            submitRagister(AppConstants.edit_profile,"",param);
        }
    }

    private boolean isValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return false;
        }
        return true;
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



    private String getValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return "";
        }
        return val;
    }
    private void goToNextActivity() {
        Intent in = new Intent(this, RegistrationPhotoActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);
    }
    public  void  goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onItemsSelected(MultiSpinnerSearch singleSpinnerSearch) {
        Common.hideSoftKeyboard(this);
        if (singleSpinnerSearch == null) return;
        if (singleSpinnerSearch.getSelectedIdsInString() == null) return;
        switch (singleSpinnerSearch.getId()) {
            case R.id.spin_religion:
                reli_id = singleSpinnerSearch.getSelectedIdsInString();
                Log.d("ressel", reli_id);
//                if (reli_id != null && !reli_id.equals("0") && !reli_id.equals(""))
//                    getDepedentList("caste_list", reli_id);
//                else {
//                    resetCaste();
//                }
                break;
            case R.id.spin_martial:
                mari_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
        }
    }

    @Override
    public void onItemsSelected(SingleSpinnerSearch singleSpinnerSearch, KeyPairBoolData item) {
        Common.hideSoftKeyboard(this);
        if (item == null) return;
        if (item.getId() == null) return;
        switch (singleSpinnerSearch.getId()) {
            case R.id.spin_height_from:
                fhite_id = item.getId();//fhite_map.get(spin_height_from.getSelectedItem().toString());
                break;
            case R.id.spin_height_to:
                thite_id = item.getId();// thite_map.get(spin_height_to.getSelectedItem().toString());
                break;
            case R.id.spin_age_from:
                fage_id = item.getId();// fage_map.get(spin_age_from.getSelectedItem().toString());
                break;
            case R.id.spin_age_to:
                tage_id = item.getId();// tage_map.get(spin_age_to.getSelectedItem().toString());
                break;
        }
    }

    private void getDepedentList(final String tag, String id) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "multi");
        param.put("retun_for", "json");

        JsonParser jsonParser = new JsonParser();

        common.makePostRequest(AppConstants.common_depedent_list, param, response -> {
            Log.d("resp", tag + "   ");
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    if ("caste_list".equals(tag)) {
                        JsonArray jsonArray = (JsonArray) jsonParser.parse(object.getJSONArray("data").toString());
//                        spin_caste.setItems(spin_caste, common.getSpinnerListFromArray(jsonArray), -1, this, "Caste");

                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),lay_first);
            }
        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),lay_first);
            }
        },lay_first);

    }

    private void resetCaste() {
//        spin_caste.setSelection(0);
//        caste_id = "";
    }
}