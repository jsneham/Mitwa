package com.matriapp.mobile.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.CurrentPlanActivity;
import com.matriapp.mobile.activities.DashboardActivity;
import com.matriapp.mobile.activities.EditPreferenceActivity;
import com.matriapp.mobile.activities.EditProfileActivity;
import com.matriapp.mobile.activities.GallaryNewActivity;
import com.matriapp.mobile.activities.ManagePhotosActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PhotoPasswordActivity;
import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.activities.ViewMyProfileActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


public class MyProfileFragment extends Fragment implements View.OnClickListener, TabHost.OnTabChangeListener, ItemClickListener {

//    private final String TAB_FIRST = "first";
//    private final String TAB_SECOND = "second";
//    private final int TAB_FIRST_POSITION = 0;
//    private final int TAB_SECOND_POSITION = 1;


    public MyProfileFragment() {
        this.setHasOptionsMenu(true);
    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.home, menu);
//        final MenuItem action_search = menu.findItem(R.id.action_search);
//        action_search.setVisible(false);
//        super.onCreateOptionsMenu(menu, inflater);
//    }

    private Context context;
    private Common common;
    private SessionManager session;

    //    private TabHost host;
    private int deviceWidth = 0;

    private RelativeLayout progressBar,llView;
    private TextView btnAll, btnShowAll, btnProfileShowAll, tvNoProfileData;

    //    private TextView tv_pro_per;
    private ImageView imgProfile, imgCover;
    //            , imgEdit;
    private RecyclerView myProfileRecyclerView, preferenceProfileRecyclerView;

    private List<ViewProfileSectionBean> myProfileDataList = new ArrayList<>();
    private List<ViewProfileSectionBean> preferenceProfileDataList = new ArrayList<>();

    private JSONArray photo_arr;
    private ProgressBar circularProgressbar;
    private int placeHolder;
    private RecyclerView lv_discover, lv_recent, lv_profile;
    private NestedScrollView scrollView;
    private DiscoverAdapter adapter;
    private RecomdationAdapter adapter1;
    private ProfileAdapter adapter2;
    private LinearLayout llNewly, llRecently, llProfile;
    private List<DashboardItem> list;
    private List<DashboardItem> recentList;
    private List<DashboardItem> profileList;
//    private TextView txtFocus;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        recentList = new ArrayList<>();
        profileList = new ArrayList<>();
    }

    private TextView tvPlan, tv_name, tv_matri_id, tvProgressBar, tv_edit_profile, tvPlansList, tvPreview, tvSRequestPhoto, tvRRequestPhoto;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_myprofile_new, container, false);
        context = getContext();
        common = new Common(context);
        session = new SessionManager(context);

        ((DashboardActivity) getActivity()).setToolbarTitle("Home");
        //  ((DashboardActivity) getActivity()).hideToolbar(false);

        deviceWidth = Common.getDisplayWidth(getActivity());


        llView = view.findViewById(R.id.llView);
        tvNoProfileData = view.findViewById(R.id.tvNoProfileData);
        llProfile = view.findViewById(R.id.llProfile);
        llRecently = view.findViewById(R.id.llRecently);
        llNewly = view.findViewById(R.id.llNewly);
        lv_discover = view.findViewById(R.id.lv_discover);
        lv_discover.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        lv_discover.setLayoutManager(layoutManager);

        lv_recent = view.findViewById(R.id.lv_recent);
        lv_recent.setHasFixedSize(true);
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        lv_recent.setLayoutManager(layoutManager1);

        lv_profile = view.findViewById(R.id.lv_profile);
        lv_profile.setHasFixedSize(true);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
        lv_profile.setLayoutManager(layoutManager2);


        btnAll = view.findViewById(R.id.btnAll);
        btnShowAll = view.findViewById(R.id.btnShowAll);
        btnProfileShowAll = view.findViewById(R.id.btnProfileShowAll);
        imgProfile = view.findViewById(R.id.imgProfile);
        imgCover = view.findViewById(R.id.imgCover);
        tv_name = view.findViewById(R.id.tv_name);
        tv_matri_id = view.findViewById(R.id.tvMaitId);
        tvPlansList = view.findViewById(R.id.tvPlansList);
        tvPlan = view.findViewById(R.id.tvPlan);
        tvPreview = view.findViewById(R.id.tvPreview);

        tvSRequestPhoto = view.findViewById(R.id.tvSRequestPhoto);
        tvRRequestPhoto = view.findViewById(R.id.tvRRequestPhoto);
        tvRRequestPhoto.setOnClickListener(this);
        tvSRequestPhoto.setOnClickListener(this);


        scrollView = view.findViewById(R.id.scrollView);


        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }
        imgProfile.setImageResource(placeHolder);
        imgCover.setImageResource(R.drawable.gradient_toolbar);


        progressBar = view.findViewById(R.id.progressBar);


        circularProgressbar = view.findViewById(R.id.circularProgressbar);

        imgProfile.setOnClickListener(this);
        imgCover.setOnClickListener(this);
//        imgEdit.setOnClickListener(this);
        tvPlansList.setOnClickListener(vm -> {
            startActivity(new Intent(getContext(), CurrentPlanActivity.class));
        });
        tvPreview.setOnClickListener(vm -> {
            startActivity(new Intent(getContext(), ViewMyProfileActivity.class));
//            startActivity(new Intent(getContext(), PreviewOthersProfileActivity.class));
        });


        btnAll.setOnClickListener(vm -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MatchesFragment(2)).commit();
        });
        btnShowAll.setOnClickListener(vm -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MatchesFragment(3)).commit();
        });
        btnProfileShowAll.setOnClickListener(vm -> {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment, new MatchesFragment(4)).commit();
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyProfile();
        getData();
        getProfileData();
        getRecentData();
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

                    if (dataObject.has("photo1") && dataObject.get("photo1").getAsString() != null && !dataObject.get("photo1").getAsString().equals("")) {
                        Picasso.get().load(data.getString("photo1"))
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .resize(deviceWidth, deviceWidth)
                                .centerInside()
                                .into(imgProfile);
                    }
                    if (dataObject.has("cover_photo") && !(data.get("cover_photo").equals(""))) {
//                        Picasso.get().load(data.getString("cover_photo"))
//                                .placeholder(R.drawable.ic_coverphoto_notuploaded)
//                                .error(R.drawable.ic_coverphoto_notuploaded)
////                                .resize(deviceWidth, deviceWidth)
//                                .into(imgCover);
                    }

                    tv_name.setText(data.getString("username"));
                    tv_matri_id.setText(data.getString("matri_id"));
                    circularProgressbar.setProgress(Integer.parseInt(data.getString("percentage")));

//                    circularProgressbar.setProgress(dataObject.get("percentage").getAsInt());
//                    tv_pro_per.setText(dataObject.get("percentage").getAsInt() + "%");

                    //TODO add my profile data dynamically
//                    myProfileDataList = gson.fromJson(dataObject.getAsJsonArray("fileds"), new TypeToken<List<ViewProfileSectionBean>>() {
//                    }.getType());
//                    myProfileDataList.get(0).setExpanded(true);
//                    //Remove photo url list
//                    myProfileDataList.remove(myProfileDataList.size() - 1);
//                  //  myProfileRecyclerView.setNestedScrollingEnabled(false);
//                    SectionedExpandableLayoutHelper myProfileSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, myProfileRecyclerView, this, 2, true);
//                    for (ViewProfileSectionBean viewProfileSectionBean : myProfileDataList) {
//                        myProfileSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
//                    }
//                    myProfileSectionedExpandableLayoutHelper.notifyDataSetChanged();
//                    //TODO add my profile data dynamically
//
//                    //TODO add preference data dynamically
//                    preferenceProfileRecyclerView.setNestedScrollingEnabled(false);
//                    preferenceProfileDataList = gson.fromJson(dataObject.getAsJsonArray("partners_field"), new TypeToken<List<ViewProfileSectionBean>>() {
//                    }.getType());
//                    preferenceProfileDataList.get(0).setExpanded(true);
//                    SectionedExpandableLayoutHelper preferenceSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(context, preferenceProfileRecyclerView, this, 2, true);
//                    for (ViewProfileSectionBean viewProfileSectionBean : preferenceProfileDataList) {
//                        preferenceSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
//                    }
//                    preferenceSectionedExpandableLayoutHelper.notifyDataSetChanged();
                    //TODO end add preference data

                    getCurrentPlan();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgProfile:
                if (photo_arr.length() > 0) {
                    goToGallery();
                }
                break;
            case R.id.imgCover:
                goToManagePhotos();
                break;
            case R.id.imgEdit:
                Intent i = new Intent(context, ManagePhotosActivity.class);
                startActivityForResult(i, 7);
                break;
            case R.id.tvRRequestPhoto:
                gotoActivity("receive", PhotoPasswordActivity.class);
                break;

            case R.id.tvSRequestPhoto:
                gotoActivity("sent", PhotoPasswordActivity.class);
                break;
        }
    }

    private void goToManagePhotos() {
        Intent i = new Intent(context, ManagePhotosActivity.class);
        i.putExtra("pageTag", "Manage Photos");
        startActivity(i);
    }

    private void goToGallery() {
        Intent intent = new Intent(context, GallaryNewActivity.class);
        intent.putExtra("imagePosition", 0);
        intent.putExtra("imageArray", photo_arr.toString());
        intent.putExtra("tag", "self");
        startActivity(intent);
    }

    private void gotoActivity(String tag, Class activity) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra("ppassword_tag", tag);
        startActivity(i);
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
//                if (tabid.equals("my")) {
//                    host.setCurrentTab(TAB_FIRST_POSITION);
//                    host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
//                    host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
//                } else {
//                    host.setCurrentTab(TAB_SECOND_POSITION);
//                    host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
//                    host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tab_selector);
//                }
            }
        }
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
        if (section.getId().equalsIgnoreCase("family_info")) {
            AppDebugLog.print("section id in itemClicked : " + section.getId());
            new Handler().postDelayed(() -> {
//                txtFocus.requestFocus();
                int height = (section.getViewProfileFieldList().size() / 2) * Common.convertDpToPixels(100, context);
                scrollView.scrollTo(0, scrollView.getBottom() + height);
                //userProfileRecyclerView.smoothScrollToPosition(userProfileRecyclerView.getAdapter().getItemCount()-1);
            }, 300);
        } else {
//            txtFocus.clearFocus();
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

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
//            case TAB_FIRST:
//                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
//                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
//
//                TextView tv = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
//                TextView tv1 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);
//
//                common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, tv);
//                common.setDrawableLeftTextViewLeft(R.drawable.user_pink, tv1);
//
//                setTextViewDrawableColor(tv,R.color.colorAccent);
//                setTextViewDrawableColor(tv1,R.color.colorAccent);
//                break;
//            case TAB_SECOND:
//                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
//                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tab_selector);
//
//                TextView tv2 = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
//                TextView tv3 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);
//
//                common.setDrawableLeftTextViewLeft(R.drawable.user_pink, tv2);
//                common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, tv3);
//
//                setTextViewDrawableColor(tv2,R.color.colorAccent);
//                setTextViewDrawableColor(tv3,R.color.colorAccent);
//                break;

        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }


    private void getCurrentPlan() {

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (object.has("plan_expired") && !object.isNull("plan_expired")) {
                    MyApplication.setPlan(common.isPlanExpired(object.getString("plan_expired")));
                }
                JSONObject data = object.getJSONObject("data");
                tvPlan.setText(data.getString("plan_name"));
                if (!data.getString("plan_name").contains("free"))
                    tvPlansList.setBackground(context.getDrawable(R.drawable.ic_upgrademembership));
//                    tvPlansList.setText("UPGRADE");
            } catch (JSONException e) {

                e.printStackTrace();
            }
            //  pd.dismiss();
        }, error -> {

        },llView);
    }


    private void getData() {
        if (list.size() > 0) list.clear();
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.recent_join, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            AppDebugLog.print("recent_join : " + response.toString());
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {

                    JSONArray data = object.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        if (common.isNotNullOrEmpty(obj.getString("matri_id"))) {
                            DashboardItem item = new DashboardItem();
                            item.setName(obj.getString("matri_id"));
                            item.setFirst_name(obj.getString("username"));
                            item.setState(obj.getString("state_name"));
                            item.setImage(obj.getString("photo1"));
                            item.setImage_approval(obj.getString("photo1_approve"));
                            item.setAge(obj.getString("age"));
                            item.setHeight(obj.getString("height"));
                            item.setCaste(obj.getString("caste_name"));
                            item.setReligion(obj.getString("religion_name"));
                            item.setCity(obj.getString("city_name"));
                            item.setCountry(obj.getString("country_name"));
                            item.setDesignation(obj.getString("designation_name"));
                            item.setPhoto_protect(obj.getString("photo_protect"));
                            item.setPhoto_view_status(obj.getString("photo_view_status"));//012
                            item.setPhoto_password(obj.getString("photo_password"));//1-act,0-disc
                            item.setId(obj.getString("id"));
                            item.setPhoto_view_count(obj.getString("photo_view_count"));
                            item.setPhotoUrl(obj.getString("photoUrl"));


                            item.setOccupation(obj.getString("occupation_name"));
                            item.setMTongueName(obj.getString("mtongue_name"));
                            item.setEducation(obj.getString("education_name"));

                            item.setBadge(obj.getString("badge"));
                            item.setBadgeUrl(obj.getString("badgeUrl"));
                            item.setPlan_status(obj.getString("plan_status"));

                            JSONArray action = obj.getJSONArray("action");
                            item.setAction(action.getJSONObject(0));
                            list.add(item);
                        }
                    }
                    if (list.size() == 0) {
                        llNewly.setVisibility(View.GONE);
                    } else {
                        llNewly.setVisibility(View.VISIBLE);
                    }
//                    adapter.notifyDataSetChanged();
                    adapter = new DiscoverAdapter(getActivity(), list,llView);
                    lv_discover.setAdapter(adapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    private void getProfileData() {
        if (profileList.size() > 0) profileList.clear();
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.who_viewed_list, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            AppDebugLog.print("recent_join : " + response.toString());
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    if (!object.getString("total_count").equals("0")) {
                        JSONArray data = object.getJSONArray("data");
                        int dataSize = data.length() >= 5 ? 5 : data.length();
                        for (int i = 0; i < dataSize; i++) {
                            JSONObject obj = data.getJSONObject(i);
                            if (Common.isNotNullOrEmpty(obj.getString("matri_id"))) {
                                DashboardItem item = new DashboardItem();
                                item.setId(obj.getString("user_id"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setUser_id(obj.getString("user_id"));
                                item.setName(obj.getString("username"));

                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
//                            String description = Common.getDetailsFromValue(
//                                    obj.getString("age").toLowerCase(),
//                                    obj.getString("height").replace("ft", "\'").replace("in", "\""),
//                                    obj.getString("caste_name"), obj.getString("religion_name"),
//                                    obj.getString("city_name"), "", obj.getString("country_name"));
//                            item.setAbout(description);

                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setImage(obj.getString("photo1"));
                                item.setPhotoUrl(obj.getString("photoUrl"));
//                        item.setPhoto_view_count(obj.getString("photo_view_count"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));

                                JSONArray action = obj.getJSONArray("action");
                                item.setAction(action.getJSONObject(0));

                                item.setState(obj.getString("state_name"));
                                item.setCity(obj.getString("city_name"));
                                item.setAge(obj.getString("age"));
                                item.setHeight(obj.getString("height"));
//                            item.setOccupation(obj.getString("occupation_name"));
                                item.setOccupation("");
//                            item.setMTongueName(obj.getString("mtongue_name"));
                                item.setMTongueName("");
                                item.setEducation(obj.getString("education_name"));
                                item.setCaste(obj.getString("caste_name"));

                                item.setBadge(obj.getString("badge"));
                                item.setBadgeUrl(obj.getString("badgeUrl"));
                                item.setPlan_status(obj.getString("plan_status"));
                                profileList.add(item);
                            }
                        }
                        if (profileList.size() == 0) {
                            llProfile.setVisibility(View.GONE);
                        } else {
                            llProfile.setVisibility(View.VISIBLE);
                        }
//                    adapter.notifyDataSetChanged()

                        removeItemById(profileList);
                        if (profileList.size() > 0) {
                            adapter2 = new ProfileAdapter(getActivity(), profileList,llView);
                            lv_profile.setAdapter(adapter2);
                        } else {
                            tvNoProfileData.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tvNoProfileData.setVisibility(View.VISIBLE);
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);


    }

    private void removeItemById(List<DashboardItem> itemList) {
        Iterator<DashboardItem> iterator = itemList.iterator();

        while (iterator.hasNext()) {
            DashboardItem item = iterator.next();

            if (item.getMatri_id().equals(session.getLoginData(SessionManager.KEY_MATRI_ID))) {
                iterator.remove();
            }
        }
    }

    private void getRecentData() {
        if (recentList.size() > 0) recentList.clear();
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.recent_login, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            AppDebugLog.print("recent_join : " + response.toString());
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {

                    JSONArray data = object.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        if (common.isNotNullOrEmpty(obj.getString("matri_id"))) {
                            DashboardItem item = new DashboardItem();
                            item.setName(obj.getString("matri_id"));
                            item.setFirst_name(obj.getString("username"));
                            item.setState(obj.getString("state_name"));
                            item.setImage(obj.getString("photo1"));
                            item.setImage_approval(obj.getString("photo1_approve"));
                            item.setAge(obj.getString("age"));
                            item.setHeight(obj.getString("height"));
                            item.setCaste(obj.getString("caste_name"));
                            item.setReligion(obj.getString("religion_name"));
                            item.setCity(obj.getString("city_name"));
                            item.setCountry(obj.getString("country_name"));
                            item.setDesignation(obj.getString("designation_name"));
                            item.setPhoto_protect(obj.getString("photo_protect"));
                            item.setPhoto_view_status(obj.getString("photo_view_status"));//012
                            item.setPhoto_password(obj.getString("photo_password"));//1-act,0-disc
                            item.setId(obj.getString("id"));
                            item.setPhoto_view_count(obj.getString("photo_view_count"));
                            item.setPhotoUrl(obj.getString("photoUrl"));

                            item.setOccupation(obj.getString("occupation_name"));
                            item.setMTongueName(obj.getString("mtongue_name"));
                            item.setEducation(obj.getString("education_name"));

                            item.setBadge(obj.getString("badge"));
                            item.setBadgeUrl(obj.getString("badgeUrl"));
                            item.setPlan_status(obj.getString("plan_status"));
                            JSONArray action = obj.getJSONArray("action");
                            item.setAction(action.getJSONObject(0));
                            recentList.add(item);
                        }
                    }
                    if (recentList.size() == 0) {
                        llRecently.setVisibility(View.GONE);
                    } else {
                        llRecently.setVisibility(View.VISIBLE);
                    }
//                    adapter.notifyDataSetChanged();
                    adapter1 = new RecomdationAdapter(getActivity(), recentList,llView);
                    lv_recent.setAdapter(adapter1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }


    public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.ViewHolder> {

        Context context;
        List<DashboardItem> list;
        Common common;
        RelativeLayout llView;


        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView tv_mid, tv_name, tv_detail, btn_interest;
            public ImageView btn_chat, ivShort, img_profile, ivConnected, imgPLanStamp;
            public LinearLayout btnInterest;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View rowView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(rowView);

                btn_interest = rowView.findViewById(R.id.btn_interest);
                btn_chat = rowView.findViewById(R.id.btn_chat);
                ivShort = rowView.findViewById(R.id.ivShort);

                tv_mid = rowView.findViewById(R.id.tv_mid);
                tv_name = rowView.findViewById(R.id.tv_name);
                tv_detail = rowView.findViewById(R.id.tv_detail);
                img_profile = rowView.findViewById(R.id.img_profile);
                btn_interest = rowView.findViewById(R.id.btn_interest);
                btnInterest = rowView.findViewById(R.id.btnInterest);
                ivConnected = rowView.findViewById(R.id.ivConnected);
                imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            }
        }


        public DiscoverAdapter(Context context, List<DashboardItem> list, RelativeLayout llView) {
            this.context = context;
            this.list = list;
            this.llView = llView;
            common = new Common(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.profile_newly_joined, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DashboardItem item = list.get(position);

            try {
                if (item.getBadge() != null) {
                    if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                        Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                                .placeholder(R.drawable.ic_transparent_placeholder)
                                .error(R.drawable.ic_transparent_placeholder)
                                .into(holder.imgPLanStamp);
                        holder.imgPLanStamp.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgPLanStamp.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgPLanStamp.setVisibility(View.GONE);
                }

                if (!item.getFirst_name().equals("null")) {
                    if (!(item.getAction().getString("is_interest").equals(""))) {
                        holder.ivConnected.setImageResource(R.drawable.rl_connected);
                        holder.btn_interest.setText(R.string.requested);
                        holder.btn_interest.setTextColor(getResources().getColor(R.color.online));
                        holder.btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_connect_green));
                    } else {
                        holder.btn_interest.setText(R.string.send_interest);
                    }

                    AppDebugLog.print("is online : " + item.getAction().getInt("is_login"));


                    Drawable img_white = context.getResources().getDrawable(R.drawable.eye_pink);
                    img_white.setBounds(0, 0, 40, 40);
                    // tv_view_count.setCompoundDrawables(img_white, null, null, null);

//            holder.tv_name.setText(item.getFirst_name());
                    holder.tv_mid.setText(item.getName());
                    String[] Name = item.getFirst_name().split(" ");
                    if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        if (Name.length == 3) {
                            holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                        } else if (Name.length == 2) {
                            holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                        } else {
                            holder.tv_name.setText(Name[0].charAt(0));
                        }
                    } else
                        holder.tv_name.setText(item.getFirst_name());


                    common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl() + item.getImage(), holder.img_profile, null, 68);


                    String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                            item.getMTongueName(), item.getCaste(),
                            item.getEducation(), item.getOccupation(), item.getCity(), item.getState(), 31);
                    holder.tv_detail.setText(description);


                    holder.btn_interest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                if (!item.getAction().getString("is_interest").equals("")) {
                                    common.showToast("You already sent interest to this user.",llView);

                                } else {
                                    LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                                    //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                                    final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                                    final BottomSheetDialog dialog = new BottomSheetDialog(context);
                                    dialog.setContentView(vv);
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
//                                            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                            common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
                                        }
                                    });
                                    dialog.show();
                                    ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                                    tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                                    Button send = vv.findViewById(R.id.btn_send_intr);
                                    send.setOnClickListener(view12 -> {
                                        dialog.dismiss();
                                        if (grp_interest.getCheckedRadioButtonId() != -1) {
                                            RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                            interestRequest(item.getName(), btn.getText().toString().trim(), holder.btn_interest, holder.btnInterest, holder.ivConnected);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    holder.tv_detail.setOnClickListener(view14 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
//                    common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });

                    tv_name.setOnClickListener(view15 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
//                    common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });
                    holder.img_profile.setOnClickListener(view16 -> {
                        if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                            alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
                        } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.show_image_alert);
                            TouchImageView img_url = dialog.findViewById(R.id.img_url);
                            Picasso.get().load(item.getImage()).into(img_url);
                            dialog.show();
                        } else {
                            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                                Intent i = new Intent(context, OtherUserProfileActivity.class);
                                i.putExtra("other_id", item.getId());
                                context.startActivity(i);
                            } else {
                                //    common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                                Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                                in.putExtra("other_id", item.getId());
                                context.startActivity(in);
                            }

                        }

                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size() >= 5 ? 5 : list.size();
        }

        private void alertPhotoPassword(final String password, final String url, final String matri_id) {
            final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                    "I am interested in your profile. I would like to view photo now, accept photo request."};
            final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

            alt_bld.setTitle("Photos View Request");
            alt_bld.setSingleChoiceItems(arr, 0, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    //dialog.dismiss();// dismiss the alertbox after chose option
                    selected[0] = arr[item];
                }
            });
            alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
            alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
                //alertpassword(password,url);
            });
            AlertDialog alert = alt_bld.create();
            alert.show();

        }

        private void sendRequest(String int_msg, String matri_id) {
            common.showProgressRelativeLayout(progressBar);

            HashMap<String, String> param = new HashMap<>();
            param.put("interest_message", int_msg);
            param.put("receiver_id", matri_id);
            param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

            common.makePostRequestTime(AppConstants.photo_password_request, param, response -> {
                common.hideProgressRelativeLayout(progressBar);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"),llView);

                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),llView);
                }
            }, error -> {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
                }
            },llView);

        }


    }

    private void interestRequest(String matri_id, String int_msg, final TextView btn_interest, final LinearLayout btnInterest, final ImageView ivConnected) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(AppConstants.send_interest, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    //  button.setLiked(true);
//                    btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_inter_filled));
                    btn_interest.setText(R.string.requested);
                    btn_interest.setTextColor(getResources().getColor(R.color.online));
                    ivConnected.setImageResource(R.drawable.rl_connected);
                    btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_connect_green));
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);

                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


    public class RecomdationAdapter extends RecyclerView.Adapter<RecomdationAdapter.ViewHolder> {

        Context context;
        List<DashboardItem> list;
        Common common;
        RelativeLayout llView;


        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView tv_mid, tv_name, tv_detail, btn_interest;
            public ImageView btn_chat, ivShort, img_profile, ivConnected, imgPLanStamp;
            public LinearLayout btnInterest;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View rowView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(rowView);

                btn_interest = rowView.findViewById(R.id.btn_interest);
                btn_chat = rowView.findViewById(R.id.btn_chat);
                ivShort = rowView.findViewById(R.id.ivShort);

                tv_mid = rowView.findViewById(R.id.tv_mid);
                tv_name = rowView.findViewById(R.id.tv_name);
                tv_detail = rowView.findViewById(R.id.tv_detail);
                img_profile = rowView.findViewById(R.id.img_profile);
                btn_interest = rowView.findViewById(R.id.btn_interest);
                btnInterest = rowView.findViewById(R.id.btnInterest);
                ivConnected = rowView.findViewById(R.id.ivConnected);
                imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            }
        }


        public RecomdationAdapter(Context context, List<DashboardItem> list, RelativeLayout llView) {
            this.context = context;
            this.list = list;
            this.llView = llView;
            common = new Common(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.profile_newly_joined, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DashboardItem item = list.get(position);

            try {

                if (item.getBadge() != null) {
                    if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                        Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                                .placeholder(R.drawable.ic_transparent_placeholder)
                                .error(R.drawable.ic_transparent_placeholder)
                                .into(holder.imgPLanStamp);
                        holder.imgPLanStamp.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgPLanStamp.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgPLanStamp.setVisibility(View.GONE);
                }

                if (!item.getFirst_name().equals("null")) {
                    if (!(item.getAction().getString("is_interest").equals(""))) {
                        holder.ivConnected.setImageResource(R.drawable.rl_connected);
                        holder.btn_interest.setText(R.string.requested);
                        holder.btn_interest.setTextColor(getResources().getColor(R.color.online));
                        holder.btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_connect_green));
                    } else {
                        holder.btn_interest.setText(R.string.send_interest);
                    }

                    AppDebugLog.print("is online : " + item.getAction().getInt("is_login"));


                    Drawable img_white = context.getResources().getDrawable(R.drawable.eye_pink);
                    img_white.setBounds(0, 0, 40, 40);
                    // tv_view_count.setCompoundDrawables(img_white, null, null, null);

//            holder.tv_name.setText(item.getFirst_name());
                    holder.tv_mid.setText(item.getName());
                    String[] Name = item.getFirst_name().split(" ");
                    if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        if (Name.length == 3) {
                            holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                        } else if (Name.length == 2) {
                            holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                        } else {
                            holder.tv_name.setText(Name[0].charAt(0));
                        }
                    } else
                        holder.tv_name.setText(item.getFirst_name());


                    common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl() + item.getImage(), holder.img_profile, null, 68);


                    String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                            item.getMTongueName(), item.getCaste(),
                            item.getEducation(), item.getOccupation(), item.getCity(), item.getState(), 31);
                    holder.tv_detail.setText(description);


                    holder.btn_interest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                if (!item.getAction().getString("is_interest").equals("")) {
                                    common.showToast("You already sent interest to this user.",llView);

                                } else {
                                    LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                                    //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                                    final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                                    final BottomSheetDialog dialog = new BottomSheetDialog(context);
                                    dialog.setContentView(vv);
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
//                                            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                            common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
                                        }
                                    });
                                    dialog.show();
                                    ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                                    tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                                    Button send = vv.findViewById(R.id.btn_send_intr);
                                    send.setOnClickListener(view12 -> {
                                        dialog.dismiss();
                                        if (grp_interest.getCheckedRadioButtonId() != -1) {
                                            RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                            interestRequest(item.getName(), btn.getText().toString().trim(), holder.btn_interest, holder.btnInterest, holder.ivConnected);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    holder.tv_detail.setOnClickListener(view14 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
                            // common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });

                    tv_name.setOnClickListener(view15 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
                            //  common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });
                    holder.img_profile.setOnClickListener(view16 -> {
                        if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                            alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
                        } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.show_image_alert);
                            TouchImageView img_url = dialog.findViewById(R.id.img_url);
                            Picasso.get().load(item.getImage()).into(img_url);
                            dialog.show();
                        } else {
                            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                                Intent i = new Intent(context, OtherUserProfileActivity.class);
                                i.putExtra("other_id", item.getId());
                                context.startActivity(i);
                            } else {
                                //     common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                                Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                                in.putExtra("other_id", item.getId());
                                context.startActivity(in);
                            }

                        }

                    });

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return list.size() >= 5 ? 5 : list.size();
        }

        private void alertPhotoPassword(final String password, final String url, final String matri_id) {
            final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                    "I am interested in your profile. I would like to view photo now, accept photo request."};
            final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

            alt_bld.setTitle("Photos View Request");
            alt_bld.setSingleChoiceItems(arr, 0, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    //dialog.dismiss();// dismiss the alertbox after chose option
                    selected[0] = arr[item];
                }
            });
            alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
            alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
                //alertpassword(password,url);
            });
            AlertDialog alert = alt_bld.create();
            alert.show();

        }

        private void sendRequest(String int_msg, String matri_id) {
            common.showProgressRelativeLayout(progressBar);

            HashMap<String, String> param = new HashMap<>();
            param.put("interest_message", int_msg);
            param.put("receiver_id", matri_id);
            param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

            common.makePostRequestTime(AppConstants.photo_password_request, param, response -> {
                common.hideProgressRelativeLayout(progressBar);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"),llView);

                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),llView);
                }
            }, error -> {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
                }
            },llView);

        }


    }


    public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ViewHolder> {

        Context context;
        List<DashboardItem> list;
        Common common;
        RelativeLayout llView;


        public class ViewHolder extends RecyclerView.ViewHolder {
            // Your holder should contain a member variable
            // for any view that will be set as you render a row
            public TextView tv_mid, tv_name, tv_detail, btn_interest;
            public ImageView btn_chat, ivShort, img_profile, ivConnected, imgPLanStamp;
            public LinearLayout btnInterest;

            // We also create a constructor that accepts the entire item row
            // and does the view lookups to find each subview
            public ViewHolder(View rowView) {
                // Stores the itemView in a public final member variable that can be used
                // to access the context from any ViewHolder instance.
                super(rowView);

                btn_interest = rowView.findViewById(R.id.btn_interest);
                btn_chat = rowView.findViewById(R.id.btn_chat);
                ivShort = rowView.findViewById(R.id.ivShort);

                tv_mid = rowView.findViewById(R.id.tv_mid);
                tv_name = rowView.findViewById(R.id.tv_name);
                tv_detail = rowView.findViewById(R.id.tv_detail);
                img_profile = rowView.findViewById(R.id.img_profile);
                btn_interest = rowView.findViewById(R.id.btn_interest);
                btnInterest = rowView.findViewById(R.id.btnInterest);
                ivConnected = rowView.findViewById(R.id.ivConnected);
                imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            }
        }


        public ProfileAdapter(Context context, List<DashboardItem> list, RelativeLayout llView) {
            this.context = context;
            this.list = list;
            this.llView = llView;
            common = new Common(context);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View rowView = inflater.inflate(R.layout.profile_newly_joined, parent, false);
            ViewHolder viewHolder = new ViewHolder(rowView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final DashboardItem item = list.get(position);

            try {
                if (item.getBadge() != null) {
                    if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                        Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                                .placeholder(R.drawable.ic_transparent_placeholder)
                                .error(R.drawable.ic_transparent_placeholder)
                                .into(holder.imgPLanStamp);
                        holder.imgPLanStamp.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgPLanStamp.setVisibility(View.GONE);
                    }
                } else {
                    holder.imgPLanStamp.setVisibility(View.GONE);
                }

                if (!item.getName().equals("null")) {
                    if (!(item.getAction().getString("is_interest").equals(""))) {
                        holder.ivConnected.setImageResource(R.drawable.rl_connected);
                        holder.btn_interest.setText(R.string.requested);
                        holder.btn_interest.setTextColor(getResources().getColor(R.color.online));
                        holder.btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_connect_green));
                    } else {
                        holder.btn_interest.setText(R.string.send_interest);
                    }

                    AppDebugLog.print("is online : " + item.getAction().getInt("is_login"));


                    Drawable img_white = context.getResources().getDrawable(R.drawable.eye_pink);
                    img_white.setBounds(0, 0, 40, 40);
                    // tv_view_count.setCompoundDrawables(img_white, null, null, null);

//            holder.tv_name.setText(item.getFirst_name());
                    holder.tv_mid.setText(item.getMatri_id());
                    try {
                        String[] Name = item.getName().split(" ");
                        if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            if (Name.length == 3) {
                                holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                            } else if (Name.length == 2) {
                                holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                            } else if (Name.length == 1) {
                                holder.tv_name.setText(Name[0].charAt(0));
                            }
                        } else
                            holder.tv_name.setText(item.getName());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl() + item.getImage(), holder.img_profile, null, 68);


                    String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                            item.getMTongueName(), item.getCaste(),
                            item.getEducation(), item.getOccupation(), item.getCity(), item.getState(), 31);
                    holder.tv_detail.setText(description);

                    holder.btn_interest.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                if (!item.getAction().getString("is_interest").equals("")) {
                                    common.showToast("You already sent interest to this user.",llView);

                                } else {
                                    LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                                    //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                                    final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                                    final BottomSheetDialog dialog = new BottomSheetDialog(context);
                                    dialog.setContentView(vv);
                                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                        @Override
                                        public void onShow(DialogInterface dialogInterface) {
//                                            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                            common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
                                        }
                                    });
                                    dialog.show();
                                    ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                                    tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                                    Button send = vv.findViewById(R.id.btn_send_intr);
                                    send.setOnClickListener(view12 -> {
                                        dialog.dismiss();
                                        if (grp_interest.getCheckedRadioButtonId() != -1) {
                                            RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                            interestRequest(item.getName(), btn.getText().toString().trim(), holder.btn_interest, holder.btnInterest, holder.ivConnected);
                                        }
                                    });
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    holder.tv_detail.setOnClickListener(view14 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
//                    common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });

                    tv_name.setOnClickListener(view15 -> {
                        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                            Intent i = new Intent(context, OtherUserProfileActivity.class);
                            i.putExtra("other_id", item.getId());
                            context.startActivity(i);
                        } else {
//                    common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getId());
                            context.startActivity(in);
                        }
                    });
                    holder.img_profile.setOnClickListener(view16 -> {
                        if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                            alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
                        } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.show_image_alert);
                            TouchImageView img_url = dialog.findViewById(R.id.img_url);
                            Picasso.get().load(item.getImage()).into(img_url);
                            dialog.show();
                        } else {
                            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                                Intent i = new Intent(context, OtherUserProfileActivity.class);
                                i.putExtra("other_id", item.getId());
                                context.startActivity(i);
                            } else {
                                //    common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                                Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                                in.putExtra("other_id", item.getId());
                                context.startActivity(in);
                            }

                        }

                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return list.size() >= 5 ? 5 : list.size();
        }

        private void alertPhotoPassword(final String password, final String url, final String matri_id) {
            final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                    "I am interested in your profile. I would like to view photo now, accept photo request."};
            final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
            AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

            alt_bld.setTitle("Photos View Request");
            alt_bld.setSingleChoiceItems(arr, 0, new DialogInterface
                    .OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {

                    //dialog.dismiss();// dismiss the alertbox after chose option
                    selected[0] = arr[item];
                }
            });
            alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
            alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
                //alertpassword(password,url);
            });
            AlertDialog alert = alt_bld.create();
            alert.show();

        }

        private void sendRequest(String int_msg, String matri_id) {
            common.showProgressRelativeLayout(progressBar);

            HashMap<String, String> param = new HashMap<>();
            param.put("interest_message", int_msg);
            param.put("receiver_id", matri_id);
            param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

            common.makePostRequestTime(AppConstants.photo_password_request, param, response -> {
                common.hideProgressRelativeLayout(progressBar);
                try {
                    JSONObject object = new JSONObject(response);
                    common.showToast(object.getString("errmessage"),llView);

                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),llView);
                }
            }, error -> {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
                }
            },llView);

        }


    }

}