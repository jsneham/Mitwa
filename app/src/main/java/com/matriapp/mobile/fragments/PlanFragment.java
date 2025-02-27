package com.matriapp.mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ContactUsActivity;
import com.matriapp.mobile.activities.MakePaymentsActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.custom.NonScrollListView;
import com.matriapp.mobile.model.PlanDatum;
import com.matriapp.mobile.utility.Common;

import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


public class PlanFragment extends Fragment {

    private View view;
    private List<PlanDatum> list = new ArrayList<>();
    private RecyclerView viewPager2;
    private Handler sliderHandler = new Handler();
    private NonScrollListView lv_plan;
    private Common common;
    private RelativeLayout progressBar,llView;
    private LinearLayout llNeedHelp;
    private SliderAdapter adapter;

    private String qrCodeListStr = "";
    private String bankDetailsListStr = "";



    public static PlanFragment newInstance(List<PlanDatum> planData,String qrCode, String bankDetails) {
        PlanFragment fragment = new PlanFragment();
        Bundle args = new Bundle();
        args.putSerializable("PlanList", (Serializable) planData);
        args.putString("qrCodeListStr", qrCode);
        args.putString("bankDetailsListStr", bankDetails);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            list = (List<PlanDatum>) getArguments().getSerializable("PlanList");
            bankDetailsListStr = getArguments().getString("bankDetailsListStr");
            qrCodeListStr = getArguments().getString("qrCodeListStr");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_plan, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        viewPager2 = view.findViewById(R.id.viewPagerImageSlider);
        llNeedHelp = view.findViewById(R.id.llNeedHelp);
        llView = view.findViewById(R.id.llView);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(), RecyclerView.HORIZONTAL, false);
        viewPager2.setLayoutManager(mLayoutManager);
        adapter = new SliderAdapter(list, getContext());
        viewPager2.setHasFixedSize(true);
        viewPager2.setAdapter(adapter);
        llNeedHelp.setOnClickListener(view ->{
            String phoneNumber = getString(R.string.phone_number_help); // Phone number with country code
            Uri uri = Uri.parse(getString(R.string.whatsapp) + phoneNumber);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        return view;
    }

    public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {

        Context context;
        List<PlanDatum> list;

        SliderAdapter(List<PlanDatum> list, Context context) {
            this.list = list;
            this.context = context;
        }

        @NonNull
        @Override
        public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SliderViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(
                            R.layout.plan_item_list, parent, false
                    )
            );
        }

        @Override
        public void onBindViewHolder(@NonNull SliderViewHolder viewHolder, int position) {
            PlanDatum item = list.get(position);
//            String planName= item.getPlanName().toUpperCase().length()>25 ? item.getPlanName().toUpperCase().substring(0,25).trim()+"..." : item.getPlanName().toUpperCase();
            viewHolder.tv_name.setText(item.getPlanName().toUpperCase());
//            viewHolder.tv_prise.setText("₹ " + item.getPlanAmount());
            viewHolder.tv_profile_view.setText(item.getProfile() + " Full Profile Views");
            viewHolder.tv_duration.setText(item.getPlanDuration() + " Days");
            viewHolder.tv_message.setText(item.getPlanMsg() + " Messages");
            viewHolder.tv_detail.setText(item.getPlanOffers().trim());


            String amountType = "₹";
            if (item.getPlanAmountType().equalsIgnoreCase("USD")) amountType = "$";
            float planAmount = Float.parseFloat(item.getPlanAmount());
            float planDiscount = Float.parseFloat(item.getOfferPer());
            float finalPlanAmount = planAmount;

            if (planDiscount > 0) {
                finalPlanAmount = planAmount - ((planAmount * planDiscount) / 100);
                viewHolder.tv_org_prise.setPaintFlags(viewHolder.tv_org_prise.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                viewHolder.tv_org_prise.setText(amountType + "" + item.getPlanAmount());
                viewHolder.tv_org_prise.setVisibility(View.VISIBLE);
                viewHolder.tv_discount.setVisibility(View.VISIBLE);
                viewHolder.tv_discount.setText(item.getOfferPer() + "% OFF");
            } else {
                viewHolder.tv_org_prise.setVisibility(View.GONE);
                viewHolder.tv_discount.setVisibility(View.GONE);
            }

            float perDayAmount = (finalPlanAmount / Integer.parseInt(item.getPlanDuration()));
            viewHolder.tv_per_day.setText(amountType + "" + new DecimalFormat("##.##").format(perDayAmount) + " per day");

            viewHolder.lblPlanAmount.setText(amountType + "" + Math.round(finalPlanAmount));


            viewHolder.btn_id.setOnClickListener(v -> {
                String plan_amount = "";
                PlanDatum object = null;
                plan_amount = item.getPlanAmount();
                object = item;
//
                if (object == null) {
                    common.showToast("Please Select Plan",llView);
                    return;
                }
                if (plan_amount.equals("0")) {
                    common.showToast("Please Contact To admin",llView);
                    Intent i = new Intent(context, ContactUsActivity.class);
                    i.putExtra("page_tag", "form");
                    startActivity(i);
                } else {
//                    Intent i = new Intent(context, MakePaymentsActivity.class);
//                    i.putExtra("plan_data", item);
//                    startActivity(i);

                    Intent i = new Intent(context, MakePaymentsActivity.class);
                    i.putExtra("qr_list",qrCodeListStr);
                    i.putExtra("bank_detail_list",bankDetailsListStr);
                    i.putExtra("plan_data", item);
                    startActivity(i);
                }
            });


            if (item.getPlanMsg().equals("Yes")) {
                setTextViewDrawable(viewHolder.tv_chat, R.drawable.plan_activie_check);
                viewHolder.tv_chat.setText("Live Chat (Total Messages " + item.getPlanMsg() + ")");

            } else viewHolder.tv_chat.setVisibility(View.GONE);

            if (item.getProfile().equals("0"))
                viewHolder.tv_profile_view.setVisibility(View.GONE);


            if (item.getPlanContacts().equals("0"))
                viewHolder.tv_contact.setVisibility(View.GONE);
            else {
                viewHolder.tv_contact.setVisibility(View.VISIBLE);
                viewHolder.tv_contact.setText(item.getPlanContacts() + " Contact Numbers of Profile(s)");
            }


            if (item.getChat().equals("0"))
                viewHolder.tv_message.setVisibility(View.GONE);


            viewHolder.tv_video.setVisibility(View.GONE);
        }

        private void setTextViewDrawable(@NonNull TextView textView, int image) {

            if (textView != null) {
                textView.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0);
            }

        }

        private void setTextViewDrawableColor(@NonNull TextView textView, @ColorRes int color) {
            for (Drawable drawable : textView.getCompoundDrawables()) {
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN));
                }
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class SliderViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name, tv_prise, tv_profile_view, tv_contact, tv_duration, tv_message, tv_detail, tv_label_prise, tv_per_day, tv_chat, tv_video, tv_org_prise, tv_discount, lblPlanAmount;
            RadioButton radio_plan;
            Button btn_id;

            SliderViewHolder(@NonNull View rowView) {
                super(rowView);
                tv_name = rowView.findViewById(R.id.tv_name);
                tv_prise = rowView.findViewById(R.id.tv_prise);
                tv_profile_view = rowView.findViewById(R.id.tv_profile_view);
                tv_contact = rowView.findViewById(R.id.tv_contact);
                tv_duration = rowView.findViewById(R.id.tv_duration);
                tv_message = rowView.findViewById(R.id.tv_message);
                tv_detail = rowView.findViewById(R.id.tv_detail);
                tv_label_prise = rowView.findViewById(R.id.tv_label_prise);
                tv_per_day = rowView.findViewById(R.id.tv_per_day);
                tv_chat = rowView.findViewById(R.id.tv_chat);
                tv_video = rowView.findViewById(R.id.tv_video);
                btn_id = rowView.findViewById(R.id.btn_id);
                tv_org_prise = rowView.findViewById(R.id.tv_org_prise);
                tv_discount = rowView.findViewById(R.id.tv_discount);
                lblPlanAmount = rowView.findViewById(R.id.lblPlanAmount);
//                radio_plan = rowView.findViewById(R.id.radio_plan);

            }


        }

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {
                list.addAll(list);
                notifyDataSetChanged();
            }
        };

    }
}



//Changed by Harshad//
//package com.matriapp.mobile.fragments;
//
//
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Paint;
//import android.graphics.PorterDuff;
//import android.graphics.PorterDuffColorFilter;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.RadioButton;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import androidx.annotation.ColorRes;
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.matriapp.mobile.R;
//import com.matriapp.mobile.activities.ContactUsActivity;
//import com.matriapp.mobile.activities.MakePaymentsActivity;
//import com.matriapp.mobile.custom.NonScrollListView;
//import com.matriapp.mobile.model.PlanDatum;
//import com.matriapp.mobile.utility.Common;
//
//import java.io.Serializable;
//import java.text.DecimalFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlanFragment extends Fragment {
//
//    private View view;
//    private List<PlanDatum> list= new ArrayList<>();
//    private RecyclerView viewPager2;
//    private Handler sliderHandler = new Handler();
//    private NonScrollListView lv_plan;
//    private Common common;
//    private RelativeLayout progressBar;
//    private   SliderAdapter  adapter;
//
//
//    public static PlanFragment newInstance(List<PlanDatum> planData ) {
//        PlanFragment fragment = new PlanFragment();
//        Bundle args = new Bundle();
//        args.putSerializable("PlanList", (Serializable) planData);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            list = (List<PlanDatum>) getArguments().getSerializable("PlanList");
//        }
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        view = inflater.inflate(R.layout.fragment_plan, container, false);
//        progressBar = view.findViewById(R.id.progressBar);
//        viewPager2 = view.findViewById(R.id.viewPagerImageSlider);
//        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
//        viewPager2.setLayoutManager(mLayoutManager);
//        adapter = new SliderAdapter(list, getContext());
//        viewPager2.setHasFixedSize(true);
//        viewPager2.setAdapter(adapter);
//        return view;
//    }
//
//    public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
//
//        Context context;
//        List<PlanDatum> list;
//        SliderAdapter(List<PlanDatum> list, Context context) {
//            this.list = list;
//            this.context = context;
//        }
//
//        @NonNull
//        @Override
//        public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            return new SliderViewHolder(
//                    LayoutInflater.from(parent.getContext()).inflate(
//                            R.layout.plan_item_list, parent, false
//                    )
//            );
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull SliderViewHolder viewHolder, int position) {
//            PlanDatum item = list.get(position);
//            viewHolder.tv_name.setText(item.getPlanName().toUpperCase());
//            viewHolder.tv_contact.setText(item.getPlanContacts() + " Contact Numbers");
////            viewHolder.tv_prise.setText("₹ " + item.getPlanAmount());
//            viewHolder.tv_profile_view.setText(item.getProfile() + " Full Profile Views");
//            viewHolder.tv_duration.setText(item.getPlanDuration() + " Days");
//            viewHolder.tv_message.setText(item.getPlanMsg() + " Messages");
//            viewHolder.tv_detail.setText(item.getPlanOffers().trim());
//
//            String amountType = "₹";
//            if (item.getPlanAmountType().equalsIgnoreCase("USD")) amountType = "$";
//            float planAmount = Float.parseFloat(item.getPlanAmount());
//            float planDiscount = Float.parseFloat(item.getOfferPer());
//            float finalPlanAmount = planAmount;
//
//            if (planDiscount > 0) {
//                finalPlanAmount = planAmount - ((planAmount * planDiscount) / 100);
//                viewHolder.tv_org_prise.setPaintFlags(viewHolder.tv_org_prise.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
//                viewHolder.tv_org_prise.setText(amountType + "" + item.getPlanAmount());
//                viewHolder.tv_org_prise.setVisibility(View.VISIBLE);
//                viewHolder.tv_discount.setVisibility(View.VISIBLE);
//                viewHolder.tv_discount.setText(item.getOfferPer() + "% OFF");
//            } else {
//                viewHolder.tv_org_prise.setVisibility(View.GONE);
//                viewHolder.tv_discount.setVisibility(View.GONE);
//            }
//            float perDayAmount =(finalPlanAmount / Integer.parseInt(item.getPlanDuration()));
//            viewHolder.tv_per_day.setText(amountType + "" + new DecimalFormat("##.##").format(perDayAmount)+ " per day");
//
//            viewHolder.lblPlanAmount.setText(amountType + "" + Math.round(finalPlanAmount));
//
//
//
//
//            viewHolder.btn_id.setOnClickListener(v -> {
//                String plan_amount = "";
//                PlanDatum object = null;
//                plan_amount = item.getPlanAmount();
//                object = item;
////
//                if (object == null) {
//                    common.showToast("Please Select Plan");
//                    return;
//                }
//                if (plan_amount.equals("0")) {
//                    common.showToast("Please Contact To admin");
//                    Intent i = new Intent(context, ContactUsActivity.class);
//                    i.putExtra("page_tag", "form");
//                    startActivity(i);
//                } else {
//                    Intent i = new Intent(context, MakePaymentsActivity.class);
//                    i.putExtra("plan_data", item);
//                    startActivity(i);
//                }
//            });
//
//
//            if (item.getPlanMsg().equals("Yes")) {
//                setTextViewDrawable(viewHolder.tv_chat, R.drawable.plan_activie_check);
//                viewHolder.tv_chat.setText("Live Chat (Total Messages " + item.getPlanMsg() + ")"); ;
//            }
//            else viewHolder.tv_chat.setVisibility(View.GONE);
//
//            if (item.getProfile().equals("0"))
//                viewHolder.tv_profile_view.setVisibility(View.GONE);
//
//
//            if (item.getPlanContacts().equals("0"))
//                viewHolder.tv_contact.setVisibility(View.GONE);
//
//
//            if (item.getChat().equals("0"))
//                viewHolder.tv_message.setVisibility(View.GONE);
//
//
//
//            viewHolder.tv_video.setVisibility(View.GONE);
//        }
//
//        private void setTextViewDrawable(@NonNull TextView textView, int image) {
//
//            if (textView != null) {
//                textView.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0);
//            }
//
//        }
//        private void setTextViewDrawableColor(@NonNull TextView textView, @ColorRes int color) {
//            for (Drawable drawable : textView.getCompoundDrawables()) {
//                if (drawable != null) {
//                    drawable.setColorFilter(new PorterDuffColorFilter(getResources().getColor(color), PorterDuff.Mode.SRC_IN));
//                }
//            }
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//
//        class SliderViewHolder extends RecyclerView.ViewHolder {
//            TextView tv_name, tv_prise, tv_profile_view, tv_contact, tv_duration, tv_message, tv_detail, tv_label_prise, tv_per_day, tv_chat, tv_video,tv_org_prise,tv_discount,lblPlanAmount;
//            RadioButton radio_plan;
//            Button btn_id;
//
//            SliderViewHolder(@NonNull View rowView) {
//                super(rowView);
//                tv_name = rowView.findViewById(R.id.tv_name);
//                tv_prise = rowView.findViewById(R.id.tv_prise);
//                tv_profile_view = rowView.findViewById(R.id.tv_profile_view);
//                tv_contact = rowView.findViewById(R.id.tv_contact);
//                tv_duration = rowView.findViewById(R.id.tv_duration);
//                tv_message = rowView.findViewById(R.id.tv_message);
//                tv_detail = rowView.findViewById(R.id.tv_detail);
//                tv_label_prise = rowView.findViewById(R.id.tv_label_prise);
//                tv_per_day = rowView.findViewById(R.id.tv_per_day);
//                tv_chat = rowView.findViewById(R.id.tv_chat);
//                tv_video = rowView.findViewById(R.id.tv_video);
//                btn_id = rowView.findViewById(R.id.btn_id);
//                tv_org_prise = rowView.findViewById(R.id.tv_org_prise);
//                tv_discount = rowView.findViewById(R.id.tv_discount);
//                lblPlanAmount = rowView.findViewById(R.id.lblPlanAmount);
////                radio_plan = rowView.findViewById(R.id.radio_plan);
//
//            }
//
//
//        }
//
//        private Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                list.addAll(list);
//                notifyDataSetChanged();
//            }
//        };
//
//    }
//}
