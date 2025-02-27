package com.matriapp.mobile.activities;

import static com.matriapp.mobile.application.MyApplication.getContext;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;

import com.matriapp.mobile.adapter.CustomProfileAdapter;
import com.matriapp.mobile.model.PartnerFields;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
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

public class PreviewProfileActivity extends AppCompatActivity {


    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private TabHost host;
    private TextView tv_basic_label_pref, tv_photo_count, tv_id, tv_name, tv_detail;
    private RecyclerView userPartnerRecyclerView;
    private int placeHolder, photoProtectPlaceHolder;
    private LinearLayout lay_basic_pref,main_content;
    private ImageView imgProfile, imgProfileTwo, imgProfileThree, imgProfileFour;
    private JSONArray photo_arr;
    private String other_id, other_matri_id;
    private boolean isProtected = false;
    private List<PartnerFields> userPartnerDataList = new ArrayList<>();

    private NestedScrollView scrollView;
    private TextView txtFocus;
    String name, phone, mobile,email;
    private String isProfileViewed = "";

    private TextView tvCreatedBy, tvAbout, tvHeight, tvAppearnce, tvMarital, tvMtounge, tvCity, tvCitizenship, tvFood, tvDrink, tvSmoke,tvHealth;
    private TextView tvEducation, tvProfession, tvIncome, tvIncomePremium, tvIncomeView, tvOccupation, tvDesignation, tvLookinFor, tvHobbies, tvLanuguage,
            tvReligion, tvCaste, tvGothram, tvHoroscope, tvStar, tvStarPremium, tvStarView, tvRaas, tvRaasPremium, tvRaasView,
            tvBloodGroup, tvBloodGroupPremium, tvBloodGroupView, tvBirthdayTime, tvBirthPlace, tvBirthPlacePremium, tvBirthPlaceView,
            tvMangalik, tvMangalikPremium, tvMangalikView, tvBirthday, tvBirthdayPremium, tvBirthdayView,
            tvContact, tvEmail, tvName, tvCall, tvAddress, tvAddressPremium, tvAddressView, tvFStatus, tvFather, tvFatherOcc, tvMother, tvMotherOcc, tvBrother, tvSister, tvFamily;
    private boolean isNotPremiumMember = false;
    private LinearLayout llML1, llML2, llML3, llML4;



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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusColor();
        session = new SessionManager(this);
        common = new Common(this);
        common.setGradient(getWindow());


        if (!session.isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        setContentView(R.layout.activity_preview_profile_own);

        ImageView img_more = findViewById(R.id.img_more);
        main_content = findViewById(R.id.main_content);
        tv_photo_count = findViewById(R.id.tv_photo_count);
        imgProfile = findViewById(R.id.imgProfile);
        imgProfileTwo = findViewById(R.id.imgProfileTwo);
        imgProfileThree = findViewById(R.id.imgProfileThree);
        imgProfileFour = findViewById(R.id.imgProfileFour);
        userPartnerRecyclerView = findViewById(R.id.userPartnerRecyclerView);

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            photoProtectPlaceHolder = R.drawable.photopassword_female;
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            photoProtectPlaceHolder = R.drawable.photopassword_male;
            placeHolder = R.drawable.male;
        }
        imgProfile.setImageResource(placeHolder);
        imgProfileTwo.setImageResource(placeHolder);
        imgProfileThree.setImageResource(placeHolder);
        imgProfileFour.setImageResource(placeHolder);
        tv_id = findViewById(R.id.tv_id);
        tv_name = findViewById(R.id.tv_name);
        tv_detail = findViewById(R.id.tv_detail);


        imgProfile.setOnClickListener(view -> {

                if (photo_arr.length() != 0) {
                    Intent intent = new Intent(getApplicationContext(), GallaryNewActivity.class);
                    intent.putExtra("imagePosition", 0);
                    intent.putExtra("imageArray", photo_arr.toString());
                    intent.putExtra("tag", "self");
                    startActivity(intent);

            }
        });
        initTextView();


        getMyProfile();
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



    private void getMyProfile() {
        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                AppDebugLog.print("resp : " + response);
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    String myProfileStarStr = data.getString("star_str");
                    String myProfileMoonSignStr = data.getString("moonsign_str");
                    ApplicationData.myProfileStarStr = myProfileStarStr;
                    ApplicationData.myProfileMoonSignStr = myProfileMoonSignStr;

                    JSONArray fileds = data.getJSONArray("fileds");
                    photo_arr = fileds.getJSONObject(fileds.length() - 1).getJSONArray("value");

                    Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    JsonObject dataObject = jsonObject.getAsJsonObject("data");

                    if (dataObject.has("photo1") && dataObject.get("photo1").getAsString() != null && !dataObject.get("photo1").getAsString().equals("")) {
//                        Picasso.get().load(data.getString("photo1"))
//                                .placeholder(R.drawable.placeholder)
//                                .error(R.drawable.placeholder)
//                                .centerInside()
//                                .into(imgProfile);

                        Glide.with(this)
                                .load(data.getString("photo1"))
                                .fitCenter()
                                .placeholder(placeHolder)
                                .into(imgProfile);
                    }

                    tv_id.setText(data.getString("matri_id"));
                    tv_name.setText(data.getString("username"));

                    String description= common.getDetailsToFullView(data.getString("age"),data.getString("height_str").replace("ft", "\'").replace("in", "\""),
                            data.getString("education_name"),data.getString("designation_name"),data.getString("occupation_name"),
                            data.getString("religion_name"),data.getString("caste_name"),data.getString("city_name"),data.getString("state_name"));

//                    String description = checkFiledIsEmptyForDt(data.getString("age"))+checkFiledIsEmptyForDt(data.getString("height").replace("ft", "\'").replace("in", "\"")) +" . "
//                            +checkFiledIsEmptyForDt(data.getString("education_name")) +checkFiledIsEmptyForDt(data.getString("designation_name"))+checkFiledIsEmptyForDt(data.getString("occupation_name")).replace(",", ".") + "\n"+
//                            checkFiledIsEmptyForDt(data.getString("religion_name")) + checkFiledIsEmptyForDt(data.getString("caste_name")).replace(", ", ". ")+ checkFiledIsEmptyForDt(data.getString("city_name"))+checkFiledIsEmptyForDt(data.getString("state_name")).replace(",","");

                    tv_detail.setText(description);

                    makeUserProfileList(dataObject);


//                    JSONArray partners_field = data.getJSONArray("partners_field");
                    makePartnerList( data);


                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),main_content);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),main_content);
            }
        },main_content);
    }


    private void makePartnerList(JSONObject data) {
        try {

            JSONArray field = data.getJSONArray("partners_field");
            for(int k =0; k<field.length(); k++){
                JSONArray array=  field.getJSONObject(k).getJSONArray("value");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String title = obj.getString("title");
//                String type = obj.getString("type");
                    String value = obj.getString("value");
                    if(!title.equals("Designation"))
                     userPartnerDataList.add(new PartnerFields(title, "", value));
                }
            }

            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            userPartnerRecyclerView.setLayoutManager(mLayoutManager);
            CustomProfileAdapter adapter = new CustomProfileAdapter(userPartnerDataList, getContext());
            userPartnerRecyclerView.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


        private String checkFiledIsEmptyForDt(String value) {
        if (value.isEmpty() || value == null || value.equals("")) return "";
        else return value + ", ";
    }

    private void makeUserProfileList(JsonObject dataObject) throws JSONException {





        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, (int) getResources().getDimension(R.dimen.dp5), (int) getResources().getDimension(R.dimen.dp14), 0);


        if(checkFiledIsEmpty(dataObject.get("profile_text").getAsString()).length()>40) {

            findViewById(R.id.ivML1).setLayoutParams(params);
            llML1.setGravity(Gravity.TOP);
            tvAbout.setGravity(Gravity.TOP);
        }
        else{
            llML1.setGravity(Gravity.CENTER_VERTICAL);
            tvAbout.setGravity(Gravity.CENTER_VERTICAL);
        }

        if(checkFiledIsEmpty(dataObject.get("hobby").getAsString()).length()>40) {
            findViewById(R.id.ivML2).setLayoutParams(params);
            llML2.setGravity(Gravity.TOP);
            tvHobbies.setGravity(Gravity.TOP);
        }
        else{

            llML2.setGravity(Gravity.CENTER_VERTICAL);
            tvHobbies.setGravity(Gravity.CENTER_VERTICAL);
        }


        if(checkFiledIsEmpty(dataObject.get("family_details").getAsString()).length()>40) {
            findViewById(R.id.ivML3).setLayoutParams(params);
            llML3.setGravity(Gravity.TOP);
            tvFamily.setGravity(Gravity.TOP);
        }
        else{
            llML3.setGravity(Gravity.CENTER_VERTICAL);
            tvFamily.setGravity(Gravity.CENTER_VERTICAL);
        }

        if(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()).length()>40) {
            findViewById(R.id.ivML4).setLayoutParams(params);
            llML4.setGravity(Gravity.TOP);
            tvLookinFor.setGravity(Gravity.TOP);
        }
        else{
            llML4.setGravity(Gravity.CENTER_VERTICAL);
            tvLookinFor.setGravity(Gravity.CENTER_VERTICAL);
        }


        tvCreatedBy.setText("Profile created by " + checkFiledIsEmpty(dataObject.get("profileby").getAsString()));
        tvAbout.setText(checkFiledIsEmpty(dataObject.get("profile_text").getAsString()));
        tvHeight.setText(checkFiledIsEmpty(dataObject.get("age").getAsString() + ", " + dataObject.get("height_str").getAsString()));
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
        tvCall.setText(checkFiledIsEmpty(dataObject.get("time_to_call").getAsString()));
        phone = checkFiledIsEmpty(dataObject.get("phone").getAsString());
//       / if(phone.equals("Not Mentioned")) findViewById(R.id.llcall).setVisibility(View.GONE);
//        else findViewById(R.id.llcall).setVisibility(View.VISIBLE);
        tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString())+ " ~ "+ checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
        tvFather.setText("Father's Name - "+ checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
        tvFatherOcc.setText("His Occupation - "+ checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
        tvMother.setText("Mother's Name - "+ checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
        tvMotherOcc.setText("Her Occupation - "+ checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
        tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
        tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)",dataObject.get("no_of_married_sister").getAsString()));
        tvFamily.setText(checkFiledIsEmpty(dataObject.get("family_details").getAsString()));

        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvMangalik.setText("Is Manglik? - "+checkFiledIsEmpty(dataObject.get("manglik").getAsString()));
        tvStar.setText("Star - " +checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
        tvRaas.setText("Moonsign (Raas) - " +checkFiledIsEmpty(dataObject.get("moonsign_str").getAsString()));
        tvBloodGroup.setText("Blood Group - " +checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
        tvBirthPlace.setText(checkBornFiledIsEmpty(dataObject.get("birthplace").getAsString(), "Born in ",dataObject.get("birthtime").getAsString(), " at "));
        tvBirthday.setText("Born on "+ updateLabel(dataObject.get("birthdate").getAsString()));
        tvContact.setText(dataObject.get("mobile").getAsString());
        tvEmail.setText(dataObject.get("email").getAsString());
        tvAddress.setText("Ancestral Origin - "+ checkFiledIsEmpty(dataObject.get("address").getAsString()));

        email= dataObject.get("email").getAsString();
        mobile= dataObject.get("mobile").getAsString();


    }


    private String checkBornFiledIsEmpty(String value1, String tag1, String value2, String tag2) {
        if(value1.isEmpty() || value1== null || value1.equals("")) {
            if(value2.isEmpty() || value2== null || value2.equals("")) return "Not Mentioned";
            else  return "Born" + tag2+ value2;
        }
        else {
            if(value2.isEmpty() || value2== null || value2.equals("")) return  tag1 + value1 ;
            return tag1 + value1  + tag2+ value2;
        }
    }
    private String checkSubCastEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return "";
        else return " ~ " +value;
    }

    private String checkMartialStatusFiled(JsonObject dataObject) {
        String mt= dataObject.get("marital_status").getAsString();
        String tc= dataObject.get("total_children").getAsString();
        String sc= dataObject.get("status_children").getAsString();
        if(mt.isEmpty() || mt== null || mt.equals("")) return "Not Mentioned";
        else {
            if (mt.equals("Never Married")) {
                return mt;
            } else {
                return mt + ", " + tc + " ~ " + sc;
            }
        }
        //dataObject.get("marital_status").getAsString()+", "+ checkFiledIsEmpty(dataObject.get("total_children").getAsString())+ " ~ "+ checkFiledIsEmpty(dataObject.get("status_children").getAsString()


    }
    private final Calendar myCalendar = Calendar.getInstance();
    private String updateLabel(String dob) {
        if (dob.isEmpty() || dob == null || dob.equals("")) return "Not Mentioned";
        else {
            String[] arr = dob.split("-");

            myCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));

            String myFormat = AppConstants.BIRTH_DATE_FORMAT; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(myCalendar.getTime()).toString();
        }


    }

    private String checkFiledIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return "Not Mentioned";
        else return value;
    }
    private String checkIncomeIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("") ) return "Not Mentioned";
        else  if (value.equalsIgnoreCase("Does not matter")|| value.equalsIgnoreCase("not working")|| value.equalsIgnoreCase("no income")) return  value;
        else return value + " annually";
    }
    private String checkFiledIsEmptyNum(String value, String Tag, String married) {
        if(value.isEmpty() || value== null || value.equals("")) return "0" + Tag;
        else {
            if(married.isEmpty()|| married==null || married.equals("")){
                return value+ Tag;
            }
            else  return value+ Tag + ", " +married;
        }


    }

    public void backClick(View view) {
        finish();
    }




}