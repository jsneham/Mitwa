package com.matriapp.mobile.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViewMyProfileActivityBackup extends AppCompatActivity implements View.OnClickListener, TabHost.OnTabChangeListener, ItemClickListener {
    private final String TAB_FIRST = "first";
    private final String TAB_SECOND = "second";
    private final int TAB_FIRST_POSITION = 0;
    private final int TAB_SECOND_POSITION = 1;

    private Common common;
    private SessionManager session;

    private TabHost host;
    private int deviceWidth = 0;

    private RelativeLayout loader;

    private TextView tv_pro_per;
    private ImageView imgProfile, imgEdit;
    private RecyclerView myProfileRecyclerView, preferenceProfileRecyclerView;

    private List<ViewProfileSectionBean> myProfileDataList = new ArrayList<>();
    private List<ViewProfileSectionBean> preferenceProfileDataList = new ArrayList<>();

    private JSONArray photo_arr;
    private ProgressBar circularProgressbar;
    private int placeHolder;

    private NestedScrollView scrollView;
    private RelativeLayout llProfileCreate;
    private TextView txtFocus,tvTitle;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_profile_backup);

         toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        common = new Common(this);
        common.setGradient(getWindow());
        session = new SessionManager(this);

        host = findViewById(R.id.tabHost);
        host.setup();

        deviceWidth = Common.getDisplayWidth(this);

        TabHost.TabSpec spec = host.newTabSpec(TAB_FIRST);
        spec.setContent(R.id.tab1);
        spec.setIndicator(" EDIT PROFILE ");
        host.addTab(spec);

        spec = host.newTabSpec(TAB_SECOND);
        spec.setContent(R.id.tab2);
        spec.setIndicator(" EDIT PREFERENCES ");
        host.addTab(spec);

        host.setCurrentTab(TAB_FIRST_POSITION);
        host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
        host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);

        imgProfile = findViewById(R.id.imgProfile);
        imgEdit = findViewById(R.id.imgEdit);
        myProfileRecyclerView = findViewById(R.id.myProfileRecyclerView);
        preferenceProfileRecyclerView = findViewById(R.id.preferenceProfileRecyclerView);
        llProfileCreate = findViewById(R.id.llProfileCreate);

        scrollView = findViewById(R.id.scrollView);
        tvTitle = findViewById(R.id.tvTitle);
        txtFocus = findViewById(R.id.txtFocus);
        loader = findViewById(R.id.loader);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }
        imgProfile.setImageResource(placeHolder);

        for (int i = 0; i < host.getTabWidget().getChildCount(); i++) {
            TextView tv = host.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
//            common.setDrawableLeftTextViewLeft(R.drawable.user_fill_pink, tv);
            tv.setTextColor(Color.parseColor("#2b2767"));
        }


        host.setOnTabChangedListener(this);

        tv_pro_per = findViewById(R.id.tv_pro_per);

        circularProgressbar = findViewById(R.id.circularProgressbar);

        imgProfile.setOnClickListener(this);
        imgEdit.setOnClickListener(this);

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

                    String name = data.getString("username");
                    tvTitle.setText(name);
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
//                        (new Common(this)).setImageMyProfile(data.getString("photo1"),imgProfile);
                        Picasso.get().load(data.getString("photo1"))
                                .placeholder(R.drawable.placeholder)
                                .error(R.drawable.placeholder)
                                .resize(deviceWidth, deviceWidth)
                                .centerInside()
                                .into(imgProfile);
                    }
                    
                    circularProgressbar.setProgress(dataObject.get("percentage").getAsInt());
                    tv_pro_per.setText(dataObject.get("percentage").getAsInt() + "%");

                    //TODO add my profile data dynamically
                    myProfileDataList = gson.fromJson(dataObject.getAsJsonArray("fileds"), new TypeToken<List<ViewProfileSectionBean>>() {
                    }.getType());
//                    myProfileDataList.get(0).setExpanded(true);
                    for(int i=0;i<myProfileDataList.size();i++){
                        myProfileDataList.get(i).setExpanded(true);
                    }
                    //Remove photo url list
                    myProfileDataList.remove(myProfileDataList.size() - 1);
                    myProfileRecyclerView.setNestedScrollingEnabled(false);
                    SectionedExpandableLayoutHelper myProfileSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(this, myProfileRecyclerView, this, 2, true);
                    for (ViewProfileSectionBean viewProfileSectionBean : myProfileDataList) {
                        myProfileSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
                    }
                    myProfileSectionedExpandableLayoutHelper.notifyDataSetChanged();
                    //TODO add my profile data dynamically

                    //TODO add preference data dynamically
                    preferenceProfileRecyclerView.setNestedScrollingEnabled(false);
                    preferenceProfileDataList = gson.fromJson(dataObject.getAsJsonArray("partners_field"), new TypeToken<List<ViewProfileSectionBean>>() {
                    }.getType());
//                    preferenceProfileDataList.get(0).setExpanded(true);
                    for(int i=0;i<preferenceProfileDataList.size();i++){
                        preferenceProfileDataList.get(i).setExpanded(true);
                    }
                    SectionedExpandableLayoutHelper preferenceSectionedExpandableLayoutHelper = new SectionedExpandableLayoutHelper(this, preferenceProfileRecyclerView, this, 2, true);
                    for (ViewProfileSectionBean viewProfileSectionBean : preferenceProfileDataList) {
                        preferenceSectionedExpandableLayoutHelper.addSection(viewProfileSectionBean, viewProfileSectionBean.getViewProfileFieldList());
                    }
                    preferenceSectionedExpandableLayoutHelper.notifyDataSetChanged();
                    //TODO end add preference data dynamically
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llProfileCreate);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgProfile:
                if (photo_arr.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), GallaryNewActivity.class);
                    intent.putExtra("imagePosition", 0);
                    intent.putExtra("imageArray", photo_arr.toString());
                    startActivity(intent);
                }
                break;
            case R.id.imgEdit:
                Intent i = new Intent(this, ManagePhotosActivity.class);
                startActivityForResult(i, 7);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 7) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                String tabid = data.getStringExtra("tabid");
                if (result.equals("reload")) {
                    getMyProfile();
                }
                if (tabid.equals("my")) {
                    host.setCurrentTab(TAB_FIRST_POSITION);
                    host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
                    host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
                } else {
                    host.setCurrentTab(TAB_SECOND_POSITION);
                    host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
                    host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tab_selector);
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
                txtFocus.requestFocus();
                int height = (section.getViewProfileFieldList().size() / 2) * Common.convertDpToPixels(100, this);
                scrollView.scrollTo(0, scrollView.getBottom() + height);
                //userProfileRecyclerView.smoothScrollToPosition(userProfileRecyclerView.getAdapter().getItemCount()-1);
            }, 300);
        } else {
            txtFocus.clearFocus();
        }
    }

    private void gotoEdit(String tag) {
        Intent i = new Intent(ViewMyProfileActivityBackup.this, EditProfileActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }

    private void gotoEditPref(String tag) {
        Intent i = new Intent(ViewMyProfileActivityBackup.this, EditPreferenceActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
            case TAB_FIRST:
                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);

                TextView tv = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
                TextView tv1 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);


                setTextViewDrawableColor(tv,R.color.colorAccent);
                setTextViewDrawableColor(tv1,R.color.colorAccent);
                break;
            case TAB_SECOND:
                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tab_selector);

                TextView tv2 = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
                TextView tv3 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);


                setTextViewDrawableColor(tv2,R.color.colorAccent);
                setTextViewDrawableColor(tv3,R.color.colorAccent);
                break;

        }
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.preview, menu);
        MenuItem item = menu.findItem(R.id.view);
        MenuItemCompat.setActionView(item, R.layout.custom_action_bar_view);
        LinearLayout rootView = (LinearLayout) MenuItemCompat.getActionView(item);;

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToActivity();
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.onepage) {
            Intent i = new Intent(this, OnePageProfileActivity.class);
//            i.putExtra("pageTag", "Manage Photos");
            startActivity(i);

        }

        return super.onOptionsItemSelected(item);
    }


    private void goToActivity() {
        Intent i = new Intent(this, PreviewProfileActivity.class);
        i.putExtra("pageTag", "Manage Photos");
        startActivity(i);
    }
}
