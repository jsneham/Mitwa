package com.matriapp.mobile.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.model.DashboardItemNew;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.like.LikeButton;
import com.matriapp.mobile.like.OnLikeListener;
import com.matriapp.mobile.shadow.ShadowView;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.MemberAction;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;


import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class CommonListAdapterNew extends RecyclerView.Adapter<CommonListAdapterNew.ViewHolder> {
    public Context mContext;
    private List<DashboardItemNew> arrayList;
    private ItemListener myListener;

    int placeHolder = 0;
    private Common common;
    private SessionManager session;
    private RelativeLayout llView;

    public CommonListAdapterNew(Context mContext, List<DashboardItemNew> arrayList, RelativeLayout llView) {
        if (mContext == null) return;
        this.mContext = mContext;
        this.arrayList = arrayList;
        this.llView = llView;
        this.common = new Common(mContext);
        session = new SessionManager(mContext);
    }

    public void setListener(ItemListener listener) {
        myListener = listener;
    }

    public interface ItemListener {
        void alertPhotoPassword(String matriId);

        void likeRequest(String value, String name, int position);

        void shortlistRequest(String value, String name);

        void interestRequest(String value, String name, LikeButton button);

        void blockRequest(String value, String name);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(inflater.inflate(R.layout.search_result, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DashboardItemNew item = arrayList.get(position);
        holder.tvMaitId.setText(item.getMatriId().toUpperCase());
        holder.tv_name.setText(item.getUsername());

//        if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS)))
//            holder.tv_name.setText(item.getFirst_name().charAt(0) + " " + item.getLastname());
//        else
//            holder.tv_name.setText(item.getFirst_name() + " " + item.getLastname());


//        String description = Common.getDetailsFromValueSearchResult(item.getAge().toLowerCase(),
//                item.getHeight().replace("ft", "\'").replace("in", "\""),
//                item.getCasteName(), item.getReligionName(),
//                item.getCityName(), item.getStateName(), item.getCountryName(), item.getEducationName(), item.getOccupationName());

        String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                item.getMTongueName(), item.getCasteName(),
                item.getEducationName(), item.getOccupationName(), item.getCityName(), item.getStateName(),28);
        holder.tv_detail.setText(description);

        common.setImage(String.valueOf(item.getPhotoViewCount()), item.getPhotoViewStatus(), item.getPhoto1Approve(),
                item.getPhotoUrl() + item.getPhoto1(), holder.img_profile, null, 68);

        if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
            Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                    .placeholder(R.drawable.ic_transparent_placeholder)
                    .error(R.drawable.ic_transparent_placeholder)
                    .into(holder.imgPLanStamp);
            holder.imgPLanStamp.setVisibility(View.VISIBLE);
        } else {
            holder.imgPLanStamp.setVisibility(View.GONE);
        }


        try {

            holder.tvPostedBy.setText(mContext.getString(R.string.postedby) + " " + item.getProfileby());
            holder.tv_view_count.setText(" " + item.getAction().get(0).getIsView());

            if (item.getAction().get(0).getIsShortlist()== 1) {
                holder.tvShortlist.setTextColor(mContext.getResources().getColor(R.color.shortllist));
                holder.ivShortlist.setImageResource(R.drawable.rl_shorted);
                holder.tvShortlist.setText("Shortlisted");
            }
            else {
                holder.ivShortlist.setImageResource(R.drawable.rl_shortlist);
                holder.tvShortlist.setTextColor(mContext.getResources().getColor(R.color.registration_hint_color));
//
                holder.tvShortlist.setText("Shortlist");

                if (!item.getAction().get(0).getIsInterest().equals("")) {
                    holder.ivConnected.setImageResource(R.drawable.rl_connected);
                    //btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_inter_filled));
                    holder.btn_interest.setText(R.string.requested);
                    holder.btn_interest.setTextColor(mContext.getResources().getColor(R.color.online));
                    // btn_interest.setLiked(true);
                } else {
                    holder.btn_interest.setText(R.string.send_interest);
                    //btn_interest.setLiked(false);
                }

            }

            holder.img_more.setOnClickListener(view13 -> {
                try {
                    showFilterPopup(view13, item.getId(), item.getAction().get(0).getIsLike(), item.getUsername(),
                            item.getAction().get(0).getIsBlock(), item.getAction().get(0),item);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


            holder.btnShortlist.setOnClickListener(view12 -> {
                try {
                    if (item.getAction().get(0).getIsShortlist() == 1) {
                        shortlistRequest("remove", item.getUsername(), holder.tvShortlist, holder.btnShortlist, holder.ivShortlist,item);
                    } else {
                        shortlistRequest("add", item.getUsername(), holder.tvShortlist, holder.btnShortlist, holder.ivShortlist,item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            holder.btnInterest.setOnClickListener(view14 -> {
                try {
                    if (item.getAction().get(0).getIsInterest().equals("")) {
                        LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                        final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                        final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                        dialog.setContentView(vv);
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface dialogInterface) {
                                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
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
                                    interestRequest(item.getMatriId(), btn.getText().toString().trim(), holder.btn_interest, holder.btnInterest,holder.ivConnected);
                                }
                            }
                        });
                    } else {
                        common.showToast("You already sent interest to this user.",llView);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, OnLikeListener {
        CardView cardView;
        ImageView imgPLanStamp;
        TextView tv_name;
        TextView tv_detail;
        ImageView img_profile;

//        LikeButton btn_interest;
//        LikeButton btn_like;
//        LikeButton btn_block;
//        LikeButton btn_chat;
//        LikeButton btn_short;

        LinearLayout btnInterest, btn_chat, btnShortlist;
        TextView tvShortlist, btn_interest, tvMaitId, tv_view_count, tvPostedBy;
        ImageView ivShortlist, ivConnected,img_more;

        public ViewHolder(@NonNull View rowView) {
            super(rowView);

            cardView = rowView.findViewById(R.id.cardView);
            imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            tv_name = rowView.findViewById(R.id.tv_name);
            tv_detail = rowView.findViewById(R.id.tv_detail);
            img_profile = rowView.findViewById(R.id.img_profile);
            btnInterest = rowView.findViewById(R.id.btnInterest);
//            btn_like = rowView.findViewById(R.id.btn_like);
//            btn_block = rowView.findViewById(R.id.btn_id);
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

            img_profile.setOnClickListener(this);
            tv_detail.setOnClickListener(this);
            tv_name.setOnClickListener(this);
            btn_chat.setOnClickListener(this);
            img_more.setOnClickListener(this);

//            btn_like.setOnLikeListener(this);
//            btn_block.setOnLikeListener(this);
//            btn_interest.setOnLikeListener(this);
//            btn_short.setOnLikeListener(this);
        }

        @Override
        public void onClick(View view) {
            DashboardItemNew item = arrayList.get(getAbsoluteAdapterPosition());
            if (view.getId() == R.id.img_profile) {
                if (item.getPhotoViewStatus().equals("0") && item.getPhotoViewCount()==0) {
                    myListener.alertPhotoPassword(item.getMatriId());
                } else if (item.getPhotoViewStatus().equals("0") && item.getPhotoViewCount()==1 && item.getPhoto1().equals("APPROVED")) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.show_image_alert);
                    TouchImageView img_url = dialog.findViewById(R.id.img_url);
                    Picasso.get().load(item.getPhoto1()).placeholder(placeHolder).error(placeHolder).into(img_url);
                    dialog.show();
                } else {
                    openScreenAsPerPlan(item);
                }
            } else if (view.getId() == R.id.btn_chat) {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(mContext, ConversationActivity.class);
                    i.putExtra("matri_id", item.getMatriId());
                    i.putExtra("username", item.getUsername());
                    mContext.startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to chat with this member.",llView);
                    mContext.startActivity(new Intent(mContext, PlanListActivity.class));
                }
            } else if (view.getId() == R.id.tv_detail) {
                openScreenAsPerPlan(item);
            } else if (view.getId() == R.id.tv_name) {
                openScreenAsPerPlan(item);
            }
        }

        @Override
        public void liked(LikeButton likeButton) {
            if (arrayList.size() == 0) return;
            DashboardItemNew item = arrayList.get(getAbsoluteAdapterPosition());
            if (likeButton.getId() == R.id.btn_like) {
                likeRequest("Yes", item);
            } else if (likeButton.getId() == R.id.btn_interest) {
                likeButton.setLiked(false);
                LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);
                final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                dialog.setContentView(vv);
                dialog.show();
                ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                Button send = vv.findViewById(R.id.btn_send_intr);
                send.setOnClickListener(view12 -> {
                    dialog.dismiss();
                    if (grp_interest.getCheckedRadioButtonId() != -1) {
                        RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                        myListener.interestRequest(item.getUsername(), btn.getText().toString().trim(), likeButton);
                    }
                });
            } else if (likeButton.getId() == R.id.btn_short) {
                myListener.shortlistRequest("add", item.getUsername());
            } else if (likeButton.getId() == R.id.btn_id) {
                blockRequest(1, "add", item);
            }
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            if (arrayList.size() == 0) return;
            DashboardItemNew item = arrayList.get(getAbsoluteAdapterPosition());
            if (likeButton.getId() == R.id.btn_like) {
                likeRequest("No", item);
            } else if (likeButton.getId() == R.id.btn_interest) {
                likeButton.setLiked(true);
                common.showToast("You already sent interest to this user.",llView);
            } else if (likeButton.getId() == R.id.btn_short) {
                myListener.shortlistRequest("remove", item.getUsername());
            } else if (likeButton.getId() == R.id.btn_id) {
                blockRequest(0, "remove", item);
            }
        }

        private void blockRequest(int val, String value, DashboardItemNew item) {
            try {
                item.getAction().get(0).setIsBlock(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
            myListener.blockRequest(value, item.getUsername());
        }

        private void likeRequest(String value, DashboardItemNew item) {
            try {
                item.getAction().get(0).setIsLike(value);
            } catch (Exception e) {
                e.printStackTrace();
            }
            myListener.likeRequest(value, item.getUsername(), getAbsoluteAdapterPosition());
        }

        private void openScreenAsPerPlan(DashboardItemNew item) {
            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                i.putExtra("other_id", item.getId());
                mContext.startActivity(i);
            } else {
                Intent i = new Intent(mContext, PreviewOthersProfileActivity.class);
                i.putExtra("other_id", item.getId());
                mContext.startActivity(i);
            }
        }
    }


    private void showFilterPopup(View v, final String id, String is_like, String name, int is_block, MemberAction action, DashboardItemNew itemv) {
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
                        i.putExtra("other_id", id);
                        mContext.startActivity(i);
                    } else {
                       Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", id);
                        mContext.startActivity(in);
                    }
                    return true;
                case R.id.like:
                    setLike(popup, R.id.like, is_like, name);
                    return true;
                case R.id.block:
                    setBlock(popup, R.id.block, is_block, name, action,itemv);
                    return true;
                default:
                    return false;
            }
        });



        popup.show();
    }

    private void setLike(PopupMenu popup, int likeButton, String is_like, String name) {
        MenuItem item = popup.getMenu().findItem(likeButton);
        if (is_like.equals("No")) {
            item.setTitle("Liked Profile");
            likeRequest("Yes", name);
        } else {
            likeRequest("No", name);
            item.setTitle("Like Profile");
        }
    }

    private void likeRequest(final String tag, String matri_id) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequestTime(AppConstants.like_profile, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("Yes")) {
                    common.showAlert("Like", object.getString("errmessage"), R.drawable.heart_fill_pink);
                } else
                    common.showAlert("Unlike", object.getString("errmessage"), R.drawable.heart_gray_fill);
                if (object.getString("status").equals("success")) {

                }
            } catch (Exception e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


    private void setBlock(PopupMenu popup, int interest, int is_block, String name, MemberAction action, DashboardItemNew itemv) {
        MenuItem item = popup.getMenu().findItem(interest);
        if (is_block == 1) {
            item.setTitle("Block");
            blockRequest("remove", name,action,itemv);
        } else {
            item.setTitle("Blocked");
            blockRequest("add", name,action,itemv);
        }
    }

    private void blockRequest(final String tag, String id, MemberAction action, DashboardItemNew itemv) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequestTime(AppConstants.block_user, param, response -> {
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("add")) {
                    common.showAlert("Block", object.getString("errmessage"), R.drawable.ban);
                   // action.remove("is_block");
                    action.setIsBlock(1);
                } else {
                    common.showAlert("Unblock", object.getString("errmessage"), R.drawable.ban_gry);
//                    action.remove("is_block");
                    action.setIsBlock(0);
                }
//                itemv.setAction(action);
            } catch (Exception e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void shortlistRequest(final String tag, String id, TextView ivShort, LinearLayout btnShortlist, ImageView ivShortlist, DashboardItemNew item) {

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", item.getMatriId());
        } else
            param.put("shortlistuserid", item.getMatriId());

        param.put("shortlist_action", tag);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
                MemberAction action = item.getAction().get(0);
                if (object.getString("status").equals("success")) {
                    if (tag.equals("add")) {
//                    action.remove("is_shortlist");
                        action.setIsShortlist(1);

                        //btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short_filled));
                        ivShort.setTextColor(mContext.getResources().getColor(R.color.shortllist));
                        ivShort.setText("Shortlisted");
                        ivShortlist.setImageResource(R.drawable.rl_shorted);
                        common.showAlert("Shortlist", "Nice! Profile Shortlisted.", R.drawable.rl_shorted);
                    } else {
//                    action.remove("is_shortlist");
                        action.setIsShortlist(0);
                        ivShortlist.setImageResource(R.drawable.rl_shortlist);
                        ivShort.setTextColor(mContext.getResources().getColor(R.color.registration_hint_color));
                        // btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short));
                        ivShort.setText("Shortlist");
                        common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.rl_shortlist);
                    }
                } else {
                    common.showAlert("Shortlist", object.getString("errmessage").toString(), R.drawable.rl_shorted);
                }


            } catch (Exception e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);


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
                    ivConnected.setImageResource(R.drawable.rl_connected);

                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

            } catch (Exception e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }



}
