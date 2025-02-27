package com.matriapp.mobile.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.AccountSettingsActivity;
import com.matriapp.mobile.activities.BlockListActivity;
import com.matriapp.mobile.activities.ChangePasswordActivity;
import com.matriapp.mobile.activities.CurrentPlanActivity;
import com.matriapp.mobile.activities.DashboardActivity;
import com.matriapp.mobile.activities.DeleteProfileActivity;
import com.matriapp.mobile.activities.EditPreferenceActivity;
import com.matriapp.mobile.activities.LoginActivity;
import com.matriapp.mobile.activities.LoginWithOtpActivityWithFirebase;
import com.matriapp.mobile.activities.ManageAccountActivity;
import com.matriapp.mobile.activities.ManagePhotosActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.ProfileVerificationActivity;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.activities.ViewMyProfileActivity;
import com.matriapp.mobile.activities.ViewedContactActivity;
import com.matriapp.mobile.custom.RecyclerItemClickListener;
import com.matriapp.mobile.model.SettingsMenu;
import com.matriapp.mobile.utility.AppDebugLog;

import java.util.ArrayList;


public class SystemSettingFragment extends Fragment {


    private View view;
    private String tag;
    private RecyclerView rvMenu;
    private CustomAdapter customAdapter;
    private ArrayList<SettingsMenu> settingsMenus;

    public SystemSettingFragment(String tag) {
        this.tag = tag;
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_settings_system, container, false);

        getData();

        setRecylerView();

        recyclerItemClick();
        return view;
    }


    private void setRecylerView() {
        // BEGIN_INCLUDE(initializeRecyclerView)
        rvMenu = (RecyclerView) view.findViewById(R.id.rvMenu);

        // LinearLayoutManager is used here, this will layout the elements in a similar fashion
        // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
        // elements are laid out.
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rvMenu.setLayoutManager(mLayoutManager);
//        rvMenu.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
//        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.message_divider));
//        rvMenu.addItemDecoration(itemDecoration);

        customAdapter = new CustomAdapter(settingsMenus);
        // Set CustomAdapter as the adapter for RecyclerView.
        rvMenu.setAdapter(customAdapter);
        // END_INCLUDE(initializeRecyclerView)
    }

    private void getData() {
        settingsMenus = new ArrayList<>();
//        ic_viewprofile_main
        settingsMenus.add(new SettingsMenu(R.drawable.profileverification_icon, "Profile Verification", "Get Profile Verified for better responses"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_membershipbadge, "Membership", "View your current plan, & other premium plans"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_partnerpreference__1_, "Update Profile & Partner Preferences", "Preview & Update profile, set partner preferences"));
        settingsMenus.add(new SettingsMenu(R.drawable.contactviewed_squarish, "Contact Viewed", "Contact info viewed by you & who viewed yours"));
//        settingsMenus.add(new SettingsMenu(R.drawable.ic_partnerpreference__1_, "Update Partner Preferences", "Set your preferences for your partner search"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_contactsettings, "Contact Privacy Settings", "Manage how your contact info will be seen"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_managephotos, "Manage Photos", "Manage your profile photos and horoscope image"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_photoprivacysettings, "Photo Privacy Settings", "Manage which members can see your photos"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_block, "Blocked Profiles", "Control which members you want to block"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_changepassword, "Change Password", "Set a new password for your account"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_reportmisuse, "Report Misuse", "Report something inappropriate you noticed"));
//        settingsMenus.add(new SettingsMenu(R.drawable.ic_membershipbadge, "Membership", "View your current plan, & other premium plans"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_deleteaccount, "Delete Account", "Delete your account permanently from here"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_logout, "LOGOUT", "Logout from this device"));
    }


    private void recyclerItemClick() {
        rvMenu.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), rvMenu, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        switch (settingsMenus.get(position).getTitle()) {
                            case "Update Profile & Partner Preferences":
                                gotoActivity("Update Profile & Partner Preferences", ViewMyProfileActivity.class);

                                break;
                            case "Update Partner Preferences":
                                gotoActivity(EditPreferenceActivity.KEY_BASIC, EditPreferenceActivity.class);
                                break;
                            case "Contact Privacy Settings":
                                gotoActivity("Contact Privacy", ManageAccountActivity.class);
                                break;
                            case "Manage Photos":
                                gotoActivityPhotos("Manage Photos", ManagePhotosActivity.class,0);
                                break;
                            case "Photo Privacy Settings":
                                gotoActivityPhotos("Photo Privacy", ManagePhotosActivity.class,2);
                                break;
                            case "Blocked Profiles":
                                gotoActivity("Blocked Profiles", BlockListActivity.class);
                                break;
                            case "Change Password":
                                gotoActivity("Change Password", ChangePasswordActivity.class);
                                break;
                            case "Report Misuse":
                                gotoActivity("Report Missuse", ReportMissuseActivity.class);
                                break;
                            case "LOGOUT":
                                ((AccountSettingsActivity) getContext()).openLogout();
                                break;
                            case "Delete Account":
                                gotoActivity("Delete Profile", DeleteProfileActivity.class);
                                break;
                            case "Membership":
                                gotoActivity("Membership Plans", PlanListActivity.class);
                                break;

                            case "Contact Viewed":
                                gotoActivity("Contact Viewed", ViewedContactActivity.class);
                                break;

                            case "Profile Verification":
                                gotoActivity("Profile Verification", ProfileVerificationActivity.class);
                                break;
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    private void gotoActivity(String tag, Class activity) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra("pageTag", tag);
        i.putExtra("position", tag);
        startActivity(i);
    }

    private void gotoActivityPhotos(String tag, Class activity, int position) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra("pageTag", tag);
        i.putExtra("position", position);
        startActivity(i);
    }
}


class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<SettingsMenu> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvTitle, tvDetails;
        public ImageView ivIcon;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            tvDetails = view.findViewById(R.id.tvDetails);
            tvTitle = view.findViewById(R.id.tvTitle);
            ivIcon = view.findViewById(R.id.ivIcon);
        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public CustomAdapter(ArrayList<SettingsMenu> dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_setting_row, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        SettingsMenu sm = localDataSet.get(position);
        viewHolder.tvDetails.setText(sm.getDescription());
        viewHolder.tvTitle.setText(sm.getTitle());
        viewHolder.ivIcon.setImageResource(sm.getIcon());
//        Picasso.get().load(sm.getIcon()).into(viewHolder.ivIcon);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
