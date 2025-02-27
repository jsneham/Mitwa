package com.matriapp.mobile.activities;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.custom.PDFConverter;
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

public class OnePageProfileActivity extends AppCompatActivity {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar,llContent;
    private JSONArray photo_arr;
    private int placeHolder;
    private ImageView imgProfile;
    private TextView tvMCall,tv_name,tv_phone,tv_detail,tvGothram,tvHoroscope,tvRaas, tvStar, tvMangalik,tvBloodGroup, tvBirthPlace,tvBirthday,tvDesignation,tvProfession,tvIncome,tvOccupation,tvEducation,
            tvDrink,tvSmoke,tvHealthInfo,tvAddInfo, tvSkin,tvChildren,tvFood,tvAppearnce,tvMtounge,tvCity,tvMarital,tvReligion,tvCaste,tvHeight,tvAge,tvLookinFor,tvAbout,tvSister,tvBrother,tvMotherOcc,tvMother,tvFatherOcc,tvFather,tvFamily,tvAddress,tvName,tvFStatus, tvFType;

    JsonObject dataObject;
    JSONObject data;
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
        llContent= findViewById(R.id.llContent);
        progressBar= findViewById(R.id.progressBar);
        imgProfile = findViewById(R.id.imgProfile);
        tvAddInfo = findViewById(R.id.tvAddInfo);
        tvHealthInfo = findViewById(R.id.tvHealthInfo);
        tvSkin = findViewById(R.id.tvSkin);
        tvChildren = findViewById(R.id.tvChildren);
        tvMCall = findViewById(R.id.tvMCall);
        tv_name = findViewById(R.id.tv_name);
        tv_phone = findViewById(R.id.tv_phone);
        tv_detail = findViewById(R.id.tv_detail);
        tvGothram = findViewById(R.id.tvGothram);
        tvHoroscope = findViewById(R.id.tvHoroscope);
//        tvCreatedBy = findViewById(R.id.tvCreatedBy);
        tvAbout = findViewById(R.id.tvAbout);
        tvHeight = findViewById(R.id.tvHeight);
        tvAge = findViewById(R.id.tvAge);
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
        tvCaste = findViewById(R.id.tvCaste);
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
        tvFType = findViewById(R.id.tvFType);
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
                    data = object.getJSONObject("data");

                    String myProfileStarStr = data.getString("star_str");
                    String myProfileMoonSignStr = data.getString("moonsign_str");
                    ApplicationData.myProfileStarStr = myProfileStarStr;
                    ApplicationData.myProfileMoonSignStr = myProfileMoonSignStr;

                    JSONArray fileds = data.getJSONArray("fileds");
                    photo_arr = fileds.getJSONObject(fileds.length() - 1).getJSONArray("value");

                    Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy hh:mm:ss a").create();
                    JsonObject jsonObject = new JsonParser().parse(response).getAsJsonObject();
                    dataObject = jsonObject.getAsJsonObject("data");

                    if (dataObject.has("photo1") && dataObject.get("photo1").getAsString() != null && !dataObject.get("photo1").getAsString().equals("")) {
//                        Picasso.get().load(data.getString("photo1")).placeholder(placeHolder).error(placeHolder).into(imgProfile);
//                        Glide.with(this)
//                                .load(data.getString("photo1"))
//                                .fitCenter()
//                                .placeholder(placeHolder)
//                                .into(imgProfile);
                        common.setImageUsingPicasso(data.getString("photo1"), null, imgProfile,placeHolder,68);

                    }

                    tv_name.setText(data.getString("username"));
                    tv_phone.setText(data.getString("mobile").replace("+91-", ""));


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
        tvAge.setText(checkFiledIsEmpty(dataObject.get("age").getAsString() ));
        tvHeight.setText(checkFiledIsEmpty(dataObject.get("height_str").getAsString()));
        tvHealthInfo.setText(checkFiledIsEmpty(dataObject.get("physical_info").getAsString()));
        tvAppearnce.setText(checkFiledIsEmpty(dataObject.get("weight").getAsString()) + " Kgs, " + checkFiledIsEmpty(dataObject.get("bodytype").getAsString()));
        tvSkin.setText(checkFiledIsEmpty(dataObject.get("complexion").getAsString()));
        tvMarital.setText(checkMartialStatusFiled(dataObject));
        tvMtounge.setText(checkFiledIsEmpty(dataObject.get("mtongue_name").getAsString()));
        tvCity.setText(checkFiledIsEmpty(dataObject.get("city_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("state_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
//        tvCitizenship.setText(checkFiledIsEmpty(dataObject.get("residence").getAsString()) + " of " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
        tvFood.setText(checkFiledIsEmpty(dataObject.get("diet").getAsString()));
//        String drink = dataObject.get("drink").getAsString().equals("No") ? "Doesn't Drink" : dataObject.get("drink").getAsString().equals("Occasionally") ? "Drinks Occasionally" : "Drinks Regularly";
//        tvDrink.setText(drink);
//        String smoke = dataObject.get("smoke").getAsString().equals("No") ? "Doesn't Smoke" : dataObject.get("smoke").getAsString().equals("Occasionally") ? "Smokes Occasionally" : "Smokes Regularly";
//        tvSmoke.setText(smoke);


        tvEducation.setText(checkFiledIsEmpty(dataObject.get("education_name").getAsString()));
        tvProfession.setText(checkFiledIsEmpty(dataObject.get("employee_in").getAsString()));
        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvOccupation.setText(checkFiledIsEmpty(dataObject.get("occupation_name").getAsString()));
        tvDesignation.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
        tvLookinFor.setText(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()));
        tvAddInfo.setText(checkFiledIsEmpty(dataObject.get("professional_additional_info").getAsString()));
//        tvHobbies.setText(checkFiledIsEmpty(dataObject.get("hobby").getAsString()));
//        tvLanuguage.setText(checkFiledIsEmpty(dataObject.get("languages_known_str").getAsString()));

        tvReligion.setText(checkFiledIsEmpty(dataObject.get("religion_name").getAsString()));
//                + "" + checkSubCastEmpty(dataObject.get("subcaste").getAsString()));
        tvCaste.setText(checkFiledIsEmpty(dataObject.get("caste_name").getAsString()));
        tvGothram.setText(checkFiledIsEmpty(dataObject.get("gothra").getAsString()));
        tvHoroscope.setText(checkFiledIsEmpty(dataObject.get("horoscope").getAsString()));
        tvName.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
//        tvCall.setText(checkFiledIsEmpty(dataObject.get("phone").getAsString()));
        tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString()));
        tvFType.setText(checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
        tvFather.setText(checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
        tvFatherOcc.setText(checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
        tvMother.setText(checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
        tvMotherOcc.setText(checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
        tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
        tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)",dataObject.get("no_of_married_sister").getAsString()));
//        tvFamily.setText(checkFiledIsEmpty(dataObject.get("family_details").getAsString()));

//        tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
        tvMangalik.setText(checkFiledIsEmpty(dataObject.get("manglik").getAsString()));
        tvStar.setText(checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
        tvRaas.setText(checkFiledIsEmpty(dataObject.get("moonsign_str").getAsString()));
        tvBloodGroup.setText(checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
        tvBirthday.setText(checkBornFiledIsEmpty(updateLabel(dataObject.get("birthdate").getAsString()), "",dataObject.get("birthtime").getAsString(), " at "));
        tvBirthPlace.setText(checkFiledIsEmpty(dataObject.get("birthplace").getAsString()));
//        tvContact.setText(dataObject.get("mobile").getAsString());
//        tvEmail.setText(dataObject.get("email").getAsString());
        tvAddress.setText(checkFiledIsEmpty(dataObject.get("address").getAsString().trim()));
        tvMCall.setText(checkFiledIsEmpty(dataObject.get("phone").getAsString()));

//        email= dataObject.get("email").getAsString();
//        mobile= dataObject.get("mobile").getAsString();

//        screenshot(getWindow().getDecorView().getRootView(), dataObject.get("username").toString());
//        screenshot(getWindow().getDecorView().getRootView(), "member");

    }

    private final Calendar myCalendar = Calendar.getInstance();
    private String updateLabel(String dob) {
        if (dob.isEmpty() || dob == null || dob.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
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
            if(value2.isEmpty() || value2== null || value2.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
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


        if(mt.isEmpty() || mt== null || mt.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else {
            if (mt.equals("Never Married")) {
                findViewById(R.id.llChild).setVisibility(View.GONE);
                return mt;
            } else {
                if(tc.isEmpty() || tc== null || tc.equals("")) tvChildren.setText(String.valueOf(Html.fromHtml(getString(R.string.not_mentioned))));
                else tvChildren.setText(tc + " ~ " + sc);
                return mt;
            }
        }

        //dataObject.get("marital_status").getAsString()+", "+ checkFiledIsEmpty(dataObject.get("total_children").getAsString())+ " ~ "+ checkFiledIsEmpty(dataObject.get("status_children").getAsString()


    }



    private String checkFiledIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else return value.trim();
    }
    private String checkIncomeIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return String.valueOf(Html.fromHtml(getString(R.string.not_mentioned)));
        else return value;
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

    private final int C_PERMISSION_REQUEST_CODE = 122;
    ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
        generatePdf();
    });

    public void downloadPdf(View view) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
        ) {

            generatePdf();
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestPermissions.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO});
            }
            else {
                requestPermissions.launch(new String[]{WRITE_EXTERNAL_STORAGE});
            }
//            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, C_PERMISSION_REQUEST_CODE);

        }



    }

    private void generatePdf() {
        Snackbar.make(
                findViewById(R.id.llContent),
                "BioData downloaded at /storage/emulated/0/Download/ com.matri",
                Snackbar.LENGTH_SHORT
        ).show();

        PDFConverter pdfConverter = new PDFConverter();
        pdfConverter.createPdf(this, dataObject, this, data);
    }
}