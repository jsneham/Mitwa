package com.matriapp.mobile.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.R;
import com.matriapp.mobile.fragments.OTPRequestDialogWithFirebaseFragment;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class DashboardActivity extends AppCompatActivity {
    private Common common;
    private Toolbar toolbar;
    private SessionManager session;
    private int placeHolder, photoProtectPlaceHolder;
    BottomNavigationView navView;
    NavController navController;
    RelativeLayout container;
    private String notificationCount = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(this);
        common.setGradient(getWindow());

        setContentView(R.layout.activity_dashboard);



        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        // toolbar.setNavigationIcon(R.drawable.jmj_logo);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }


        container = findViewById(R.id.container);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_matches, R.id.navigation_interest, R.id.navigation_message, R.id.navigation_account, R.id.navigation_currentPlan)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);



            if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.placeholder;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.placeholder;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TAG", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("TAG", token);
                    }
                });

//        BottomNavigationViewHelper.disableShiftMode(navView);
    }
//
//
//    private void setAccountCreationPopup() {
//
//        try {
//            View view=getLayoutInflater().inflate(R.layout.account_creation_popup,null,false);
//
//            PopupWindow popupWindow=new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
//            popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
//
//            view.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popupWindow.dismiss();
//                }
//            });
//
//            view.findViewById(R.id.btnSettings).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    popupWindow.dismiss();
//                    Intent in= new Intent(getApplicationContext(), AccountSettingsActivity.class);
//                    startActivity(in);
//
////                    navController.navigate(R.id.navigation_account);
////                    FragmentManager fragmentManager = getSupportFragmentManager();
////                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
////                    fragmentTransaction.replace(R.id.nav_host_fragment, new SettingFragment());
////                    fragmentTransaction.commit();
//
//                }
//            });
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);

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
    protected void onResume() {
        super.onResume();
        getMyprofile();
    }

    private void getCurrentPlan() {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
//                MyApplication.setPlan(object.getBoolean("is_show"));
                if (object.has("plan_expired") && !object.isNull("plan_expired")) {
                    MyApplication.setPlan(common.isPlanExpired(object.getString("plan_expired")));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            //  pd.dismiss();
        }, error -> {

        },container);
    }

    private void getMyprofile() {
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            try {
                JSONObject object = new JSONObject(response);
                JSONObject data = object.getJSONObject("data");
                if(data.has("notification_message_count") &&  data.getString("notification_message_count").length() > 0) {
                    notificationCount = data.getString("notification_message_count");
                    invalidateOptionsMenu();
                }

                session.setUserData("full_mobile",data.getString("mobile"));                if(!ApplicationData.isOTPProcess && data.has("mobile_verify_status")
                        && data.getString("mobile_verify_status").equalsIgnoreCase("No")) {
                    ApplicationData.isOTPProcess = true;
                    OTPRequestDialogWithFirebaseFragment dialogFragment = OTPRequestDialogWithFirebaseFragment.newInstance();
                    dialogFragment.show(getSupportFragmentManager(), "OTP Dialog");
                }

                getCurrentPlan();

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),container);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),container);
            }
        },container);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_search) {
            Intent in= new Intent(this, SearchActivity.class);
            startActivity(in);

        }
        else  if (id == R.id.navigation_settings) {
            Intent in= new Intent(this, AccountSettingsActivity.class);
            startActivity(in);
        }

        else  if (id == R.id.action_notifications) {
            Intent in= new Intent(this, NotificationListActivity.class);
            startActivity(in);
        }
else  if (id == R.id.navigation_account) {
            Intent in= new Intent(this, DashboardActivityCopy.class);
            startActivity(in);
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

    public void hideToolbar1(boolean flag) {
        try {
            if(flag)  getSupportActionBar().show();
            else getSupportActionBar().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void conformLogout() {
        AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
        alert.setMessage("Are you sure you want logout from this app?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> session.logoutUser());
        alert.setNegativeButton("No", null);
        alert.show();
    }

    private void setStatusColor() {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            getWindow().setStatusBarColor(0x66000000);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(0x66000000);
        }
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }


}
