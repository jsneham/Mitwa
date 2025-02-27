package com.matriapp.mobile.activities;

import static com.matriapp.mobile.application.MyApplication.getContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.adapter.CustomPartnerProfileAdapter;
import com.matriapp.mobile.model.PartnerFields;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PreviewOthersProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private String height;
    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private TabHost host;
    private RelativeLayout loader;
    private TextView tv_basic_label_pref, tv_photo_count, tv_id, tv_name, tv_detail;
    private RecyclerView userPartnerRecyclerView;
    private int placeHolder, photoProtectPlaceHolder;
    private LinearLayout lay_basic_pref;
    private ImageView imgProfile, imgProfileTwo, imgProfileThree, imgProfileFour;
    private JSONArray photo_arr;
    private String other_id, other_matri_id;
    private boolean isProtected = false;
    private List<PartnerFields> userPartnerDataList = new ArrayList<>();

    private NestedScrollView scrollView;
    private TextView txtFocus;
    private String name, time_to_call, mobile, email;
    private String isProfileViewed = "";

    private TextView tvCreatedBy, tvAbout, tvHeight, tvAppearnce, tvMarital, tvMtounge, tvCity, tvCitizenship, tvFood, tvDrink, tvSmoke,tvHealth;
    private TextView tvEducation, tvProfession, tvProfPremium, tvProfView, tvIncome, tvIncomePremium, tvIncomeView, tvOccupation, tvDesignation, tvLookinFor, tvHobbies, tvLanuguage,
            tvReligion, tvCaste, tvGothram, tvGothPremium, tvGothView, tvHoroscope, tvHoroscopePremium,tvHoroscopeView ,tvStar, tvStarPremium, tvStarView, tvRaas, tvRaasPremium, tvRaasView,
            tvBloodGroup, tvBloodGroupPremium, tvBloodGroupView, tvBirthdayTime, tvBirthPlace, tvBirthPlacePremium, tvBirthPlaceView,
            tvMangalik, tvMangalikPremium, tvMangalikView, tvBirthday, tvBirthdayPremium, tvBirthdayView,
            tvContact, tvEmail, tvName, tvCall, tvAddress, tvAddressPremium, tvAddressView, tvFStatus, tvFather, tvFatherOcc, tvMother, tvMotherOcc, tvBrother, tvSister, tvFamily;
    private boolean isNotPremiumMember = false;
    private LinearLayout llML1, llML2, llML3, llML4;

    private ImageView circleImageView;
    private CoordinatorLayout llView;
    private TextView tvTitle;

    public void backClick(View view) {
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setStatusColor();
        session = new SessionManager(this);
        common = new Common(this);
        common.setGradient(getWindow());

        isNotPremiumMember = common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS));

        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_preview_others_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_backarrow_viewprofile);
        toolbar.setNavigationOnClickListener(view -> finish());

        ImageView img_more = findViewById(R.id.img_more);
        tv_photo_count = findViewById(R.id.tv_photo_count);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfileTwo = findViewById(R.id.imgProfileTwo);
        imgProfileThree = findViewById(R.id.imgProfileThree);
        imgProfileFour = findViewById(R.id.imgProfileFour);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        }
        imgProfile.setImageResource(placeHolder);
        imgProfileTwo.setImageResource(placeHolder);
        imgProfileThree.setImageResource(placeHolder);
        imgProfileFour.setImageResource(placeHolder);

        Bundle b = getIntent().getExtras();
        if (b != null && b.containsKey("other_id")) {
            other_id = b.getString("other_id");
        }
        userPartnerRecyclerView = findViewById(R.id.userPartnerRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        tv_photo_count = findViewById(R.id.tv_photo_count);
        tv_photo_count.setOnClickListener(this);

        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
        tv_detail = findViewById(R.id.tv_detail);
        loader = findViewById(R.id.loader);

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

        findViewById(R.id.llcall1).setOnClickListener(v -> {
            viewContact();
        });

        initTextView();


        getMyProfile();


        findViewById(R.id.llcall).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + time_to_call));
            startActivity(intent);
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
        tvProfPremium = findViewById(R.id.tvProfPremium);
        tvProfView = findViewById(R.id.tvProfView);
        tvIncome = findViewById(R.id.tvIncome);
        tvIncomePremium = findViewById(R.id.tvIncomePremium);
        tvIncomeView = findViewById(R.id.tvIncomeView);
        tvOccupation = findViewById(R.id.tvOccupation);
        tvDesignation = findViewById(R.id.tvDesignation);
//        tvDesignation.setVisibility(View.GONE);
        tvLookinFor = findViewById(R.id.tvLookinFor);
        tvHobbies = findViewById(R.id.tvHobbies);
        tvLanuguage = findViewById(R.id.tvLanuguage);
        tvReligion = findViewById(R.id.tvReligion);
        tvCaste = findViewById(R.id.tvCaste);
        tvGothram = findViewById(R.id.tvGothram);
        tvGothPremium = findViewById(R.id.tvGothPremium);
        tvGothView = findViewById(R.id.tvGothView);
        tvHoroscope = findViewById(R.id.tvHoroscope);
        tvHoroscopePremium  = findViewById(R.id.tvHoroscopePremium);
        tvHoroscopeView = findViewById(R.id.tvHoroscopeView);
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


    private void makePartnerList(JSONObject data) {
        try {

            JSONArray field = data.getJSONArray("partners_field");
            for(int k =0; k<field.length(); k++){
                JSONArray array=  field.getJSONObject(k).getJSONArray("value");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String title = obj.getString("title");
                    String type = obj.getString("type");
                    String value = obj.getString("value");
                    if(!title.equals("Designation"))
                        userPartnerDataList.add(new PartnerFields(title, type, value));
                }
            }

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            userPartnerRecyclerView.setLayoutManager(mLayoutManager);
            CustomPartnerProfileAdapter adapter = new CustomPartnerProfileAdapter(userPartnerDataList, getContext());
            userPartnerRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void getMyProfile() {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", other_id);
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));


        common.makePostRequest(AppConstants.other_user_profile_for_mobile, param, response -> {
            AppDebugLog.print("profile response in other user profile : " + response);
            common.hideProgressRelativeLayout(progressBar);
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

                    common.setImage(photo_view_count,photo_view_status, data.getString("photo1_approve"), data.getString("photo1"), imgProfile, null,68);
                    common.setImage(photo_view_count,photo_view_status, data.getString("photo1_approve"), data.getString("photo1"), circleImageView, null,68);
                    common.setImage(photo_view_count,photo_view_status, data.getString("photo2_approve"), data.getString("photo2"), imgProfileTwo, null,68);
                    common.setImage(photo_view_count,photo_view_status, data.getString("photo3_approve"), data.getString("photo3"), imgProfileThree, null,68);
                    common.setImage(photo_view_count,photo_view_status, data.getString("photo4_approve"), data.getString("photo4"), imgProfileFour, null,68);

                    if (photo_view_status.equals("0")) {
                        if (photo_view_count.equals("0")) {
                            isProtected = true;
                            imgProfile.setImageResource(photoProtectPlaceHolder);
                            imgProfileTwo.setImageResource(photoProtectPlaceHolder);
                            imgProfileThree.setImageResource(photoProtectPlaceHolder);
                            imgProfileFour.setImageResource(photoProtectPlaceHolder);
                        } else if (photo_view_count.equals("1")) {
                            if (!data.getString("photo1_approve").equals("UNAPPROVED")) {
                                Glide.with(this)
                                        .load(data.getString("photo1"))
                                        .fitCenter()
                                        .placeholder(placeHolder)
                                        .into(imgProfile);

                                Glide.with(this)
                                        .load(data.getString("photo2"))
                                        .fitCenter()
                                        .placeholder(placeHolder)
                                        .into(imgProfileTwo);

                                Glide.with(this)
                                        .load(data.getString("photo3"))
                                        .fitCenter()
                                        .placeholder(placeHolder)
                                        .into(imgProfileThree);

                                Glide.with(this)
                                        .load(data.getString("photo4"))
                                        .fitCenter()
                                        .placeholder(placeHolder)
                                        .into(imgProfileFour);
                            } else {
                                imgProfile.setImageResource(placeHolder);
                                imgProfileTwo.setImageResource(placeHolder);
                                imgProfileThree.setImageResource(placeHolder);
                                imgProfileFour.setImageResource(placeHolder);
                            }
                        }
                    }


                    tv_id.setText(data.getString("matri_id"));
                    String[] Name = data.getString("username").split(" ");
                    if (common.getIsUserPaid(session.getLoginData(SessionManager.KEY_PLAN_STATUS))) {
                        if (Name.length == 3) {
                            tv_name.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                            tvTitle.setText(Name[0].charAt(0) + " " + Name[1] + " " + Name[2]);
                        } else if (Name.length == 2) {
                            tv_name.setText(Name[0].charAt(0) + " " + Name[1]);
                            tvTitle.setText(Name[0].charAt(0) + " " + Name[1]);
                        } else {
                            tv_name.setText(Name[0].charAt(0));
                            tvTitle.setText(Name[0].charAt(0));
                        }
                    } else {
                        tv_name.setText(data.getString("username"));
                        tvTitle.setText(data.getString("username"));

                    }

//                    tv_name.setText(data.getString("username"));
//                    String description = data.getString("age")+", " +data.getString("height_str").replace("ft", "\'").replace("in", "\"") +" . "
//                            +checkFiledIsEmptyForDt(data.getString("education_name")) +checkFiledIsEmptyForDt(data.getString("designation_name"))+checkFiledIsEmptyForDt(data.getString("occupation_name")).replace(",", ".") + "\n"+
//                            checkFiledIsEmptyForDt(data.getString("religion_name")) + checkFiledIsEmptyForDt(data.getString("caste_name")).replace(", ", ". ")+ checkFiledIsEmptyForDt(data.getString("city_name"))+checkFiledIsEmptyForDt(data.getString("state_name")).replace(",","");


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


                } else if (object.getString("status").equals("warning")) {
                    common.showToast("Your current membership plan does not allow this action.",llView);
                    startActivity(new Intent(PreviewOthersProfileActivity.this, PlanListActivity.class));
                    finish();
                } else if (object.getString("status").equals("error")) {
                    if (object.has("errmessage")) {
                        common.showToast(object.getString("errmessage"),llView);
                        finish();
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

    private String checkFiledIsEmptyForDt(String value) {
        if (value.isEmpty() || value == null || value.equals("")) return "";
        else return value + ", ";
    }

    private void alertPhotoPassword() {
        final String[] arr = new String[]{"We found your profile to be a good match. Please accept Photo request to proceed further.",
                "I am interested in your profile. I would like to view photo now, accept photo request."};
        final String[] selected = {"We found your profile to be a good match. Please accept Photo request to proceed further."};
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
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("interest_message", int_msg);
        param.put("receiver_id", other_matri_id);
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


    private void setStatusColor() {
        //make translucent statusBar on kitkat devices
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
    }


    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_photo_count:
                if (!isProtected && photo_arr.length() > 0) {
                    Intent intent = new Intent(getApplicationContext(), GallaryNewActivity.class);
                    intent.putExtra("imagePosition", 0);
                    intent.putExtra("imageArray", photo_arr.toString());
                    intent.putExtra("tag", "other_profile");
                    startActivity(intent);
                }
                break;
        }

    }

    private void makeUserProfileList(JsonObject dataObject) {

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
//        tvIncome.setText(checkIncomeIsEmpty("Earn Rs. " + dataObject.get("income").getAsString()));
        tvOccupation.setText(checkFiledIsEmpty(dataObject.get("occupation_name").getAsString()));
        tvDesignation.setText("Designated as - " + checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
        tvLookinFor.setText(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()));
        tvHobbies.setText(checkFiledIsEmpty(dataObject.get("hobby").getAsString()));
        tvLanuguage.setText(checkFiledIsEmpty(dataObject.get("languages_known_str").getAsString()));

        tvReligion.setText(checkFiledIsEmpty(dataObject.get("religion_name").getAsString()));
        tvCaste.setText(checkFiledIsEmpty(dataObject.get("caste_name").getAsString()) + "" + checkSubCastEmpty(dataObject.get("subcaste").getAsString()));
        tvGothram.setText("Gothra(m) - " + checkFiledIsEmpty(dataObject.get("gothra").getAsString()));
//        tvHoroscope.setText("Horoscope belief - " + checkFiledIsEmpty(dataObject.get("horoscope").getAsString()));
        String matchmaker_name = checkFiledIsEmpty(dataObject.get("reference").getAsString());
        tvName.setText(maskString(matchmaker_name, 0, matchmaker_name.length(),'*'));
//        tvName.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
//        tvCall.setText(checkFiledIsEmpty(dataObject.get("time_to_call").getAsString()));
        time_to_call = checkFiledIsEmpty(dataObject.get("time_to_call").getAsString());
        tvCall.setText(maskString(time_to_call, 0, time_to_call.length(), '*'));
//        if (time_to_call.equals("Not Mentioned")) findViewById(R.id.llcall).setVisibility(View.GONE);
//        else findViewById(R.id.llcall).setVisibility(View.VISIBLE);
        tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString()) + " ~ " + checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
        tvFather.setText("Father's Name - " + checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
        tvFatherOcc.setText("His Occupation - " + checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
        tvMother.setText("Mother's Name - " + checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
        tvMotherOcc.setText("Her Occupation - " + checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
        tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
        tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)", dataObject.get("no_of_married_sister").getAsString()));
        tvFamily.setText(checkFiledIsEmpty(dataObject.get("family_details").getAsString()));


        isNotPremiumMebersOnly(dataObject);

    }

    private void isNotPremiumMebersOnly(JsonObject dataObject) {
//        if(isNotPremiumMember){
        tvProfession.setText("Work Sector -");
        tvProfPremium.setVisibility(View.VISIBLE);
        tvProfView.setVisibility(View.VISIBLE);
        tvIncome.setText("Earn Rs. ");
        tvIncomePremium.setVisibility(View.VISIBLE);
        tvIncomeView.setVisibility(View.VISIBLE);

        tvStar.setText("Star - ");
        tvStarPremium.setVisibility(View.VISIBLE);
        tvStarView.setVisibility(View.VISIBLE);

        tvGothram.setText("Gothra(m) - ");
        tvGothPremium.setVisibility(View.VISIBLE);
        tvGothView.setVisibility(View.VISIBLE);


        tvHoroscope.setText("Horoscope belief - ");
        tvHoroscopePremium.setVisibility(View.VISIBLE);
        tvHoroscopeView.setVisibility(View.VISIBLE);

        tvRaas.setText("Moonsign (Raas) - ");
        tvRaasPremium.setVisibility(View.VISIBLE);
        tvRaasView.setVisibility(View.VISIBLE);

        tvMangalik.setText("Is Manglik? - ");
        tvMangalikPremium.setVisibility(View.VISIBLE);
        tvMangalikView.setVisibility(View.VISIBLE);

        tvBloodGroup.setText("Blood Group - ");
        tvBloodGroupPremium.setVisibility(View.VISIBLE);
        tvBloodGroupView.setVisibility(View.VISIBLE);

        tvBirthPlace.setText("Born in ");
        tvBirthPlacePremium.setVisibility(View.VISIBLE);
        tvBirthPlaceView.setVisibility(View.VISIBLE);

        tvBirthday.setText("Born on ");
        tvBirthdayPremium.setVisibility(View.VISIBLE);
        tvBirthdayView.setVisibility(View.VISIBLE);
//        tvBirthdayTime.setText( " at "+ checkFiledIsEmpty(dataObject.get("birthtime").getAsString()));

        tvContact.setText(maskString(dataObject.get("mobile").getAsString(), 0 ,10, '*'));
        int index = dataObject.get("email").getAsString().indexOf("@");
        tvEmail.setText(maskString(dataObject.get("email").getAsString(), 0, index, '*'));

        tvAddress.setText("Ancestral Origin - ");
        tvAddressPremium.setVisibility(View.VISIBLE);
        tvAddressView.setVisibility(View.VISIBLE);

//        }
//        else{
//            tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
//        tvStar.setText("Star is " +checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
//        tvRaas.setText("Star is " +checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
//        tvBloodGroup.setText("Blood Group is " +checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
//        tvBirthPlace.setText(checkBornFiledIsEmpty(dataObject.get("birthplace").getAsString(), "Born in ","",""));
//        tvBirthday.setText(checkBornFiledIsEmpty(dataObject.get("birthplace").getAsString(), "Born on ", dataObject.get("birthtime").getAsString(), " at "));
//        tvContact.setText(checkFiledIsEmpty(dataObject.get("mobile").getAsString()));
//        tvEmail.setText(checkFiledIsEmpty(dataObject.get("email").getAsString()));

//        }
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

    private String checkFiledIsEmpty(String value) {
        if (value.isEmpty() || value == null || value.equals(""))
            return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else return value;
    }

    private String checkIncomeIsEmpty(String value) {
        if (value.isEmpty() || value == null || value.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else  if (value.equalsIgnoreCase("Does not matter")|| value.equalsIgnoreCase("not working")|| value.equalsIgnoreCase("no income")) return  value;
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
    }


    public void openPlanIntent(View view) {
//        common.showToast("Your profile viewed count has been not available,Please upgrade your membership.",llView);
        startActivity(new Intent(PreviewOthersProfileActivity.this, PlanListActivity.class));
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

            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("success").equals("success")) {

                    showContactPoup();
                } else {
                    viewContactIfPlanOudated(object.getString("errmessage"));
//                    common.showToast(object.getString("errmessage"),llView);
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
            startActivity(new Intent(PreviewOthersProfileActivity.this, PlanListActivity.class));
//            finish();
        });
        bottomSheetDialog.show();
    }

    public void viewContact(View view) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_contact);

        ImageView close = bottomSheetDialog.findViewById(R.id.close);
        Button btnUpgrade = bottomSheetDialog.findViewById(R.id.btnUpgrade);


        close.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
        });

        btnUpgrade.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(PreviewOthersProfileActivity.this, PlanListActivity.class));
//            finish();
        });
        bottomSheetDialog.show();
    }
}