package com.matriapp.mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.matriapp.mobile.R;

import java.util.ArrayList;
import java.util.List;


public class AllMatchesViewFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_all_match_view, container, false);
        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = view.findViewById(R.id.tabs);
        //  setTab();
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }



    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(AllMatchFragment.newInstance("Never Married"), "Never Married");
        adapter.addFragment(AllMatchFragment.newInstance("Widowed"), "Widowed");
        adapter.addFragment(AllMatchFragment.newInstance("Divorced"), "Divorced");
        adapter.addFragment(AllMatchFragment.newInstance("Separated"), "Separated");
        adapter.addFragment(AllMatchFragment.newInstance("Annulled"), "Annulled");
//        adapter.addFragment(new AllMatchFragment(""), "All");
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);

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