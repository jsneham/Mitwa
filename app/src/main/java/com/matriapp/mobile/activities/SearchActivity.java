package com.matriapp.mobile.activities;

import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;

import com.matriapp.mobile.fragments.SavedSearchFragment;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.fragments.AdvanceSearchFragment;
import com.matriapp.mobile.fragments.IdSearchFragment;
import com.matriapp.mobile.fragments.KeywordSearchFragment;
import com.matriapp.mobile.fragments.QuickSearchFragment;
import com.matriapp.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private Common common;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Search Profiles");
        toolbar.setNavigationOnClickListener(v -> finish());

        common = new Common(this);
        common.setGradient(getWindow());
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SavedSearchFragment(), "Saved");

//        adapter.addFragment(new QuickSearchFragment(), "Quick");
        adapter.addFragment(new AdvanceSearchFragment(), "Search");
        adapter.addFragment(new IdSearchFragment(), "By ID");
        adapter.addFragment(new KeywordSearchFragment(), "By Keyword");
        viewPager.setAdapter(adapter);
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
