package com.matriapp.mobile.adapter;

import static com.matriapp.mobile.utility.Common.showToast;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.Registration.RegistrationHoroscopeActivity;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomSpokenLaguagesAdapter extends RecyclerView.Adapter<CustomSpokenLaguagesAdapter.ViewHolder> implements Filterable {
    List<KeyPairBoolData> arrayList= new ArrayList<>();
    private List<KeyPairBoolData> mOriginalValues;
    private int selectedItemPosition = 0;
    private boolean selectedItem = false;
    private String tag = "STEP_H";
    private Context context;
    private Button btn;
    private String ragister_id,lang_ids = "";
    private ListItemClickListener listItemClickListener;

//    public CustomSpokenLaguagesAdapter(Context context, Button btn, String ragister_id) {
//        this.context = context;
//        this.btn = btn;
//        this.ragister_id = ragister_id;
//    }

    public void addItem(List<KeyPairBoolData> item) {
        arrayList.addAll(item);
    }

    public interface ListItemClickListener {
        void onItemsClick(String Crop, boolean isChecked);
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView label;
        public CheckBox alertTextView;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            alertTextView = view.findViewById(R.id.alertTextView);
            label = view.findViewById(R.id.label);
            cardView = view.findViewById(R.id.cardView);



        }

        void bind(KeyPairBoolData sm, int position) {
            // Set the text
            alertTextView.setText(sm.getName());
            if(sm.isSelected()) alertTextView.setChecked(true);
            else alertTextView.setChecked(false);

            // Listen to changes (i.e. when the user checks or unchecks the box)
            alertTextView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    // Invoke the callback
                    if(isChecked)  {
                        sm.setSelected(true);
                        lang_ids = lang_ids + sm.getId() + ",".trim();
                    }
                    else sm.setSelected(false);

                    if(listItemClickListener != null) listItemClickListener.onItemsClick(sm.getId(), isChecked);
                }
            });
        }


    }


    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.alertTextView.setOnCheckedChangeListener(null);
        super.onViewRecycled(holder);
    }

    public CustomSpokenLaguagesAdapter(List<KeyPairBoolData> dataSet, String tag, Context context, ListItemClickListener listItemClickListener) {
        mOriginalValues = dataSet;
        this.arrayList = dataSet;
        this.tag = tag;
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CustomSpokenLaguagesAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_listview_multiple_r, viewGroup, false);

        return new CustomSpokenLaguagesAdapter.ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CustomSpokenLaguagesAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        KeyPairBoolData sm = arrayList.get(position);
        viewHolder.bind(sm, position);
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
