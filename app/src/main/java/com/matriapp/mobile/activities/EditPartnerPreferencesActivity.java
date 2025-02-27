package com.matriapp.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.matriapp.mobile.R;
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.SectionedExpandableLayoutHelper;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditPartnerPreferencesActivity extends AppCompatActivity implements View.OnClickListener, ItemClickListener {

    private Context context;
    private Common common;
    private SessionManager session;



    private RelativeLayout progressBar,container;

    private RecyclerView preferenceProfileRecyclerView;

    private List<ViewProfileSectionBean> preferenceProfileDataList = new ArrayList<>();

    private JSONArray photo_arr;
    private int placeHolder;

    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(this);
        common.setGradient(getWindow());

        setContentView(R.layout.activity_edit_partner_preferences);
        context=this;
//        common = new Common(context);
        session = new SessionManager(context);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Partner Preferences");
        toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        toolbar.setNavigationOnClickListener(v -> finish());

        preferenceProfileRecyclerView = findViewById(R.id.preferenceProfileRecyclerView);

        container = findViewById(R.id.container);
        scrollView = findViewById(R.id.scrollView);

        progressBar = findViewById(R.id.progressBar);
        getMyProfile();
    }
    private void getMyProfile() {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                AppDebugLog.print("resp : " + response);
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    String myProfileStarStr = data.getString("star_str");
                    String myProfileMoonSignStr = data.getString("moonsign_str");
                    ApplicationData.myProfileStarStr = myProfileStarStr;
                    ApplicationData.myProfileMoonSignStr = myProfileMoonSignStr;

                    JSONArray fileds = data.getJSONArray("fileds");
                    photo_arr = fileds.getJSONObject(fileds.length() - 1).getJSONArray("value");

                    Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    preferenceProfileRecyclerView.setNestedScrollingEnabled(false);
                    preferenceProfileDataList = gson.fromJson(dataObject.getAsJsonArray("partners_field"), new TypeToken<List<ViewProfileSectionBean>>() {
                    }.getType());
//                    preferenceProfileDataList.get(0).setExpanded(true);
//                    SectionedExpandableLayoutHelper preferenceSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, preferenceProfileRecyclerView, this, 2, true);
                    for(int i=0;i<preferenceProfileDataList.size();i++){
                        preferenceProfileDataList.get(i).setExpanded(true);
                    }
                    SectionedExpandableLayoutHelper preferenceSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, preferenceProfileRecyclerView, this, 2, true);
                    for (ViewProfileSectionBean viewProfileSectionBean : preferenceProfileDataList) {
                        preferenceSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
                    }
                    preferenceSectionedExpandableLayoutHelper.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),container);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),container);
            }
        },container);
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                String tabid = data.getStringExtra("tabid");
                if (result.equals("reload")) {
                    getMyProfile();
                }
            }
        }
    }

    @Override
    public void itemClicked(ViewProfileFieldsBean item) {
        Log.d("TAG", "itemClicked: ");
    }

    @Override
    public void itemClicked(ViewProfileSectionBean section) {
        switch (section.getId().toLowerCase()) {
            case "basic_info":
                gotoEdit(EditProfileActivity.KEY_BASIC);
                break;
            case "religion_info":
                gotoEdit(EditProfileActivity.KEY_RELIGION);
                break;
            case "about_me_and_hobby":
                gotoEdit(EditProfileActivity.KEY_PROFILE);
                break;
            case "edu_occup":
                gotoEdit(EditProfileActivity.KEY_EDUCATION);
                break;
            case "life_style_info":
                gotoEdit(EditProfileActivity.KEY_LIFE);
                break;
            case "location_info":
                gotoEdit(EditProfileActivity.KEY_LOCATION);
                break;
            case "family_info":
                gotoEdit(EditProfileActivity.KEY_FAMILY);
                break;
            case "basic_partner_info":
                gotoEditPref(EditPreferenceActivity.KEY_BASIC);
                break;
            case "religion_partner_info":
                gotoEditPref(EditPreferenceActivity.KEY_RELIGION);
                break;
            case "location_partner_info":
                gotoEditPref(EditPreferenceActivity.KEY_LOCATION);
                break;
            case "edu_occup_partner_info":
                gotoEditPref(EditPreferenceActivity.KEY_EDUCATION);
                break;
        }
    }

    @Override
    public void viewContact(ViewProfileSectionBean section) {

    }

    @Override
    public void lastSectionExpand(ViewProfileSectionBean section) {
        if (section.getId().equalsIgnoreCase("family_info")) {
            AppDebugLog.print("section id in itemClicked : " + section.getId());
            new Handler().postDelayed(() -> {

                int height = (section.getViewProfileFieldList().size() / 2) * Common.convertDpToPixels(100, context);
                scrollView.scrollTo(0, scrollView.getBottom() + height);
                //userProfileRecyclerView.smoothScrollToPosition(userProfileRecyclerView.getAdapter().getItemCount()-1);
            }, 300);
        } else {

        }
    }

    private void gotoEdit(String tag) {
        Intent i = new Intent(context, EditProfileActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }

    private void gotoEditPref(String tag) {
        Intent i = new Intent(context, EditPreferenceActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }



}