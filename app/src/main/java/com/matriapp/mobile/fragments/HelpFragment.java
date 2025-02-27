package com.matriapp.mobile.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.AllCmsActivity;
import com.matriapp.mobile.custom.RecyclerItemClickListener;
import com.matriapp.mobile.model.SettingsMenu;
import com.matriapp.mobile.utility.AppConstants;

import java.util.ArrayList;


public class HelpFragment extends Fragment {

    private View view;
    private RecyclerView rvMenu;
    private CustomAdapter customAdapter;
    private ArrayList<SettingsMenu> settingsMenus;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_help, container, false);
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
        settingsMenus.add(new SettingsMenu(R.drawable.ic_safety, "Stay Safe", "Safety tips, platform usage guidelines, etc"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_faqhelp, "FAQs", "Frequently asked questions can be found here"));
       // settingsMenus.add(new SettingsMenu(R.drawable.ic_getintouchemail, "Get In Touch", "Email your feedback or suggestion"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_aboutusinfo, "About Us", "Know more about us"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_termsandconditions, "Terms & Conditions", "Terms of Service, Usage & other conditions"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_privacysettings, "Privacy Policy", "Data policy, consents & more"));
        settingsMenus.add(new SettingsMenu(R.drawable.ic_refundpolicy, "Refund Policy", "Know about the refund policy"));


    }

    private void recyclerItemClick() {
        rvMenu.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), rvMenu, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        switch (position) {
                            case 0:
                                gotoActivity("Safety Tips", AllCmsActivity.class);
                                break;
                            case 1:
                                gotoActivity("FAQs", AllCmsActivity.class);
                                break;
//                            case 2:
//                                gotoActivity("Get in touch", ContactUsActivity.class);
//                                break;

                            case 2:
                                gotoActivity("about", AllCmsActivity.class);
                                break;
                            case 3:
                                gotoActivity("term", AllCmsActivity.class);
                                break;
                            case 4:
                                gotoActivity("privacy", AllCmsActivity.class);
                                break;
                            case 5:
                                gotoActivity("refund", AllCmsActivity.class);
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
        i.putExtra(AppConstants.KEY_INTENT, tag);
        startActivity(i);
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
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return localDataSet.size();
        }
    }

}
