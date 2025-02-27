package com.matriapp.mobile.custom;

import android.app.Activity;
import android.content.Context;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.JsonObject;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class PDFConverter {

    private ImageView imgProfile;
    private Common common;
    private LinearLayout llChild;
    private TextView tvMCall, tv_name, tv_phone, tv_detail, tvGothram, tvHoroscope, tvRaas, tvStar, tvMangalik, tvBloodGroup, tvBirthPlace, tvBirthday, tvDesignation, tvProfession, tvIncome, tvOccupation, tvEducation,
            tvDrink, tvSmoke, tvHealthInfo, tvAddInfo, tvSkin, tvChildren, tvFood, tvAppearnce, tvMtounge, tvCity, tvMarital, tvReligion, tvCaste, tvHeight, tvAge, tvLookinFor, tvAbout, tvSister, tvBrother, tvMotherOcc, tvMother, tvFatherOcc, tvFather, tvFamily, tvAddress, tvName, tvFStatus, tvFType;

    private Bitmap createBitmapFromView(
            Context context,
            View view,
            JsonObject dataObject,
            Activity activity,
            JSONObject data
    ) {
        llChild = view.findViewById(R.id.llChild);
        imgProfile = view.findViewById(R.id.imgProfile);
        tvAddInfo = view.findViewById(R.id.tvAddInfo);
        tvHealthInfo = view.findViewById(R.id.tvHealthInfo);
        tvSkin = view.findViewById(R.id.tvSkin);
        tvChildren = view.findViewById(R.id.tvChildren);
        tvMCall = view.findViewById(R.id.tvMCall);
        tv_name = view.findViewById(R.id.tv_name);
        tv_phone = view.findViewById(R.id.tv_phone);
        tv_detail = view.findViewById(R.id.tv_detail);
        tvGothram = view.findViewById(R.id.tvGothram);
        tvHoroscope = view.findViewById(R.id.tvHoroscope);
        tvAbout = view.findViewById(R.id.tvAbout);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvAge = view.findViewById(R.id.tvAge);
        tvAppearnce = view.findViewById(R.id.tvAppearnce);
        tvMarital = view.findViewById(R.id.tvMarital);
        tvMtounge = view.findViewById(R.id.tvMtounge);
        tvCity = view.findViewById(R.id.tvCity);
        tvFood = view.findViewById(R.id.tvFood);
        tvDrink = view.findViewById(R.id.tvDrink);
        tvSmoke = view.findViewById(R.id.tvSmoke);
        tvEducation = view.findViewById(R.id.tvEducation);
        tvProfession = view.findViewById(R.id.tvProfession);
        tvIncome = view.findViewById(R.id.tvIncome);
        tvOccupation = view.findViewById(R.id.tvOccupation);
        tvDesignation = view.findViewById(R.id.tvDesignation);
        tvLookinFor = view.findViewById(R.id.tvLookinFor);
        tvReligion = view.findViewById(R.id.tvReligion);
        tvCaste = view.findViewById(R.id.tvCaste);
        tvGothram = view.findViewById(R.id.tvGothram);
        tvHoroscope = view.findViewById(R.id.tvHoroscope);
        tvStar = view.findViewById(R.id.tvStar);
        tvRaas = view.findViewById(R.id.tvRaas);
        tvBloodGroup = view.findViewById(R.id.tvBloodGroup);
        tvBirthday = view.findViewById(R.id.tvBirthday);
        tvBirthPlace = view.findViewById(R.id.tvBirthPlace);
        tvMangalik = view.findViewById(R.id.tvMangalik);
        tvName = view.findViewById(R.id.tvName);
        tvAddress = view.findViewById(R.id.tvAddress);
        tvFStatus = view.findViewById(R.id.tvFStatus);
        tvFType = view.findViewById(R.id.tvFType);
        tvFather = view.findViewById(R.id.tvFather);
        tvFatherOcc = view.findViewById(R.id.tvFatherOcc);
        tvMother = view.findViewById(R.id.tvMother);
        tvMotherOcc = view.findViewById(R.id.tvMotherOcc);
        tvBrother = view.findViewById(R.id.tvBrother);
        tvSister = view.findViewById(R.id.tvSister);
        tvFamily = view.findViewById(R.id.tvFamily);

        common =new Common(activity);
        makeUserProfileList(dataObject, data, activity);
        return createBitmap(context, view, activity);
    }

    private void makeUserProfileList(JsonObject dataObject, JSONObject data, Activity activity) {
        try {
            Common common = new Common(activity);
            if (dataObject.has("photo1") && dataObject.get("photo1").getAsString() != null && !dataObject.get("photo1").getAsString().equals("")) {
//                common.setImageUsingPicasso(data.getString("photo1"), null, imgProfile, R.drawable.placeholder, 68);
                Picasso.get().load(data.getString("photo1")).into(imgProfile);
            }

            tv_name.setText(data.getString("username"));
            tv_phone.setText(data.getString("mobile").replace("+91-", ""));


            tvAbout.setText(checkFiledIsEmpty(dataObject.get("profile_text").getAsString()));
            tvAge.setText(checkFiledIsEmpty(dataObject.get("age").getAsString()));
            tvHeight.setText(checkFiledIsEmpty(dataObject.get("height_str").getAsString()));
            tvHealthInfo.setText(checkFiledIsEmpty(dataObject.get("physical_info").getAsString()));
            tvAppearnce.setText(checkFiledIsEmpty(dataObject.get("weight").getAsString()) + " Kgs, " + checkFiledIsEmpty(dataObject.get("bodytype").getAsString()));
            tvSkin.setText(checkFiledIsEmpty(dataObject.get("complexion").getAsString()));
            tvMarital.setText(checkMartialStatusFiled(dataObject));
            tvMtounge.setText(checkFiledIsEmpty(dataObject.get("mtongue_name").getAsString()));
            tvCity.setText(checkFiledIsEmpty(dataObject.get("city_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("state_name").getAsString()) + ", " + checkFiledIsEmpty(dataObject.get("country_name").getAsString()));
            tvFood.setText(checkFiledIsEmpty(dataObject.get("diet").getAsString()));
            tvEducation.setText(checkFiledIsEmpty(dataObject.get("education_name").getAsString()));
            tvProfession.setText(checkFiledIsEmpty(dataObject.get("employee_in").getAsString()));
            tvIncome.setText(checkIncomeIsEmpty(dataObject.get("income").getAsString()));
            tvOccupation.setText(checkFiledIsEmpty(dataObject.get("occupation_name").getAsString()));
            tvDesignation.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
            tvLookinFor.setText(checkFiledIsEmpty(dataObject.get("part_expect").getAsString()));
            tvAddInfo.setText(checkFiledIsEmpty(dataObject.get("professional_additional_info").getAsString()));
            tvReligion.setText(checkFiledIsEmpty(dataObject.get("religion_name").getAsString()));
            tvCaste.setText(checkFiledIsEmpty(dataObject.get("caste_name").getAsString()));
            tvGothram.setText(checkFiledIsEmpty(dataObject.get("gothra").getAsString()));
            tvHoroscope.setText(checkFiledIsEmpty(dataObject.get("horoscope").getAsString()));
            tvName.setText(checkFiledIsEmpty(dataObject.get("designation_name").getAsString()));
            tvFStatus.setText(checkFiledIsEmpty(dataObject.get("family_status").getAsString()));
            tvFType.setText(checkFiledIsEmpty(dataObject.get("family_type").getAsString()));
            tvFather.setText(checkFiledIsEmpty(dataObject.get("father_name").getAsString()));
            tvFatherOcc.setText(checkFiledIsEmpty(dataObject.get("father_occupation").getAsString()));
            tvMother.setText(checkFiledIsEmpty(dataObject.get("mother_name").getAsString()));
            tvMotherOcc.setText(checkFiledIsEmpty(dataObject.get("mother_occupation").getAsString()));
            tvBrother.setText(checkFiledIsEmptyNum(dataObject.get("no_of_brothers").getAsString(), " Brother(s)", dataObject.get("no_of_married_brother").getAsString()));
            tvSister.setText(checkFiledIsEmptyNum(dataObject.get("no_of_sisters").getAsString(), " Sister(s)", dataObject.get("no_of_married_sister").getAsString()));
            tvMangalik.setText(checkFiledIsEmpty(dataObject.get("manglik").getAsString()));
            tvStar.setText(checkFiledIsEmpty(dataObject.get("star_str").getAsString()));
            tvRaas.setText(checkFiledIsEmpty(dataObject.get("moonsign_str").getAsString()));
            tvBloodGroup.setText(checkFiledIsEmpty(dataObject.get("blood_group").getAsString()));
            tvBirthday.setText(checkBornFiledIsEmpty(updateLabel(dataObject.get("birthdate").getAsString()), "", dataObject.get("birthtime").getAsString(), " at "));
            tvBirthPlace.setText(checkFiledIsEmpty(dataObject.get("birthplace").getAsString()));
            tvAddress.setText(checkFiledIsEmpty(dataObject.get("address").getAsString().trim()));
            tvMCall.setText(checkFiledIsEmpty(dataObject.get("phone").getAsString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private Bitmap createBitmap(
            Context context,
            View view,
            Activity activity
    ) {
        // Get display metrics
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Measure and layout the view
        int widthSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.widthPixels, View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(displayMetrics.heightPixels, View.MeasureSpec.EXACTLY);
        view.measure(widthSpec, heightSpec);
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        // Create bitmap
        Bitmap bitmap = Bitmap.createBitmap(
                view.getMeasuredWidth(),
                view.getMeasuredHeight(), Bitmap.Config.ARGB_8888
        );

        // Render view onto bitmap
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }


    private void convertBitmapToPdf(Bitmap bitmap, Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        page.getCanvas().drawBitmap(bitmap, 0F, 0F, null);
        pdfDocument.finishPage(page);
        File directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), context.getPackageName());
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String fileName = currentDate + "_bio_data.pdf";
        File pdfFile = new File(directory, fileName);

        try {

            FileOutputStream outputStream = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(outputStream);
            pdfDocument.close();
            outputStream.close();
            common.showToast("Biodata downloaded at " + pdfFile.getPath(), llChild);
        } catch (IOException e) {
            e.printStackTrace();
            common.showToast( "failed to download Biodata", llChild);
        }
    }

    public void createPdf(
            Context context,
            JsonObject pdfDetails,
            Activity activity,
            JSONObject nameData
    ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_one_page_profile_pdf, null);

        Bitmap bitmap = createBitmapFromView(context, view, pdfDetails, activity, nameData);
        convertBitmapToPdf(bitmap, activity);
    }


    private String checkFiledIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>"));
        else return value.trim();
    }
    private String checkIncomeIsEmpty(String value) {
        if(value.isEmpty() || value== null || value.equals("")) return String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>"));
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

    private String checkMartialStatusFiled(JsonObject dataObject) {
        String mt= dataObject.get("marital_status").getAsString();
        String tc= dataObject.get("total_children").getAsString();
        String sc= dataObject.get("status_children").getAsString();


        if(mt.isEmpty() || mt== null || mt.equals("")) return String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>"));
        else {
            if (mt.equals("Never Married")) {
                llChild.setVisibility(View.GONE);
                return mt;
            } else {
                if(tc.isEmpty() || tc== null || tc.equals("")) tvChildren.setText(String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>")));
                else tvChildren.setText(tc + " ~ " + sc);
                return mt;
            }
        }


    }

    private String checkBornFiledIsEmpty(String value1, String tag1, String value2, String tag2) {
        if(value1.isEmpty() || value1== null || value1.equals("")) {
            if(value2.isEmpty() || value2== null || value2.equals("")) return String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>"));
            else  return "Born" + tag2+ value2;
        }
        else {
            if(value2.isEmpty() || value2== null || value2.equals("")) return  tag1 + value1 ;
            return tag1 + value1  + tag2+ value2;
        }
    }

    private String updateLabel(String dob) {
        Calendar myCalendar = Calendar.getInstance();
        if (dob.isEmpty() || dob == null || dob.equals("")) return String.valueOf(Html.fromHtml("<i><font color=\"#CDD1D2\">Not Mentioned</font></i>"));
        else {
            String[] arr = dob.split("-");

            myCalendar.set(Integer.parseInt(arr[0]), Integer.parseInt(arr[1]) - 1, Integer.parseInt(arr[2]));

            String myFormat = AppConstants.BIRTH_DATE_FORMAT_New; //In which you need put here
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
            return sdf.format(myCalendar.getTime()).toString();
        }


    }
}

