package com.matriapp.mobile.activities;

import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.matriapp.mobile.BuildConfig;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.MenuListAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.fragments.DashboardFragment;
import com.matriapp.mobile.fragments.OTPRequestDialogWithFirebaseFragment;
import com.matriapp.mobile.model.MenuChildBean;
import com.matriapp.mobile.model.MenuGroupBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DashboardActivityCopy extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tv_edit_profile, tv_name, tv_matri_id;
    private Common common;
    private SessionManager session;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Button btn_member;
    boolean doubleBackToExitPressedOnce = false;
    private RelativeLayout loader;
    private ImageView imgProfile;
    private int placeHolder, photoProtectPlaceHolder;

    private ExpandableListView drawerExpandableListView;
    private List<MenuGroupBean> drawerListData;
    private List<MenuGroupBean> menuList = new ArrayList<>();
    private String notificationCount = "";

    private DashboardFragment dashboardFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_deshboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        common = new Common(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photo_protected;
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photo_protected;
            placeHolder = R.drawable.male;
        }

        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        loader = findViewById(R.id.loader);
        tv_edit_profile = headerView.findViewById(R.id.tv_edit_profile);
        tv_name = headerView.findViewById(R.id.tv_name);
       // tv_matri_id = headerView.findViewById(R.id.tv_matri_id);
        imgProfile = headerView.findViewById(R.id.img_profile);
        common.setDrawableLeftTextView(R.drawable.eye_white, tv_edit_profile);
        btn_member = findViewById(R.id.btn_member);

        initMenu();

        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        btn_member.setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivityCopy.this, CurrentPlanActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });

        tv_edit_profile.setOnClickListener(view -> {
            startActivity(new Intent(DashboardActivityCopy.this, ViewMyProfileActivity.class));
            drawer.closeDrawer(GravityCompat.START);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void getCurrentPlan() {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, response -> {
            AppDebugLog.print("plan response : " + response);
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                if (object.has("plan_expired") && !object.isNull("plan_expired")) {
                    MyApplication.setPlan(common.isPlanExpired(object.getString("plan_expired")));
                }

            } catch (JSONException e) {
                common.hideProgressRelativeLayout(loader);
                e.printStackTrace();
            }
            //  pd.dismiss();
        }, error -> {
            common.hideProgressRelativeLayout(loader);
        },viewPager);
    }

    public void setMyProfile(JSONObject data) {
        try {
            tv_name.setText(data.getString("username") + " (" + data.getString("matri_id") + ")");
            if (!data.getString("photo1").equals(""))
                Picasso.get().load(data.getString("photo1"))
                        .placeholder(placeHolder)
                        .error(placeHolder)
                        .fit()
                        .centerCrop(Gravity.TOP | Gravity.CENTER_HORIZONTAL)
                        .into(imgProfile);
            else {
                imgProfile.setImageResource(placeHolder);
            }

            if(data.has("notification_message_count") &&  data.getString("notification_message_count").length() > 0) {
                notificationCount = data.getString("notification_message_count");
                invalidateOptionsMenu();
            }

            session.setUserData("full_mobile",data.getString("mobile"));
            if(!ApplicationData.isOTPProcess && data.has("mobile_verify_status")
                    && data.getString("mobile_verify_status").equalsIgnoreCase("No")) {
                ApplicationData.isOTPProcess = true;
                OTPRequestDialogWithFirebaseFragment dialogFragment = OTPRequestDialogWithFirebaseFragment.newInstance();
                dialogFragment.show(getSupportFragmentManager(), "OTP Dialog");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        dashboardFragment = new DashboardFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(dashboardFragment, "Dashboard");//DiscoverFragment
        //adapter.addFragment(new DiscoverFragment(), "Recently Join");//DiscoverFragment
        //adapter.addFragment(new RecommendationFragment(), "Recently Login");//RecommendationFragment
        viewPager.setAdapter(adapter);
    }

    private void initMenu() {
        drawerExpandableListView = findViewById(R.id.list_menu);
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);

        Gson gson = new GsonBuilder().setDateFormat(AppConstants.GSONDateTimeFormat).create();
        menuList.clear();
        String jsonData = session.getDrawerMenuArrayObject();
        if (jsonData != null) {
            JsonObject jsonObject = new JsonParser().parse(jsonData).getAsJsonObject();
            menuList = gson.fromJson(jsonObject.getAsJsonArray("menu_arr"), new TypeToken<List<MenuGroupBean>>() {
            }.getType());
            AppDebugLog.print("menu_arr : " + menuList.size());
        } else {
            JsonObject jsonObject = new JsonParser().parse(AppConstants.DRAWER_MENU_DATA).getAsJsonObject();
            menuList = gson.fromJson(jsonObject.getAsJsonArray("menu_arr"), new TypeToken<List<MenuGroupBean>>() {
            }.getType());
        }

        drawerListData = menuList; //new ArrayList<>();
        AppDebugLog.print("drawerListData size : " + drawerListData.size());

        MenuListAdapter adapter = new MenuListAdapter(this, menuList);
        drawerExpandableListView.setAdapter(adapter);

        drawerExpandableListView.setOnChildClickListener((expandableListView, view, groupPosition, childPosition, l) -> {
            MenuChildBean menuChildBean = menuList.get(groupPosition).getDrawerChildList().get(childPosition);
            try {
                AppDebugLog.print("Group Menu Action : " + ("com.matriapp.mobile.activities." + menuChildBean.getSubMenuAction()));
                Class<?> c = Class.forName("com.matriapp.mobile.activities." + menuChildBean.getSubMenuAction());
                Intent intent = new Intent(this, c);
                if (menuChildBean.getSubmenuTag() != null && menuChildBean.getSubmenuTag().length() > 0) {
                    intent.putExtra(AppConstants.KEY_INTENT, menuChildBean.getSubmenuTag());
                }
                if (menuChildBean.getSubMenuTitle() != null && menuChildBean.getSubMenuTitle().length() > 0) {
                    intent.putExtra("sub_menu_title", menuChildBean.getSubMenuTitle());
                }
                startActivity(intent);
            } catch (ClassNotFoundException ignored) {
                ignored.printStackTrace();
                AppDebugLog.print("Activity not found");
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        drawerExpandableListView.setOnGroupClickListener((expandableListView, view, groupPosition, l) -> {
            MenuGroupBean groupBean = menuList.get(groupPosition);
            if (groupBean.getMenuAction() != null && groupBean.getMenuAction().length() > 0) {
                if (groupBean.getMenuAction().equalsIgnoreCase("Dashboard")) {
                    //nothing
                } else if (groupBean.getMenuAction().equalsIgnoreCase("Logout")) {
                    conformLogout();
                } else if (groupBean.getMenuAction().equalsIgnoreCase("ShareApp")) {
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                } else {
                    try {
                        AppDebugLog.print("Group Menu Action : " + ("com.matriapp.mobile.activities." + groupBean.getMenuAction()));
                        Class<?> c = Class.forName("com.matriapp.mobile.activities." + groupBean.getMenuAction());
                        Intent intent = new Intent(this, c);
                        startActivity(intent);
                    } catch (ClassNotFoundException ignored) {
                        ignored.printStackTrace();
                        AppDebugLog.print("Activity not found");
                    }
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            } else {
                return false;
            }
        });
    }

    private void conformLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivityCopy.this);
        alert.setTitle("Logout");
        alert.setMessage("Are you sure you want logout from this app?");
        alert.setPositiveButton("Logout", (dialogInterface, i) -> session.logoutUser());
        alert.setNegativeButton("Cancel", null);
        alert.show();
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

    @Override
    public void onBackPressed() {
        finish();
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            if (doubleBackToExitPressedOnce) {
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
//                startActivity(intent);
//                finish();
//                System.exit(0);
//                return;
//            }
//
//            this.doubleBackToExitPressedOnce = true;
//            common.showToast(getApplicationContext(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
//        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notifications, menu);

        MenuItem itemNotification = menu.findItem(R.id.action_notifications);
        LayerDrawable icon = (LayerDrawable) itemNotification.getIcon();
        if(notificationCount.length() > 0) {
            Common.setBadgeCount(this, icon, notificationCount);
        }else{
            Common.setBadgeCount(this, icon, "0");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            startActivity(new Intent(this, NotificationListActivity.class));
        }else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, SearchActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }



    public void setToolbarTitle(String title) {
        try {
            getSupportActionBar().setTitle(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
