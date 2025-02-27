package com.matriapp.mobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class CustomHeightAdapter extends RecyclerView.Adapter<CustomHeightAdapter.ViewHolder> implements Filterable {
    List<KeyPairBoolData> arrayList;
    private List<KeyPairBoolData> mOriginalValues;
    private int selectedItemPosition = -1;
    private boolean selectedItem = false;
    private String tag = "STEP_H";
    private Context context;
    private ListItemClickListener listItemClickListener;


    public interface ListItemClickListener {
        void onItemsClick(KeyPairBoolData Crop, int position, String tag);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView alertTextView, label;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            alertTextView = view.findViewById(R.id.alertTextView);
            label = view.findViewById(R.id.label);
            cardView = view.findViewById(R.id.cardView);

        }


    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView.
     */
    public CustomHeightAdapter(List<KeyPairBoolData> dataSet, String tag, ListItemClickListener listItemClickListener, Context context) {
        mOriginalValues = dataSet;
        this.arrayList = dataSet;
        this.tag = tag;
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomHeightAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_listview_single_r, viewGroup, false);

        return new CustomHeightAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomHeightAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        KeyPairBoolData sm = arrayList.get(position);
        if (tag.equals("STEP_FT")) {
            viewHolder.label.setVisibility(View.VISIBLE);
            if (sm.getName().trim().equals("Separate Family"))
                viewHolder.label.setText(R.string.separete_family);
            else viewHolder.label.setText(R.string.joint_family);

        } else viewHolder.label.setVisibility(View.GONE);

        viewHolder.alertTextView.setText(sm.getName().trim());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedItem = true;
                selectedItemPosition = position;
                listItemClickListener.onItemsClick(sm, position, tag);
                notifyDataSetChanged();
            }
        });
        if (selectedItemPosition == position) {
            if (selectedItem) {
                viewHolder.cardView.setBackgroundColor(context.getColor(R.color.colorAccent));
                viewHolder.alertTextView.setTextColor(context.getColor(R.color.white));
                viewHolder.label.setTextColor(context.getColor(R.color.white));
            }
        } else {
            viewHolder.cardView.setBackgroundColor(context.getColor(R.color.registration_background2));
            viewHolder.alertTextView.setTextColor(context.getColor(R.color.default_small_text_title1));
            viewHolder.label.setTextColor(context.getColor(R.color.default_small_text_title1));

        }
//        viewHolder.alertTextView.setTextColor(context.getColor(R.color.default_small_text_title1));
//        viewHolder.label.setTextColor(context.getColor(R.color.default_small_text_title1));
//            viewHolder.cardView.setBackgroundColor(Color.parseColor("#FAFAFA"));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if (arrayList == null) {
            return 0;
        }
        return arrayList.size();
    }


    @Override
    public Filter getFilter() {
        return new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                arrayList = (List<KeyPairBoolData>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                List<KeyPairBoolData> FilteredArrList = new ArrayList<>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<>(arrayList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        Log.i("TAG", "Filter : " + mOriginalValues.get(i).getName() + " -> " + mOriginalValues.get(i).isSelected());
                        String data = mOriginalValues.get(i).getName();
                        if (data.toLowerCase().contains(constraint.toString())) {
                            FilteredArrList.add(mOriginalValues.get(i));
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
    }
}
