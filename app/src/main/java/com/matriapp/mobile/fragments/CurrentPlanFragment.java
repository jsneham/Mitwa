package com.matriapp.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.CurrentPlanActivity;
import com.matriapp.mobile.activities.DashboardActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.model.CurrentPlanItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CurrentPlanFragment extends Fragment {

    private Button btn_upgrade;
    private TextView tv_no_data;
    private List<CurrentPlanItem> list = new ArrayList<>();
    private SessionManager session;
    private ExpandableRecyclerViewAdapter adapter;
    //    private currentAdapter adapter;
    private Common common;
    private RelativeLayout progressBar,llContent;
    private RecyclerView lv_plan;
    private boolean isFromSuccessPayment;

    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_current_plan, container, false);


        context = getContext();
        common = new Common(context);
        session = new SessionManager(context);

        ((DashboardActivity) getActivity()).setToolbarTitle("Membership");
        progressBar = view.findViewById(R.id.loader);
        llContent = view.findViewById(R.id.llContent);
        lv_plan = view.findViewById(R.id.lv_plan);
        btn_upgrade = view.findViewById(R.id.btn_upgrade);
        tv_no_data = view.findViewById(R.id.tv_no_data);

        btn_upgrade.setOnClickListener(v -> startActivity(new Intent(context, PlanListActivity.class)));


        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        lv_plan.setLayoutManager(layoutManager);

        adapter = new ExpandableRecyclerViewAdapter(context, list);
        lv_plan.setAdapter(adapter);


        getPlanData();
        
        return  view;
    }


    private void getPlanData() {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.check_plan, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                MyApplication.setPlan(object.getBoolean("is_show"));
                if (object.getString("status").equals("success")) {
                    lv_plan.setVisibility(View.VISIBLE);
                    tv_no_data.setVisibility(View.GONE);
                    JSONObject obj = object.getJSONObject("data");
                    CurrentPlanItem item = new CurrentPlanItem();
                    item.setPlan_name(obj.getString("plan_name").toUpperCase());
                    item.setCurrency(obj.getString("currency"));
                    item.setPlan_amount(obj.getString("plan_amount"));
                    item.setPlan_activated(changeDate(obj.getString("plan_activated"), "MMM dd, yyyy"));
                    item.setPlan_expired(changeDate(obj.getString("plan_expired"), "MMM dd, yyyy"));
                    item.setPlan_duration(obj.getString("plan_duration") + " Days");

                    item.setMessage_used(obj.getString("message_used"));
                    item.setMessage(obj.getString("message"));

                    item.setContacts(obj.getString("contacts"));
                    item.setContacts_used(obj.getString("contacts_used"));

                    item.setChat(obj.getString("chat"));
                    item.setProfile(obj.getString("profile"));
                    item.setProfile_used(obj.getString("profile_used"));
                    item.setTax_percentage(obj.getString("tax_percentage"));
                    item.setTax_name(obj.getString("tax_name"));
                    item.setGrand_total(obj.getString("grand_total"));
                    item.setTax_amount(obj.getString("tax_amount"));
                    item.setDiscount_amount(obj.getString("discount_amount"));
                    item.setOffer_per(obj.getString("offer_per"));

                    list.add(item);
                    adapter.notifyDataSetChanged();

                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    lv_plan.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    public String changeDate(String time, String outputPattern) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    class currentAdapter extends ArrayAdapter<CurrentPlanItem> {
        Context context;
        List<CurrentPlanItem> list;

        public currentAdapter(Context context, List<CurrentPlanItem> list) {
            super(context, R.layout.current_plan_item, list);
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.current_plan_item, null, true);

            TextView tv_prise = convertView.findViewById(R.id.tv_prise);
            TextView tv_active_on = convertView.findViewById(R.id.tv_active_on);
            TextView tv_exp_on = convertView.findViewById(R.id.tv_exp_on);
            TextView tv_duration = convertView.findViewById(R.id.tv_duration);
            TextView tv_message = convertView.findViewById(R.id.tv_message);
            TextView tv_contact = convertView.findViewById(R.id.tv_contact);
            TextView tv_name = convertView.findViewById(R.id.tv_name);
            TextView lblOffer = convertView.findViewById(R.id.lblOffer);
            TextView tv_offer = convertView.findViewById(R.id.tv_offer);

            RelativeLayout lay_gst = convertView.findViewById(R.id.lay_gst);

            TextView tv_chat = convertView.findViewById(R.id.tv_chat);
            TextView tv_pro = convertView.findViewById(R.id.tv_pro);
            TextView tv_gst = convertView.findViewById(R.id.tv_gst);
            TextView tv_t_amt = convertView.findViewById(R.id.tv_t_amt);
            TextView gst_label = convertView.findViewById(R.id.gst_label);
            TextView tv_disc = convertView.findViewById(R.id.tv_disc);

            CurrentPlanItem item = list.get(position);

            tv_name.setText(item.getPlan_name());
            tv_chat.setText(item.getChat());

            int pro_use = Integer.parseInt(item.getProfile_used());
            int pro_t = Integer.parseInt(item.getProfile());
            int avl_pro = pro_t - pro_use;
            String pro = avl_pro + " out of " + item.getProfile();
            tv_pro.setText(pro);

            if (item.getDiscount_amount().equals("0")) {
                tv_disc.setText("N/A");
            } else {
                tv_disc.setText(item.getCurrency() + " " + item.getDiscount_amount());
            }

            if (item.getOffer_per().equals("0")) {
                lblOffer.setText("Discounted Price");
                tv_offer.setText("N/A");
            } else {
                lblOffer.setText("Discounted Price (" + item.getOffer_per() + "% Off)");

                float planAmount = Float.parseFloat(item.getPlan_amount());
                float planDiscount = Float.parseFloat(item.getOffer_per());
                float finalOffAmount = planAmount * planDiscount / 100;

                tv_offer.setText(item.getCurrency() + " " +new DecimalFormat("##.##").format(finalOffAmount));
            }

            String gst = item.getCurrency() + " " + item.getTax_amount();
            tv_gst.setText(gst);

            String tamt = item.getCurrency() + " " + item.getGrand_total();
            tv_t_amt.setText(tamt);

            if (item.getTax_name().equals("")) {
                lay_gst.setVisibility(View.GONE);
            } else {
                lay_gst.setVisibility(View.VISIBLE);
                String gst_lbl = item.getTax_name() + " (" + item.getTax_percentage() + "%)";
                gst_label.setText(gst_lbl);
            }

            String prise = item.getCurrency() + " " + item.getPlan_amount();
            tv_prise.setText(prise);
            tv_active_on.setText(item.getPlan_activated());
            tv_exp_on.setText(item.getPlan_expired());
            tv_duration.setText(item.getPlan_duration());

            int msg_use = Integer.parseInt(item.getMessage_used());
            int msg_t = Integer.parseInt(item.getMessage());
            int avl_msg = msg_t - msg_use;
            String msg = avl_msg + " out of " + item.getMessage();
            tv_message.setText(msg);

            int cont_use = Integer.parseInt(item.getContacts_used());
            int cont_t = Integer.parseInt(item.getContacts());
            int avl_cont = cont_t - cont_use;
            String cont = avl_cont + " out of " + item.getContacts();
            tv_contact.setText(cont);

            return convertView;
        }
    }


    class ExpandableRecyclerViewAdapter extends RecyclerView.Adapter<ExpandableRecyclerViewAdapter.ParentViewHolder> {

        private final Context context;
        private final List<CurrentPlanItem> parentItems;
        private final Set<Integer> expandedParents;

        public ExpandableRecyclerViewAdapter(Context context, List<CurrentPlanItem> parentItems) {
            this.context = context;
            this.parentItems = parentItems;
            this.expandedParents = new HashSet<>();
        }

        @Override
        public ExpandableRecyclerViewAdapter.ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.current_plan_item, parent, false);
            return new ExpandableRecyclerViewAdapter.ParentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExpandableRecyclerViewAdapter.ParentViewHolder holder, int position) {
            CurrentPlanItem item = parentItems.get(position);


            holder.tv_name.setText(item.getPlan_name());
            holder.tv_chat.setText(item.getChat());

            int pro_use = Integer.parseInt(item.getProfile_used());
            int pro_t = Integer.parseInt(item.getProfile());
            int avl_pro = pro_t - pro_use;
            String pro = avl_pro + " out of " + item.getProfile();
            holder.tv_pro.setText(pro);

            if (item.getDiscount_amount().equals("0")) {
                holder.tv_disc.setText("N/A");
            } else {
                holder.tv_disc.setText(item.getCurrency() + " " + item.getDiscount_amount());
            }

            if (item.getOffer_per().equals("0")) {
                holder.lblOffer.setText("Discounted Price");
                holder.tv_offer.setText("N/A");
            } else {
                holder.lblOffer.setText("Discounted Price (" + item.getOffer_per() + "% Off)");

                float planAmount = Float.parseFloat(item.getPlan_amount());
                float planDiscount = Float.parseFloat(item.getOffer_per());
                float finalOffAmount = planAmount * planDiscount / 100;

                holder.tv_offer.setText(item.getCurrency() + " " +new DecimalFormat("##.##").format(finalOffAmount));
            }

            String gst = item.getCurrency() + " " + item.getTax_amount();
            holder.tv_gst.setText(gst);

            String tamt = item.getCurrency() + " " + item.getGrand_total();
            holder.tv_t_amt.setText(tamt);

            if (item.getTax_name().equals("")) {
                holder.lay_gst.setVisibility(View.GONE);
            } else {
                holder.lay_gst.setVisibility(View.VISIBLE);
                String gst_lbl = item.getTax_name() + " (" + item.getTax_percentage() + "%)";
                holder.gst_label.setText(gst_lbl);
            }

            String prise = item.getCurrency() + " " + item.getPlan_amount();
            holder.tv_prise.setText(prise);
            holder.tv_active_on.setText(item.getPlan_activated());
            holder.tv_exp_on.setText(item.getPlan_expired());
            holder.tv_duration.setText(item.getPlan_duration());

            int msg_use = Integer.parseInt(item.getMessage_used());
            int msg_t = Integer.parseInt(item.getMessage());
            int avl_msg = msg_t - msg_use;
            String msg = avl_msg + " out of " + item.getMessage();
            holder.tv_message.setText(msg);

            int cont_use = Integer.parseInt(item.getContacts_used());
            int cont_t = Integer.parseInt(item.getContacts());
            int avl_cont = cont_t - cont_use;
            String cont = avl_cont + " out of " + item.getContacts();
            holder.tv_contact.setText(cont);

            boolean isExpanded = expandedParents.contains(position);
            holder.child.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            if (isExpanded) {
                holder.tvShowMore.setText("show less");
                holder.icon.setImageDrawable(context.getDrawable(R.drawable.chevron_up));
            } else {
                holder.tvShowMore.setText("show more");
                holder.icon.setImageDrawable(context.getDrawable(R.drawable.chevron_down));
            }

            holder.tvShowMore.setOnClickListener(v -> {
                if (isExpanded) {
                    expandedParents.remove(position);
                } else {
                    expandedParents.add(position);
                }
                notifyItemChanged(position);
            });
        }

        @Override
        public int getItemCount() {
            return parentItems.size();
        }

        public class ParentViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name,tv_duration,tvShowMore,tv_prise,tv_active_on,tv_exp_on,
                    tv_message,tv_contact,tv_offer,tv_chat,tv_pro,tv_gst,lblOffer, tv_t_amt,tv_disc,gst_label;
            LinearLayout child,lay_gst;
            ImageView icon;

            public ParentViewHolder(View convertView) {
                super(convertView);
                tvShowMore = convertView.findViewById(R.id.tvShowMore);
                tv_name = convertView.findViewById(R.id.tv_name);
                tv_duration = convertView.findViewById(R.id.tv_duration);
                child = convertView.findViewById(R.id.child);
                icon = convertView.findViewById(R.id.icon);


                tv_prise = convertView.findViewById(R.id.tv_prise);
                tv_active_on = convertView.findViewById(R.id.tv_active_on);
                tv_exp_on = convertView.findViewById(R.id.tv_exp_on);
                tv_duration = convertView.findViewById(R.id.tv_duration);
                tv_message = convertView.findViewById(R.id.tv_message);
                tv_contact = convertView.findViewById(R.id.tv_contact);
                tv_name = convertView.findViewById(R.id.tv_name);
                lblOffer = convertView.findViewById(R.id.lblOffer);
                tv_offer = convertView.findViewById(R.id.tv_offer);

                lay_gst = convertView.findViewById(R.id.lay_gst);

                tv_chat = convertView.findViewById(R.id.tv_chat);
                tv_pro = convertView.findViewById(R.id.tv_pro);
                tv_gst = convertView.findViewById(R.id.tv_gst);
                tv_t_amt = convertView.findViewById(R.id.tv_t_amt);
                gst_label = convertView.findViewById(R.id.gst_label);
                tv_disc = convertView.findViewById(R.id.tv_disc);
            }
        }
    }

}