package com.matriapp.mobile.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.multispinnerfilter.MultiSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SingleSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SpinnerListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
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

import com.matriapp.mobile.crystalrangeseekbar.widgets.CrystalRangeSeekbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.cardStack.CardItem;
import com.matriapp.mobile.cardStack.CardStackAdapter;
import com.matriapp.mobile.custom.TouchImageView;
import com.matriapp.mobile.model.DashboardItem;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;
import com.matriapp.mobile.cardstackview.CardStackLayoutManager;
import com.matriapp.mobile.cardstackview.CardStackListener;
import com.matriapp.mobile.cardstackview.CardStackView;
import com.matriapp.mobile.cardstackview.Direction;
import com.matriapp.mobile.cardstackview.Duration;
import com.matriapp.mobile.cardstackview.RewindAnimationSetting;
import com.matriapp.mobile.cardstackview.StackFrom;
import com.matriapp.mobile.cardstackview.SwipeAnimationSetting;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomMatchActivity extends AppCompatActivity implements SpinnerListener, CardStackAdapter.ItemListener, CardStackListener {
    private LinearLayout layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private LinearLayout layoutDistance;
    private MultiSpinnerSearch spin_mari, spin_complex, spin_tongue, spin_religion, spin_caste, spin_country, spin_edu;
    private TextView tv_min_height, tv_max_height, search_tv_min_age, search_tv_max_age, search_tv_min_area, search_tv_max_area;
    private CrystalRangeSeekbar range_height, search_range_age,search_range_area;
    private Button btn_save_search;
    private Common common;
    private SessionManager session;
    private String mari_id = "", religion_id = "", tongue_id = "", country_id = "", edu_id = "", height_from = "",
            height_to = "", complex_id = "", caste_id = "", age_from, age_to, area_from, area_to;
    private HashMap<String, String> height_map = new HashMap<>();
    private RelativeLayout loader;
    private List<DashboardItem> list = new ArrayList<>();
    private int page = 0;
    private TextView tv_no_data;
    private ImageView btnClose;
    private Toolbar toolbar;
    private ListView lv_match;
    //swipe card stack
    private CardStackView cardStack;
    public CardStackAdapter cardsAdapter;
    private List<CardItem> cardItems = new ArrayList<>();
    private CardStackLayoutManager manager;
    private CoordinatorLayout llContent;
    private ListAdapter adapter;
    private boolean continue_request;
    private String type = "recommended";
    private String subMenuTitle = "Recommended Match";
    private int placeHolder, photoProtectPlaceHolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_match);

        subMenuTitle = getIntent().getStringExtra("sub_menu_title");
        if(subMenuTitle.equalsIgnoreCase("Premium Match")) {
            type = "premium-match";
        }else if(subMenuTitle.equalsIgnoreCase("NearBy Match")) {
            type = "near-by-me";
        }

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(subMenuTitle);

        common = new Common(this);
        session = new SessionManager(this);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }
        cardStack = findViewById(R.id.swipeStack);
        cardStack = findViewById(R.id.swipeStack);
        manager = new CardStackLayoutManager(getApplicationContext(), this);
        loader = findViewById(R.id.loader);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        tv_no_data = findViewById(R.id.tv_no_data);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        btnClose = findViewById(R.id.btnClose);

        btnClose.setOnClickListener(view -> {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            toolbar.setVisibility(View.VISIBLE);
        });

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        toolbar.setVisibility(View.GONE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        toolbar.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        toolbar.setVisibility(View.VISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        btn_save_search = findViewById(R.id.btn_save_search);
        spin_mari = findViewById(R.id.spin_mari);
        spin_complex = findViewById(R.id.spin_complex);
        spin_tongue = findViewById(R.id.spin_tongue);
        spin_religion = findViewById(R.id.spin_religion);
        spin_caste = findViewById(R.id.spin_caste);
        spin_country = findViewById(R.id.spin_country);
        spin_edu = findViewById(R.id.spin_edu);
        tv_min_height = findViewById(R.id.search_tv_min_height);
        tv_max_height = findViewById(R.id.search_tv_max_height);

        search_tv_min_age = findViewById(R.id.search_tv_min_age);
        search_tv_max_age = findViewById(R.id.search_tv_max_age);

        search_tv_min_area = findViewById(R.id.search_tv_min_area);
        search_tv_max_area = findViewById(R.id.search_tv_max_area);

        layoutDistance = findViewById(R.id.layoutDistance);
        layoutDistance.setVisibility(View.GONE);
        if(type.equalsIgnoreCase("near-by-me")) {
            layoutDistance.setVisibility(View.VISIBLE);
        }

        initializeRecyclerView();

        try {
            initData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btn_save_search.setOnClickListener(view -> checkData());
    }

    private void checkData() {
        if (mari_id.equals("") || mari_id.equals(",") || mari_id.equals("0")) {
            common.showToast("Please select marital status.",llContent);
            return;
        }
        if (religion_id.equals("") || religion_id.equals(",") || religion_id.equals("0")) {
            common.showToast("Please select religion.",llContent);
            return;
        }
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        HashMap<String, String> param = new HashMap<>();
        param.put("looking_for", getValue(mari_id));
        param.put("part_frm_age", getValue(age_from));
        param.put("part_to_age", getValue(age_to));
        param.put("part_height", getValue(height_from));
        param.put("part_height_to", getValue(height_to));
        param.put("distance_from", getValue(area_from));
        param.put("distance_to", getValue(area_to));
        param.put("part_complexion", getValue(complex_id));
        param.put("part_mother_tongue", getValue(tongue_id));
        param.put("part_religion", getValue(religion_id));
        param.put("part_caste", getValue(caste_id));
        param.put("part_country_living", getValue(country_id));
        param.put("part_education", getValue(edu_id));
        param.put("match_type", type);
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        Log.d("resp", param.toString());
        submitData(param);
    }

    private String getValue(String val) {
        if (val == null || val.equals("0")) return "";
        else return val;
    }

    private void submitData(HashMap<String, String> param) {
        common.showProgressRelativeLayout(loader);
        common.makePostRequest(AppConstants.save_matches, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }

    private void getMyProfile() {
        if (loader != null || loader.getVisibility() == View.GONE) {
            common.showProgressRelativeLayout(loader);
        }

        final HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
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
                        common.hideProgressRelativeLayout(loader);
                    }
                    spin_country.setSelection(country_id);
                    spin_edu.setSelection(edu_id);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
            page = page + 1;
            getListData(page);

        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    private void initializeRecyclerView() {
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.5f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.HORIZONTAL);
        manager.setCanScrollHorizontal(true);
        manager.setCanScrollVertical(false);

        adapter = new ListAdapter(this, list);
        lv_match= findViewById(R.id.lv_match);
        lv_match.setAdapter(adapter);

//        cardsAdapter = new CardStackAdapter(cardItems, this);
//        cardsAdapter.setListener(this);
//        cardStack.setLayoutManager(manager);
//        cardStack.setAdapter(cardsAdapter);
//        cardsAdapter.setLoadMoreListener(() -> {
//            cardStack.post(() -> {
//                AppDebugLog.print("load more SEARCH_NOW SEARCH_NOW ");
//                //when total count is less
//                if (cardItems.size() < total_count)
//                    loadMore();// a method which requests remote data
//            });
//        });
    }

    /**
     * Load more task on scroll task list
     */
    private void loadMore() {
        page++;
        AppDebugLog.print("In loadMore");
        getListData(page);
    }

    int total_count;

    private void getListData(int page) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.search_now + page, param, response -> {
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
                            DashboardItem item = new DashboardItem();
                            item.setName(obj.getString("matri_id"));
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
//                            item.setOccupation_name(obj.getString("occupation_name"));
//                            item.setAnnual_income(obj.getString("income"));
                            JSONArray action = obj.getJSONArray("action");
                            item.setAction(action.getJSONObject(0));
                            list.add(item);
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

            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }

    private void initData() throws JSONException {
        if (MyApplication.getSpinData() != null) {
            search_range_age = findViewById(R.id.search_range_age);
            search_range_area = findViewById(R.id.search_range_area);

            search_range_age.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                age_to = String.valueOf(maxValue);
                age_from = String.valueOf(minValue);

                search_tv_max_age.setText(maxValue + " Years");
                search_tv_min_age.setText(minValue + " Years");
            });

            search_range_area.setOnRangeSeekbarChangeListener((minValue, maxValue) -> {
                area_from = String.valueOf(minValue);
                area_to = String.valueOf(maxValue);

                search_tv_max_area.setText(maxValue + " km");
                search_tv_min_area.setText(minValue + " km");
            });

            range_height = findViewById(R.id.search_range_height);
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

    private void getList() {
        common.showProgressRelativeLayout(loader);
        common.makePostRequest(AppConstants.common_list, new HashMap<String, String>(), response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));

                MyApplication.setSpinData(object);
                initData();
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }

        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    private String disHeight(String val) {
        return height_map.get(val);
    }

    private void getDependentList(final String tag, String id) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "multi");
        param.put("retun_for", "json");

        JsonParser jsonParser = new JsonParser();

        common.makePostRequest(AppConstants.common_depedent_list, param, response -> {
            Log.d("resp", response + "   ");
            common.hideProgressRelativeLayout(loader);
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
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_match_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.filter) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            toolbar.setVisibility(View.GONE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupSearchDropDown(MultiSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void setupInitializeSearchDropDown(MultiSpinnerSearch spinner, String hint) {
        spinner.setItems(spinner, new ArrayList<>(), -1, this, hint);
    }

    @Override
    public void onItemsSelected(MultiSpinnerSearch singleSpinnerSearch) {
        Common.hideSoftKeyboard(this);
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

    private void loadPaginateData(List<CardItem> newList) {

        AppDebugLog.print("loadPaginateData cardItems size :" + cardItems.size());
        AppDebugLog.print("loadPaginateData getSpots size :" + cardsAdapter.getSpots().size());

        int oldCount = cardsAdapter.getSpots().size() + 1;
        List<CardItem> old = cardsAdapter.getSpots();
        List<CardItem> totalWithNew = new ArrayList<>();
        old.addAll(newList);
        AppDebugLog.print("loadPaginateData old :" + old.size());
        totalWithNew = old;
        AppDebugLog.print("loadPaginateData totalWithNew :" + totalWithNew.size());
        cardsAdapter.setSpots(totalWithNew);
        AppDebugLog.print("loadPaginateData after set spot :" + cardsAdapter.getSpots().size());
        cardsAdapter.notifyItemRangeInserted(oldCount, totalWithNew.size());
    }

    @Override
    public void previuosClicked(int position) {
        RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        manager.setRewindAnimationSetting(setting);
        cardStack.rewind();
    }

    @Override
    public void nextClicked(int position) {
        SwipeAnimationSetting setting = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new DecelerateInterpolator())
                .build();
        manager.setSwipeAnimationSetting(setting);
        cardStack.swipe();
    }

    @Override
    public void shareClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void sendMessageClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void moreClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void showPhotosClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void callWhatsappClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void connectClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void itemClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void notNowClicked(int position, @Nullable CardItem item) {

    }

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    int count = 0;

    @Override
    public void onCardSwiped(Direction direction) {
        try {
            if (manager.getTopPosition() == cardsAdapter.getItemCount() - 5) {
                AppDebugLog.print("call here page 2 api");
                AppDebugLog.print("cardItems.size() : " + cardItems.size());

                if (cardItems.size() < total_count)
                    loadMore();// a method which requests remote data
            }
        } catch (Exception e) {
            cardsAdapter.notifyDataChanged();
        }

        count++;
        if (count == cardItems.size()) {
            // do your work
            tv_no_data.setVisibility(View.VISIBLE);
        } else {
            tv_no_data.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }


    public class ListAdapter extends ArrayAdapter<DashboardItem> {
        Context context;
        List<DashboardItem> list;
        Common common;

        public ListAdapter(Context context, List<DashboardItem> list) {
            super(context, R.layout.custom_match_row, list);
            this.context = context;
            this.list = list;
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

//            RelativeLayout lay_online = rowView.findViewById(R.id.lay_online);
//            RelativeLayout lay_ofline = rowView.findViewById(R.id.lay_ofline);

            final DashboardItem item = list.get(position);

            try {
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

            tv_mid.setText(item.getName().toUpperCase());

            common.setImage(item.getPhoto_view_count(),item.getPhoto_view_status(), item.getImage_approval(), item.getImage(), img_profile, null,68);

            String description = common.getDetailsFromValuePreferred(item.getAge().toLowerCase(), item.getHeight().replace("ft", "\'").replace("in", "\""),
                    item.getCaste(), item.getReligion(),
                    item.getCity(), item.getState(), item.getCountry(), item.getEducation(), "","");
            tv_detail.setText(description);

            btn_chat.setOnClickListener(view1 -> {
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(context, ConversationActivity.class);
                    i.putExtra("matri_id", item.getName());
                    startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to chat with this member.",llContent);
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
                            common.showToast("You already sent interest to this user.",llContent);

                        } else {
                            btn_interest.setImageResource(R.drawable.send_interest);
                            LayoutInflater inflater1 = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                            final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                            //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                            final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                            final BottomSheetDialog dialog = new BottomSheetDialog(context);
                            dialog.setContentView(vv);
                            dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                @Override
                                public void onShow(DialogInterface dialogInterface) {
                                    BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                                    common.setupFullHeight(bottomSheetDialog, (Activity) getContext());
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
                                    interestRequest(item.getName(), btn.getText().toString().trim(), btn_interest, tvInSent);
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
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
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
//                if (MyApplication.getPlan()) {
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
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        dialog.setContentView(R.layout.show_image_alert);
                        TouchImageView img_url = dialog.findViewById(R.id.img_url);
                        Picasso.get().load(url).placeholder(placeHolder).error(placeHolder).into(img_url);
                        dialog.show();
                    } else
                        common.showToast("Password not match,Please try again.",llContent);

                }
            });
            alert.show();
        }


        

    }

    private void showFilterPopup(View v, final String id, String is_like, String name, int is_block, JSONObject action, DashboardItem itemv) {
        PopupMenu popup = new PopupMenu(this, v);
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
                    startActivity(new Intent(this, ReportMissuseActivity.class));
                    return true;
                case R.id.view_profile:
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        Intent i = new Intent(this, OtherUserProfileActivity.class);
                        i.putExtra("other_id", id);
                        startActivity(i);
                    } else {
                        //     common.showToast("Please upgrade your membership to view this profile.");
//                        context.startActivity(new Intent(context, PlanListActivity.class));
                        Intent in = new Intent(this, PreviewOthersProfileActivity.class);
                        in.putExtra("other_id", id);
                        startActivity(in);
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
//            applyFontToMenuItem(mi);
        }


        popup.show();
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

    private void likeRequest(final String tag, String matri_id) {
        
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", matri_id);
        param.put("like_status", tag);

        common.makePostRequest(AppConstants.like_profile, param, response -> {
            
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("Yes")) {
                    common.showAlert("Like", object.getString("errmessage"), R.drawable.heart_fill_pink);
                } else
                    common.showAlert("Unlike", object.getString("errmessage"), R.drawable.heart_gray_fill);

                if (object.getString("status").equals("success")) AppDebugLog.print("Success");
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }

    private void shortlistRequest(final String tag, String id, ImageView ivShort, JSONObject action, DashboardItem item) {
        
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
                if (tag.equals("add")) {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 1);
                    ivShort.setImageResource(R.drawable.ic_shortlistfill);
                    common.showAlert("Shortlist", "Nice! Profile Shortlisted.", R.drawable.ic_shortlistfill);
                } else {
                    action.remove("is_shortlist");
                    action.put("is_shortlist", 0);
                    ivShort.setImageResource(R.drawable.ic_shortliststroke);
                    common.showAlert("Remove From Shortlist", "Profile removed from Shortlisted profiles.", R.drawable.ic_shortliststroke);
                }
                item.setAction(action);
                if (object.getString("status").equals("success")) {

                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);


    }

    private void interestRequest(String matri_id, String int_msg, final ImageView button, ImageView tvInSent) {
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequest(AppConstants.send_interest, param, response -> {
            
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
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }

    private void sendRequest(String int_msg, String matri_id) {
        

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.photo_password_request, param, response -> {
            
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llContent);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }

    private void blockRequest(final String tag, String id, JSONObject action, DashboardItem itemv) {
        
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequest(AppConstants.block_user, param, response -> {
            
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
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);

    }


}
