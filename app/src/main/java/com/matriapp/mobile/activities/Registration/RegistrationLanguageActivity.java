package com.matriapp.mobile.activities.Registration;

import static com.matriapp.mobile.application.MyApplication.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.CustomSpokenLaguagesAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RegistrationLanguageActivity extends AppCompatActivity implements CustomSpokenLaguagesAdapter.ListItemClickListener {
    private Common common;
    private SessionManager session;
    private RecyclerView rvList;
    private RelativeLayout progressBar,llProfileCreate;
    private ProgressBar pbState;
    private String lang_ids = "", ragister_id;
    private EditText editText;
    private Button btn_first_submit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_language);
        common = new Common(this);
        session = new SessionManager(this);
        rvList = findViewById(R.id.rvList);
        llProfileCreate = findViewById(R.id.llProfileCreate);
        progressBar = findViewById(R.id.progressBar);
        ragister_id = getIntent().getStringExtra("ragister_id");
        btn_first_submit =findViewById(R.id.btn_first_submit);
        setList();
    }

    private void setList() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvList.setLayoutManager(mLayoutManager);
        common.setItemDecoration(rvList);
        al = setupListForRecyclerView("mothertongue_list");
        CustomSpokenLaguagesAdapter adapter = new CustomSpokenLaguagesAdapter(al, "", getContext(), this);
//        adapter.setCallback(new CustomSpokenLaguagesAdapter.ListItemClickListener() {
//            @Override
//            public void onItemsClick(String data, boolean isChecked) {
//                editText.setText("");
//                if(isChecked)  lang_ids = lang_ids + data + ",".trim();
//            }
//        });
        rvList.setAdapter(adapter);
        adapter.addItem(al);
        adapter.notifyDataSetChanged();


        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.GONE);
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
    public void onItemsClick(String data, boolean isChecked) {
        if(isChecked) {
            editText.setText("");
            lang_ids = lang_ids + data + ",".trim();
        }
        else{
            lang_ids=  removeWordFromString(lang_ids, data);

        }
    }


    public static String removeWordFromString(String inputString, String wordToRemove) {
        String[] words = inputString.split(",");

        // Create a StringBuilder to build the modified string
        StringBuilder resultBuilder = new StringBuilder();

        // Iterate through the words and add them to the resultBuilder if they are not equal to the wordToRemove
        for (String word : words) {
            if (!word.trim().equals(wordToRemove)) {
                resultBuilder.append(word).append(",");
            }
        }

        // Remove the trailing comma, if any
        if (resultBuilder.length() > 0) {
            resultBuilder.setLength(resultBuilder.length() - 1);
        }

        return resultBuilder.toString();
    }

    public void validFirst(View view) {
        try {
            if (!lang_ids.isEmpty()) {
                lang_ids = lang_ids.substring(0, lang_ids.length() - 1);
                String validIds = getValidId(lang_ids);

                if (!validIds.isEmpty()) {
                    HashMap<String, String> param = new HashMap<>();
                    param.put("languages_known", validIds);
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, "", param);
                } else {
                    common.showToast("Please select language(s) you speak.",llProfileCreate);
                }
            } else {
                common.showToast("Please select language(s) you speak.",llProfileCreate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void submitRagister(String url, final String tag, HashMap<String, String> param) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",llProfileCreate);
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
        Intent in = new Intent(this, RegistrationHoroscopeActivity.class);
        in.putExtra("ragister_id", ragister_id);

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