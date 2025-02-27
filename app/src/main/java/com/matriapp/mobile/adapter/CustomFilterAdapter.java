package com.matriapp.mobile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class CustomFilterAdapter extends RecyclerView.Adapter<CustomFilterAdapter.ViewHolder> {
    private List<KeyPairBoolData> itemList;
    private List<KeyPairBoolData> filteredList;
    private int selectedPosition = RecyclerView.NO_POSITION;
    private String tag = "STEP_H";
    private Context context;
    private CustomHeightAdapter.ListItemClickListener listItemClickListener;


    public interface ListItemClickListener {
        void onItemsClick(KeyPairBoolData Crop, int position, String tag);
    }

    public CustomFilterAdapter(List<KeyPairBoolData> dataSet, String tag, CustomHeightAdapter.ListItemClickListener listItemClickListener, Context context) {
        filteredList = new ArrayList<>(dataSet);
        itemList = dataSet;
        this.tag = tag;
        this.context = context;
        this.listItemClickListener = listItemClickListener;
    }
    // Constructor and other methods

    // ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Views in the item layout
        public TextView alertTextView, label;
        public CardView cardView;

        public ViewHolder(View view) {
            super(view);
            alertTextView = view.findViewById(R.id.alertTextView);
            label = view.findViewById(R.id.label);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listview_single_r, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, @SuppressLint("RecyclerView") int position) {
        KeyPairBoolData item = filteredList.get(position);
        viewHolder.alertTextView.setText(item.getName().trim());

        // Change background color based on selection
        if (selectedPosition == position) {
            viewHolder.cardView.setBackgroundColor(context.getColor(R.color.colorAccent));
            viewHolder.alertTextView.setTextColor(context.getColor(R.color.white));
            viewHolder.label.setTextColor(context.getColor(R.color.white));

        } else {
            viewHolder.cardView.setBackgroundColor(context.getColor(R.color.registration_background2));
            viewHolder.alertTextView.setTextColor(context.getColor(R.color.default_small_text_title1));
            viewHolder.label.setTextColor(context.getColor(R.color.default_small_text_title1));

        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int previousSelectedPosition = selectedPosition;
                selectedPosition = viewHolder.getAdapterPosition();
                listItemClickListener.onItemsClick(item, position, tag);
                notifyItemChanged(previousSelectedPosition);
                notifyItemChanged(selectedPosition);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }


    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(itemList);
            selectedPosition = RecyclerView.NO_POSITION;
        } else {
            for (KeyPairBoolData item : itemList) {
                if (item.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
