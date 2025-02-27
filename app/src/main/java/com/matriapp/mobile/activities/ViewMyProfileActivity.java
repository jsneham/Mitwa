package com.matriapp.mobile.activities;

import static java.lang.Math.abs;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.SectionedExpandableLayoutHelper;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.fragments.EditPreferenceFragment;
import com.matriapp.mobile.fragments.EditProfileFragment;
import com.matriapp.mobile.fragments.ProfileIViewedFragment;
import com.matriapp.mobile.fragments.ViewFragment;
import com.matriapp.mobile.fragments.ViewedMyProfileFragment;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.utility.AppConstants;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ViewMyProfileActivity extends AppCompatActivity implements View.OnClickListener, ItemClickListener {
    private final String TAB_FIRST = "first";
    private final String TAB_SECOND = "second";
    private final int TAB_FIRST_POSITION = 0;
    private final int TAB_SECOND_POSITION = 1;

    private Common common;
    private SessionManager session;



    private RelativeLayout loader;

    private TextView tv_pro_per;
    private ImageView imgProfile, imgEdit;
    private RecyclerView myProfileRecyclerView, preferenceProfileRecyclerView;

    private List<ViewProfileSectionBean> myProfileDataList = new ArrayList<>();
    private List<ViewProfileSectionBean> preferenceProfileDataList = new ArrayList<>();

    private JSONArray photo_arr;
    private ProgressBar circularProgressbar;
    private int placeHolder;

    private TextView txtFocus,tvTitle;
    private Toolbar toolbar;
    private int deviceWidth = 0;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private CoordinatorLayout llProfileCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_my_profile);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });

        common = new Common(this);
        common.setGradient(getWindow());
        deviceWidth = Common.getDisplayWidth(this);
        session = new SessionManager(this);
        imgProfile = findViewById(R.id.imgProfile);
        imgEdit = findViewById(R.id.imgEdit);

        tvTitle = findViewById(R.id.tvTitle);
//        txtFocus = findViewById(R.id.txtFocus);
//        loader = findViewById(R.id.loader);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }
        imgProfile.setImageResource(placeHolder);

        AppBarLayout appbar = findViewById(R.id.appbar);


        tv_pro_per = findViewById(R.id.tv_pro_per);

        circularProgressbar = findViewById(R.id.circularProgressbar);
        llProfileCreate = findViewById(R.id.llProfileCreate);

        imgProfile.setOnClickListener(this);
        imgEdit.setOnClickListener(this);

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        appbar.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (abs(verticalOffset) >= appbar.getTotalScrollRange()) {
//                collapsingToolbar.title = "Collapsed"
            } else {
//                collapsingToolbar.title = ""
            }
        });

        getMyProfile();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new EditProfileFragment(), "EDIT PROFILE");
        adapter.addFragment(new EditPreferenceFragment(), "EDIT PREFERENCES");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
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
                                .into(imgProfile);
//                                .resize(deviceWidth, deviceWidth)
//                                .centerInside()

                    }

                    circularProgressbar.setProgress(dataObject.get("percentage").getAsInt());
                    tv_pro_per.setText(dataObject.get("percentage").getAsInt() + "%");
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
//                scrollView.scrollTo(0, scrollView.getBottom() + height);
                //userProfileRecyclerView.smoothScrollToPosition(userProfileRecyclerView.getAdapter().getItemCount()-1);
            }, 300);
        } else {
            txtFocus.clearFocus();
        }
    }

    private void gotoEdit(String tag) {
        Intent i = new Intent(ViewMyProfileActivity.this, EditProfileActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }

    private void gotoEditPref(String tag) {
        Intent i = new Intent(ViewMyProfileActivity.this, EditPreferenceActivity.class);
        i.putExtra("pageTag", tag);
        startActivityForResult(i, 7);
    }


    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.preview, menu);
//        MenuItem item = menu.findItem(R.id.view);
//        MenuItemCompat.setActionView(item, R.layout.custom_action_bar_view);
//        LinearLayout rootView = (LinearLayout) MenuItemCompat.getActionView(item);;
//
//        rootView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goToActivity();
//            }
//        });
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.onepage) {
//            Intent i = new Intent(this, OnePageProfileActivity.class);
//            startActivity(i);
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    private void goToActivity() {
        Intent i = new Intent(this, PreviewProfileActivity.class);
        i.putExtra("pageTag", "Manage Photos");
        startActivity(i);
    }



    public void openManagePhotos(View view) {
        Intent i = new Intent(this, ManagePhotosActivity.class);
        startActivityForResult(i, 7);
    }

    public void goToPreviewProfile(View view) {
        Intent i = new Intent(this, PreviewProfileActivity.class);
        i.putExtra("pageTag", "Manage Photos");
        startActivity(i);
    }
    public void goToOnePager(View view) {
        if(!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
            Intent i = new Intent(this, OnePageProfileActivity.class);
            startActivity(i);
        }
        else premiumMemebrSheet();
    }

    public void premiumMemebrSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.premiummemebrsheet);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        Button btnUpgrade = bottomSheetDialog.findViewById(R.id.btnUpgrade);
        TextView tv = bottomSheetDialog.findViewById(R.id.tv);


        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        btnUpgrade.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(this, PlanListActivity.class));

        });
        bottomSheetDialog.show();
    }
}


class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
