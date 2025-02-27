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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.adapter.AllMatchesAdapter;
import com.matriapp.mobile.multispinnerfilter.SpinnerListener;
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


public class AllMatchFragment extends Fragment implements AllMatchesAdapter.ItemListener {

    private RecyclerView lv_short;
    private List<DashboardItem> list = new ArrayList<>();
    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private boolean continue_request;
    private TextView tv_no_data;
    private FrameLayout llView;
    private AllMatchesAdapter adapter;
    private int page = 0;
    private int placeHolder, photoProtectPlaceHolder;

    private Context context;
    private HashMap<String, String> height_map = new HashMap<>();
    private String religion_id = "", caste_id = "", tongue_id = "", country_id = "", state_id = "", city_id = "", mari_id = "", height_from = "",
            height_to = "", age_from = "", age_to = "", edu_id = "", manglik_id = "", star_id = "", bodytype_id = "", complex_id = "", smok_id = "",
            drink_id = "", incom_id = "", emp_id = "", eat_id = "", ocu_id = "";
    HashMap<String, String> param = new HashMap<>();

    public AllMatchFragment() {
    }

    public static AllMatchFragment newInstance(String mari_id) {
        AllMatchFragment fragment = new AllMatchFragment();
        Bundle args = new Bundle();
        args.putString("mari_id", mari_id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mari_id = getArguments().getString("mari_id", "");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_match_marital, container, false);
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

        initializeRecyclerView();


        setData();
        return view;
    }

    private void initializeRecyclerView() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        lv_short.setLayoutManager(mLayoutManager);
        adapter = new AllMatchesAdapter(context, list, this,llView);
        lv_short.setAdapter(adapter);

        lv_short.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        totalItem - currentFirstVisibleItem == currentVisibleItemCount) {
                    if (continue_request) {
                        if (progressBar != null)
                            common.hideProgressRelativeLayout(progressBar);
                        page = page + 1;
                        getListData(page);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    currentVisibleItemCount = layoutManager.getChildCount();
                    totalItem = layoutManager.getItemCount();
                    currentFirstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                }
            }
        });

    }

    private void setData() {

        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("from_age", age_from);
        param.put("to_age", age_to);
        param.put("from_height", height_from);
        param.put("to_height", height_to);
        param.put("looking_for", getValue(mari_id));
        param.put("religion", getValue(religion_id));
        param.put("caste", getValue(caste_id));
        param.put("mothertongue", getValue(tongue_id));
        param.put("country", getValue(country_id));
        param.put("state", getValue(state_id));
        param.put("city", getValue(city_id));
        param.put("education", getValue(edu_id));
        param.put("occupation", getValue(ocu_id));
        param.put("employee_in", getValue(emp_id));
        param.put("income", getValue(incom_id));
        param.put("diet", getValue(eat_id));
        param.put("drink", getValue(drink_id));
        param.put("smoking", getValue(smok_id));
        param.put("complexion", getValue(complex_id));
        param.put("bodytype", getValue(bodytype_id));
        param.put("photo_search", "");

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            param.put("gender", "Male");
        } else {
            param.put("gender", "Female");
        }

        page = page + 1;
        getListData(page);
    }

    private String getValue(String val) {
        if (val == null || val.equals("0")) return "";
        else return val;
    }

    private void getListData(int page) {
        common.showProgressRelativeLayout(progressBar);

        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequestTime(AppConstants.search_result + page, param, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                common.hideProgressRelativeLayout(progressBar);
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
                                Log.d("TAG", "shortlist: " + data);
                                if (common.isNotNullOrEmpty(obj.getString("matri_id"))) {
                                    DashboardItem item = new DashboardItem();

                                    item.setMatri_id(obj.getString("matri_id"));
                                    item.setName(obj.getString("username"));
                                    item.setImage(obj.getString("photo1"));
                                    item.setImage_approval(obj.getString("photo1_approve"));
                                    item.setAge(obj.getString("age"));
                                    item.setHeight(obj.getString("height"));
                                    item.setCaste(obj.getString("caste_name"));
                                    item.setReligion(obj.getString("religion_name"));
                                    item.setFirst_name(obj.getString("firstname"));
                                    item.setLastname(obj.getString("lastname"));
                                    item.setState(obj.getString("state_name"));
                                    item.setCity(obj.getString("city_name"));
                                    item.setCountry(obj.getString("country_name"));
                                    item.setDesignation(obj.getString("designation_name"));
                                    item.setPhoto_protect(obj.getString("photo_protect"));
                                    item.setPhoto_view_status(obj.getString("photo_view_status"));
                                    item.setPhoto_password(obj.getString("photo_password"));
                                    item.setId(obj.getString("id"));
                                    item.setPhoto_view_count(obj.getString("photo_view_count"));
                                    item.setEducation(obj.getString("education_name"));
                                    item.setPhotoUrl(obj.getString("photoUrl"));

                                    item.setOccupation(obj.getString("occupation_name"));
                                    item.setMTongueName(obj.getString("mtongue_name"));

                                    item.setBadge(obj.getString("badge"));
                                    item.setBadgeUrl(obj.getString("badgeUrl"));
                                    item.setPlan_status(obj.getString("plan_status"));
                                    JSONArray action = obj.getJSONArray("action");
                                    item.setAction(action.getJSONObject(0));
                                    list.add(item);
//                                    item.setId(obj.getString("id"));
//                                    item.setMatri_id(obj.getString("matri_id"));
//                                    item.setName(obj.getString("username"));
//                                    JSONArray action = obj.getJSONArray("action");
//                                    item.setAction(action.getJSONObject(0));
//
//
//                                    String description = Common.getDetailsFromValueAllPremium(obj.getString("age"), obj.getString("height").replace("ft", "\'").replace("in", "\""),
//                                            obj.getString("caste_name"), obj.getString("religion_name"),
//                                            obj.getString("city_name"), obj.getString("state_name"), obj.getString("country_name"), obj.getString("education_name"), obj.getString("occupation_name"), "");
//
//                                    item.setAbout(description);
//                                    item.setImage_approval(obj.getString("photo1_approve"));
//                                    item.setImage(obj.getString("photo1"));
////                                    item.setUser_id(obj.getString("user_id"));
//                                    item.setPhoto_view_status(obj.getString("photo_view_status"));
//                                    item.setPhotoUrl(obj.getString("photoUrl"));
//
//                                    // item.setPhoto_view_count(obj.getString("photo_view_count"));
//                                    Log.d("resp", obj.getString("photo_view_status") + "  " + obj.getString("matri_id") +
//                                            "    " + obj.getString("photo1") + "   " + obj.getString("photo1_approve"));
//                                    list.add(item);
                                }
                            }
                            adapter.notifyDataSetChanged();

                            if (list.size() == 0) {
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






    @Override
    public void alertPhotoPassword(final String password, final String url, final String matri_id) {
        final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                "I am interested in your profile. I would like to view photo now, accept photo request."};
        final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(context);

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
                common.showToast(context.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
//            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }






    @Override
    public void blockRequest(final String tag, String id, JSONObject action, DashboardItem itemv) {
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
                common.showToast(context.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    @Override
    public void shortlistRequest(final String tag, String id, ImageView ivShort, DashboardItem item) {

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", id);
        } else
            param.put("shortlistuserid", id);

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
                common.showToast(context.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);


    }


    @Override
    public void interestRequest(String matri_id, String int_msg, final ImageView btn_interest, final ImageView ivConnected) {
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
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                } else
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_gray_fill);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(context.getString(R.string.err_msg_try_again_later),llView);
            }
        }, error -> {
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


}