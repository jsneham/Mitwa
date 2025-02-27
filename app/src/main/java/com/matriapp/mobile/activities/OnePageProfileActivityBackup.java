package com.matriapp.mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.ApplicationData;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class OnePageProfileActivityBackup extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar,llContent;
    private JSONArray photo_arr;
    private int placeHolder;
    private ImageView imgProfile;
    private TextView tvMCall,tv_name,tv_detail,tvGothram,tvHoroscope,tvRaas, tvStar, tvMangalik,tvBloodGroup, tvBirthPlace,tvBirthday,tvDesignation,tvProfession,tvIncome,tvOccupation,tvEducation,
    tvDrink,tvSmoke, tvFood,tvAppearnce,tvMtounge,tvCity,tvMarital,tvReligion,tvHeight,tvLookinFor,tvAbout,tvSister,tvBrother,tvMotherOcc,tvMother,tvFatherOcc,tvFather,tvFamily,tvAddress,tvName,tvFStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_page_profile);
        session = new SessionManager(this);
        common = new Common(this);
        init();
        verifystoragepermissions(this);
        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.female;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.male;
        }
        getMyProfile();
    }

    private void init() {
        progressBar= findViewById(R.id.progressBar);
        imgProfile = findViewById(R.id.imgProfile);
        tvMCall = findViewById(R.id.tvMCall);
        tv_name = findViewById(R.id.tv_name);
        tv_detail = findViewById(R.id.tv_detail);
        tvGothram = findViewById(R.id.tvGothram);
        tvHoroscope = findViewById(R.id.tvHoroscope);
//        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvAbout = findViewById(R.id.tvAbout);
        llContent = findViewById(R.id.llContent);
        tvHeight = findViewById(R.id.tvHeight);
        tvAppearnce = findViewById(R.id.tvAppearnce);
        tvMarital = findViewById(R.id.tvMarital);
        tvMtounge = findViewById(R.id.tvMtounge);
        tvCity = findViewById(R.id.tvCity);
//        tvCitizenship = findViewById(R.id.tvCitizenship);
        tvFood = findViewById(R.id.tvFood);
        tvDrink = findViewById(R.id.tvDrink);
        tvSmoke = findViewById(R.id.tvSmoke);
        tvEducation = findViewById(R.id.tvEducation);
        tvProfession = findViewById(R.id.tvProfession);
        tvIncome = findViewById(R.id.tvIncome);
        tvOccupation = findViewById(R.id.tvOccupation);
        tvDesignation = findViewById(R.id.tvDesignation);
        tvLookinFor = findViewById(R.id.tvLookinFor);
//        tvHobbies = findViewById(R.id.tvHobbies);
//        tvLanuguage = findViewById(R.id.tvLanuguage);
        tvReligion = findViewById(R.id.tvReligion);
//        tvCaste = findViewById(R.id.tvCaste);
        tvGothram = findViewById(R.id.tvGothram);
        tvHoroscope = findViewById(R.id.tvHoroscope);
        tvStar = findViewById(R.id.tvStar);
        tvRaas = findViewById(R.id.tvRaas);
        tvBloodGroup = findViewById(R.id.tvBloodGroup);
//        tvBirthdayTime = findViewById(R.id.tvBirthdayTime);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvBirthPlace = findViewById(R.id.tvBirthPlace);
        tvMangalik = findViewById(R.id.tvMangalik);
//        tvContact = findViewById(R.id.tvContact);
//        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
//        tvCall = findViewById(R.id.tvCall);
        tvAddress = findViewById(R.id.tvAddress);
        tvFStatus = findViewById(R.id.tvFStatus);
        tvFather = findViewById(R.id.tvFather);
        tvFatherOcc = findViewById(R.id.tvFatherOcc);
        tvMother = findViewById(R.id.tvMother);
        tvMotherOcc = findViewById(R.id.tvMotherOcc);
        tvBrother = findViewById(R.id.tvBrother);
        tvSister = findViewById(R.id.tvSister);
        tvFamily = findViewById(R.id.tvFamily);
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

                        Glide.with(this)
                                .load(data.getString("photo1"))
                                .fitCenter()
                                .placeholder(placeHolder)
                                .into(imgProfile);
                    }

                    tv_name.setText(data.getString("username"));


                    String description= common.getOnePagerFullView(data.getString("age"),data.getString("height_str").replace("ft", "\'").replace("in", "\""),
                            data.getString("education_name"),data.getString("occupation_name"),
                            data.getString("religion_name"),data.getString("caste_name"),data.getString("city_name"),data.getString("state_name"));

                    tv_detail.setText(description);

                    makeUserProfileList(dataObject);


                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llContent);
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llContent);
            }
        },llContent);
    }



    private void makeUserProfileList(JsonObject dataObject) throws JSONException {



//        tvCreatedBy.setText("Profile created by " + checkFiledIsEmpty(dataObject.get("profileby").getAsString()));
        tvAbout.setText(checkFiledIsEmpty(dataObject.get("profile_text").getAsString()));
        tvHeight.setText(checkFiledIsEmpty(dataObject.get("age").getAsString() + ", " + dataObject.get("height_str").getAsString()));
        tvAppearnce.setText(checkFiledIsEmpty(dataObject.get("bodytype").getAsString() + ", " + dataObject.get("weight").getAsString()) + " Kgs, " + checkFiledIsEmpty(dataObject.get("complexion").getAsString()) + " Skin");
        tvMarital.setText(checkMartialStatusFiled(dataObject));
        tvMtounge.setText("Mother Tongue - " + checkFiledIsEmpty(dataObject.get("mtongue_name").getAsString()));
        tvCity.setText("Lives at " + checkFiledIsEmpty(dataObject.get("city_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("state_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
//        tvCitizenship.setText(checkFiledIsEmpty(dataObject.get("residence").getAsString()) + " of " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
        tvFood.setText(checkFiledIsEmpty(dataObject.get("diet").getAsString()));
        String drink = dataObject.get("drink").getAsString().equals("No") ? "Doesn't Drink" : dataObject.get("drink").getAsString().equals("Occasionally") ? "Drinks Occasionally" : "Drinks Regularly";
        tvDrink.setText(drink);
        String smoke = dataObject.get("smoke").getAsString().equals("No") ? "Doesn't Smoke" : dataObject.get("smoke").getAsString().equals("Occasionally") ? "Smokes Occasionally" : "Smokes Regularly";
        tvSmoke.setText(smoke);


        tvEducation.setText(checkFiledIsEmpty(dataObject.get("education_name").getAsString()));
        tvProfession.setText("Work Sector - " + checkFiledIsEmpty(dataObject.get("employee_in").getAsString()));
        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvOccupation.setText(checkFiledIsEmpty(dataObject.get("occupation_name").getAsString()));
//        tvDesignation.setText("Designated as " + checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
        tvLookinFor.setText(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()));
//        tvHobbies.setText(checkFiledIsEmpty(dataObject.get("hobby").getAsString()));
//        tvLanuguage.setText(checkFiledIsEmpty(dataObject.get("languages_known_str").getAsString()));

        tvReligion.setText(checkFiledIsEmpty(dataObject.get("religion_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("caste_name").getAsString()));
//                + "" + checkSubCastEmpty(dataObject.get("subcaste").getAsString()));
//        tvCaste.setText(checkFiledIsEmpty(dataObject.get("caste_name").getAsString()) + "" + checkSubCastEmpty(dataObject.get("subcaste").getAsString()));
        tvGothram.setText("Gothra(m) - " + checkFiledIsEmpty(dataObject.get("gothra").getAsString()));
        tvHoroscope.setText("Horoscope belief - " + checkFiledIsEmpty(dataObject.get("horoscope").getAsString()));
        tvName.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
//        tvCall.setText(checkFiledIsEmpty(dataObject.get("phone").getAsString()));
        tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString())+ " ~ "+ checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
        tvFather.setText("Father's Name - "+ checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
        tvFatherOcc.setText("His Occupation - "+ checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
        tvMother.setText("Mother's Name - "+ checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
        tvMotherOcc.setText("Her Occupation - "+ checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
        tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
        tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)",dataObject.get("no_of_married_sister").getAsString()));
//        tvFamily.setText(checkFiledIsEmpty(dataObject.get("family_details").getAsString()));

//        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvMangalik.setText("Is Manglik? - "+checkFiledIsEmpty(dataObject.get("manglik").getAsString()));
        tvStar.setText("Star - " +checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
        tvRaas.setText("Moonsign (Raas) - " +checkFiledIsEmpty(dataObject.get("moonsign_str").getAsString()));
        tvBloodGroup.setText("Blood Group - " +checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
        tvBirthday.setText(checkBornFiledIsEmpty(updateLabel(dataObject.get("birthdate").getAsString()), "Born on ",dataObject.get("birthtime").getAsString(), " at "));
        tvBirthPlace.setText("Born in " + dataObject.get("birthplace").getAsString() );
//        tvContact.setText(dataObject.get("mobile").getAsString());
//        tvEmail.setText(dataObject.get("email").getAsString());
        tvAddress.setText("Native - "+ checkFiledIsEmpty(dataObject.get("address").getAsString().trim()));
        tvMCall.setText(checkFiledIsEmpty(dataObject.get("phone").getAsString()));

//        email= dataObject.get("email").getAsString();
//        mobile= dataObject.get("mobile").getAsString();

//        screenshot(getWindow().getDecorView().getRootView(), dataObject.get("username").toString());
//        screenshot(getWindow().getDecorView().getRootView(), "member");

    }

    private final Calendar myCalendar = Calendar.getInstance();
    private String updateLabel(String dob) {
        if (dob.isEmpty() || dob == null || dob.equals("")) return "Not Mentioned";
        else {
            String[] arr = dob.split("-");

            myCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));

            String myFormat = AppConstants.BIRTH_DATE_FORMAT_New; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(myCalendar.getTime()).toString();
        }


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



    private String checkFiledIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return "Not Mentioned";
        else return value.trim();
    }
    private String checkIncomeIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return "Not Mentioned";
        else return "Earn " +  value + " annually";
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

    private String checkSubCastEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return "";
        else return " ~ " +value;
    }


    @RequiresApi(api = Build.VERSION_CODES.R)
    protected static File screenshot(View view, String filename) {
        Date date = new Date();

        // Here we are initialising the format of our image name
        CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
        try {
            // Initialising the directory of storage
            String dirpath = Environment.getExternalStorageState() + "";
            File file = new File(dirpath);
            if (!file.exists()) {
                boolean mkdir = file.mkdir();
            }

            // File name
            String path = dirpath + "/" + filename + "-" + format + ".jpeg";
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            File imageurl = new File(path);
            FileOutputStream outputStream = new FileOutputStream(imageurl);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            outputStream.flush();
            outputStream.close();
            return imageurl;

        } catch (FileNotFoundException io) {
            io.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void verifystoragepermissions(Activity activity) {

        int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // If storage permission is not given then request for External Storage Permission
        if (permissions != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe);
        }
    }

    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};



}