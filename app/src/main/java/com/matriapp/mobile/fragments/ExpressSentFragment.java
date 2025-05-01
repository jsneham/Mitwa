package com.matriapp.mobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.matriapp.mobile.activities.ConversationActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.ExpressItem;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.CustomTypeFaceSpan;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpressSentFragment extends Fragment {
    private ListView lv_exp_sent;
    private TextView tv_no_data;
    private FrameLayout llView;
    private Spinner spin_exp_sent;
    private Context context;
    private List<ExpressItem> list = new ArrayList<>();
    private Sent_Adapter adapter;
    private Common common;
    private SessionManager session;
    private RelativeLayout loader;
    private boolean continue_request=true;
    private int page = 1;
    private String tag = "pending_sent";
    private int placeHolder, photoProtectPlaceHolder;
    private SwipeRefreshLayout swipe;

    public ExpressSentFragment(){

    }
    public ExpressSentFragment(String tag) {
        this.tag=tag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        common = new Common(getActivity());
        session = new SessionManager(getActivity());
        context = getActivity();

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_express_sent, container, false);
        loader = view.findViewById(R.id.loader);
        lv_exp_sent = view.findViewById(R.id.lv_exp_sent);
        spin_exp_sent = view.findViewById(R.id.spin_exp_sent);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        llView = view.findViewById(R.id.llView);

//        List<String> lst = new ArrayList<>();
//        lst.add("All Interest");
//        lst.add("Interest Sent Accept");
//        lst.add("Interest Sent Reject");
//        lst.add("Interest Sent Pending");
//
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, lst);
//        spin_exp_sent.setAdapter(arrayAdapter);
//
//        spin_exp_sent.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                page = 0;
//                page = page + 1;
//                continue_request = true;
//                list.clear();
//                switch (position) {
//                    case 0:
//                        getData(page, "all_sent");
//                        tag = "all_sent";
//                        break;
//                    case 1:
//                        getData(page, "accept_sent");
//                        tag = "accept_sent";
//                        break;
//                    case 2:
//                        getData(page, "reject_sent");
//                        tag = "reject_sent";
//                        break;
//                    case 3:
//                        getData(page, "pending_sent");
//                        tag = "pending_sent";
//                        break;
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        getData(page, tag);
        lv_exp_sent.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                    if (continue_request) {
                        page = page + 1;
                        getData(page, tag);
                        common.hideProgressRelativeLayout(loader);
                    }
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItemm, int visibleItemCountt, int totalItemCountt) {
                this.currentFirstVisibleItem = firstVisibleItemm;
                this.currentVisibleItemCount = visibleItemCountt;
                this.totalItem = totalItemCountt;
            }
        });

        adapter = new Sent_Adapter(getActivity(), list, tag,llView);
        lv_exp_sent.setAdapter(adapter);


        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {
            list.clear();
            page=0;
            page = page + 1;
            getData(page, tag);
        });


        return view;
    }

    private void getData(int page, String tag) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("exp_status", tag);

        common.makePostRequest(AppConstants.express_interest + page, param, response -> {
            common.hideProgressRelativeLayout(loader);
            swipe.setRefreshing(false);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                int total_count = object.getInt("total_count");
                continue_request = object.getBoolean("continue_request");

                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    lv_exp_sent.setVisibility(View.VISIBLE);
                    if (total_count != list.size()) {
                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            if (common.isNotNullOrEmpty(obj.getString("matri_id") )) {
                            ExpressItem item = new ExpressItem();

                                item.setId(obj.getString("id"));
                                item.setMatri_id(obj.getString("matri_id"));
                                item.setAge(obj.getString("age"));
                                item.setUser_id(obj.getString("user_id"));
                                item.setUsername(obj.getString("username"));
                                item.setHeight(obj.getString("height"));
                                item.setCaste_name(obj.getString("caste_name"));
                                item.setReligion_name(obj.getString("religion_name"));
                                item.setState_name(obj.getString("state_name"));
                                item.setCity_name(obj.getString("city_name"));
                                item.setCountry_name(obj.getString("country_name"));
                                item.setMtongue_name(obj.getString("mtongue_name"));
                                item.setOccupation_name(obj.getString("occupation_name"));
                                item.setAbout(obj.getString("message"));
                                item.setReceiver_response(obj.getString("receiver_response"));
                                item.setImage(obj.getString("photo1"));
                                item.setImage_approval(obj.getString("photo1_approve"));
                                item.setPhoto_view_status(obj.getString("photo_view_status"));
                                JSONArray action = obj.getJSONArray("action");
                                item.setAction(action.getJSONObject(0));
                                item.setBadge(obj.getString("badge"));
                                item.setBadgeUrl(obj.getString("badgeUrl"));
                                item.setColor(obj.getString("color"));
                                item.setPhotoUrl(obj.getString("photoUrl"));
                                item.setName(obj.getString("username"));
                                item.setPlan_status(obj.getString("plan_status"));
//                                item.setEducation_name(obj.getString("education_name"));
                                list.add(item);
                            }
                        }
                        if(list.size() < 10) continue_request = false;
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    lv_exp_sent.setVisibility(View.GONE);
                    tv_no_data.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    private class Sent_Adapter extends ArrayAdapter<ExpressItem> {

        Context context;
        List<ExpressItem> list;
        String tag;
        FrameLayout llView;

        public Sent_Adapter(Context context, List<ExpressItem> list, String tag, FrameLayout llView) {
            super(context, R.layout.express_item, list);
            this.context = context;
            this.list = list;
            this.llView = llView;
            this.tag = tag;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.express_item, null, true);

            LinearLayout llShortlist = rowView.findViewById(R.id.llShortlist);
            ImageView imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);
            ImageView img_more = rowView.findViewById(R.id.img_more);
            ImageView ivShortlist = rowView.findViewById(R.id.ivShortlist);
            TextView tvShortlist = rowView.findViewById(R.id.tvShortlist);
            LinearLayout llStatus = rowView.findViewById(R.id.llStatus);
            LinearLayout llRejected = rowView.findViewById(R.id.llRejected);
            LinearLayout llAccepted = rowView.findViewById(R.id.llAccepted);
            LinearLayout llBottomMenu = rowView.findViewById(R.id.llBottomMenu);
            TextView tvMaitId = rowView.findViewById(R.id.tvMaitId);
            TextView tv_name = rowView.findViewById(R.id.tv_name);
            TextView tv_status = rowView.findViewById(R.id.tv_status);
            ImageView img_profile = rowView.findViewById(R.id.img_profile);
            final TextView tv_about = rowView.findViewById(R.id.tv_about);


            final ExpressItem item = list.get(position);

            if (tag.equals("reject_sent")) {
                llBottomMenu.setVisibility(View.GONE);
            }

            tvMaitId.setText(item.getMatri_id());
            String[] Name = item.getUsername().split(" ");
            if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                if (Name.length == 3) {
                    tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                } else if (Name.length == 2) {
                    tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                } else {
                    tv_name.setText(Name[0].charAt(0));
                }
            } else
                tv_name.setText(item.getUsername());


            rowView.findViewById(R.id.llMessage).setOnClickListener(v -> {
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

            rowView.findViewById(R.id.llShortlist).setOnClickListener(v -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    try {
                        if (item.getAction().getInt("is_shortlist") == 1) {
                            shortlistRequest("remove", item.getId(), tvShortlist, ivShortlist, item);
                        } else {
                            shortlistRequest("add", item.getId(), tvShortlist, ivShortlist, item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    common.showToast("Please upgrade your membership to chat with this member.",llView);
                    context.startActivity(new Intent(context, PlanListActivity.class));
                }
            });

            String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                    item.getMtongue_name(), item.getCaste_name(),
                    item.getEducation_name(), item.getOccupation_name(), item.getCity_name(), item.getState_name(), 30);

            tv_about.setText(description);
            tv_status.setText(item.getAbout());

            common.setImage(item.getPhoto_view_count(), item.getPhoto_view_status(), item.getImage_approval(),
                    item.getPhotoUrl() + item.getImage(), img_profile, null, 0);

            rowView.setOnClickListener(view12 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getUser_id());
                    context.startActivity(i);
                } else {
                    Intent i = new Intent(context, PreviewOthersProfileActivity.class);
                    i.putExtra("other_id", item.getUser_id());
                    context.startActivity(i);
                }
            });

            try {
                if (item.getAction().getInt("is_shortlist") == 1) {
                    tvShortlist.setTextColor(getResources().getColor(R.color.shortllist));
                    // btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short_filled));
                    ivShortlist.setImageResource(R.drawable.in_shorted);
                    tvShortlist.setText("Shortlisted");
                } else {
                    ivShortlist.setImageResource(R.drawable.in_shortlist);
                    tvShortlist.setTextColor(getResources().getColor(R.color.registration_hint_color));
                    //                    btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short));
                    tvShortlist.setText("Shortlist");

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (item.getAbout().equals("") || item.getAbout() == null)
                llStatus.setVisibility(View.GONE);

            rowView.setOnClickListener(view12 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getUser_id());
                    context.startActivity(i);
                } else {
                    //     common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                    Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                    in.putExtra("other_id", item.getUser_id());
                    context.startActivity(in);
                }
            });

//            switch (item.getReceiver_response()) {
//                case "All":
//                    tv_status.setText("Sent");
//                    break;
//                case "Accepted":
//                    tv_status.setText("Accepted");
//                    break;
//                case "Rejected":
//                    tv_status.setText("Rejected");
//                    break;
//                case "Pending":
//                    tv_status.setText("Pending");
//                    break;
//            }

            if (item.getBadge().length() > 0 && item.getPlan_status().equalsIgnoreCase("Paid")) {
                Picasso.get().load(item.getBadgeUrl() + item.getBadge())
                        .placeholder(R.drawable.ic_transparent_placeholder)
                        .error(R.drawable.ic_transparent_placeholder)
                        .into(imgPLanStamp);
                imgPLanStamp.setVisibility(View.VISIBLE);
            } else {
                imgPLanStamp.setVisibility(View.GONE);
            }

//            if (item.getColor().length() > 0) {
//                cardView.setShadowColor(Color.parseColor("" + item.getColor()));
//            }

            img_profile.setOnClickListener(view1 -> {
                if (item.getPhoto_view_status() != null && item.getPhoto_view_count() != null) {
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
                            //         common.showToast("Please upgrade your membership to view this profile.");
//                                context.startActivity(new Intent(context, PlanListActivity.class));
                            Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                            in.putExtra("other_id", item.getUser_id());
                            context.startActivity(in);
                        }
                    }
                }
            });


            img_more.setOnClickListener(view13 -> {
                try {
                    showFilterPopup(view13, item.getId(), item.getAction().getString("is_like"), item.getName(),
                            item.getAction().getInt("is_block"), position, item.getUser_id(), item.getAction(), item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });

            return rowView;
        }
    }
    private void showFilterPopup(View v, final String id, String is_like, String name, int is_block, int position, String user_id,JSONObject action, ExpressItem itemv) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.more_menu);
        MenuItem it = popup.getMenu().findItem(R.id.block);
        if (is_block == 1) {
            it.setTitle("Unblock Member");
        } else {
            it.setTitle("Block");
        }
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.report:
                    context.startActivity(new Intent(context, ReportMissuseActivity.class));
                    return true;
                case R.id.view_profile:
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", user_id);
                        context.startActivity(i);
                    } else {
                        //       common.showToast("Please upgrade your membership to view this profile.");
//                            context.startActivity(new Intent(context, PlanListActivity.class));
                        Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", user_id);
                        context.startActivity(in);
                    }
                    return true;
                case R.id.like:
                    setLike(popup, R.id.like, is_like, name);
                    return true;
                case R.id.block:
                    setBlock(popup, R.id.block, is_block, name,action,itemv);
                    return true;
                case R.id.delete:
                    deleteInterestAlert(id, position);
                    return true;


                default:
                    return false;
            }
        });

        Menu menu = popup.getMenu();
        for (int i = 0; i < menu.size(); i++) {
            MenuItem mi = menu.getItem(i);
            applyFontToMenuItem(mi);
        }


        popup.show();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = ResourcesCompat.getFont(context, R.font.medium_slnt);
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void alertPhotoPassword(final String matri_id) {
        final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                "I am interested in your profile. I would like to view photo now, accept photo request."};
        final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
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
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.photo_password_request, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast( object.getString("errmessage"),llView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


    private void deleteInterestAlert(final String id, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to delete this interest?");
        alert.setPositiveButton("Yes", (dialogInterface, i) -> deleteApi(id, position));
        alert.setNegativeButton("No", null);
        alert.show();
    }

    private void deleteApi(String id, final int position) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("exp_status", tag);
        param.put("id", id);
        param.put("status", "delete");

        common.makePostRequest(AppConstants.action_update_status, param, response -> {
            common.hideProgressRelativeLayout(loader);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errormessage"),llView);
                if (object.getString("status").equals("success")) {
                    list.remove(position);
                    if (list.size() == 0) {
                        lv_exp_sent.setVisibility(View.GONE);
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void shortlistRequest(final String tag, String id, TextView ivShort, ImageView ivShortlist, ExpressItem item) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", item.getMatri_id());
        } else
            param.put("shortlistuserid", item.getMatri_id());

        param.put("shortlist_action", tag);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                JSONObject action = item.getAction();

                if (object.getString("status").equals("success")) {
                    if (tag.equals("add")) {
                        action.remove("is_shortlist");
                        action.put("is_shortlist", 1);
                        //btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short_filled));
                        ivShort.setTextColor(getResources().getColor(R.color.shortllist));
                        ivShort.setText("Shortlisted");
                        ivShortlist.setImageResource(R.drawable.in_shorted);
                        common.showAlert("Shortlist", "Nice! Profile Shortlisted.", R.drawable.in_shorted);
                    } else {
                        action.remove("is_shortlist");
                        action.put("is_shortlist", 0);
                        ivShortlist.setImageResource(R.drawable.in_shortlist);
                        ivShort.setTextColor(getResources().getColor(R.color.registration_hint_color));
                        // btnShortlist.setBackground(getResources().getDrawable(R.drawable.btn_short));
                        ivShort.setText("Shortlist");
                        common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.in_shortlist);
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);


    }


    private void setBlock(PopupMenu popup, int interest, int is_block, String name, JSONObject action, ExpressItem itemv) {
        MenuItem item = popup.getMenu().findItem(interest);
        if (is_block == 1) {
            item.setTitle("Block");
            blockRequest("remove", name,action,itemv);
        } else {
            item.setTitle("Blocked");
            blockRequest("add", name,action,itemv);
        }
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
    private void blockRequest(final String tag, String id, JSONObject action, ExpressItem itemv) {
        loader.setVisibility(View.VISIBLE);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("unblockuserid", itemv.getMatri_id());
        } else
            param.put("blockuserid", itemv.getMatri_id());

        param.put("blacklist_action", tag);

        common.makePostRequestTime(AppConstants.block_user, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }
    private void likeRequest(final String tag, String matri_id) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequestTime(AppConstants.like_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }




}
