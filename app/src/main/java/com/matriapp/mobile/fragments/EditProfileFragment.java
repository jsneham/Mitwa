package com.matriapp.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.SectionedExpandableLayoutHelper;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;

import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class EditProfileFragment extends Fragment implements View.OnClickListener, ItemClickListener {


    private Common common;
    private SessionManager session;

   private  View view;

    private RecyclerView myProfileRecyclerView;

    private List<ViewProfileSectionBean> myProfileDataList = new ArrayList<>();
    private Context context;
    private RelativeLayout loader;
    private FrameLayout llView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_edit_profile, container, false);
        context=getContext();
        common = new Common(context);
        session = new SessionManager(context);
        myProfileRecyclerView = view.findViewById(R.id.myProfileRecyclerView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
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



                    //TODO add my profile data dynamically
                    myProfileDataList = gson.fromJson(dataObject.getAsJsonArray("fileds"), new TypeToken<List<ViewProfileSectionBean>>() {
                    }.getType());
                    myProfileDataList.get(0).setExpanded(true);
                    for(int i=0;i<myProfileDataList.size();i++){
                        myProfileDataList.get(i).setExpanded(true);
                        for (int  k=0; k < myProfileDataList.get(i).getViewProfileFieldList().size(); k++) {

                            myProfileDataList.get(i).getViewProfileFieldList().removeIf(v -> (v.getTitle().equals("Designation")|| v.getTitle().equals("Partner Complexion")|| v.getTitle().equals("Residence")|| v.getTitle().equals("Star")|| v.getTitle().equals("part_bodytype")|| v.getTitle().equals("Phone")|| v.getTitle().equals("time_to_call")));
                        }
                    }


                    for (ViewProfileSectionBean section : myProfileDataList) {
                        List<ViewProfileFieldsBean> fields = section.getViewProfileFieldList();

                        if (fields != null) {
                            boolean isNeverMarried = fields.stream()
                                    .anyMatch(field -> "Marital Status".equalsIgnoreCase(field.getTitle()) && "Never Married".equalsIgnoreCase(field.getValue()));

                            // Remove fields based on conditions
                            fields.removeIf(field -> isNeverMarried && (
                                    "Total Children".equalsIgnoreCase(field.getTitle()) ||
                                            "Children Living".equalsIgnoreCase(field.getTitle())
                            ));

                            // Rename fields
                            fields.forEach(field -> {
                                if ("Total Children".equalsIgnoreCase(field.getTitle())) {
                                    field.setTitle("No. of Children");
                                } else if ("Children Living".equalsIgnoreCase(field.getTitle())) {
                                    field.setTitle("Children Living With");
                                }
                            });
                        }
                    }


                    //Remove photo url list
                    myProfileDataList.remove(myProfileDataList.size() - 1);
                    myProfileRecyclerView.setNestedScrollingEnabled(true);
                    SectionedExpandableLayoutHelper myProfileSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, myProfileRecyclerView, this, 2, true);
                    for (ViewProfileSectionBean viewProfileSectionBean : myProfileDataList) {
                        myProfileSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
                    }
                    myProfileSectionedExpandableLayoutHelper.notifyDataSetChanged();
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