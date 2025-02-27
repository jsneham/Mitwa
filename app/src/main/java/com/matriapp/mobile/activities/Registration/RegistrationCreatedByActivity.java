package com.matriapp.mobile.activities.Registration;

import static com.matriapp.mobile.application.MyApplication.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.matriapp.mobile.activities.LoginActivity;
import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.CreatedByAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class RegistrationCreatedByActivity extends AppCompatActivity implements CreatedByAdapter.ListItemClickListener{
    private Common common;
    private SessionManager session;
    private RecyclerView rvCreatedBy;
    private RelativeLayout progressBar;
    private ProgressBar pbState;
    private static final String CREATED_BY = "CREATED_BY";
    private  String staff_assign_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_created_by);
        common = new Common(this);
        session = new SessionManager(this);
        rvCreatedBy = findViewById(R.id.rvCreatedBy);
        progressBar = findViewById(R.id.progressBar);
        staff_assign_id = getIntent().getStringExtra("staff_assign_id");
        setList();
    }


    private void setList() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvCreatedBy.setLayoutManager(mLayoutManager);
        common.setItemDecoration(rvCreatedBy);
        al = setupListForRecyclerView("profileby");
        CreatedByAdapter adapter = new CreatedByAdapter(al, CREATED_BY, this, getContext());
        rvCreatedBy.setAdapter(adapter);

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
    public void onItemClick(KeyPairBoolData data, int position, String tag) {
        String created_id = data.getId();
        Intent in = new Intent(this, RegistrationMainActivity.class);
        in.putExtra("created_id",created_id);
        in.putExtra("staff_assign_id",staff_assign_id);
        startActivity(in);
    }

    public  void  goBack(View view) {
        onBackPressed();
    }

    public void onHaveAccountClick(View view) {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}