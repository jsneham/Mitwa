package com.matriapp.mobile.adapter;

import static com.matriapp.mobile.utility.Common.showToast;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ChatConversationActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.model.QuickItem;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LiveMessageAdapter extends RecyclerView.Adapter<LiveMessageAdapter.MyViewHolder> implements Filterable {
    private Context context;
    private boolean paidFlag;
    private List<QuickItem> list = null;
    private List<QuickItem> contactListFiltered;
    SessionManager session ;
    Common common ;
    View llView ;

    public LiveMessageAdapter(Context context, List<QuickItem> list, boolean paidFlag, View llView) {
        this.context = context;
        this.list = list;
        this.contactListFiltered = list;
        this.paidFlag = paidFlag;
        this.llView = llView;
        session = new SessionManager(context);
        common = new Common(context);
    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    contactListFiltered = list;
                } else {
                    List<QuickItem> filteredList = new ArrayList<>();
                    for (QuickItem row : list) {
                        if (row.getOtherID().toLowerCase().contains(charString.toLowerCase()) || row.getContent().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }
                    contactListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = contactListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                contactListFiltered = (ArrayList<QuickItem>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name, tv_msg, tv_date, tv_count;
        public CircleImageView img_profile;
        public RelativeLayout viewBackground, viewForeground;

        public MyViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_msg = view.findViewById(R.id.tv_msg);
            tv_date = view.findViewById(R.id.tv_date);
            tv_count = view.findViewById(R.id.tv_count);
            img_profile = view.findViewById(R.id.img_profile);
            viewBackground = view.findViewById(R.id.view_background);
            viewForeground = view.findViewById(R.id.view_foreground);

            view.setOnClickListener(view1 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, ChatConversationActivity.class);
                    i.putExtra("matri_id", list.get(getAdapterPosition()).getId());
                    i.putExtra("username", list.get(getAdapterPosition()).getUsername());
                    i.putExtra("profile_img", list.get(getAdapterPosition()).getPhoto_url());
                    context.startActivity(i);
                }
                else {
                    showToast("Please upgrade your membership to chat with this member.",llView);
                    context.startActivity(new Intent(context, PlanListActivity.class));
                }

            });
        }
    }

   

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_chat_image, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final QuickItem item = contactListFiltered.get(position);

        if(common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))){
            String Name[]= item.getUsername().split(" ");
            if (Name.length == 3) {
                holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]+ " " + Name[2]);
            }
            else if (Name.length == 2) {
                holder.tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
            } else {
                holder.tv_name.setText(Name[0].charAt(0));
            }
        }
        else
            holder.tv_name.setText(item.getUsername());

//        if(!paidFlag)holder.tv_name.setText(item.getUsername());
//        else holder.tv_name.setText(item.getOtherID());

        if (item.getUnread_count()!= null &&   !item.getUnread_count().equals("0"))
            holder.tv_count.setText(item.getUnread_count());
        else
            holder.tv_count.setVisibility(View.GONE);
        String msg = "";
        if (item.getContent().length() >= 30) {
            msg = item.getContent().substring(0, 30) + "...<font color=#a30412/>";
        } else {
            msg = item.getContent();
        }
        holder.tv_msg.setText(Html.fromHtml(msg));
        holder.tv_date.setText(item.getSent_on());
        if (item.getPhoto_url().equals(context.getString(R.string.female_pic)) || item.getPhoto_url().equals(context.getString(R.string.male_pic)))
            holder.img_profile.setImageResource(R.drawable.placeholder);
        else
            Picasso.get().load(item.getPhoto_url()).into(holder.img_profile);
    }

    @Override
    public int getItemCount() {
        return contactListFiltered.size();
    }

    public void removeItem(int position) {
        contactListFiltered.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(QuickItem item, int position) {
        contactListFiltered.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }
}

