package com.matriapp.mobile.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AllCmsActivity extends AppCompatActivity {
    private TextView tv_about;
    private Common common;
    private RelativeLayout loader,view;
    private String cms_name = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_cms);
        common = new Common(this);
        common.setGradient(getWindow());
        loader = findViewById(R.id.loader);
        view = findViewById(R.id.view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("CMS");
        toolbar.setNavigationOnClickListener(v -> finish());



        Bundle b = getIntent().getExtras();
        if (b != null) {
            if (b.containsKey(AppConstants.KEY_INTENT)) {
                switch (b.getString(AppConstants.KEY_INTENT)) {
                    case "privacy":
                        getSupportActionBar().setTitle("Privacy Policy");
                        cms_name = "Privacy Policy";
                        break;
                    case "about":
                        getSupportActionBar().setTitle("About us");
                        cms_name = "About Us";
                        break;
                    case "refund":
                        getSupportActionBar().setTitle("Refund Policy");
                        cms_name = "Refund Policy";
                        break;
                    case "term":
                        getSupportActionBar().setTitle("Terms and Conditions");
                        cms_name = "Terms and Condition";
                        break;
                    case "Safety Tips":
                        getSupportActionBar().setTitle("Safety Tips");
                        cms_name = "Safety Tips";
                        break;
                    case "FAQs":
                        getSupportActionBar().setTitle("FAQs");
                        cms_name = "FAQs";
                        break;
                }
            }
        }

        tv_about = findViewById(R.id.tv_about);
        getCMS();
    }

    private void getCMS() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("cms_name", cms_name);

        common.makePostRequest(AppConstants.all_cms, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");
                    String page_content = data.getString("page_content");
                    if(page_content.isEmpty())  tv_about.setText(Html.fromHtml(getString(R.string.comming_version)));
                    else  tv_about.setText(Html.fromHtml(page_content));
                }
                else {
                    if(cms_name.equals("Terms and Condition"))tv_about.setText(getString(R.string.terms_version));
                    else tv_about.setText(Html.fromHtml(getString(R.string.comming_version)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> common.hideProgressRelativeLayout(loader),view);
    }
}
