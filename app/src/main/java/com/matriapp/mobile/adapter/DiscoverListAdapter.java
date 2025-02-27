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

public class DiscoverListAdapter extends RecyclerView.Adapter<DiscoverListAdapter.ViewHolder> {
    public Context mContext;
    private List<DashboardItem> arrayList;
    private ItemListener myListener;

    int placeHolder = 0;
    private Common common;
    private SessionManager session;
    private FrameLayout llView;

    public DiscoverListAdapter(Context mContext, List<DashboardItem> arrayList, FrameLayout llView) {
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
        return new ViewHolder(inflater.inflate(R.layout.discover_list_item, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        try {
            final DashboardItem item = arrayList.get(position);

            try {

                if(item.getBadge()!=null) {
                    if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                        Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                                .placeholder(R.drawable.ic_transparent_placeholder)
                                .error(R.drawable.ic_transparent_placeholder)
                                .into(holder.imgPLanStamp);
                        holder.imgPLanStamp.setVisibility(View.VISIBLE);
                    } else {
                        holder.imgPLanStamp.setVisibility(View.GONE);
                    }
                }
                else {
                    holder.imgPLanStamp.setVisibility(View.GONE);
                }


                if (item.getAction().getInt("is_shortlist") == 1)
                    holder.ivShort.setImageResource(R.drawable.ic_shortlistfill);
                else
                    holder.ivShort.setImageResource(R.drawable.ic_shortliststroke);

                if (!(item.getAction().getString("is_interest").equals(""))) {
                    holder.btn_interest.setImageResource(R.drawable.ic_connectfillednewmargin);
                    holder.btn_interest.setVisibility(View.GONE);
                    holder.tvInSent.setVisibility(View.VISIBLE);
                } else
                    holder.btn_interest.setImageResource(R.drawable.send_interest);

                AppDebugLog.print("is online : " + item.getAction().getInt("is_login"));
//                if (item.getAction().getInt("is_login") == 1) {
//                    lay_online.setVisibility(View.VISIBLE);
//                    lay_ofline.setVisibility(View.GONE);
//                } else {
//                    lay_online.setVisibility(View.GONE);
//                    lay_ofline.setVisibility(View.VISIBLE);
//                }
//
//                tv_view_count.setText(" " + item.getAction().getString("is_view"));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            Drawable img_white = mContext.getResources().getDrawable(R.drawable.eye_pink);
            img_white.setBounds(0, 0, 40, 40);
            // tv_view_count.setCompoundDrawables(img_white, null, null, null);

            if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS)))
                holder.tv_name.setText(item.getFirst_name().charAt(0) + " " + item.getLastname());
            else
                holder.tv_name.setText(item.getFirst_name() + " " + item.getLastname());

            holder.tv_mid.setText(item.getMatri_id());

            common.setImage(item.getPhoto_view_count(),item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl()+item.getImage(), holder.img_profile, null,68);

            String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                    item.getMTongueName(), item.getCaste(),
                    item.getEducation(), item.getOccupation(), item.getCity(), item.getState(), 45);

            holder.tv_detail.setText(description);

            holder.btn_chat.setOnClickListener(view1 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(mContext, ConversationActivity.class);
                    i.putExtra("matri_id", item.getMatri_id());
                    i.putExtra("username", item.getName());
                    mContext.startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to chat with this member.",llView);
                    mContext.startActivity(new Intent(mContext, PlanListActivity.class));
                }
            });

            holder.ivShort.setOnClickListener(view12 -> {
                try {
                    if (item.getAction().getInt("is_shortlist") == 1) {
                        shortlistRequest("remove", item.getName(), holder.ivShort, item);
                    } else {
                        shortlistRequest("add", item.getName(), holder.ivShort, item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            holder.btn_interest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        if (!item.getAction().getString("is_interest").equals("")) {
                            holder.btn_interest.setImageResource(R.drawable.ic_connectfillednewmargin);
                            holder.btn_interest.setVisibility(View.GONE);
                            holder.tvInSent.setVisibility(View.VISIBLE);
                            common.showToast("You already sent interest to this user.",llView);

                        } else {
//                            holder.btn_interest.setImageResource(R.drawable.send_interest);
                            LayoutInflater inflater1 = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                            //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                            final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                            final BottomSheetDialog dialog = new BottomSheetDialog(mContext);
                            dialog.setContentView(vv);
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                    common.setupFullHeight(bottomSheetDialog, (Activity) mContext);
                                }
                            });
                            dialog.show();
                            ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                            tv_cancel.setOnClickListener(view13 -> dialog.dismiss());

                            Button send = vv.findViewById(R.id.btn_send_intr);
                            send.setOnClickListener(view12 -> {
                                dialog.dismiss();
                                if (grp_interest.getCheckedRadioButtonId() != -1) {
                                    RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                    interestRequest(item.getMatri_id(), btn.getText().toString().trim(), holder.btn_interest, holder.tvInSent);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            holder.img_more.setOnClickListener(view13 -> {
                try {
                    showFilterPopup(view13, item.getId(), item.getAction().getString("is_like"), item.getName(),
                            item.getAction().getInt("is_block"), item.getAction(), item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            holder.tv_detail.setOnClickListener(view14 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    mContext.startActivity(i);
                } else {
                    //   common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                    Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
                    in.putExtra("other_id", item.getId());
                    mContext.startActivity(in);
                }
            });

            holder.tv_name.setOnClickListener(view15 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    mContext.startActivity(i);
                } else {
                    //     common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                    Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
                    in.putExtra("other_id", item.getId());
                    mContext.startActivity(in);
                }
            });
            holder.img_profile.setOnClickListener(view16 -> {
                if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                    alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
                } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                    final Dialog dialog = new Dialog(mContext);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                    dialog.setContentView(R.layout.show_image_alert);
                    TouchImageView img_url = dialog.findViewById(R.id.img_url);
                    Picasso.get().load(item.getImage()).into(img_url);
                    dialog.show();
                } else {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(mContext, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getId());
                        mContext.startActivity(i);
                    } else {
                        //    common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                        Intent in = new Intent(mContext, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", item.getId());
                        mContext.startActivity(in);
                    }

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
            } else if (view.getId() == R.id.btn_chat) {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(mContext, ConversationActivity.class);
                    i.putExtra("matri_id", item.getMatri_id());
                    i.putExtra("username", item.getName());
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
            DashboardItem item = arrayList.get(getAbsoluteAdapterPosition());
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
                        myListener.interestRequest(item.getName(), btn.getText().toString().trim(), likeButton);
                    }
                });
            } else if (likeButton.getId() == R.id.btn_short) {
                myListener.shortlistRequest("add", item.getName());
            } else if (likeButton.getId() == R.id.btn_id) {
                blockRequest(1, "add", item);
            }
        }

        @Override
        public void unLiked(LikeButton likeButton) {
            if (arrayList.size() == 0) return;
            DashboardItem item = arrayList.get(getAbsoluteAdapterPosition());
            if (likeButton.getId() == R.id.btn_like) {
                likeRequest("No", item);
            } else if (likeButton.getId() == R.id.btn_interest) {
                likeButton.setLiked(true);
                common.showToast("You already sent interest to this user.",llView);
            } else if (likeButton.getId() == R.id.btn_short) {
                myListener.shortlistRequest("remove", item.getName());
            } else if (likeButton.getId() == R.id.btn_id) {
                blockRequest(0, "remove", item);
            }
        }

        private void blockRequest(int val, String value, DashboardItem item) {
            try {
                item.getAction().put("is_block", val);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myListener.blockRequest(value, item.getName());
        }

        private void likeRequest(String value, DashboardItem item) {
            try {
                item.getAction().put("is_like", value);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            myListener.likeRequest(value, item.getName(), getAbsoluteAdapterPosition());
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

    private void alertPhotoPassword(final String password, final String url, final String matri_id) {
        final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                "I am interested in your profile. I would like to view photo now, accept photo request."};
        final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);

        alt_bld.setTitle("Photos View Request");
        alt_bld.setSingleChoiceItems(arr, 0, new DialogInterface
                .OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                //dialog.dismiss();// dismiss the alertbox after chose option
                selected[0] = arr[item];
            }
        });
        alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
        alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
            //alertpassword(password,url);
        });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    private void sendRequest(String int_msg, String matri_id) {
//        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequestTime(AppConstants.photo_password_request, param, response -> {
//            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
//            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

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
                    setBlock(popup, R.id.block, is_block, name, action, itemv);
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
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


    private void setBlock(PopupMenu popup, int interest, int is_block, String name, JSONObject action, DashboardItem itemv) {
        MenuItem item = popup.getMenu().findItem(interest);
        if (is_block == 1) {
            item.setTitle("Block");
            blockRequest("remove", name, action, itemv);
        } else {
            item.setTitle("Blocked");
            blockRequest("add", name, action, itemv);
        }
    }

    private void blockRequest(final String tag, String id, JSONObject action, DashboardItem itemv) {
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
                    action.remove("is_block");
                    action.put("is_block", 1);
                } else {
                    common.showAlert("Unblock", object.getString("errmessage"), R.drawable.ban_gry);
                    action.remove("is_block");
                    action.put("is_block", 0);
                }
                itemv.setAction(action);
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void shortlistRequest(final String tag, String id, ImageView ivShort, DashboardItem item) {

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", item.getMatri_id());
        } else
            param.put("shortlistuserid", item.getMatri_id());

        param.put("shortlist_action", tag);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {

            try {
                JSONObject object = new JSONObject(response);
                JSONObject action = item.getAction();
                if (tag.equals("add")) {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 1);
                    ivShort.setImageResource(R.drawable.ic_shortlistfill);
                    common.showAlert("Shortlist", "Nice! Profile Shortlisted.", R.drawable.rl_shorted);
                } else {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 0);
                    ivShort.setImageResource(R.drawable.ic_shortliststroke);
                    common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.rl_shortlist);
                }
                if (object.getString("status").equals("success")) {

                }


            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(mContext.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);


    }


    private void interestRequest(String matri_id, String int_msg, final ImageView btn_interest, final ImageView ivConnected) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(AppConstants.send_interest, param, response -> {
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    btn_interest.setVisibility(View.GONE);
                    ivConnected.setVisibility(View.VISIBLE);
                    btn_interest.setImageResource(R.drawable.ic_connectfillednewmargin);
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.ic_connectfillednewmargin);
                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.ic_connectrestnewwithmargin);

            } catch (JSONException e) {
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
