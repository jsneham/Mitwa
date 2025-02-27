package com.matriapp.mobile.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Response;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.PremiumPlanAdapter;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.fragments.PlanFragment;
import com.matriapp.mobile.model.PlanDatum;
import com.matriapp.mobile.model.PlanItem;
import com.matriapp.mobile.model.PremiumPlanBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlanListActivity extends AppCompatActivity implements PremiumPlanAdapter.ItemListener {
    private List<PlanItem> list = new ArrayList<>();
    private Button btn_payment;
    private Common common;
    private RelativeLayout loader,llView;
    private TextView tv_no_data;
    private String qrCodeListStr = "";
    private String bankDetailsListStr = "";

    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ArrayList<PremiumPlanBean> arrayListCategory = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_list_new);
        common = new Common(this);
        common.setGradient(getWindow());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Membership Plan");
        toolbar.setNavigationOnClickListener(v -> finish());

        llView = findViewById(R.id.llView);
        loader = findViewById(R.id.loader);
        btn_payment = findViewById(R.id.btn_id);
        tv_no_data = findViewById(R.id.tv_no_data);

        viewPager = findViewById(R.id.viewpager);
        tabLayout = findViewById(R.id.tabs);
        //  setTab();
        tabLayout.setupWithViewPager(viewPager);
        getPlanData();

        btn_payment.setOnClickListener(view -> {
            String plan_amount = "";
            JSONObject object = null;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).isIs_select()) {
                    plan_amount = list.get(i).getPrise();
                    object = list.get(i).getPlan_object();
                }

            }
            if (object == null) {
                common.showToast("Please Select Plan", llView);
                return;
            }
            if (plan_amount.equals("0")) {
                common.showToast("Please Contact To admin",llView);
                Intent i = new Intent(PlanListActivity.this, ContactUsActivity.class);
                i.putExtra("page_tag", "form");
                startActivity(i);
            } else {
                Intent i = new Intent(PlanListActivity.this, MakePaymentsActivity.class);
                i.putExtra("plan_data", object.toString());
                startActivity(i);
            }
        });
    }


    private void getPlanData() {
        common.showProgressRelativeLayout(loader);
        common.makePostRequest(AppConstants.plan_list, new HashMap<String, String>(), response -> {
            if (loader != null)
                common.hideProgressRelativeLayout(loader);
            Log.d("resp", response);

            try {
                JSONObject object = new JSONObject(response);

                Gson gson = new GsonBuilder().setDateFormat(AppConstants.GSONDateTimeFormat).create();

                JsonParser jsonParser = new JsonParser();
                JsonObject data = (JsonObject) jsonParser.parse(object.toString());

                if (object.getString("status").equals("success")) {
                    if (data.has("scan_pay") && data.get("scan_pay").isJsonArray()) {
                        qrCodeListStr = data.get("scan_pay").getAsJsonArray().toString();
                    }
                    if (data.has("offline_payment") && data.get("offline_payment").isJsonArray()) {
                        bankDetailsListStr = data.get("offline_payment").getAsJsonArray().toString();
                    }
                    if (data.get("plan_data").isJsonArray()) {
                        arrayListCategory = gson.fromJson(data.getAsJsonArray("plan_data"), new TypeToken<List<PremiumPlanBean>>() {
                        }.getType());

                        if (arrayListCategory.size() != 0) {
                            setupViewPager(arrayListCategory,qrCodeListStr,bankDetailsListStr);
                        } else {
                            tv_no_data.setVisibility(View.VISIBLE);
                        }
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {
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

    private void setupViewPager(ArrayList<PremiumPlanBean> arrayListCategory, String qrCodeListStr, String bankDetailsListStr) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for(int i=0; i<arrayListCategory.size();i++){
            PlanFragment fragment = PlanFragment.newInstance(arrayListCategory.get(i).getPlanData(), qrCodeListStr,
                    bankDetailsListStr);
            adapter.addFragment(fragment, arrayListCategory.get(i).getCategoryName());

        }

        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(2);
    }


    @Override
    public void itemClicked(PlanDatum premiumPlanBean, int position) {
        if (premiumPlanBean == null) {
            common.showToast("Please Select Plan", llView);
            return;
        }
        if (premiumPlanBean.getPlanAmount().equals("0")) {
            common.showToast("Please Contact To admin", llView);
            Intent i = new Intent(PlanListActivity.this, ContactUsActivity.class);
            i.putExtra("page_tag", "form");
            startActivity(i);
        } else {

            Gson gson = new Gson();
            String premiumPlanBeanStr = gson.toJson(premiumPlanBean);
            Intent i = new Intent(PlanListActivity.this, MakePaymentsActivity.class);
            i.putExtra("qr_list",qrCodeListStr);
            i.putExtra("bank_detail_list",bankDetailsListStr);
            i.putExtra("plan_data", premiumPlanBeanStr);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCurrentPlan();
    }

    private void getCurrentPlan() {

        SessionManager session = new SessionManager(this);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        common.makePostRequest(AppConstants.check_plan, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.has("plan_expired") && !object.isNull("plan_expired")) {
                        MyApplication.setPlan(common.isPlanExpired(object.getString("plan_expired")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    common.showToast(getString(R.string.err_msg_try_again_later),llView);
                }
                //pd.dismiss();
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    public void checkPlanHistory(View view) {
        startActivity(new Intent(PlanListActivity.this, CurrentPlanActivity.class));
    }

    public void onWhatsAppCall(View view) {

            String phoneNumber = getString(R.string.phone_number_help); // Phone number with country code
            Uri uri = Uri.parse(getString(R.string.whatsapp) + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);

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

}
