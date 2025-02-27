package com.matriapp.mobile.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.matriapp.mobile.adapter.CustomPartnerProfileAdapter;
import com.matriapp.mobile.model.PartnerFields;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.dynamicprofile.ItemClickListener;
import com.matriapp.mobile.dynamicprofile.SectionedExpandableLayoutHelper;
import com.matriapp.mobile.dynamicprofile.ViewProfileFieldsBean;
import com.matriapp.mobile.dynamicprofile.ViewProfileSectionBean;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OtherUserProfileActivity extends AppCompatActivity implements View.OnClickListener, TabHost.OnTabChangeListener, ItemClickListener {

    //when Loged in user is paid  or premium  member, then this Activity opens for other
    private final String TAB_FIRST = "first";
    private final String TAB_SECOND = "second";
    private final int TAB_FIRST_POSITION = 0;
    private final int TAB_SECOND_POSITION = 1;
    private Common common;
    private SessionManager session;
    private RelativeLayout loader;
    private TabHost host;
    private TextView tv_basic_label_pref, tv_photo_count, tv_id, tv_name, tv_detail;
    private RecyclerView userProfileRecyclerView;
    private List<ViewProfileSectionBean> userProfileDataList = new ArrayList<>();
    private int placeHolder, photoProtectPlaceHolder;
    private LinearLayout lay_basic_pref;
    private ImageView imgProfile, circleImageView, imgProfileTwo, imgProfileThree,imgProfileFour;
    private JSONArray photo_arr;
    private String other_id, other_matri_id;
    private boolean isProtected = false;
    private SectionedExpandableLayoutHelper myProfileSectionedExpandableLayoutHelper;
    private String name, time_to_call, mobile, email;
    private String isProfileViewed = "";
    private NestedScrollView scrollView;
    private TextView txtFocus;
    private String username = "";
    private RecyclerView userPartnerRecyclerView;
    private List<PartnerFields> userPartnerDataList = new ArrayList<>();
    //    private FloatingActionsMenu fabMenu;
//    private FloatingActionButton fabCall, fabLike, fabBlock, fabShortlist, fabMessage, fabSendInterest;
    private ImageView fabCall, fabLike, fabBlock, fabShortlist, fabMessage, fabSendInterest;
    private boolean isNotPremiumMember = false;
    private TextView tvTitle, tvCreatedBy, tvAbout, tvHeight, tvAppearnce, tvMarital, tvMtounge, tvCity, tvCitizenship, tvFood, tvDrink, tvSmoke, tvHealth;
    private TextView tvEducation, tvProfession, tvIncome, tvIncomePremium, tvIncomeView, tvOccupation, tvDesignation, tvLookinFor, tvHobbies, tvLanuguage,
            tvReligion, tvCaste, tvGothram, tvHoroscope, tvStar, tvStarPremium, tvStarView, tvRaas, tvRaasPremium, tvRaasView,
            tvBloodGroup, tvBloodGroupPremium, tvBloodGroupView, tvBirthdayTime, tvBirthPlace, tvBirthPlacePremium, tvBirthPlaceView,
            tvMangalik, tvMangalikPremium, tvMangalikView, tvBirthday, tvBirthdayPremium, tvBirthdayView,
            tvContact, tvEmail, tvName, tvMName, tvCall, tvAddress, tvAddressPremium, tvAddressView, tvFStatus, tvFather, tvFatherOcc, tvMother, tvMotherOcc, tvBrother, tvSister, tvFamily;
    private LinearLayout llML1, llML2, llML3, llML4;
    private String height;
    private boolean collaped = false;


    private LinearLayout layoutActions; // Your target layout
    private int lastScrollY = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(this);
        common = new Common(this);
        common.setGradient(getWindow());

        isNotPremiumMember = common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS));

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_other_user_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow_viewprofile);
        toolbar.setNavigationOnClickListener(view -> finish());

        tv_photo_count = findViewById(R.id.tv_photo_count);
//        tv_basic_label_pref = findViewById(R.id.tv_basic_label_pref);
//        lay_basic_pref = findViewById(R.id.lay_basic_pref);

        layoutActions = findViewById(R.id.layoutActions);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfileTwo = findViewById(R.id.imgProfileTwo);
        imgProfileThree = findViewById(R.id.imgProfileThree);
        imgProfileFour = findViewById(R.id.imgProfileFour);

//        userProfileRecyclerView = findViewById(R.id.userProfileRecyclerView);

        scrollView = findViewById(R.id.scrollView);
        txtFocus = findViewById(R.id.txtFocus);

        fabCall = findViewById(R.id.fabCall);
        fabLike = findViewById(R.id.fabLike);
        fabBlock = findViewById(R.id.fabBlock);
        fabShortlist = findViewById(R.id.fabShortlist);
        fabMessage = findViewById(R.id.fabMessage);
        fabSendInterest = findViewById(R.id.fabSendInterest);

        fabCall.setOnClickListener(this);
        fabLike.setOnClickListener(this);
        fabBlock.setOnClickListener(this);
        fabShortlist.setOnClickListener(this);
        fabMessage.setOnClickListener(this);
        fabSendInterest.setOnClickListener(this);


        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }
        imgProfile.setImageResource(placeHolder);
//        imgProfileTwo.setImageResource(placeHolder);
//        imgProfileThree.setImageResource(placeHolder);
//        imgProfileFour.setImageResource(placeHolder);

        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey("other_id")) {
            other_id = b.getString("other_id");
        }
        userPartnerRecyclerView = findViewById(R.id.userPartnerRecyclerView);


        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
        tv_detail = findViewById(R.id.tv_detail);
        loader = findViewById(R.id.loader);
        tv_photo_count = findViewById(R.id.tv_photo_count);
        tv_photo_count.setOnClickListener(this);

        imgProfile.setOnClickListener(view -> {
            if (isProtected) {
                alertPhotoPassword();
            } else {
                if (photo_arr.length() != 0) {
                    Intent intent = new Intent(getApplicationContext(), GallaryNewActivity.class);
                    intent.putExtra("imagePosition", 0);
                    intent.putExtra("imageArray", photo_arr.toString());
                    intent.putExtra("tag", "other_profile");
                    startActivity(intent);
                }
            }
        });
        initTextView();
        getMyProfile();

        findViewById(R.id.llcall).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + time_to_call));
            startActivity(intent);
        });
        findViewById(R.id.llcall1).setOnClickListener(v -> {
//            Intent intent = new Intent(Intent.ACTION_DIAL);
//            intent.setData(Uri.parse("tel:" + forCallMobileNo));
//            startActivity(intent);
            viewContact();
        });
        circleImageView = findViewById(R.id.circleImageView);
        tvTitle = findViewById(R.id.tvTitle);
        AppBarLayout appBarLayout = findViewById(R.id.appBarLayout); // get the AppBarLayout
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) == appBarLayout.getTotalScrollRange()) {
                    circleImageView.setVisibility(View.VISIBLE);
                    tvTitle.setVisibility(View.VISIBLE);
                } else if (verticalOffset == 0) {
                    circleImageView.setVisibility(View.GONE);
                    tvTitle.setVisibility(View.GONE);
                } else {
                }
            }
        });
    }

    private void initTextView() {
        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvAbout = findViewById(R.id.tvAbout);
        tvHeight = findViewById(R.id.tvHeight);
        tvAppearnce = findViewById(R.id.tvAppearnce);
        tvHealth = findViewById(R.id.tvHealth);
        tvMarital = findViewById(R.id.tvMarital);
        tvMtounge = findViewById(R.id.tvMtounge);
        tvCity = findViewById(R.id.tvCity);
        tvCitizenship = findViewById(R.id.tvCitizenship);
        tvFood = findViewById(R.id.tvFood);
        tvDrink = findViewById(R.id.tvDrink);
        tvSmoke = findViewById(R.id.tvSmoke);
        tvEducation = findViewById(R.id.tvEducation);
        tvProfession = findViewById(R.id.tvProfession);
        tvIncome = findViewById(R.id.tvIncome);
        tvIncomePremium = findViewById(R.id.tvIncomePremium);
        tvIncomeView = findViewById(R.id.tvIncomeView);
        tvOccupation = findViewById(R.id.tvOccupation);
        tvDesignation = findViewById(R.id.tvDesignation);
        tvLookinFor = findViewById(R.id.tvLookinFor);
        tvHobbies = findViewById(R.id.tvHobbies);
        tvLanuguage = findViewById(R.id.tvLanuguage);
        tvReligion = findViewById(R.id.tvReligion);
        tvCaste = findViewById(R.id.tvCaste);
        tvGothram = findViewById(R.id.tvGothram);
        tvHoroscope = findViewById(R.id.tvHoroscope);
        tvStar = findViewById(R.id.tvStar);
        tvStarPremium = findViewById(R.id.tvStarPremium);
        tvStarView = findViewById(R.id.tvStarView);
        tvRaas = findViewById(R.id.tvRaas);
        tvRaasPremium = findViewById(R.id.tvRaasPremium);
        tvRaasView = findViewById(R.id.tvRaasView);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
        tvBloodGroupPremium = findViewById(R.id.tvBloodGroupPremium);
        tvBloodGroupView = findViewById(R.id.tvBloodGroupView);
        tvBirthdayTime = findViewById(R.id.tvBirthdayTime);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvBirthdayPremium = findViewById(R.id.tvBirthdayPremium);
        tvBirthdayView = findViewById(R.id.tvBirthdayView);
        tvBirthPlace = findViewById(R.id.tvBirthPlace);
        tvBirthPlacePremium = findViewById(R.id.tvBirthPlacePremium);
        tvBirthPlaceView = findViewById(R.id.tvBirthPlaceView);
        tvMangalik = findViewById(R.id.tvMangalik);
        tvMangalikPremium = findViewById(R.id.tvMangalikPremium);
        tvMangalikView = findViewById(R.id.tvMangalikView);
        tvContact = findViewById(R.id.tvContact);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvMName = findViewById(R.id.tvMName);
        tvCall = findViewById(R.id.tvCall);
        tvAddress = findViewById(R.id.tvAddress);
        tvAddressPremium = findViewById(R.id.tvAddressPremium);
        tvAddressView = findViewById(R.id.tvAddressView);
        tvFStatus = findViewById(R.id.tvFStatus);
        tvFather = findViewById(R.id.tvFather);
        tvFatherOcc = findViewById(R.id.tvFatherOcc);
        tvMother = findViewById(R.id.tvMother);
        tvMotherOcc = findViewById(R.id.tvMotherOcc);
        tvBrother = findViewById(R.id.tvBrother);
        tvSister = findViewById(R.id.tvSister);
        tvFamily = findViewById(R.id.tvFamily);
        llML1 = findViewById(R.id.llML1);
        llML2 = findViewById(R.id.llML2);
        llML3 = findViewById(R.id.llML3);
        llML4 = findViewById(R.id.llML4);

    }


    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle b = intent.getExtras();
        if (b != null && b.containsKey("other_id")) {
            if (!other_id.equals(b.getString("other_id"))) {
                other_id = b.getString("other_id");
                getMyProfile();
            }
        }
    }


    private void makePartnerList(JSONObject data) {
        try {

            JSONArray field = data.getJSONArray("partners_field");
            for (int k = 0; k < field.length(); k++) {
                JSONArray array = field.getJSONObject(k).getJSONArray("value");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String title = obj.getString("title");
                    String type = obj.getString("type");
                    String value = obj.getString("value");
                    if (!title.equals("Designation"))
                        userPartnerDataList.add(new PartnerFields(title, type, value));
                }
            }

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            userPartnerRecyclerView.setLayoutManager(mLayoutManager);
            CustomPartnerProfileAdapter adapter = new CustomPartnerProfileAdapter(userPartnerDataList, this);
            userPartnerRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", other_id);
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.other_user_profile, param, response -> {
            AppDebugLog.print("profile response in other user profile : " + response);
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");
                    if (data.has("contact_viewed"))
                        isProfileViewed = data.getString("contact_viewed");
                    //tv_photo_count
                    JSONArray fileds = data.getJSONArray("fileds");
                    photo_arr = fileds.getJSONObject(fileds.length() - 1).getJSONArray("value");
                    if (photo_arr.length() >= 2)
                        tv_photo_count.setText((photo_arr.length() - 1) + "+");
                    else
                        tv_photo_count.setVisibility(View.GONE);

                    Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    String photo_view_status = dataObject.get("photo_view_status").getAsString();
                    String photo_view_count = dataObject.get("photo_view_count").getAsString();

                    common.setImage(photo_view_count, photo_view_status, data.getString("photo1_approve"),
                            data.getString("photo1"), imgProfile, null, 0);
                    common.setImage(photo_view_count, photo_view_status, data.getString("photo1_approve"),
                            data.getString("photo1"), circleImageView, null, 0);
//                    common.setImage(photo_view_count,photo_view_status, data.getString("photo2_approve"), data.getString("photo2"), imgProfileTwo, null,68);
//                    common.setImage(photo_view_count,photo_view_status, data.getString("photo3_approve"), data.getString("photo3"), imgProfileThree, null,68);
//                    common.setImage(photo_view_count,photo_view_status, data.getString("photo4_approve"), data.getString("photo4"), imgProfileFour, null,68);


//                    if (photo_view_status.equals("0")) {
//                        if (photo_view_count.equals("0")) {
//                            isProtected = true;
//                            imgProfile.setImageResource(photoProtectPlaceHolder);
//                        } else if (photo_view_count.equals("1")) {
//                            if (!data.getString("photo1_approve").equals("UNAPPROVED")) {
//                                Picasso.get().load(data.getString("photo1")).into(imgProfile);
//                            } else {
//                                imgProfile.setImageResource(placeHolder);
//                            }
//                        }
//                    }
                    username = data.getString("username");

                    tv_id.setText(data.getString("matri_id"));
                    String[] Name = data.getString("username").split(" ");
                    if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        if (Name.length == 3) {
                            tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                            tvTitle.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
//                            getSupportActionBar().setTitle(Name[0].charAt(0) + " " + Name[1]+ " " + Name[2]);
                        } else if (Name.length == 2) {
                            tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                            tvTitle.setText(Name[0].charAt(0) + " " + Name[1]);
//                            getSupportActionBar().setTitle(Name[0].charAt(0) + " " + Name[1]);
                        } else {
                            tvTitle.setText(Name[0].charAt(0));
                            tv_name.setText(Name[0].charAt(0));
//                            getSupportActionBar().setTitle(Name[0].charAt(0));
                        }
                    } else {
//                        getSupportActionBar().setTitle(data.getString("username"));
                        tv_name.setText(data.getString("username"));
                        tvTitle.setText(data.getString("username"));
                    }

                    if (data.has("height_str")) {
                        height = data.getString("height_str");
                    } else height = data.getString("height");

                    String description = common.getDetailsToFullView(data.getString("age"), height.replace("ft", "\'").replace("in", "\""),
                            data.getString("education_name"), data.getString("designation_name"), data.getString("occupation_name"),
                            data.getString("religion_name"), data.getString("caste_name"), data.getString("city_name"), data.getString("state_name"));

                    tv_detail.setText(description);

                    other_matri_id = data.getString("matri_id");

//                    JSONArray partners_field = data.getJSONArray("partners_field");
                    makePartnerList(data);

                    makeUserProfileList(dataObject);

                    other_matri_id = data.getString("matri_id");
                    JSONObject action = data.getJSONArray("action").getJSONObject(0);
                    forCallMobileNo = data.getString("mobile");


//                    JSONArray partners_field = data.getJSONArray("partners_field");
//                    displayPref(partners_field);

                    try {
                        if (action.getString("is_like").equals("Yes")) {
                            fabLike.setImageResource(R.drawable.heart_fill_pink);
                            fabLike.setTag(1);
                        } else {
                            fabLike.setImageResource(R.drawable.heart_gray_fill);
                            fabLike.setTag(0);
                        }

                        if (action.getInt("is_block") == 1) {
                            fabBlock.setImageResource(R.drawable.ban);
                            fabBlock.setTag(1);
                        } else {
                            fabBlock.setImageResource(R.drawable.ban_gry);
                            fabBlock.setTag(0);
                        }

                        if (action.getInt("is_shortlist") == 1) {
                            fabShortlist.setTag(1);
                            fabShortlist.setImageResource(R.drawable.star_fill_yellow);

                        } else {
                            fabShortlist.setTag(0);
                            fabShortlist.setImageResource(R.drawable.star_gray_fill);
                        }

                        if (!action.getString("is_interest").equals("")) {
                            fabSendInterest.setTag(1);
                            fabSendInterest.setImageResource(R.drawable.check_fill_green);
                        } else {
                            fabSendInterest.setTag(0);
                            fabSendInterest.setImageResource(R.drawable.check_gray_fill);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else if (object.getString("status").equals("warning")) {
                    common.showToast("Your current membership plan does not allow this action.",scrollView);
                    startActivity(new Intent(OtherUserProfileActivity.this, PlanListActivity.class));
                    finish();
                } else if (object.getString("status").equals("error")) {
                    if (object.has("errmessage")) {
                        common.showToast(object.getString("errmessage"),scrollView);
                        finish();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void displayPref(JSONArray partners_field) throws JSONException {
        JSONArray array = partners_field.getJSONObject(0).getJSONArray("value");
        lay_basic_pref.removeAllViews();
        int pref = 0;
        for (int i = 0; i < array.length(); i++) {
            JSONObject obj = array.getJSONObject(i);
            String title = obj.getString("title");
            String type = obj.getString("type");
            String value = obj.getString("value");

            LinearLayout main = new LinearLayout(this);
            main.setOrientation(LinearLayout.HORIZONTAL);
            main.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            main.setBackgroundResource(R.drawable.underline_gray);

            LinearLayout submain = new LinearLayout(this);
            submain.setOrientation(LinearLayout.VERTICAL);
            submain.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1));

            TextView textView1 = new TextView(this);
            textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            textView1.setPadding(20, 10, 10, 10);
            textView1.setText(title);
            textView1.setTextSize(16f);
            submain.addView(textView1);

            TextView textView2 = new TextView(this);
            textView2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            textView2.setTextColor(Color.BLACK);
            textView2.setTypeface(Typeface.DEFAULT_BOLD);

            String prefValue = "N/A";
            if (value != null && value.length() > 0) {
                prefValue = value;
            } else {
                continue;
            }
            textView2.setText(prefValue);
            textView2.setPadding(20, 10, 10, 10);
            textView2.setTextSize(15f);
            submain.addView(textView2);

            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 4);
            param.setMargins(0, 20, 0, 0);
            LinearLayout submain1 = new LinearLayout(this);
            submain1.setOrientation(LinearLayout.VERTICAL);
            submain1.setGravity(Gravity.CENTER);
            submain1.setLayoutParams(param);

            main.addView(submain);
            if (type.equals("Yes")) {
                pref = pref + 1;
                ImageView img = new ImageView(this);
                img.setImageResource(R.drawable.check_fill_green);
                img.setLayoutParams(new LinearLayout.LayoutParams(50, 50));

                submain1.addView(img);
                main.addView(submain1);
            }
            lay_basic_pref.addView(main);
        }

        tv_basic_label_pref.setText(" You match " + pref + " out of 9 Preferences");
        String mainTitle = partners_field.getJSONObject(0).getString("name");
        if (mainTitle != null && mainTitle.length() > 0) {
            tv_basic_label_pref.setText(mainTitle);
        }
    }

    private void alertPhotoPassword() {
        final String[] arr = new String[]{"We found your profile to be a good match. Please accept photo password request to proceed further.",
                "I am interested in your profile. I would like to view photo now, accept photo request."};
        final String[] selected = {"We found your profile to be a good match. Please accept photo password request to proceed further."};
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(this);

        alt_bld.setTitle("Photos View Request");
        alt_bld.setSingleChoiceItems(arr, 0, (dialog, item) -> {
            //dialog.dismiss();// dismiss the alertbox after chose option
            selected[0] = arr[item];
        });
        alt_bld.setPositiveButton("Send", (dialogInterface, i) -> sendRequest(selected[0]));
        alt_bld.setNegativeButton("Cancel", (dialogInterface, i) -> {
            //alertpassword(password,url);
        });
        AlertDialog alert = alt_bld.create();
        alert.show();
    }

    private void sendRequest(String int_msg) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", other_matri_id);
        param.put("requester_id", session.getLoginData(SessionManager.KEY_MATRI_ID));

        common.makePostRequest(AppConstants.photo_password_request, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast( object.getString("errmessage"), scrollView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);

    }

    private boolean isNormalCall = false;
    private String forCallMobileNo = "";

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_photo_count:
                if (!isProtected && photo_arr.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), GallaryNewActivity.class);
                    intent.putExtra("imagePosition", 0);
                    intent.putExtra("imageArray", photo_arr.toString());
                    startActivity(intent);
                }
                break;

            case R.id.fabCall:
                if (forCallMobileNo.length() > 0) {
                    if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        callFbCall();
                    } else {
                        startActivity(new Intent(this, PlanListActivity.class));
                        common.showToast("Please upgrade your membership to Call/Whatsapp.",scrollView);
                    }
                } else {
                    common.showToast("Mobile number is not available",scrollView);
                }

                break;

            case R.id.fabLike:
                int likeTag = (int) fabLike.getTag();
                if (likeTag == 1) {
                    likeRequest("No", other_matri_id);
                } else {
                    likeRequest("Yes", other_matri_id);
                }
                break;
            case R.id.fabBlock:
                int blockTag = (int) fabBlock.getTag();
                if (blockTag == 1) {
                    blockRequest("remove", other_matri_id);
                } else {
                    blockRequest("add", other_matri_id);
                }
                break;
            case R.id.fabShortlist:
                int shortlistTag = (int) fabShortlist.getTag();
                if (shortlistTag == 1) {
                    shortlistRequest("remove", other_matri_id);
                } else {
                    shortlistRequest("add", other_matri_id);
                }
                break;
            case R.id.fabMessage:
                if (!common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                    Intent i = new Intent(this, ConversationActivity.class);
                    i.putExtra("matri_id", other_matri_id);
                    i.putExtra("username", username);
                    startActivity(i);
                } else {
                    common.showToast("Please upgrade your membership to chat with this member.", scrollView);
                    startActivity(new Intent(this, PlanListActivity.class));
                }
                break;

            case R.id.fabSendInterest:
                int interestTag = (int) fabSendInterest.getTag();
                if (interestTag == 1) {
                    common.showToast("You already sent interest to this user.",scrollView);
                } else {
                    //fabSendInterest.setPressed(false);
                    LayoutInflater inflater1 = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    final View vv = inflater1.inflate(R.layout.bottom_sheet_interest, null, true);
                    //context.getLayoutInflater().inflate(R.layout.bottom_sheet_interest, null);
                    final RadioGroup grp_interest = vv.findViewById(R.id.grp_interest);

                    final BottomSheetDialog dialog = new BottomSheetDialog(this);
                    dialog.setContentView(vv);
                    dialog.show();

                    ImageView tv_cancel = vv.findViewById(R.id.tv_cancel);
                    tv_cancel.setOnClickListener(view13 -> dialog.dismiss());

                    Button send = vv.findViewById(R.id.btn_send_intr);
                    send.setOnClickListener(view12 -> {
                        dialog.dismiss();
                        if (grp_interest.getCheckedRadioButtonId() != -1) {
                            RadioButton btn = vv.findViewById(grp_interest.getCheckedRadioButtonId());
                            interestRequest(other_matri_id, btn.getText().toString().trim());
                        }
                    });
                }
                break;
        }
    }


    public void callFbCall() {

//        AlertDialog.Builder alertConfirmViewContactDetails = new AlertDialog.Builder(this);
//        alertConfirmViewContactDetails.setTitle("Connect with");
//        alertConfirmViewContactDetails.setMessage("Please select option from below to connect with partner");
//        alertConfirmViewContactDetails.setPositiveButton("Call", (dialogInterface, i) -> {
//            isNormalCall = true;
//            showContactDeductConfirm();
//        });
//        alertConfirmViewContactDetails.setNegativeButton("Whatsapp", (dialogInterface, i) -> {
//            isNormalCall = false;
//            showContactDeductConfirm();
//        });
//        AlertDialog alert = alertConfirmViewContactDetails.create();
//        alert.show();


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomsheetView = getLayoutInflater().inflate(R.layout.call_popup, null);
        bottomSheetDialog.setContentView(bottomsheetView);

        RelativeLayout tvMessage = bottomsheetView.findViewById(R.id.tvMessageOne);
        Button btnNeedHelp = bottomsheetView.findViewById(R.id.btnNeedHelp);
        Button btnLoginWithOtpOne = bottomsheetView.findViewById(R.id.btnLoginWithOtpOne);



        tvMessage.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

        });

        btnLoginWithOtpOne.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();
            isNormalCall = false;
            showContactDeductConfirm();

        });


        btnNeedHelp.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            isNormalCall = true;
            showContactDeductConfirm();

        });

        bottomSheetDialog.show();
    }

    private void likeRequest(String tag, String other_matri_id) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("other_id", other_matri_id);
        param.put("like_status", tag);

        common.makePostRequestTime(AppConstants.like_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            AppDebugLog.print("");
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("Yes")) {
                    fabLike.setTag(1);
                    fabLike.setImageResource(R.drawable.heart_fill_pink);
                    common.showAlert("Like", object.getString("errmessage"), R.drawable.heart_fill_pink);
                } else {
                    fabLike.setTag(0);
                    fabLike.setImageResource(R.drawable.heart_gray_fill);
                    common.showAlert("Unlike", object.getString("errmessage"), R.drawable.heart_fill_pink);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    @Override
    public void onTabChanged(String tabId) {
        switch (tabId) {
            case TAB_FIRST:
                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tab_selector);
                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tabunselcolor);

                TextView tv = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
                TextView tv1 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);

                common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv);
                common.setDrawableLeftTextViewLefttab(R.drawable.user_pink, tv1);

                setTextViewDrawableColor(tv, R.color.colorAccent);
                setTextViewDrawableColor(tv1, R.color.colorAccent);
                break;
            case TAB_SECOND:
                host.getTabWidget().getChildAt(TAB_FIRST_POSITION).setBackgroundResource(R.drawable.tabunselcolor);
                host.getTabWidget().getChildAt(TAB_SECOND_POSITION).setBackgroundResource(R.drawable.tab_selector);

                TextView tv2 = host.getTabWidget().getChildAt(TAB_FIRST_POSITION).findViewById(android.R.id.title);
                TextView tv3 = host.getTabWidget().getChildAt(TAB_SECOND_POSITION).findViewById(android.R.id.title);

                common.setDrawableLeftTextViewLefttab(R.drawable.user_pink, tv2);
                common.setDrawableLeftTextViewLefttab(R.drawable.user_fill_pink, tv3);

                setTextViewDrawableColor(tv2, R.color.colorAccent);
                setTextViewDrawableColor(tv3, R.color.colorAccent);
                break;
        }
    }

    @Override
    public void itemClicked(ViewProfileFieldsBean item) {
    }

    @Override
    public void itemClicked(ViewProfileSectionBean section) {
    }

    @Override
    public void lastSectionExpand(ViewProfileSectionBean section) {
        if (section.getId().equalsIgnoreCase("contact_info")) {
            AppDebugLog.print("section id in itemClicked : " + section.getId());
            new Handler().postDelayed(() -> {
                txtFocus.requestFocus();
                int height = (section.getViewProfileFieldList().size() / 2) * Common.convertDpToPixels(100, this);
                scrollView.scrollTo(0, scrollView.getBottom() + height);
                //userProfileRecyclerView.smoothScrollToPosition(userProfileRecyclerView.getAdapter().getItemCount()-1);
            }, 100);
        } else {
            txtFocus.clearFocus();
        }
    }


    @Override
    public void viewContact(ViewProfileSectionBean section) {
        if (isProfileViewed == "0") {
            AlertDialog.Builder alertConfirmViewContactDetails = new AlertDialog.Builder(this);
            alertConfirmViewContactDetails.setTitle("Contact Details");
            alertConfirmViewContactDetails.setMessage("This action will deduct by one contact view count, are you sure want continue?");
            alertConfirmViewContactDetails.setPositiveButton("Yes", (dialogInterface, i) -> viewContactRequest(section));
            alertConfirmViewContactDetails.setNegativeButton("No", (dialogInterface, i) -> {
            });
            AlertDialog alert = alertConfirmViewContactDetails.create();
            alert.show();
        } else {
            viewContactRequest(section);
        }
    }

    private void viewContactRequest(ViewProfileSectionBean section) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver_matri_id", other_matri_id);

        common.makePostRequest(AppConstants.view_contact, param, response -> {
            common.hideProgressRelativeLayout(loader);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("success").equals("success")) {
                    section.setContactVisible(true);
                    section.setExpanded(true);

                    new Handler().postDelayed(() -> {
                        txtFocus.requestFocus();
                        int height = (section.getViewProfileFieldList().size() / 2) * Common.convertDpToPixels(100, this);
                        scrollView.scrollTo(0, scrollView.getBottom() + height);
                    }, 100);
                    myProfileSectionedExpandableLayoutHelper.notifyDataSetChanged();
                } else
                    common.showToast(object.getString("errmessage"),scrollView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void setTextViewDrawableColor(TextView textView, int color) {
        for (Drawable drawable : textView.getCompoundDrawables()) {
            if (drawable != null) {
                drawable.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(textView.getContext(), color), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    private void blockRequest(final String tag, String id) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("unblockuserid", id);
        } else
            param.put("blockuserid", id);

        param.put("blacklist_action", tag);

        common.makePostRequestTime(AppConstants.block_user, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("add")) {
                    fabBlock.setImageResource(R.drawable.ban);
                    common.showAlert("Block", object.getString("errmessage"), R.drawable.ban);
                    fabBlock.setTag(1);
                } else {
                    fabBlock.setImageResource(R.drawable.ban_gry);
                    common.showAlert("Unblock", object.getString("errmessage"), R.drawable.ban_gry);
                    fabBlock.setTag(0);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void shortlistRequest(final String tag, String id) {
        common.showProgressRelativeLayout(loader);
        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        if (tag.equals("remove")) {
            param.put("shortlisteduserid", id);
        } else
            param.put("shortlistuserid", id);

        param.put("shortlist_action", tag);

        common.makePostRequestTime(AppConstants.shortlist_user, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                if (tag.equals("add")) {
                    fabShortlist.setTag(1);
                    fabShortlist.setImageResource(R.drawable.star_fill_yellow);
                    common.showAlert("Shortlist", object.getString("errmessage"), R.drawable.star_fill_yellow);
                } else {
                    fabShortlist.setTag(0);
                    fabShortlist.setImageResource(R.drawable.star_gray_fill);
                    common.showAlert("Remove From Shortlist", object.getString("errmessage"), R.drawable.star_gray_fill);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void interestRequest(String matri_id, String int_msg) {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver", matri_id);
        param.put("message", int_msg);

        common.makePostRequestTime(AppConstants.send_interest, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    fabSendInterest.setTag(1);
                    fabSendInterest.setImageResource(R.drawable.check_fill_green);
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                } else {
                    fabSendInterest.setTag(0);
                    fabSendInterest.setImageResource(R.drawable.check_gray_fill);
                    common.showAlert("Interest", object.getString("errmessage"), R.drawable.check_fill_green);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void showContactDeductConfirm() {
//        AlertDialog.Builder alertConfirmViewContactDetails = new AlertDialog.Builder(this);
//        alertConfirmViewContactDetails.setTitle("Contact Details");
//        alertConfirmViewContactDetails.setMessage("This action will deduct by one contact view count, are you sure want continue?");
//        alertConfirmViewContactDetails.setPositiveButton("Yes", (dialogInterface, i) -> viewContactApi());
//        alertConfirmViewContactDetails.setNegativeButton("No", (dialogInterface, i) -> {
//        });
//        AlertDialog alert = alertConfirmViewContactDetails.create();
//        alert.show();


        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomsheetView = getLayoutInflater().inflate(R.layout.conatct_confirmation__popup, null);
        bottomSheetDialog.setContentView(bottomsheetView);

        RelativeLayout tvMessage = bottomsheetView.findViewById(R.id.tvMessage);
        Button btnNo = bottomsheetView.findViewById(R.id.btnNo);
        Button btnYes = bottomsheetView.findViewById(R.id.btnYes);

        tvMessage.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();

        });

        btnNo.setOnClickListener(view -> {
            bottomSheetDialog.dismiss();

        });


        btnYes.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            viewContactApi();

        });
        bottomSheetDialog.show();
    }

    private void viewContactApi() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver_matri_id", other_matri_id);

        common.makePostRequest(AppConstants.view_contact, param, response -> {
            common.hideProgressRelativeLayout(loader);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("success").equals("success")) {
                    if (isNormalCall) {
                        //open dialer
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + forCallMobileNo));
                        startActivity(intent);
                    } else {
                        // whatsapp call
                        String url = "https://api.whatsapp.com/send?phone=" + forCallMobileNo;
                        try {
                            PackageManager pm = this.getPackageManager();
                            pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(Uri.parse(url));
                            intent.setPackage("com.whatsapp");
                            startActivity(intent);
                        } catch (PackageManager.NameNotFoundException e) {
                            common.showToast("Whatsapp app not installed in your phone", scrollView);
                            e.printStackTrace();
                        }
                    }
                } else
                    common.showToast(object.getString("errmessage"),scrollView);

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    public void viewContact() {
        if (isProfileViewed == "0") {
            AlertDialog.Builder alertConfirmViewContactDetails = new AlertDialog.Builder(this);
            alertConfirmViewContactDetails.setTitle("Contact Details");
            alertConfirmViewContactDetails.setMessage("This action will deduct by one contact view count, are you sure want continue?");
            alertConfirmViewContactDetails.setPositiveButton("Yes", (dialogInterface, i) -> viewContactRequest());
            alertConfirmViewContactDetails.setNegativeButton("No", (dialogInterface, i) -> {
            });
            AlertDialog alert = alertConfirmViewContactDetails.create();
            alert.show();
        } else {
            viewContactRequest();
        }
    }

    private void viewContactRequest() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("matri_id", session.getLoginData(SessionManager.KEY_MATRI_ID));
        param.put("receiver_matri_id", other_matri_id);

        common.makePostRequest(AppConstants.view_contact, param, response -> {
            common.hideProgressRelativeLayout(loader);
            Log.d("resp", response);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("success").equals("success")) {

                    showContactPoup();
                } else {
                    viewContactIfPlanOudated(object.getString("errmessage"));
//                    common.showToast(object.getString("errmessage"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),scrollView);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),scrollView);
            }
        },scrollView);
    }

    private void showContactPoup() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_contact_premium);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        TextView tvContact = bottomSheetDialog.findViewById(R.id.tvContact);
        TextView tvEmail = bottomSheetDialog.findViewById(R.id.tvEmail);
        LinearLayout llcall = bottomSheetDialog.findViewById(R.id.llcall);
        tvContact.setText(mobile);
        tvEmail.setText(email);

        llcall.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mobile));
            startActivity(intent);
        });

        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });


        bottomSheetDialog.show();
    }

    public void viewContactOnClick(View view) {
        viewContact();

    }


    public void viewContactIfPlanOudated(String errmessage) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_contact);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        Button btnUpgrade = bottomSheetDialog.findViewById(R.id.btnUpgrade);
        TextView tv = bottomSheetDialog.findViewById(R.id.tv);
        tv.setText(errmessage);


        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        btnUpgrade.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(OtherUserProfileActivity.this, PlanListActivity.class));
//            finish();
        });
        bottomSheetDialog.show();
    }

    private void makeUserProfileList(JsonObject dataObject) throws JSONException {
        Button btnCall = findViewById(R.id.btnPremium);
        btnCall.setText("View Contact Details");


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, (int) getResources().getDimension(R.dimen.dp5), (int) getResources().getDimension(R.dimen.dp14), 0);


        if (checkFiledIsEmpty(dataObject.get("profile_text").getAsString()).length() > 40) {

            findViewById(R.id.ivML1).setLayoutParams(params);
            llML1.setGravity(Gravity.TOP);
            tvAbout.setGravity(Gravity.TOP);
        } else {
            llML1.setGravity(Gravity.CENTER_VERTICAL);
            tvAbout.setGravity(Gravity.CENTER_VERTICAL);
        }

        if (checkFiledIsEmpty(dataObject.get("hobby").getAsString()).length() > 40) {
            findViewById(R.id.ivML2).setLayoutParams(params);
            llML2.setGravity(Gravity.TOP);
            tvHobbies.setGravity(Gravity.TOP);
        } else {

            llML2.setGravity(Gravity.CENTER_VERTICAL);
            tvHobbies.setGravity(Gravity.CENTER_VERTICAL);
        }


        if (checkFiledIsEmpty(dataObject.get("family_details").getAsString()).length() > 40) {
            findViewById(R.id.ivML3).setLayoutParams(params);
            llML3.setGravity(Gravity.TOP);
            tvFamily.setGravity(Gravity.TOP);
        } else {
            llML3.setGravity(Gravity.CENTER_VERTICAL);
            tvFamily.setGravity(Gravity.CENTER_VERTICAL);
        }

        if (checkFiledIsEmpty(dataObject.get("part_expect").getAsString()).length() > 40) {
            findViewById(R.id.ivML4).setLayoutParams(params);
            llML4.setGravity(Gravity.TOP);
            tvLookinFor.setGravity(Gravity.TOP);
        } else {
            llML4.setGravity(Gravity.CENTER_VERTICAL);
            tvLookinFor.setGravity(Gravity.CENTER_VERTICAL);
        }


        tvCreatedBy.setText("Profile created by " + checkFiledIsEmpty(dataObject.get("profileby").getAsString()));
        tvAbout.setText(checkFiledIsEmpty(dataObject.get("profile_text").getAsString()));
        tvHeight.setText(checkFiledIsEmpty(dataObject.get("age").getAsString() + ", " + height));
        tvAppearnce.setText(checkFiledIsEmpty(dataObject.get("bodytype").getAsString() + ", " + dataObject.get("weight").getAsString()) + " Kgs, " + checkFiledIsEmpty(dataObject.get("complexion").getAsString()) + " Skin");
        tvHealth.setText("Health/Challenged - " + checkFiledIsEmpty(dataObject.get("physical_info").getAsString()));
        tvMarital.setText(checkMartialStatusFiled(dataObject));
        tvMtounge.setText("Mother Tongue - " + checkFiledIsEmpty(dataObject.get("mtongue_name").getAsString()));
        tvCity.setText("Lives at " + checkFiledIsEmpty(dataObject.get("city_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("state_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
        tvCitizenship.setText(checkFiledIsEmpty(dataObject.get("residence").getAsString()) + " of " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
        tvFood.setText(checkFiledIsEmpty(dataObject.get("diet").getAsString()));
        String drink = dataObject.get("drink").getAsString().equals("No") ? "Doesn't Drink" : dataObject.get("drink").getAsString().equals("Occasionally") ? "Drinks Occasionally" : "Drinks Regularly";
        tvDrink.setText(drink);
        String smoke = dataObject.get("smoke").getAsString().equals("No") ? "Doesn't Smoke" : dataObject.get("smoke").getAsString().equals("Occasionally") ? "Smokes Occasionally" : "Smokes Regularly";
        tvSmoke.setText(smoke);


        tvEducation.setText(checkFiledIsEmpty(dataObject.get("education_name").getAsString()));
        tvProfession.setText("Work Sector - " + checkFiledIsEmpty(dataObject.get("employee_in").getAsString()));
        tvIncome.setText(checkIncomeIsEmpty("Earn Rs. " + dataObject.get("income").getAsString()));
        tvOccupation.setText(checkFiledIsEmpty(dataObject.get("occupation_name").getAsString()));
        tvDesignation.setText("Designated as - " + checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
        tvLookinFor.setText(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()));
        tvHobbies.setText(checkFiledIsEmpty(dataObject.get("hobby").getAsString()));
        tvLanuguage.setText(checkFiledIsEmpty(dataObject.get("languages_known_str").getAsString()));

        tvReligion.setText(checkFiledIsEmpty(dataObject.get("religion_name").getAsString()));
        tvCaste.setText(checkFiledIsEmpty(dataObject.get("caste_name").getAsString()) + "" + checkSubCastEmpty(dataObject.get("subcaste").getAsString()));
        tvGothram.setText("Gothra(m) - " + checkFiledIsEmpty(dataObject.get("gothra").getAsString()));
        tvHoroscope.setText("Horoscope belief - " + checkFiledIsEmpty(dataObject.get("horoscope").getAsString()));
//        tvName.setText(checkFiledIsEmpty(dataObject.get("time_to_call").getAsString()));
        tvName.setText(checkFiledIsEmpty(dataObject.get("reference").getAsString()));
        tvMName.setText(checkFiledIsEmpty(dataObject.get("reference").getAsString()));
        tvCall.setText(checkFiledIsEmpty(dataObject.get("time_to_call").getAsString()));
        time_to_call = checkFiledIsEmpty(dataObject.get("time_to_call").getAsString());
        if (time_to_call.equals("Not Mentioned"))
            findViewById(R.id.llcall).setVisibility(View.GONE);
        else findViewById(R.id.llcall).setVisibility(View.VISIBLE);
//        if (time_to_call.equals("Not Mentioned")) findViewById(R.id.root).setVisibility(View.GONE);
//        else findViewById(R.id.root).setVisibility(View.VISIBLE);
        tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString()) + " ~ " + checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
        tvFather.setText("Father's Name - " + checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
        tvFatherOcc.setText("His Occupation - " + checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
        tvMother.setText("Mother's Name - " + checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
        tvMotherOcc.setText("Her Occupation - " + checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
        tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
        tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)", dataObject.get("no_of_married_sister").getAsString()));
        tvFamily.setText(checkFiledIsEmpty(dataObject.get("family_details").getAsString()));

        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvMangalik.setText("Is Manglik? - " + checkFiledIsEmpty(dataObject.get("manglik").getAsString()));
        tvStar.setText("Star - " + checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
        tvRaas.setText("Moonsign (Raas) - " + checkFiledIsEmpty(dataObject.get("moonsign_str").getAsString()));
        tvBloodGroup.setText("Blood Group - " + checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
        tvBirthPlace.setText(checkBornFiledIsEmpty(dataObject.get("birthplace").getAsString(), "Born in ", dataObject.get("birthtime").getAsString(), " at "));
        tvBirthday.setText("Born on " + updateLabel(dataObject.get("birthdate").getAsString()));
        tvContact.setText(maskString(dataObject.get("mobile").getAsString(), 0, 10, '*'));
        int index = dataObject.get("email").getAsString().indexOf("@");
        tvEmail.setText(maskString(dataObject.get("email").getAsString(), 0, index, '*'));
        tvAddress.setText("Ancestral Origin - " + checkFiledIsEmpty(dataObject.get("address").getAsString()));

        email = dataObject.get("email").getAsString();
        mobile = dataObject.get("mobile").getAsString();


    }

    private String maskString(String strText, int start, int end, char maskChar) {

        if (strText == null || strText.equals(""))
            return "";

        if (start < 0)
            start = 0;

        if (end > strText.length())
            end = strText.length();

        if (start > end)
            return "NA";

        int maskLength = end - start;

        if (maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for (int i = 0; i < maskLength; i++) {
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }

    private final Calendar myCalendar = Calendar.getInstance();

    private String updateLabel(String dob) {
        if (dob.isEmpty() || dob == null || dob.equals(""))
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else {
            String[] arr = dob.split("-");

            myCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));

            String myFormat = AppConstants.BIRTH_DATE_FORMAT; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(myCalendar.getTime()).toString();
        }


    }


    private String checkBornFiledIsEmpty(String value1, String tag1, String value2, String tag2) {
        if (value1.isEmpty() || value1 == null || value1.equals(""))
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else {
            if (value2.isEmpty() || value2 == null || value2.equals("")) return tag1 + value1;
            return tag1 + value1 + tag2 + value2;
        }
    }

    private String checkSubCastEmpty(String value) {
        if (value.isEmpty() || value == null || value.equals("")) return "";
        else return " ~ " + value;
    }

    private String checkMartialStatusFiled(JsonObject dataObject) {
        String mt = dataObject.get("marital_status").getAsString();
        String tc = dataObject.get("total_children").getAsString();
        String sc = dataObject.get("status_children").getAsString();
        if (mt.isEmpty() || mt == null || mt.equals(""))
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else {
            if (mt.equals("Never Married")) {
                return mt;
            } else {
                return mt + ", " + tc + " ~ " + sc;
            }
        }
        //dataObject.get("marital_status").getAsString()+", "+ checkFiledIsEmpty(dataObject.get("total_children").getAsString())+ " ~ "+ checkFiledIsEmpty(dataObject.get("status_children").getAsString()


    }

    private String checkFiledIsEmpty(String value) {
        if (value.isEmpty() || value == null || value.equals("")) {
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        } else return value;
    }

    private String checkIncomeIsEmpty(String value) {
        if (value.isEmpty() || value == null || value.equals(""))
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else if (value.equalsIgnoreCase("Does not matter") || value.equalsIgnoreCase("not working") || value.equalsIgnoreCase("no income"))
            return value;
        else return value + " annually";
    }

    private String checkFiledIsEmptyNum(String value, String Tag, String married) {
        if (value.isEmpty() || value == null || value.equals("")) return "0" + Tag;
        else {
            if (married.isEmpty() || married == null || married.equals("")) {
                return value + Tag;
            } else return value + Tag + ", " + married;
        }


    }

}
