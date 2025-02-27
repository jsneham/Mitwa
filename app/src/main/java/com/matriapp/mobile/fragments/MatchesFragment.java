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


public class MatchesFragment extends Fragment {

    private int position=0;
    private View view;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public MatchesFragment() {

        if(position==0) this.setHasOptionsMenu(true);
    }

    public MatchesFragment(int i) {
        if(i==1|| i==2 || i==3||i==4){
            position=i;
            this.setHasOptionsMenu(false);
        }
        else position=0;

    }

//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//        getActivity().getMenuInflater().inflate(R.menu.home, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }


//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_search) {
//            Intent in= new Intent(getContext(), SearchActivity.class);
//            startActivity(in);
//
//        }
//        else  if (id == R.id.navigation_settings) {
//            Intent in= new Intent(getContext(), AccountSettingsActivity.class);
//            startActivity(in);
//        }
//
//        else  if (id == R.id.navigation_account) {
//            Intent in= new Intent(getContext(), ViewMyProfileActivity.class);
//            startActivity(in);
//        }
//        return super.onOptionsItemSelected(item);
//    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_matches, container, false);
        ((DashboardActivity) getActivity()).setToolbarTitle("Matches");
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
        adapter.addFragment(new AllMatchesViewFragment(), "All");
        adapter.addFragment(new CustomMatchFragment(), getString(R.string.preferred));
        adapter.addFragment(new DiscoverFragment(), "Newly Joined");
        adapter.addFragment(new RecommendationFragment(), "Recently Logged In");
        adapter.addFragment(new ViewFragment(), getString(R.string.viewed));
        adapter.addFragment(new ContactViewFragment(), "Contact Viewed");
        adapter.addFragment(new SavedSearchFragment(), "Saved");
//        adapter.addFragment(new PremiumMatchViewFragment(), "Premium Matches");
        viewPager.setAdapter(adapter);
       if(position==1|| position==2 || position==3|| position==4) viewPager.setCurrentItem(position);
       else viewPager.setCurrentItem(1);
//        viewPager.setOffscreenPageLimit(4);

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