package com.matriapp.mobile.activities.Registration;

import static com.matriapp.mobile.application.MyApplication.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.LoginActivity;
import com.matriapp.mobile.adapter.CustomFilterAdapter;
import com.matriapp.mobile.adapter.CustomHeightAdapter;
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

public class RegistrationReferredByActivity_backup extends AppCompatActivity implements CustomHeightAdapter.ListItemClickListener {

    private Common common;
    private SessionManager session;
    private RecyclerView rvMatchmakerName;
    private RelativeLayout progressBar,llProfileCreate;
    private EditText editText;
    private static final String MatchMaker_DETAIL = "MatchMaker_DETAIL";

    public void onHaveAccountClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referred_by);
        common = new Common(this);
        session = new SessionManager(this);
        rvMatchmakerName = findViewById(R.id.rvMatchmakerName);
        progressBar = findViewById(R.id.progressBar);
        llProfileCreate = findViewById(R.id.llProfileCreate);

        getList();

    }


    private void setList() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMatchmakerName.setLayoutManager(mLayoutManager);
        common.setItemDecoration(rvMatchmakerName);
//        al = setupListForRecyclerView("reference");
        al = setupListForRecyclerView("staff_list");

        CustomFilterAdapter adapter = new CustomFilterAdapter(al, "", this, getContext());
        rvMatchmakerName.setAdapter(adapter);

//        editText = findViewById(R.id.alertSearchEditText);
//        editText.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
////                adapter.getFilter().filter(s.toString());
//                adapter.filter(s.toString());
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//            }
//        });

//        CustomHeightAdapter adapter = new CustomHeightAdapter(al, MatchMaker_DETAIL, this, getContext());
//        rvMatchmakerName.setAdapter(adapter);
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

    //    API Call  //
    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",llProfileCreate);
            return;
        }
        common.showProgressRelativeLayout(progressBar);
        common.makePostRequest(AppConstants.common_list, new HashMap<>(), response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                MyApplication.setSpinData(object);
                setList();
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llProfileCreate);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);
    }
    public  void  goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onItemsClick(KeyPairBoolData data, int position, String tag) {
        String staff_assign_id = data.getId();
        Intent in = new Intent(this, RegistrationCreatedByActivity.class);
        in.putExtra("staff_assign_id",staff_assign_id);
        startActivity(in);
    }
}