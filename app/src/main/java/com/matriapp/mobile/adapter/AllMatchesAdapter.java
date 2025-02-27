package com.matriapp.mobile.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.like.LikeButton;
import com.matriapp.mobile.like.OnLikeListener;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class AllMatchesAdapter extends RecyclerView.Adapter<AllMatchesAdapter.ViewHolder> {
    public Context mContext;
    private List<DashboardItem> arrayList;

    int placeHolder = 0;
    private Common common;
    private SessionManager session;
    private ItemListener itemListener;
    private FrameLayout llView;

    public AllMatchesAdapter(Context mContext, List<DashboardItem> arrayList,ItemListener itemListener,FrameLayout llView) {
        if (mContext == null) return;
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.itemListener = itemListener;
        this.llView = llView;
        this.common = new Common(mContext);
        session = new SessionManager(mContext);
    }

    public interface ItemListener {
        void alertPhotoPassword(String photoPassword, String image, String name);

//        void likeRequest(String value, String name, int position);

        void shortlistRequest(String action,String id, ImageView imageView,  DashboardItem item);

        void interestRequest(String value, String name, ImageView imageView, ImageView imageView2);

        void blockRequest(String value, String name,JSONObject action, DashboardItem itemv);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.discover_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            final DashboardItem item = arrayList.get(position);

            // Handle Plan Badge visibility
            handlePlanBadge(holder, item);

            // Set Shortlist Icon
            handleShortlistIcon(holder, item);

            // Set Interest Button visibility
            handleInterestButton(holder, item);

            // Set User Profile Info
            handleUserInfo(holder, item);

            // Set Profile Image
            handleProfileImage(holder, item);

            // Set Click Listeners
            setClickListeners(holder, item);


//            if(item.getBadge()!=null) {
//                if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
//                    Picasso.get().load(item.getBadgeUrl() + item.getBadge())
//                            .placeholder(R.drawable.ic_transparent_placeholder)
//                            .error(R.drawable.ic_transparent_placeholder)
//                            .into(holder.imgPLanStamp);
//                    holder.imgPLanStamp.setVisibility(View.VISIBLE);
//                } else {
//                    holder.imgPLanStamp.setVisibility(View.GONE);
//                }
//            }
//            else {
//                holder.imgPLanStamp.setVisibility(View.GONE);
//            }
//
//            try {
//                if (item.getAction().getInt("is_shortlist") == 1)
//                    holder.ivShort.setImageResource(R.drawable.ic_shortlistfill);
//                else
//                    holder.ivShort.setImageResource(R.drawable.ic_shortliststroke);
//
//                if (!(item.getAction().getString("is_interest").equals(""))) {
//                    holder.btn_interest.setVisibility(View.GONE);
//                    holder.tvInSent.setVisibility(View.VISIBLE);
//                } else
//                    holder.btn_interest.setImageResource(R.drawable.send_interest);
//
//                AppDebugLog.print("is online : " + item.getAction().getInt("is_login"));
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//            Drawable img_white = mContext.getResources().getDrawable(R.drawable.eye_pink);
//            img_white.setBounds(0, 0, 40, 40);
//            // tv_view_count.setCompoundDrawables(img_white, null, null, null);
//
//            if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS)))
//                holder.tv_name.setText(item.getFirst_name().charAt(0) + " " + item.getLastname());
//            else
//                holder.tv_name.setText(item.getFirst_name() + " " + item.getLastname());
//
//            holder.tv_mid.setText(item.getMatri_id());
//
//            common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(),item.getImage_approval(), item.getPhotoUrl()+item.getImage(), holder.img_profile, null,68);
//
//            String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
//                    item.getMTongueName(), item.getCaste(),
//                    item.getEducation(), item.getOccupation(), item.getCity(), item.getState(),30);
//            holder.tv_detail.setText(description);
//
//            holder.btn_chat.setOnClickListener(view1 -> {
//                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
//                    Intent i = new Intent(mContext, ConversationActivity.class);
//                    i.putExtra("matri_id", item.getMatri_id());
//                    i.putExtra("username", item.getName());
//                    mContext.startActivity(i);
//                } else {
//                    common.showToast("Please upgrade your membership to chat with this member.");
//                    mContext.startActivity(new Intent(mContext, PlanListActivity.class));
//                }
//            });
//
//            holder.ivShort.setOnClickListener(view12 -> {
//                try {
//                    if (item.getAction().getInt("is_shortlist") == 1) {
//                        itemListener.shortlistRequest("remove", item.getMatri_id(), holder.ivShort, item);
//                    } else {
//                        itemListener.shortlistRequest("add", item.getMatri_id(), holder.ivShort, item);
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            });
//
//
//            holder.btn_interest.setOnClickListener(view -> {
//                try {
//                    if (!item.getAction().getString("is_interest").equals("")) {
//                        holder.btn_interest.setVisibility(View.GONE);
//                        holder.tvInSent.setVisibility(View.VISIBLE);
//                        common.showToast("You already sent interest to this user.");
//
//                    } else {
//                        LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//
//                        final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
//                        //mContext.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
//                        final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);
//
//                        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
//                        dialog.setContentView(vv);
//                        dialog.show();
//
//                        ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
//                        tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
//                        Button send = vv.findViewById(R.id.btn_send_intr);
//                        send.setOnClickListener(view12 -> {
//                            dialog.dismiss();
//                            if (grp_interest.getCheckedRadioButtonId() != -1) {
//                                RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
////                                    interestRequest(item.getMatri_id(), btn.getText().toString().trim(), holder.btn_interest, holder.tvInSent);
//                                itemListener.interestRequest(item.getMatri_id(), btn.getText().toString().trim(), holder.btn_interest, holder.tvInSent);
//
//                            }
//                        });
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            });
//
//
//            holder.img_more.setOnClickListener(view13 -> {
//                try {
//                    showFilterPopup(view13, item.getMatri_id(), item.getAction().getString("is_like"), item.getName(),
//                            item.getAction().getInt("is_block"), item.getAction(), item);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            holder.tv_detail.setOnClickListener(view14 -> {
//                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
//                    Intent i = new Intent(mContext, OtherUserProfileActivity.class);
//                    i.putExtra("other_id", item.getId());
//                    mContext.startActivity(i);
//                } else {
//                    //   common.showToast("Please upgrade your membership to view this profile.");
////                    mContext.startActivity(new Intent(mContext, PlanListActivity.class));
//                    Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
//                    in.putExtra("other_id", item.getId());
//                    mContext.startActivity(in);
//                }
//            });
//
//            holder.tv_name.setOnClickListener(view15 -> {
//                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
//                    Intent i = new Intent(mContext, OtherUserProfileActivity.class);
//                    i.putExtra("other_id", item.getId());
//                    mContext.startActivity(i);
//                } else {
//                    //     common.showToast("Please upgrade your membership to view this profile.");
////                    mContext.startActivity(new Intent(mContext, PlanListActivity.class));
//                    Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
//                    in.putExtra("other_id", item.getId());
//                    mContext.startActivity(in);
//                }
//            });
//            holder.img_profile.setOnClickListener(view16 -> {
//                if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
//                    itemListener.alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
//                } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
//                    final Dialog dialog = new Dialog(mContext);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.setContentView(R.layout.show_image_alert);
//                    TouchImageView img_url = dialog.findViewById(R.id.img_url);
//                    Picasso.get().load(item.getImage()).into(img_url);
//                    dialog.show();
//                } else {
//                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
//                        Intent i = new Intent(mContext, OtherUserProfileActivity.class);
//                        i.putExtra("other_id", item.getId());
//                        mContext.startActivity(i);
//                    } else {
//                        //    common.showToast("Please upgrade your membership to view this profile.");
////                        mContext.startActivity(new Intent(mContext, PlanListActivity.class));
//                        Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
//                        in.putExtra("other_id", item.getId());
//                        mContext.startActivity(in);
//                    }
//
//                }
//
//            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handlePlanBadge(ViewHolder holder, DashboardItem item) {
        if (item.getBadge() != null && item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
            Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                    .placeholder(R.drawable.ic_transparent_placeholder)
                    .error(R.drawable.ic_transparent_placeholder)
                    .into(holder.imgPLanStamp);
            holder.imgPLanStamp.setVisibility(View.VISIBLE);
        } else {
            holder.imgPLanStamp.setVisibility(View.GONE);
        }
    }

    private void handleShortlistIcon(ViewHolder holder, DashboardItem item) {
        try {
            int shortlistIcon = (item.getAction().getInt("is_shortlist") == 1) ? R.drawable.ic_shortlistfill : R.drawable.ic_shortliststroke;
            holder.ivShort.setImageResource(shortlistIcon);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleInterestButton(ViewHolder holder, DashboardItem item) {
        try {
            if (!item.getAction().getString("is_interest").equals("")) {
                holder.btn_interest.setVisibility(View.GONE);
                holder.tvInSent.setVisibility(View.VISIBLE);
            } else {
                holder.btn_interest.setImageResource(R.drawable.send_interest);
                holder.btn_interest.setVisibility(View.VISIBLE);
                holder.tvInSent.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void handleUserInfo(ViewHolder holder, DashboardItem item) {
        String name = common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))
                ? item.getFirst_name().charAt(0) + " " + item.getLastname()
                : item.getFirst_name() + " " + item.getLastname();
        holder.tv_name.setText(name);
        holder.tv_mid.setText(item.getMatri_id());

        String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                item.getMTongueName(), item.getCaste(),
                item.getEducation(), item.getOccupation(), item.getCity(), item.getState(), 31);
        holder.tv_detail.setText(description);
    }

    private void handleProfileImage(ViewHolder holder, DashboardItem item) {
        common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(),
                item.getPhotoUrl() + item.getImage(), holder.img_profile, null, 68);
    }

    private void setClickListeners(ViewHolder holder, DashboardItem item) {
        holder.btn_chat.setOnClickListener(view -> openChatActivity(item));

        holder.ivShort.setOnClickListener(view -> toggleShortlist(item, holder));

        holder.btn_interest.setOnClickListener(view -> sendInterest(item, holder));

        holder.img_more.setOnClickListener(view -> showFilterPopup(view, item));

        holder.tv_detail.setOnClickListener(view -> openProfileActivity(item));

        holder.tv_name.setOnClickListener(view -> openProfileActivity(item));

        holder.img_profile.setOnClickListener(view -> openProfileImageDialog(item));
    }

    private void openChatActivity(DashboardItem item) {
        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
            Intent i = new Intent(mContext, ConversationActivity.class);
            i.putExtra("matri_id", item.getMatri_id());
            i.putExtra("username", item.getName());
            mContext.startActivity(i);
        } else {
            common.showToast("Please upgrade your membership to chat with this member.",llView);
            mContext.startActivity(new Intent(mContext, PlanListActivity.class));
        }
    }

    private void toggleShortlist(DashboardItem item, ViewHolder holder) {
        try {
            if (item.getAction().getInt("is_shortlist") == 1) {
                itemListener.shortlistRequest("remove", item.getMatri_id(), holder.ivShort, item);
            } else {
                itemListener.shortlistRequest("add", item.getMatri_id(), holder.ivShort, item);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendInterest(DashboardItem item, ViewHolder holder) {
        try {
            if (!item.getAction().getString("is_interest").equals("")) {
                holder.btn_interest.setVisibility(View.GONE);
                holder.tvInSent.setVisibility(View.VISIBLE);
                common.showToast("You already sent interest to this user.",llView);
            } else {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                final View vv = inflater.inflate(R.layout.bottom_sheet_interest, null, true);
                final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(vv);
                dialog.show();

                vv.findViewById(R.id.tv_cancel).setOnClickListener(view -> dialog.dismiss());
                vv.findViewById(R.id.btn_send_intr).setOnClickListener(view -> {
                    dialog.dismiss();
                    if (grp_interest.getCheckedRadioButtonId() != -1) {
                        RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                        itemListener.interestRequest(item.getMatri_id(), btn.getText().toString().trim(), holder.btn_interest, holder.tvInSent);
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showFilterPopup(View v, final String id, String is_like, String name, int is_block, JSONObject action, DashboardItem itemv) {
        PopupMenu popup = new PopupMenu(mContext, v);
        popup.inflate(R.menu.discover_more);
        MenuItem it = popup.getMenu().findItem(R.id.block);
        if (is_block == 1) {
            it.setTitle("Unblock Member");
        } else {
            it.setTitle("Block");
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.report:
                    mContext.startActivity(new Intent(mContext, ReportMissuseActivity.class));
                    return true;
                case R.id.view_profile:
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                        i.putExtra("other_id", itemv.getId());
                        mContext.startActivity(i);
                    } else {
                        Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", itemv.getId());
                        mContext.startActivity(in);
                    }
                    return true;
                case R.id.block:
                    setBlock(popup, R.id.block, is_block, id, action, itemv);
                    return true;
                default:
                    return false;
            }
        });


        popup.show();
    }


    private void showFilterPopup(View view, DashboardItem item) {
        try {
            showFilterPopup(view, item.getMatri_id(), item.getAction().getString("is_like"), item.getName(),
                    item.getAction().getInt("is_block"), item.getAction(), item);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void openProfileActivity(DashboardItem item) {
        if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
            Intent i = new Intent(mContext, OtherUserProfileActivity.class);
            i.putExtra("other_id", item.getId());
            mContext.startActivity(i);
        } else {
            Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
            in.putExtra("other_id", item.getId());
            mContext.startActivity(in);
        }
    }

    private void openProfileImageDialog(DashboardItem item) {
        if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
            itemListener.alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
        } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
            final Dialog dialog = new Dialog(mContext);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.show_image_alert);
            TouchImageView img_url = dialog.findViewById(R.id.img_url);
            Picasso.get().load(item.getImage()).into(img_url);
            dialog.show();
        } else {
            openProfileActivity(item);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imgPLanStamp;
        TextView tv_name;
        TextView tv_detail;
        ImageView img_profile;
        ImageView btn_chat, btn_interest, tvInSent;
        LinearLayout btnInterest, btnShortlist;
        TextView tvShortlist, tvMaitId, tv_view_count, tvPostedBy,tv_mid;
        ImageView ivShortlist, ivConnected, img_more, ivShort;

        public ViewHolder(@NonNull View rowView) {
            super(rowView);
            ivShort = rowView.findViewById(R.id.ivShort);
            cardView = rowView.findViewById(R.id.cardView);
            imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            tvInSent = rowView.findViewById(R.id.tvInSent);
            tv_name = rowView.findViewById(R.id.tv_name);
            tv_mid = rowView.findViewById(R.id.tv_mid);
            tv_detail = rowView.findViewById(R.id.tv_detail);
            img_profile = rowView.findViewById(R.id.img_profile);
            btnInterest = rowView.findViewById(R.id.btnInterest);
            btn_chat = rowView.findViewById(R.id.btn_chat);
            btnShortlist = rowView.findViewById(R.id.btnShortlist);
            tvShortlist = rowView.findViewById(R.id.tvShortlist);
            ivShortlist = rowView.findViewById(R.id.ivShortlist);
            ivConnected = rowView.findViewById(R.id.ivConnected);
            btn_interest = rowView.findViewById(R.id.btn_interest);
            tvMaitId = rowView.findViewById(R.id.tvMaitId);
            tv_view_count = rowView.findViewById(R.id.tv_view_count);
            tvPostedBy = rowView.findViewById(R.id.tvPostedBy);
            img_more = rowView.findViewById(R.id.img_more);
        }

        

        private void openScreenAsPerPlan(DashboardItem item) {
            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                i.putExtra("other_id", item.getId());
                mContext.startActivity(i);
            } else {
                common.showToast("Please upgrade your membership to chat with this member.",llView);
                mContext.startActivity(new Intent(mContext, PlanListActivity.class));
            }
        }
    }



    private void setBlock(PopupMenu popup, int interest, int is_block, String name, JSONObject action, DashboardItem itemv) {
        MenuItem item = popup.getMenu().findItem(interest);
        if (is_block == 1) {
            item.setTitle("Block");
            itemListener.blockRequest("remove", name, action, itemv);
        } else {
            item.setTitle("Blocked");
            itemListener.blockRequest("add", name, action, itemv);
        }
    }



}

