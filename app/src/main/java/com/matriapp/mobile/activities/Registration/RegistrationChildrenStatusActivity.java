package com.matriapp.mobile.activities.Registration;

import static com.matriapp.mobile.application.MyApplication.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.CustomHeightAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistrationChildrenStatusActivity extends AppCompatActivity implements  CustomHeightAdapter.ListItemClickListener {

    private Common common;
    private SessionManager session;
    private RecyclerView rvList;
    private RelativeLayout progressBar,llProfileCreate;
    private ProgressBar pbState;
    private  String status_child_id="",ragister_id;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_children_status);
        common = new Common(this);
        session = new SessionManager(this);
        rvList = findViewById(R.id.rvList);
        llProfileCreate = findViewById(R.id.llProfileCreate);
        progressBar = findViewById(R.id.progressBar);
        ragister_id=getIntent().getStringExtra("ragister_id");
        setList();
    }

    private void setList() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mLayoutManager);
        common.setItemDecoration(rvList);
        al = setupListForRecyclerView("status_children");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, "", this, getContext());
        rvList.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private List<KeyPairBoolData> setupListForRecyclerView(String listJsonKey) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
            return common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onItemsClick(KeyPairBoolData data, int position, String tag) {
        editText.setText("");
        HashMap<String, String> param = new HashMap<>();
        status_child_id = data.getId();
        param.put("status_children", getValidId(status_child_id));
        param.put("id", ragister_id);
        submitRagister(AppConstants.register_step, "", param);

    }


    private void submitRagister(String url, final String tag, HashMap<String, String> param) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!", llProfileCreate);
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
                    goToNext();
                } else {
                    common.showToast(object.getString("errmessage"),llProfileCreate);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);
    }


    private void goToNext() {
        Intent in = new Intent(this, RegistrationHeightActivity.class);
        in.putExtra("ragister_id",ragister_id);
        startActivity(in);
    }

    private String getValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return "";
        }
        return val;
    }
    public  void  goBack(View view) {
        onBackPressed();
    }
}