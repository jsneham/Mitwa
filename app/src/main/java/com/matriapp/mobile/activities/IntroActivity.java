package com.matriapp.mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import me.relex.circleindicator.CircleIndicator;

public class IntroActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private RelativeLayout container;

    private int[] layouts;
    private MyViewPagerAdapter myViewPagerAdapter;
    private SessionManager session;
    private Common common;
    private TextView btnSkip, btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        session = new SessionManager(this);
        common = new Common(this);

        container = findViewById(R.id.container);
        btnSkip = findViewById(R.id.btnSkip);
        btnNext = findViewById(R.id.btnNext);

        viewPager = findViewById(R.id.pager);

        layouts = new int[]{
                R.layout.slider_slide_1,
                R.layout.slider_slide_2,
                R.layout.slider_slide_3,
                R.layout.slider_slide_4,
                R.layout.slider_slide_5,
                R.layout.slider_slide_6,
                R.layout.slider_slide_7};

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);

        CircleIndicator indicator = findViewById(R.id.indicator);
        indicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 6) {
                    btnNext.setText("Finish");
                } else {
                    btnNext.setText("Next");
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        btnNext.setOnClickListener(v -> {
            if (btnNext.getText().toString().equalsIgnoreCase("Finish")) {
                session.setIntroDone();
                startActivity(new Intent(IntroActivity.this, NewWelcomeActivity.class));
//                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            } else {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }

        });
        btnSkip.setOnClickListener(view -> {
            session.setIntroDone();
//            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            startActivity(new Intent(IntroActivity.this, NewWelcomeActivity.class));
            finish();
        });
        getList();
    }

    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!", container);
            return;
        }

        common.makePostRequest(AppConstants.common_list, new HashMap<String, String>(), response -> {
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                MyApplication.setSpinData(object);
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),container);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),container);
            }
        },container);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
