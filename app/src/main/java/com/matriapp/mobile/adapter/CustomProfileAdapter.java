package com.matriapp.mobile.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.R;

import com.matriapp.mobile.model.PartnerFields;

import java.util.List;

public class CustomProfileAdapter extends RecyclerView.Adapter<CustomProfileAdapter.ViewHolder>  {
    List<PartnerFields> arrayList;
    Context context;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView lblName,lblValue;
        public ImageView ivIcon;


        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            lblName = view.findViewById(R.id.lblName);
            lblValue = view.findViewById(R.id.lblValue);
            ivIcon = view.findViewById(R.id.ivIcon);


        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public CustomProfileAdapter(List<PartnerFields> dataSet,
                                Context context) {
        this.arrayList = dataSet;
        this.context = context;

    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_partner, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        PartnerFields sm= arrayList.get(position);
        viewHolder.lblName.setText(sm.getTitle().trim().replace("Looking For", "Marital Status"));

        String prefValue = "N/A";
        String value = sm.getValue().trim();
        if (value != null && value.length() > 0) {
            prefValue = value;
        } else {
            prefValue = "N/A";
            //continue;
        }
        viewHolder.lblValue.setText(prefValue);

//        if (sm.getType().equals("Yes")) {
//            viewHolder.ivIcon.setImageResource(R.drawable.ic_baseline_check_24);
//        }
        if (sm.getTitle().equals("Designation")) {
            viewHolder.lblValue.setVisibility(View.GONE);
            viewHolder.lblName.setVisibility(View.GONE);
            viewHolder.ivIcon.setVisibility(View.GONE);
        }
        viewHolder.ivIcon.setVisibility(View.GONE);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(arrayList==null){
            return 0;
        }
        return arrayList.size();
    }



}
