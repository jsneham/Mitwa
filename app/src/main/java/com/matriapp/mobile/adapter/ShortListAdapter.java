package com.matriapp.mobile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.like.LikeButton;
import com.matriapp.mobile.like.OnLikeListener;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class ShortListAdapter extends RecyclerView.Adapter<ShortListAdapter.ViewHolder> {
    public Context mContext;
    private List<DashboardItem> arrayList;
    private ItemListener myListener;
    private SessionManager session;

    int placeHolder = 0;
    private Common common;
    private View llView;

    public ShortListAdapter(Context mContext, List<DashboardItem> arrayList, View llView) {
        if (mContext == null) return;
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.llView = llView;
        this.common = new Common(mContext);
        this.session = new SessionManager(mContext);
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    public interface ItemListener {
        void itemClicked(DashboardItem object, int position);

        void removeShortlist(int position, String matriId);

        void alertPhotoPassword(String matriId);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.short_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardItem item = arrayList.get(position);

        holder.tv_name.setText(item.getMatri_id().toUpperCase());

        common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(),
                item.getPhotoUrl() + item.getImage(), holder.img_profile, null, 68);

        if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
            Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                    .placeholder(R.drawable.ic_transparent_placeholder)
                    .error(R.drawable.ic_transparent_placeholder)
                    .into(holder.imgPLanStamp);
            holder.imgPLanStamp.setVisibility(View.VISIBLE);
        } else {
            holder.imgPLanStamp.setVisibility(View.GONE);
        }

//        if (item.getColor().length() > 0) {
//            holder.cardView.setShadowColor(Color.parseColor("" + item.getColor()));
//        }

        //String about=item.getAbout()+"...<font color='#ff041a'>Read More</font>";
        holder.tv_detail.setText(item.getAbout());


        try {
            if (item.getAction().getInt("is_shortlist") == 1) {
                holder.tvShortlist.setTextColor(mContext.getResources().getColor(R.color.shortllist));
                holder.ivShortlist.setImageResource(R.drawable.in_shorted);
                holder.tvShortlist.setText("Shortlisted");
            } else {
                holder.ivShortlist.setImageResource(R.drawable.in_shortlist);
                holder.tvShortlist.setTextColor(mContext.getResources().getColor(R.color.registration_hint_color));
                holder.tvShortlist.setText("Shortlist");

            }

            if (!item.getAction().getString("is_interest").equals("")) {
                holder.ivConnected.setImageResource(R.drawable.sl_connected);
                holder.btn_interest.setText(R.string.requested);
                holder.btn_interest.setTextColor(mContext.getResources().getColor(R.color.online));
            } else {
                holder.btn_interest.setText(R.string.send_interest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.btn_chat.setOnClickListener(view1 -> {
            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                Intent i = new Intent(mContext, ConversationActivity.class);
                i.putExtra("matri_id", item.getMatri_id());
                mContext.startActivity(i);
            } else {
                common.showToast("Please upgrade your membership to chat with this member.",llView);
                mContext.startActivity(new Intent(mContext, PlanListActivity.class));
            }

        });



        holder.btnInterest.setOnClickListener(view14 -> {
            try {
                if (item.getAction().getString("is_interest").equals("")) {
                    LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                    final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                    final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                    dialog.setContentView(vv);
                    dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialogInterface) {
                            BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                        }
                    });
                    dialog.show();
                    ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                    tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                    Button send = vv.findViewById(R.id.btn_send_intr);
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            if (grp_interest.getCheckedRadioButtonId() != -1) {
                                RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                interestRequest(item.getMatri_id(), btn.getText().toString().trim(), holder.btn_interest, holder.btnInterest, holder.ivConnected);
                            }
                        }
                    });
                } else {
                    common.showToast("You already sent interest to this user.",llView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
//        ShadowView cardView;
        ImageView imgPLanStamp;
        TextView tv_name;
        TextView tv_detail;
        ImageView img_profile;
        LikeButton btn_short;

        //for multi selection
        private ImageView layoutBg, ivShortlist, ivConnected;

        LinearLayout btnInterest, btn_chat, btnShortlist;
        TextView tvShortlist, btn_interest;

        public ViewHolder(@NonNull View rowView) {
            super(rowView);

//            cardView = rowView.findViewById(R.id.cardView);
            imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            tv_name = rowView.findViewById(R.id.tv_name);
            tv_detail = rowView.findViewById(R.id.tv_detail);
            img_profile = rowView.findViewById(R.id.img_profile);
            btn_short = rowView.findViewById(R.id.btn_short);

            img_profile.setOnClickListener(this);
            tv_detail.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            btn_short.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    if (arrayList.size() > 0) {
                        DashboardItem item = arrayList.get(getAbsoluteAdapterPosition());
                        myListener.removeShortlist(getAbsoluteAdapterPosition(), item.getMatri_id());
                    }
                }
            });


            btnInterest = rowView.findViewById(R.id.btnInterest);
            btn_chat = rowView.findViewById(R.id.btn_chat);
            btnShortlist = rowView.findViewById(R.id.btnShortlist);
            tvShortlist = rowView.findViewById(R.id.tvShortlist);
            ivShortlist = rowView.findViewById(R.id.ivShortlist);
            ivConnected = rowView.findViewById(R.id.ivConnected);
            btn_interest = rowView.findViewById(R.id.btn_interest);

            btnShortlist.setOnClickListener(view12 -> {
                try {
                    if (arrayList.size() > 0) {
                        DashboardItem item = arrayList.get(getAbsoluteAdapterPosition());
                        myListener.removeShortlist(getAbsoluteAdapterPosition(), item.getMatri_id());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        }

        @Override
        public void onClick(View view) {
            DashboardItem item = arrayList.get(getAbsoluteAdapterPosition());
            if (view.getId() == R.id.img_profile) {
                if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                    myListener.alertPhotoPassword(item.getMatri_id());
                } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.show_image_alert);
                    TouchImageView img_url = dialog.findViewById(R.id.img_url);
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_url);
                    dialog.show();
                } else {
                    openScreenAsPerPlan(item);
                }
            } else if (view.getId() == R.id.tv_detail) {
                openScreenAsPerPlan(item);
            } else if (view.getId() == R.id.tv_name) {
                openScreenAsPerPlan(item);
            }
        }

        private void openScreenAsPerPlan(DashboardItem item) {
            //    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
            Intent i = new Intent(mContext, OtherUserProfileActivity.class);
            i.putExtra("other_id", item.getUser_id());
            mContext.startActivity(i);
            //   } else {
            //     common.showToast("Please upgrade your membership to chat with this member.");
            //     mContext.startActivity(new Intent(mContext, PlanListActivity.class));
            //  }
        }
    }


    private void interestRequest(String matri_id, String int_msg, final TextView btn_interest, final LinearLayout btnInterest, final ImageView ivConnected) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(AppConstants.send_interest, param, response -> {

            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    //  button.setLiked(true);
//                    btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_inter_filled));
                    btn_interest.setText(R.string.requested);
                    btn_interest.setTextColor(mContext.getResources().getColor(R.color.online));
                    ivConnected.setImageResource(R.drawable.sl_connected);

                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later), llView);
            }
        }, error -> {

            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode), llView);
            }
        },llView);

    }



}
