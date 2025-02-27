package com.matriapp.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.EditPreferenceActivity;
import com.matriapp.mobile.activities.EditProfileActivity;
import com.matriapp.mobile.activities.ViewMyProfileActivityBackup;
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.SectionedExpandableLayoutHelper;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


public class EditPreferenceFragment extends Fragment implements View.OnClickListener, ItemClickListener {

    private  View view;
    private Common common;
    private SessionManager session;
    private RecyclerView preferenceProfileRecyclerView;
    private List<ViewProfileSectionBean> preferenceProfileDataList = new ArrayList<>();
    private Context context;
    private RelativeLayout loader;
    private FrameLayout llView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_preference, container, false);

        context=getContext();
        common = new Common(context);
        session = new SessionManager(context);
        preferenceProfileRecyclerView = view.findViewById(R.id.preferenceProfileRecyclerView);
        loader = view.findViewById(R.id.loader);
        llView = view.findViewById(R.id.llView);


        return  view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyProfile();
    }

    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                AppDebugLog.print("resp : " + response);
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    JSONArray fileds = data.getJSONArray("fileds");
                    Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    preferenceProfileRecyclerView.setNestedScrollingEnabled(true);
                    preferenceProfileDataList = gson.fromJson(dataObject.getAsJsonArray("partners_field"), new TypeToken<List<ViewProfileSectionBean>>() {
                    }.getType());
//                    preferenceProfileDataList.get(0).setExpanded(true);

                    List<String> fieldsToExclude = Arrays.asList("part_complexion", "part_bodytype", "part_expect", "part_star", "part_resi_status");

                    for(int i=0;i<preferenceProfileDataList.size();i++){
                        preferenceProfileDataList.get(i).setExpanded(true);
                    }
                    SectionedExpandableLayoutHelper preferenceSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, preferenceProfileRecyclerView, this, 2, true);
                    for (ViewProfileSectionBean viewProfileSectionBean : preferenceProfileDataList) {
                        List<ViewProfileFieldsBean> filteredFields = viewProfileSectionBean.getViewProfileFieldList().stream()
                                .filter(field -> !fieldsToExclude.contains(field.getId()))
                                .collect(Collectors.toList());

                        // Update the section bean with the filtered fields
                        viewProfileSectionBean.setViewProfileFieldList((ArrayList<ViewProfileFieldsBean>) filteredFields);

                        // Add the filtered section
                        preferenceSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
                    }
                    preferenceSectionedExpandableLayoutHelper.notifyDataSetChanged();
                    //TODO end add preference data dynamically
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void itemClicked(ViewProfileFieldsBean item) {

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