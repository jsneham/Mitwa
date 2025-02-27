package com.matriapp.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Response;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.matriapp.mobile.like.LikeButton;
import com.matriapp.mobile.like.OnLikeListener;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShortlistFragment extends Fragment {

    private ListView lv_short;
    private List<DashboardItem> list = new ArrayList<>();
    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private boolean continue_request;
    private TextView tv_no_data;
    private ShortAdapter adapter;
    private int page = 0;
    private int placeHolder, photoProtectPlaceHolder;

    private Context context;
    private SwipeRefreshLayout swipe;
    private ConstraintLayout llView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shortlist, container, false);
        context = getContext();
        common = new Common(context);
        session = new SessionManager(context);

        llView = view.findViewById(R.id.llView);
        progressBar = view.findViewById(R.id.progressBar);
        lv_short = view.findViewById(R.id.lv_short);
        tv_no_data = view.findViewById(R.id.tv_no_data);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }


        page = page + 1;
        getListData(page);

        adapter = new ShortAdapter(context, list,llView);
        lv_short.setAdapter(adapter);

        lv_short.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        if (progressBar != null)
                            common.hideProgressRelativeLayout(progressBar);
                        page = page + 1;
                        getListData(page);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {
            list.clear();
            page=0;
            page = page + 1;
            getListData(page);
        });

        return  view;
    }



    private void getListData(int page) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.shortlist_profile + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                common.hideProgressRelativeLayout(progressBar);
                swipe.setRefreshing(false);
                Log.d("resp", response);
                try {
                    JSONObject object = new JSONObject(response);
                    int total_count = object.getInt("total_count");
                    if (total_count != 0) {
                        tv_no_data.setVisibility(View.GONE);
                        lv_short.setVisibility(View.VISIBLE);
                        continue_request = object.getBoolean("continue_request");
                        if (list.size() != total_count) {
                            JSONArray data = object.getJSONArray("data");
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject obj = data.getJSONObject(i);
                                Log.d("TAG", "shortlist: "+ data );
                                if (common.isNotNullOrEmpty(obj.getString("matri_id") )) {
                                DashboardItem item = new DashboardItem();

                                    item.setId(obj.getString("id"));
                                    item.setMatri_id(obj.getString("matri_id"));
                                    item.setName(obj.getString("username"));
                                    JSONArray action = obj.getJSONArray("action");
                                    item.setAction(action.getJSONObject(0));


//                                    String description = Common.getDetailsFromValuePreferred(obj.getString("age"), obj.getString("height").replace("ft", "\'").replace("in", "\""),
//                                            obj.getString("caste_name"), obj.getString("religion_name"),
//                                            obj.getString("city_name"), "", obj.getString("country_name"), "", "", "");

//                                    item.setAbout(description);
                                    item.setImage_approval(obj.getString("photo1_approve"));
                                    item.setImage(obj.getString("photo1"));
                                    item.setUser_id(obj.getString("user_id"));
                                    item.setPhoto_view_status(obj.getString("photo_view_status"));
                                    item.setPhotoUrl(obj.getString("photoUrl"));
                                    // item.setPhoto_view_count(obj.getString("photo_view_count"));
                                    Log.d("resp", obj.getString("photo_view_status") + "  " + obj.getString("matri_id") +
                                            "    " + obj.getString("photo1") + "   " + obj.getString("photo1_approve"));

//                                    item.setOccupation(obj.getString("occupation_name"));
//                                    item.setMTongueName(obj.getString("mtongue_name"));
                                    item.setAge(obj.getString("age"));
                                    item.setCaste(obj.getString("caste_name"));
                                    item.setCity(obj.getString("city_name"));
                                    item.setState(obj.getString("state_name"));
                                    item.setEducation(obj.getString("education_name"));
                                    item.setHeight(obj.getString("height").replace("ft", "\'").replace("in", "\""));

                                    item.setBadge(obj.getString("badge"));
                                    item.setBadgeUrl(obj.getString("badgeUrl"));
                                    item.setPlan_status(obj.getString("plan_status"));
                                    list.add(item);
                                }
                            }
                            adapter.notifyDataSetChanged();

                            if(list.size()==0){
                                tv_no_data.setVisibility(View.VISIBLE);
                                lv_short.setVisibility(View.GONE);
                            }
                        }

                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        lv_short.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
//                    common.showToast(getString(R.string.err_msg_try_again_later));/
                }
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private String checkData(String text) {
        if (!text.equals("") && !text.equals("null")) {
            return text + " , ";
        }
        return "";
    }

    public class ShortAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        View llView;

        public ShortAdapter(Context context, List<DashboardItem> list, View llView) {
            super(context, R.layout.short_list_item, list);
            this.context = context;
            this.list = list;
            this.llView = llView;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            View rowView = null;
            try {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.short_list_item, null, true);

                TextView tv_id = rowView.findViewById(R.id.tv_id);
                TextView tv_name = rowView.findViewById(R.id.tv_name);
                TextView tv_detail = rowView.findViewById(R.id.tv_detail);
                ImageView img_profile = rowView.findViewById(R.id.img_profile);
                ImageView imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
                LikeButton btn_short = rowView.findViewById(R.id.btn_short);

                final DashboardItem item = list.get(position);


                if(item.getBadge()!=null) {
                    if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                        Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                                .placeholder(R.drawable.ic_transparent_placeholder)
                                .error(R.drawable.ic_transparent_placeholder)
                                .into(imgPLanStamp);
                        imgPLanStamp.setVisibility(View.VISIBLE);
                    } else {
                        imgPLanStamp.setVisibility(View.GONE);
                    }
                }
                else {
                    imgPLanStamp.setVisibility(View.GONE);
                }

                tv_id.setText(item.getMatri_id().toUpperCase());
               // tv_name.setText(item.getName());
                String [] Name= item.getName().split(" ");
                if(common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    if (Name.length == 3) {
                        tv_name.setText(Name[0].charAt(0) + " " + Name[1]+ " " + Name[2]);
                    }
                    else if (Name.length == 2) {
                        tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                    } else {
                        tv_name.setText(Name[0].charAt(0));
                    }
                } else
                    tv_name.setText(item.getName());

                common.setImage(item.getPhoto_view_count(),item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl()+item.getImage(), img_profile, null,68);


                //String about=item.getAbout()+"...<font color='#ff041a'>Read More</font>";
//                tv_detail.setText(item.getAbout());

                String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                        item.getMTongueName(), item.getCaste(),
                        item.getEducation(), item.getOccupation(), item.getCity(), item.getState(),35);
                tv_detail.setText(description);

                img_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                            alertPhotoPassword(item.getMatri_id());
                        } else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                            final Dialog dialog = new Dialog(context);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                            dialog.setContentView(R.layout.show_image_alert);
                            TouchImageView img_url = dialog.findViewById(R.id.img_url);
                            Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_url);
                            dialog.show();
                        } else {
                            if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                                Intent i = new Intent(context, OtherUserProfileActivity.class);
                                i.putExtra("other_id", item.getUser_id());
                                context.startActivity(i);
                            } else {
                                //          common.showToast("Please upgrade your membership to view this profile.");
//                                context.startActivity(new Intent(context, PlanListActivity.class));
                                Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                                in.putExtra("other_id", item.getUser_id());
                                context.startActivity(in);
                            }
                        }
                    }
                });

                tv_detail.setOnClickListener(view1 -> {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getUser_id());
                        context.startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to chat with this member.",llView);
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }
                });
                tv_name.setOnClickListener(view12 -> {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getUser_id());
                        context.startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to chat with this member.",llView);
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }
                });
                btn_short.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        removeShortlist(position, item.getMatri_id());
                    }
                });

                final LinearLayout btnInterest = rowView.findViewById(R.id.btnInterest);
                LinearLayout btn_chat = rowView.findViewById(R.id.btn_chat);
                LinearLayout btnShortlist = rowView.findViewById(R.id.btnShortlist);
                TextView tvShortlist = rowView.findViewById(R.id.tvShortlist);
                ImageView ivShortlist = rowView.findViewById(R.id.ivShortlist);
                ImageView ivConnected = rowView.findViewById(R.id.ivConnected);
                final TextView btn_interest = rowView.findViewById(R.id.btn_interest);


                if (item.getAction().getInt("is_shortlist") == 1) {
                    tvShortlist.setTextColor(getResources().getColor(R.color.shortllist));
                    ivShortlist.setImageResource(R.drawable.in_shorted);
                    tvShortlist.setText("Shortlisted");
                } else {
                    ivShortlist.setImageResource(R.drawable.in_shortlist);
                    tvShortlist.setTextColor(getResources().getColor(R.color.registration_hint_color));
                    tvShortlist.setText("Shortlist");

                }

                if (!item.getAction().getString("is_interest").equals("")) {
                    ivConnected.setImageResource(R.drawable.sl_connected);
                    btn_interest.setText(R.string.requested);
                    btn_interest.setTextColor(getResources().getColor(R.color.online));
                } else {
                    btn_interest.setText(R.string.send_interest);
                }

                btn_chat.setOnClickListener(view1 -> {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, ConversationActivity.class);
                        i.putExtra("matri_id", item.getMatri_id());
                        i.putExtra("username", item.getName());
                        startActivity(i);
                    } else {
                        common.showToast("Please upgrade your membership to chat with this member.",llView);
                        context.startActivity(new Intent(context, PlanListActivity.class));
                    }

                });

                btnShortlist.setOnClickListener(view12 -> {
                    try {
                        removeShortlist(position, item.getMatri_id());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                btnInterest.setOnClickListener(view14 -> {
                    try {
                        if (item.getAction().getString("is_interest").equals("")) {
                            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                            final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                            final BottomSheetDialog dialog = new BottomSheetDialog(context);
                            dialog.setContentView(vv);
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
//                                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
//                                    common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
                                }
                            });
                            dialog.show();



                            ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                            tv_cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                            Button send = vv.findViewById(R.id.btn_send_intr);
                            send.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                    if (grp_interest.getCheckedRadioButtonId() != -1) {
                                        RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                        interestRequest(item.getMatri_id(), btn.getText().toString().trim(), btn_interest, btnInterest,ivConnected);
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

            } catch (JSONException e) {
                e.printStackTrace();
            }


            return rowView;

        }
    }

    private void interestRequest(String matri_id, String int_msg, final TextView btn_interest, final LinearLayout btnInterest, final ImageView ivConnected) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(AppConstants.send_interest, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    //  button.setLiked(true);
//                    btnInterest.setBackground(getResources().getDrawable(R.drawable.btn_inter_filled));
                    btn_interest.setText(R.string.requested);
                    btn_interest.setTextColor(getResources().getColor(R.color.online));
                    ivConnected.setImageResource(R.drawable.sl_connected);

                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }



    private void alertPhotoPassword(final String matri_id) {
        final String[] arr = new String[]{"We found your profile to be a good match. Please send me Photo password to proceed further.",
                "I am interested in your profile. I would like to view photo now, send me password."};
        final String[] selected = {"We found your profile to be a good match. Please send me Photo password to proceed further."};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

        alt_bld.setTitle("Photos View Request");
        alt_bld.setSingleChoiceItems(arr, 0, (dialog, item) -> {

            //dialog.dismiss();// dismiss the alertbox after chose option
            selected[0] = arr[item];
        });
        alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0], matri_id));
        alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
            //alertpassword(password,url);
        });
        AlertDialog alert = alt_bld.create();
        alert.show();

    }

    private void sendRequest(String int_msg, String matri_id) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequestTime(AppConstants.photo_password_request, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void removeShortlist(final int position, String id) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("shortlist_action", "remove");
        param.put("shortlisteduserid", id);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);

                if (object.getString("status").equals("success")) {
                    ApplicationData.getSharedInstance().isProfileChanged = true;
                    common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.star_gray_fill);
                    list.remove(position);
                    if (list.size() == 0) {
                        tv_no_data.setVisibility(View.VISIBLE);
                        lv_short.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

}