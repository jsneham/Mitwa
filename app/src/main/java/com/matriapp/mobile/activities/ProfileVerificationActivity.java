package com.matriapp.mobile.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.matriapp.mobile.R;
import com.matriapp.mobile.fragments.ProfileVerification.EducationVerificationFragment;
import com.matriapp.mobile.fragments.ProfileVerification.GovIdVerificationFragment;
import com.matriapp.mobile.fragments.ProfileVerification.MobileVerificationFragment;
import com.matriapp.mobile.fragments.ProfileVerification.ProfilePhotoVerificationFragment;
import com.matriapp.mobile.fragments.ProfileVerification.SalaryVerificationFragment;
import com.matriapp.mobile.utility.Common;

public class ProfileVerificationActivity extends AppCompatActivity {

    private Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_verification);

        common = new Common(this);
        common.setGradient(getWindow());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile Verification");
        toolbar.setNavigationOnClickListener(v -> finish());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        // Set up the ViewPager with the adapter
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Attach TabLayout to ViewPager2
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    // Set tab titles here
                    switch (position) {
//                        case 0:
//                            tab.setText("Photo");
//                            break;
                        case 0:
                            tab.setText("Profile Photo");
                            break;
                        case 1:
                            tab.setText("Mobile");
                            break;
//                        case 2:
//                            tab.setText("Email");
//                            break;
                        case 2:
                            tab.setText("Govt. ID");
                            break;
                        case 3:
                            tab.setText("Education");
                            break;
                        case 4:
                            tab.setText("Salary");
                            break;
                    }
                }
        ).attach();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.help_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.help) {
            common.callWhatsApp(this);
            return true;

        }
        return onOptionsItemSelected(item);
    }

    class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Return the corresponding fragment for each tab
            switch (position) {
                case 0:
//                    return new PhotoVerificationFragment();
                    return new ProfilePhotoVerificationFragment();
                case 1:
                    return new MobileVerificationFragment();
//                case 2:
//                    return new EmailVerificationFragment();

                case 2:
                    return new GovIdVerificationFragment();
                case 3:
                    return new EducationVerificationFragment();
                case 4:
                    return new SalaryVerificationFragment();
                default:
                    return new MobileVerificationFragment(); // Default tab
            }
        }

        @Override
        public int getItemCount() {
            return 5; // Number of tabs
        }
    }
}





