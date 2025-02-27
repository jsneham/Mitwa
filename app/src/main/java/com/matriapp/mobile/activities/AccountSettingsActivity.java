package com.matriapp.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.fragments.HelpFragment;
import com.matriapp.mobile.fragments.SystemSettingFragment;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountSettingsActivity extends AppCompatActivity {

    private TextView tvPlan, tv_name, tv_matri_id,tvProgressBar,tv_edit_profile;
    private LinearLayout llView;
    private Button tvPlansList;
    private ImageView imgProfile;
    private ProgressBar progressBar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private SessionManager session;
    private Common common;
    private Context context=this;
    private int placeHolder, photoProtectPlaceHolder;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(this);
        common.setGradient(getWindow());


        setContentView(R.layout.activity_account_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings & Support");
        toolbar.setNavigationIcon(R.drawable.ic_backarrow);
        toolbar.setNavigationOnClickListener(view -> {
            finish();
        });


        llView = findViewById(R.id.llView);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        init();
        session = new SessionManager(this);
       // common = new Common(this);


        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        }

        tv_edit_profile.setOnClickListener(view -> {
            startActivity(new Intent(this, ViewMyProfileActivity.class));


        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyprofile();
    }

    private void init() {
        tv_edit_profile = findViewById(R.id.tv_edit_profile);
        progressBar = findViewById(R.id.progressBar);
        tvProgressBar = findViewById(R.id.tvProgressBar);
        tvPlan = findViewById(R.id.tvPlan);
        tvPlansList = findViewById(R.id.tvPlansList);
        tv_name = findViewById(R.id.tv_name);
        tv_matri_id = findViewById(R.id.tvMaitId);
        imgProfile = findViewById(R.id.img_profile);

        tvPlansList.setOnClickListener(view -> {
            startActivity(new Intent(this, PlanListActivity.class));
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SystemSettingFragment("activity"), "System Settings");//RecommendationFragment
        adapter.addFragment(new HelpFragment(), "Help & Support");//RecommendationFragment
        viewPager.setAdapter(adapter);
    }


    private void getMyprofile() {
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(AppConstants.get_my_profile, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONObject data = object.getJSONObject("data");
//                percentage
                tv_name.setText(data.getString("username"));
                tv_matri_id.setText(data.getString("matri_id"));
                tvProgressBar.setText(data.getString("percentage")+ " % COMPLETED");
                progressBar.setProgress(Integer.parseInt(data.getString("percentage")));
                if (!data.getString("photo1").equals("")){
//                    Picasso.get().load(data.getString("photo1")).placeholder(placeHolder).error(placeHolder).into(imgProfile);
                    Picasso.get().load(data.getString("photo1"))
                            .placeholder(placeHolder)
                            .error(placeHolder)
                            .fit()
                            .centerCrop(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                            .into(imgProfile);
            }
                else {
                    imgProfile.setImageResource(placeHolder);
                }
                getCurrentPlan();

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {

            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        }, llView);
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
                if(!data.getString("plan_name").contains("free"))
                    tvPlansList.setBackground(context.getDrawable(R.drawable.ic_upgrademembership));
//                    tvPlansList.setBackground(context.getDrawable(R.drawable.ic_upgradetag));
//                    tvPlansList.setText("UPGRADE");
            } catch (JSONException e) {

                e.printStackTrace();
            }
            //  pd.dismiss();
        }, error -> {

        },llView);
    }

    public void conformLogout() {
        session.logoutUser();
       // ((DashboardActivity) context).conformLogout();

//        AlertDialog.Builder alert = new AlertDialog.Builder(AccountSettingsActivity.this);
//        alert.setMessage("Are you sure you want logout from this app?");
//        alert.setPositiveButton("Yes", (dialogInterface, i) -> session.logoutUser());
//        alert.setNegativeButton("No", null);
//        alert.show();

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




    public void openLogout() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomsheetView = getLayoutInflater().inflate(R.layout.logout_popup, null);
        bottomSheetDialog.setContentView(bottomsheetView);

        RelativeLayout tvMessage = bottomsheetView.findViewById(R.id.tvMessage);
        Button btnNo = bottomsheetView.findViewById(R.id.btnNo);
        Button btnYes = bottomsheetView.findViewById(R.id.btnYes);

        tvMessage.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

        });

        btnNo.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

        });


        btnYes.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

            conformLogout();
        });
        bottomSheetDialog.show();
    }

}