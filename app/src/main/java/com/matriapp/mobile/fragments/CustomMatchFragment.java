package com.matriapp.mobile.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.multispinnerfilter.MultiSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SingleSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SpinnerListener;
import com.matriapp.mobile.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.ConversationActivity;

import com.matriapp.mobile.activities.EditPartnerPreferencesActivity;
import com.matriapp.mobile.activities.EditPreferenceActivity;
import com.matriapp.mobile.activities.OtherUserProfileActivity;
import com.matriapp.mobile.activities.PlanListActivity;
import com.matriapp.mobile.activities.PreviewOthersProfileActivity;
import com.matriapp.mobile.activities.ReportMissuseActivity;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.custom.MultiSelectionSpinner;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CustomMatchFragment extends Fragment implements SpinnerListener {

    private LinearLayout layoutBottomSheet;
//    private BottomSheetBehavior sheetBehavior;
    private MultiSpinnerSearch spin_mari, spin_complex, spin_tongue, spin_religion, spin_caste, spin_country, spin_edu;
    private TextView tv_min_height, tv_max_height, search_tv_min_age, search_tv_max_age;
    private CrystalRangeSeekbar range_height, search_range_age;
    private Button btn_save_search;
    private Common common;
    private SessionManager session;
    private String mari_id = "", religion_id = "", tongue_id = "", country_id = "", edu_id = "", height_from = "",
            height_to = "", complex_id = "", caste_id = "", age_from, age_to;
    private HashMap<String, String> height_map = new HashMap<>();
    private RelativeLayout progressBar;
    private List<DashboardItem> list = new ArrayList<>();
    private int page = 0;
    private ListView lv_match;
    private boolean continue_request;
    private TextView tv_no_data;
    private TextView etEditPreference;
    private ListAdapter adapter;
    private ImageView btnClose;
    private Context context;
    private View view;
    private int placeHolder, photoProtectPlaceHolder;

    private SwipeRefreshLayout swipe;
    private CoordinatorLayout llView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_custom_match, container, false);
        context = getContext();
        common = new Common(context);
        session = new SessionManager(context);



        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }

        progressBar = view.findViewById(R.id.progressBar);
        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        lv_match = view.findViewById(R.id.lv_match);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        etEditPreference = view.findViewById(R.id.etEditPreference);
        llView = view.findViewById(R.id.llView);

        btnClose = view.findViewById(R.id.btnClose);

        etEditPreference.setOnClickListener(v -> {
            gotoActivity(EditPreferenceActivity.KEY_BASIC, EditPartnerPreferencesActivity.class);

        });

//        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
//        btnClose.setOnClickListener(v -> {
//            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//        });
//
//        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                switch (newState) {
//                    case BottomSheetBehavior.STATE_HIDDEN:
//                        break;
//                    case BottomSheetBehavior.STATE_EXPANDED:
//                        //toolbar.setVisibility(View.GONE);
//                        break;
//                    case BottomSheetBehavior.STATE_COLLAPSED:
//                        //  toolbar.setVisibility(View.VISIBLE);
//                        break;
//                    case BottomSheetBehavior.STATE_DRAGGING:
//                        //  toolbar.setVisibility(View.VISIBLE);
//                        break;
//                    case BottomSheetBehavior.STATE_SETTLING:
//                        break;
//                }
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//
//            }
//        });

        btn_save_search = view.findViewById(R.id.btn_save_search);
        spin_mari = view.findViewById(R.id.spin_mari);
        spin_complex = view.findViewById(R.id.spin_complex);
        spin_tongue = view.findViewById(R.id.spin_tongue);
        spin_religion = view.findViewById(R.id.spin_religion);
        spin_caste = view.findViewById(R.id.spin_caste);
        spin_country = view.findViewById(R.id.spin_country);
        spin_edu = view.findViewById(R.id.spin_edu);
        tv_min_height = view.findViewById(R.id.search_tv_min_height);
        tv_max_height = view.findViewById(R.id.search_tv_max_height);

        search_tv_min_age = view.findViewById(R.id.search_tv_min_age);
        search_tv_max_age = view.findViewById(R.id.search_tv_max_age);

        lv_match.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentFirstVisibleItem;
            private int totalItem;

            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                        && scrollState == SCROLL_STATE_IDLE) {
                    if (continue_request) {
                        if (progressBar != null)
                            progressBar.setVisibility(View.GONE);
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

//        try {
//            initData();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        btn_save_search.setOnClickListener(v -> checkData());

        adapter = new ListAdapter(context, list,llView);
        lv_match.setAdapter(adapter);


        swipe = view.findViewById(R.id.swipe);
        swipe.setOnRefreshListener(() -> {
            list.clear();
            page = 1;
            getListData(page);
        });

        return view;
    }
    private void gotoActivity(String tag, Class activity) {
        Intent i = new Intent(getContext(), activity);
        i.putExtra("pageTag", tag);
        startActivity(i);
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkData() {
        if (mari_id.equals("") || mari_id.equals(",") || mari_id.equals("0")) {
            common.showToast("Please select marital status.",llView);
            return;
        }
        if (religion_id.equals("") || religion_id.equals(",") || religion_id.equals("0")) {
            common.showToast("Please select religion.",llView);
            return;
        }
//        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        HashMap<String, String> param = new HashMap<>();
        param.put("looking_for", getValue(mari_id));
        param.put("part_frm_age", getValue(age_from));
        param.put("part_to_age", getValue(age_to));
        param.put("part_height", getValue(height_from));
        param.put("part_height_to", getValue(height_to));
        param.put("part_complexion", getValue(complex_id));
        param.put("part_mother_tongue", getValue(tongue_id));
        param.put("part_religion", getValue(religion_id));
        param.put("part_caste", getValue(caste_id));
        param.put("part_country_living", getValue(country_id));
        param.put("part_education", getValue(edu_id));
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        Log.d("resp", param.toString());
        submitData(param);
    }

    private String getValue(String val) {
        if (val == null || val.equals("0")) return "";
        else return val;
    }

    private void submitData(HashMap<String, String> param) {
        common.showProgressRelativeLayout(progressBar);
        common.makePostRequest(AppConstants.save_matches, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    list.clear();
                    page = 0;
                    page = page + 1;
                    getListData(page);
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

    private void getMyProfile() {
        if (progressBar != null || progressBar.getVisibility() == View.GONE) {
            common.showProgressRelativeLayout(progressBar);
        }

        final HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");


                    mari_id = data.getString("looking_for");
                    height_from = data.getString("part_height");
                    height_to = data.getString("part_height_to");
                    complex_id = data.getString("part_complexion");
                    tongue_id = data.getString("part_mother_tongue");
                    religion_id = data.getString("part_religion");
                    caste_id = data.getString("part_caste");
                    country_id = data.getString("part_country_living");
                    edu_id = data.getString("part_education");
                    age_from = data.getString("part_frm_age");
                    age_to = data.getString("part_to_age");

                    search_range_age.setMinStartValue(Float.parseFloat(age_from)).setMaxStartValue(Float.parseFloat(age_to)).apply();
                    range_height.setMinStartValue(Float.parseFloat(height_from)).setMaxStartValue(Float.parseFloat(height_to)).apply();

                    spin_mari.setSelection(mari_id);
                    spin_complex.setSelection(complex_id);
                    spin_tongue.setSelection(tongue_id);
                    spin_religion.setSelection(religion_id);
                    if (!religion_id.equals("") && !religion_id.equals("null")) {
                        getDependentList("caste_list", data.getString("part_religion"));
                        common.hideProgressRelativeLayout(progressBar);
                    }
                    spin_country.setSelection(country_id);
                    spin_edu.setSelection(edu_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llView);
            }
            page = 0;
            list=new ArrayList<>();
            adapter = new ListAdapter(context, list,llView);
            lv_match.setAdapter(adapter);
            page = page + 1;
            getListData(page);

        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);
    }

    private void  getListData(int page) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.search_now + page, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            swipe.setRefreshing(false);
            try {
                JSONObject object = new JSONObject(response);
                int total_count = object.getInt("total_count");
                continue_request = object.getBoolean("continue_request");

                if (total_count != 0) {
                    tv_no_data.setVisibility(View.GONE);
                    lv_match.setVisibility(View.VISIBLE);
                    if (total_count != list.size()) {
                        JSONArray data = object.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
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
//                            item.setAnnual_income(obj.getString("income"));

                                item.setBadge(obj.getString("badge"));
                                item.setBadgeUrl(obj.getString("badgeUrl"));
                                item.setPlan_status(obj.getString("plan_status"));
                                JSONArray action = obj.getJSONArray("action");
                                item.setAction(action.getJSONObject(0));
                                list.add(item);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    tv_no_data.setVisibility(View.VISIBLE);
                    lv_match.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("TAG", "getListData: "+ e.getMessage());
//                common.showToast(getString(R.string.err_msg_try_again_later));
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }

    private void sendRequest(String int_msg, String matri_id) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.photo_password_request, param, response -> {
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

    private void likeRequest(final String tag, String matri_id) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequest(AppConstants.like_profile, param, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("Yes")) {
                    common.showAlert("Like", object.getString("errmessage"), R.drawable.heart_fill_pink);
                } else
                    common.showAlert("Unlike", object.getString("errmessage"), R.drawable.heart_gray_fill);

                if (object.getString("status").equals("success")) AppDebugLog.print("Success");
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

    private void interestRequest(String matri_id, String int_msg, final ImageView button, ImageView tvInSent) {
        common.hideProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequest(AppConstants.send_interest, param, response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(response);

                //common.showToast(object.getString("errmessage"));
                if (object.getString("status").equals("success")) {
                    button.setImageResource(R.drawable.ic_connectfillednewmargin);
                    button.setVisibility(View.GONE);
                    tvInSent.setVisibility(View.VISIBLE);
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

    private void blockRequest(final String tag, String id, JSONObject action, DashboardItem itemv) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequest(AppConstants.block_user, param, response -> {
            progressBar.setVisibility(View.GONE);
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
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llView);
            }
        },llView);

    }


    public class ListAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        Common common;
        View llView;

        public ListAdapter(Context context, List<DashboardItem> list, View llView) {
            super(context, R.layout.custom_match_row, list);
            this.context = context;
            this.list = list;
            this.llView = llView;
            common = new Common(context);
        }

        public View getView(final int position, View view, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.custom_match_row, null, true);
            final ImageView btn_interest = rowView.findViewById(R.id.btn_interest);
            ImageView btn_chat = rowView.findViewById(R.id.btn_chat);
            ImageView ivShort = rowView.findViewById(R.id.ivShort);
            // ivShort.setOutlineProvider(new CircularOutlineProvider())

            ImageView tvInSent = rowView.findViewById(R.id.tvInSent);
            TextView tv_mid = rowView.findViewById(R.id.tv_mid);
            TextView tv_name = rowView.findViewById(R.id.tv_name);
            TextView tv_detail = rowView.findViewById(R.id.tv_detail);
            ImageView img_profile = rowView.findViewById(R.id.img_profile);
            ImageView img_more = rowView.findViewById(R.id.img_more);
            ImageView imgPLanStamp = rowView.findViewById(R.id.imgPLanStamp);

//            RelativeLayout lay_online = rowView.findViewById(R.id.lay_online);
//            RelativeLayout lay_ofline = rowView.findViewById(R.id.lay_ofline);

            final DashboardItem item = list.get(position);

            try {

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

                if (item.getAction().getInt("is_shortlist") == 1)
                    ivShort.setImageResource(R.drawable.ic_shortlistfill);
                else
                    ivShort.setImageResource(R.drawable.ic_shortliststroke);

                if (!(item.getAction().getString("is_interest").equals(""))) {
                    btn_interest.setImageResource(R.drawable.ic_connectfillednewmargin);
                    btn_interest.setVisibility(View.GONE);
                    tvInSent.setVisibility(View.VISIBLE);
                }
                else
                    btn_interest.setImageResource(R.drawable.send_interest);

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

            Drawable img_white = context.getResources().getDrawable(R.drawable.eye_pink);
            img_white.setBounds(0, 0, 40, 40);
            // tv_view_count.setCompoundDrawables(img_white, null, null, null);

            String na=session.getLoginData(SessionManager.KEY_PLAN_STATUS);

            if(common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS)))
            tv_name.setText(item.getFirst_name().charAt(0) + " " + item.getLastname());
            else
                tv_name.setText(item.getFirst_name() + " " + item.getLastname());

            tv_mid.setText(item.getMatri_id().toUpperCase());
            common.setImage(item.getPhoto_view_count(),item.getPhoto_view_status(), item.getImage_approval(), item.getPhotoUrl()+item.getImage(), img_profile, null,68);

            String description = Common.getDetails(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                    item.getMTongueName(), item.getCaste(),
                    item.getEducation(), item.getOccupation(), item.getCity(), item.getState(),35);
            tv_detail.setText(description);

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

            img_more.setOnClickListener(view13 -> {
                try {
                    showFilterPopup(view13, item.getId(), item.getAction().getString("is_like"), item.getName(),
                            item.getAction().getInt("is_block"), item.getAction(),item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            ivShort.setOnClickListener(view12 -> {
                try {
                    if (item.getAction().getInt("is_shortlist") == 1) {
                        shortlistRequest("remove", item.getName(), ivShort, item.getAction(), item);
                    } else {
                        shortlistRequest("add", item.getName(), ivShort, item.getAction(), item);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            });


            btn_interest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        if (!item.getAction().getString("is_interest").equals("")) {
                            btn_interest.setImageResource(R.drawable.ic_connectfillednewmargin);
                            btn_interest.setVisibility(View.GONE);
                            tvInSent.setVisibility(View.VISIBLE);
                            common.showToast("You already sent interest to this user.",llView);

                        } else {

                            btn_interest.setImageResource(R.drawable.send_interest);
                            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                            //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                            final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                            final BottomSheetDialog dialog = new BottomSheetDialog(context);
                            dialog.setContentView(vv);

                            dialog.show();
                            ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                            tv_cancel.setOnClickListener(view13 -> dialog.dismiss());
                            Button send = vv.findViewById(R.id.btn_send_intr);
                            send.setOnClickListener(view12 -> {
                                dialog.dismiss();
                                if (grp_interest.getCheckedRadioButtonId() != -1) {
                                    RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                                    interestRequest(item.getMatri_id(), btn.getText().toString().trim(), btn_interest, tvInSent);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


            img_profile.setOnClickListener(view1 -> {
                if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("0")) {
                    //common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                    //                            item.getPhoto_view_status()).equals("male_password") ||
                    //                            common.validImage(item.getImage(),item.getImage_approval(),item.getPhoto_protect(),
                    //                                    item.getPhoto_view_status()).equals("female_password")
                    alertPhotoPassword(item.getPhoto_password(), item.getImage(), item.getName());
                }
                else if (item.getPhoto_view_status().equals("0") && item.getPhoto_view_count().equals("1") && item.getImage_approval().equals("APPROVED")) {
                    final Dialog dialog = new Dialog(context);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.setContentView(R.layout.show_image_alert);
                    TouchImageView img_url = dialog.findViewById(R.id.img_url);
                    Picasso.get().load(item.getImage()).placeholder(placeHolder).error(placeHolder).into(img_url);
                    dialog.show();
                } else {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", item.getId());
                        context.startActivity(i);
                    } else {
                        //         common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                        Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", item.getId());
                        context.startActivity(in);
                    }
                }
            });

            tv_detail.setOnClickListener(view1 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    context.startActivity(i);
                } else {
                    //      common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                    Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                    in.putExtra("other_id", item.getId());
                    context.startActivity(in);
                }
            });

            tv_name.setOnClickListener(view12 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, OtherUserProfileActivity.class);
                    i.putExtra("other_id", item.getId());
                    context.startActivity(i);
                } else {
                    //      common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
                    Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                    in.putExtra("other_id", item.getId());
                    context.startActivity(in);
                }
            });

//            btn_profile.setOnClickListener(view13 -> {
//                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
//                    Intent i = new Intent(context, OtherUserProfileActivity.class);
//                    i.putExtra("other_id", item.getId());
//                    context.startActivity(i);
//                } else {
//                    common.showToast("Please upgrade your membership to view this profile.");
//                    context.startActivity(new Intent(context, PlanListActivity.class));
//                }
//            });

            return rowView;
        }

        private void alertPhotoPassword(final String password, final String url, final String matri_id) {
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
            alt_bld.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sendRequest(selected[0], matri_id);
                }
            });
            alt_bld.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //alertpassword(password,url);
                }
            });
            AlertDialog alert = alt_bld.create();
            alert.show();

        }

        private void alertpassword(final String password, final String url) {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Enter Password");
            final EditText edittext = new EditText(context);

            edittext.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            edittext.setHint("Password");
            alert.setView(edittext);
            alert.setPositiveButton("I don't have password", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    // alertPhotoPassword();
                    dialogInterface.dismiss();
                }
            });
            alert.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (edittext.getText().toString().trim().equals(password)) {
                        final Dialog dialog = new Dialog(context);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setContentView(R.layout.show_image_alert);
                        TouchImageView img_url = dialog.findViewById(R.id.img_url);
                        Picasso.get().load(url).placeholder(placeHolder).error(placeHolder).into(img_url);
                        dialog.show();
                    } else
                        common.showToast("Password not match,Please try again.",llView);

                }
            });
            alert.show();
        }

    }

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            search_range_age = view.findViewById(R.id.search_range_age);

            search_range_age.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                age_to = String.valueOf(maxValue);
                age_from = String.valueOf(minValue);

                search_tv_max_age.setText(maxValue + " Years");
                search_tv_min_age.setText(minValue + " Years");
            });

            range_height = view.findViewById(R.id.search_range_height);
            AppDebugLog.print("MyApplication.getSpinData() : " + MyApplication.getSpinData().getJSONArray("height_list"));
            JSONArray arr = MyApplication.getSpinData().getJSONArray("height_list");
            JSONObject obj = arr.getJSONObject(1);
            JSONObject obj1 = arr.getJSONObject(arr.length() - 1);
            range_height.setMinStartValue(Float.parseFloat(obj.getString("id"))).setMaxStartValue(Float.parseFloat(obj1.getString("id"))).apply();

            for (int i = 0; i < arr.length(); i++) {
                JSONObject object = arr.getJSONObject(i);
                if (i == 0) {

                } else if (object.getString("id").equals("85")) {
                    height_map.put(object.getString("id"), "Above 7ft");
                } else {
                    height_map.put(object.getString("id"), object.getString("val"));
                }
            }

            range_height.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {

                height_from = String.valueOf(minValue);
                height_to = String.valueOf(maxValue);
                tv_min_height.setText(disHeight(height_from));
                tv_max_height.setText(disHeight(height_to));
            });

//            setUpMultiSelectDropDown(spin_mari, "marital_status", "Marital Status");
//            setUpMultiSelectDropDown(spin_religion, "religion_list", "Religion");
//            initializeDependentDropdown(spin_caste, "Caste");
//            setUpMultiSelectDropDown(spin_complex, "complexion", "Complexion");
//            setUpMultiSelectDropDown(spin_tongue, "mothertongue_list", "Mother Tongue");
//            setUpMultiSelectDropDown(spin_country, "country_list", "Country");
//            setUpMultiSelectDropDown(spin_edu, "education_list", "Education");

            setupSearchDropDown(spin_mari, "Marital Status", "marital_status");
            setupSearchDropDown(spin_religion, "Religion", "religion_list");
            setupInitializeSearchDropDown(spin_caste, "Caste");
            setupSearchDropDown(spin_complex, "Complexion", "complexion");
            setupSearchDropDown(spin_tongue, "Mother Tongue", "mothertongue_list");
            setupSearchDropDown(spin_country, "Country", "country_list");
            setupSearchDropDown(spin_edu, "Education", "education_list");


            getMyProfile();
        } else {
            getList();
        }
    }

    public void selectedIndices(List<Integer> indices) {

    }

    public void selectedStrings(MultiSelectionSpinner spinner, List<String> strings) {
        switch (spinner.getId()) {
            case R.id.spin_mari:
                mari_id = listToString(strings);
                break;
            case R.id.spin_religion:
                religion_id = listToString(strings);
                if (religion_id != null && !religion_id.equals("0")) {
                    getDependentList("caste_list", religion_id);
                } else {
                    // initializeDependentDropdown(spin_caste, "Caste");
                }
                break;
            case R.id.spin_caste:
                caste_id = listToString(strings);
                break;
            case R.id.spin_tongue:
                tongue_id = listToString(strings);
                break;
            case R.id.spin_country:
                country_id = listToString(strings);
                break;
            case R.id.spin_complex:
                complex_id = listToString(strings);
                break;
            case R.id.spin_edu:
                edu_id = listToString(strings);
                break;

        }
    }

    @Override
    public void onItemsSelected(MultiSpinnerSearch singleSpinnerSearch) {
        Common.hideSoftKeyboard(getActivity());
        if (singleSpinnerSearch == null) return;
        if (singleSpinnerSearch.getSelectedIdsInString() == null) return;

        switch (singleSpinnerSearch.getId()) {
            case R.id.spin_mari:
                mari_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_religion:
                religion_id = singleSpinnerSearch.getSelectedIdsInString();
                if (religion_id != null && !religion_id.equals("0")) {
                    getDependentList("caste_list", religion_id);
                } else {
                    setupInitializeSearchDropDown(spin_caste, "Caste");
                }
                break;
            case R.id.spin_caste:
                caste_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_tongue:
                tongue_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_country:
                country_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_complex:
                complex_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_edu:
                edu_id = singleSpinnerSearch.getSelectedIdsInString();
                break;

        }
    }

    @Override
    public void onItemsSelected(SingleSpinnerSearch singleSpinnerSearch, KeyPairBoolData item) {

    }

    private void setupInitializeSearchDropDown(MultiSpinnerSearch spinner, String hint) {
        spinner.setItems(spinner, new ArrayList<>(), -1, this, hint);
    }

    private void setupSearchDropDown(MultiSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void initializeDependentDropdown(MultiSelectionSpinner multiSelectionSpinner, String title) {
        List<String> valueList = new ArrayList<>(1);
        List<String> idList = new ArrayList<>(1);
        valueList.add(title);
        idList.add("0");
        multiSelectionSpinner.setItems_string_id(valueList, idList, "Select " + title);
        multiSelectionSpinner.setSelection(0);
        // multiSelectionSpinner.setListener(this);
    }

    private void setUpMultiSelectDependentDropDown(MultiSelectionSpinner multiSelectionSpinner, JSONArray jsonArray, String title) throws JSONException {
        if (jsonArray == null) return;
        List<String> valueList = common.getListFromArray(jsonArray, title);
        List<String> idList = common.getListFromArrayId(jsonArray);
        multiSelectionSpinner.setItems_string_id(valueList, idList, "Select " + title);
        // multiSelectionSpinner.setListener(this);
        multiSelectionSpinner.setSpinnerObject(multiSelectionSpinner);
    }

    private void setUpMultiSelectDropDown(MultiSelectionSpinner multiSelectionSpinner, String key, String title) throws JSONException {
        JSONArray jsonArray = MyApplication.getSpinData().getJSONArray(key);
        if (jsonArray == null) return;
        List<String> valueList = common.getListFromArray(jsonArray, title);
        List<String> idList = common.getListFromArrayId(jsonArray);
        multiSelectionSpinner.setItems_string_id(valueList, idList, "Select " + title);
        // multiSelectionSpinner.setListener(this);
        multiSelectionSpinner.setSpinnerObject(multiSelectionSpinner);
    }

    private void getList() {
        common.showProgressRelativeLayout(progressBar);
        common.makePostRequest(AppConstants.common_list, new HashMap<String, String>(), response -> {
            progressBar.setVisibility(View.GONE);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                MyApplication.setSpinData(object);
                initData();
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

    private String disHeight(String val) {
        return height_map.get(val);
    }

    private String listToString(List<String> list) {
        String listString = "";

        for (String s : list) {
            listString += s + ",";// \t
        }

        listString = listString.replaceAll(",$", "");
        return listString;
    }

    private void getDependentList(final String tag, String id) {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "multi");
        param.put("retun_for", "json");
        JsonParser jsonParser = new JsonParser();
        common.makePostRequest(AppConstants.common_depedent_list, param, response -> {
            Log.d("resp", response + "   ");
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    switch (tag) {
                        case "caste_list":
                            JsonArray jsonArray = (JsonArray) jsonParser.parse(object.getJSONArray("data").toString());
                            spin_caste.setItems(spin_caste, common.getSpinnerListFromArray(jsonArray), -1, this, "Caste");
                            if (!caste_id.equals("")) spin_caste.setSelection(caste_id);
                            break;
                    }
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



    private void setLike1(PopupMenu popup, int likeButton, String is_like, String name) {
        MenuItem item = popup.getMenu().findItem(likeButton);
        if (is_like.equals("No")) {
            item.setTitle("Liked Profile");
            likeRequest("Yes", name);
        } else {
            likeRequest("No", name);
            item.setTitle("Like Profile");
        }
    }

    private void shortlistRequest(final String tag, String id, ImageView ivShort, JSONObject action, DashboardItem item) {
        common.showProgressRelativeLayout(progressBar);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", item.getMatri_id());
        } else
            param.put("shortlistuserid", item.getMatri_id());

        param.put("shortlist_action", tag);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("add")) {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 1);
                    ivShort.setImageResource(R.drawable.ic_shortlistfill);
                    common.showAlert("Shortlist", "Nice! Profile Shortlisted.",R.drawable.rl_shorted);
                } else {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 0);
                    ivShort.setImageResource(R.drawable.ic_shortliststroke);
                    common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.rl_shortlist);
                }
                item.setAction(action);
                if (object.getString("status").equals("success")) {

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

    private void showFilterPopup(View v, final String id, String is_like, String name, int is_block, JSONObject action, DashboardItem itemv) {
        PopupMenu popup = new PopupMenu(context, v);
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
                    context.startActivity(new Intent(context, ReportMissuseActivity.class));
                    return true;
                case R.id.view_profile:
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(context, OtherUserProfileActivity.class);
                        i.putExtra("other_id", id);
                        context.startActivity(i);
                    } else {
                        //     common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                        Intent in = new Intent(context, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", id);
                        context.startActivity(in);
                    }
                    return true;
                case R.id.like:
                    setLike(popup, R.id.like, is_like, name);
                    return true;
                case R.id.block:

                    setBlock(popup, R.id.block, is_block, name,action,itemv);
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
//        Typeface font = ResourcesCompat.getFont(context, R.font.medium_slnt);
//        SpannableString mNewTitle = new SpannableString(mi.getTitle());
//        mNewTitle.setSpan(new CustomTypeFaceSpan("", font, Color.BLACK), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
//        mi.setTitle(mNewTitle);
    }


    private void setBlock(PopupMenu popup, int block, int is_block, String name, JSONObject action, DashboardItem itemv) {
        MenuItem item = popup.getMenu().findItem(block);
        if (is_block == 1) {
            item.setTitle("Block");
            blockRequest("remove", name,action,itemv);
        } else {
            item.setTitle("Unblock Member");
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






}