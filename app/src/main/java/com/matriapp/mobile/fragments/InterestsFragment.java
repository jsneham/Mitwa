package com.matriapp.mobile.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.DashboardActivity;

import java.util.ArrayList;
import java.util.List;


public class InterestsFragment extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int position=0;

    public InterestsFragment() {

        this.setHasOptionsMenu(true);
    }

    public InterestsFragment(int i) {

    position=i;
    }
//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.home, menu);
//        final MenuItem action_search = menu.findItem(R.id.action_search);
//        action_search.setVisible(false);
//        super.onCreateOptionsMenu(menu, inflater);
//    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_interest, container, false);
        ((DashboardActivity) getActivity()).setToolbarTitle("Interests");
//        ((DashboardActivity) getActivity()).hideToolbar(true);

        viewPager = view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        viewPager.setOffscreenPageLimit(1);
        tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new InterestReceivedFragment(), "Received");
        adapter.addFragment(new InterestSentFragment(), "Sent");
        adapter.addFragment(new ShortlistFragment(), "Shortlisted");
        adapter.addFragment(new PhotoRequestFragment(), "Photo Request");
//        adapter.addFragment(new LikedProfilesFragment(), "Liked Profiles");
        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(position);
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