package com.matriapp.mobile.activities;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.matriapp.mobile.application.MyApplication.getContext;
import static com.matriapp.mobile.utility.AppConstants.MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.matriapp.mobile.adapter.CreatedByAdapter;
import com.matriapp.mobile.adapter.CustomHeightAdapter;
import com.matriapp.mobile.retrofit.ApiRequestResponse;
import com.matriapp.mobile.multispinnerfilter.KeyPairBoolData;
import com.matriapp.mobile.multispinnerfilter.MultiSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SingleSpinnerSearch;
import com.matriapp.mobile.multispinnerfilter.SpinnerListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matriapp.mobile.R;
import com.matriapp.mobile.application.MyApplication;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.retrofit.AppApiService;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.retrofit.RetrofitClient;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import pl.aprilapps.easyphotopicker.MediaFile;
import pl.aprilapps.easyphotopicker.MediaSource;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RegisterFirstActivity extends AppCompatActivity implements CreatedByAdapter.ListItemClickListener, View.OnClickListener, ProgressRequestBody.UploadCallbacks, CustomHeightAdapter.ListItemClickListener, SpinnerListener, ApiRequestResponse.AppApiRequestCallbacks {

    private Common common;
    private SessionManager session;
    private RelativeLayout progressBar;
    private ProgressBar pbState;
    private EditText editText;
    private LinearLayout llProfileCreate, lay_first, lay_photos, lay_state, llLastStep, llFamilyStep, layoutBottomSheet;
    private RelativeLayout lay_second, llAbout, llHobby, llFamilyAbout, lay_welcome_after_basic_regis;
    private TextView tvStateTitle, tvStateHeader;
    private EditText et_birth_place, et_birth_time, et_father_ocu, et_mother_ocu, et_mother_name, et_father_name, et_phone, et_time_call, et_address;


    private RecyclerView rvCreatedBy;
    private BottomSheetBehavior sheetBehavior;

    private CountryCodePicker spin_code;
    private Button btn_male, btn_female, btn_login, btn_first_submit, btn_choose;

    private EditText et_f_name, et_fabout, et_l_name, et_email, et_password, et_mobile, et_sub_caste, et_gothra, et_hoby, et_about;
    private static EditText et_dob;
    private DatePickerDialog.OnDateSetListener date;
    private TextInputLayout pass_input;
    private TextView lblTerms, tv_cancel, tv_gallary, tv_camera;
//    private ScrollView ragi_scroll;

    private static final String CREATED_BY = "CREATED_BY";
    private static final String BASIC_DETAIL = "BASIC_DETAIL";
    private static final String STEP_Photos = "photos";

    private static final String STEP_H = "height";
    private static final String STEP_S = "m_status";
    private static final String STEP_R = "religion";
    private static final String STEP_C = "caste";
    private static final String STEP_MT = "language";
    private static final String STEP_Country = "country";
    private static final String STEP_State = "state";
    private static final String STEP_City = "city";
    private static final String STEP_EDU = "edu";
    private static final String STEP_OCC = "occ";
    private static final String STEP_EMP = "emp";
    private static final String STEP_DESG = "desg";
    private static final String STEP_LIFE = "lifestyle";
    private static final String STEP_WEL = "STEP_WEL";
    private static final String STEP_MoonSign = "STEP_MoonSign";
    private static final String STEP_INCOME = "STEP_INCOME";
    private static final String STEP_Weight = "STEP_Weight";
    private static final String STEP_EatingH = "STEP_EatingH";
    private static final String STEP_Smoking = "STEP_Smoking";
    private static final String STEP_Drinking = "STEP_Drinking";
    private static final String STEP_BodyType = "STEP_BodyType";
    private static final String STEP_Lang = "STEP_Lang";
    private static final String STEP_SkinTone = "STEP_SkinTone";
    private static final String STEP_Mangalik = "STEP_Mangalik";
    private static final String STEP_Star = "STEP_Star";
    private static final String STEP_Horoscope = "STEP_Horoscope";
    private static final String STEP_Editable = "STEP_Editable";
    private static final String STEP_FT = "STEP_FT";
    private static final String STEP_FS = "STEP_FS";
    private static final String STEP_Last = "STEP_Last";
    private static final String STEP_ABOUT = "STEP_ABOUT";
    private static final String STEP_Hobby = "STEP_Hobby";
    private static final String STEP_FAMILYDETAILS = "STEP_FAMILYDETAILS";
    private static final String STEP_NOB = "STEP_NOB";
    private static final String STEP_NOBM = "STEP_NOBM";
    private static final String STEP_NOS = "STEP_NOS";
    private static final String STEP_NOSM = "STEP_NOSM";
    private static final String STEP_Login = "STEP_Login";
    private static final String STEP_Blood = "STEP_Blood";
    private static final String STEP_FAMILYABOUT = "STEP_FAMILYABOUT";

    private ArrayList<KeyPairBoolData> casteArrayList = new ArrayList<>();
    private ArrayList<KeyPairBoolData> cityArrayList = new ArrayList<>();
    private ArrayList<KeyPairBoolData> stateArrayList = new ArrayList<>();

    private ImageView img_one, img_two, img_three, img_four;
    private String photo1, photo2, photo3, photo4, photo5, photo6;
    private int image_id;
    private boolean imag_one_seleted = false;

    private Button btn_photo_submit;
    private CheckBox checkBox;
    private LinearLayout swipe;


    final Calendar myCalendar = Calendar.getInstance();
    private String mCurrentPhotoPath, page_name = BASIC_DETAIL, religion_id = "", caste_id = "", tongue_id = "", gender = "", country_code = "+91",
            ragister_id = "", reference_id = "", lang_id = "", country_id = "", state_id = "", city_id = "", mari_id = "", total_child_id = "", status_child_id = "",
            edu_id = "", emp_id = "", income_id = "", occu_id = "", desig_id = "", resi_id = "", hite_id = "", weight_id = "", eat_id = "", smok_id = "", drink_id = "",
            body_id = "", skin_id = "", manglik_id = "", blood_id = "", star_id = "", horo_id = "", moon_id = "", org_path, crop_path, fb_id = "", created_id, family_status_id, family_type_id, no_bro_id, no_mari_bro_id, no_sis_id, no_mari_sis_id;
    ;
    private int age = 0;

    private RecyclerView rvHeight, rvMStatus, rvReligion, rvCaste, rvMT, rvlanguage, rvLifestyle, rvDesignation,
            rvEmployeedIn, rvOccupation, rvEducation, rvCity, rvState, rvCountry, rvIncome, rvMoonSign, rvWeight, rvEatingH,
            rvSmoking, rvDrinking, rvBodyType, rvSkinTone, rvMangalik, rvStar, rvHoroscope, rvPhysicalStatus, rvFamilyStatus, rvFamilyType, rvNoOfBrother, rvNoOfBrotherMarried, rvNoOfSister, rvNoOfSisterMarried, rvBlood;
    private SingleSpinnerSearch spin_reference, spin_designation, spin_residence;
    private MultiSpinnerSearch spin_lang;
    private final Calendar mcurrentTime = Calendar.getInstance();
    private SimpleDateFormat mFormat = null;
    private HashMap<String, String> image_map;
    private Uri resultUri;
    boolean isImageSelect = false;
    boolean isCheckSelect = false;
    TextView tvSkip;

    private EasyImage easyImage = null;

    private final int CROP_PIC = 3;
    private final int PERMISSION_REQUEST_CODE = 122;
    private File compressedFile = null;
    private File originalFile = null;
    private String originalFilePath = "", cropFilePath = "";
    private Uri cropUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_base);
        initView();
    }

    private void initView() {
        common = new Common(this);
        session = new SessionManager(this);
        image_map = new HashMap<>();
        swipe = findViewById(R.id.swipe);
        progressBar = findViewById(R.id.progressBar);
//        ragi_scroll = findViewById(R.id.ragi_scroll);
        pbState = findViewById(R.id.pbState);
        setUpEasyImage();
        getList();

        llProfileCreate = findViewById(R.id.llProfileCreate);
        lay_welcome_after_basic_regis = findViewById(R.id.lay_welcome_after_basic_regis);
        lay_photos = findViewById(R.id.lay_photos);
        rvCreatedBy = findViewById(R.id.rvCreatedBy);


        tvSkip = findViewById(R.id.skip);
        tvSkip.setVisibility(View.GONE);
        lay_first = findViewById(R.id.lay_first);
        et_phone = findViewById(R.id.et_phone);
        et_time_call = findViewById(R.id.et_time_call);
        et_time_call.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        llLastStep = findViewById(R.id.llLastStep);
        llFamilyStep = findViewById(R.id.llFamilyStep);
        et_f_name = findViewById(R.id.et_f_name);
        et_f_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_l_name = findViewById(R.id.et_l_name);
        et_l_name.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et_email = findViewById(R.id.et_email);
        et_about = findViewById(R.id.et_about);
        et_hoby = findViewById(R.id.et_hoby);
        et_fabout = findViewById(R.id.et_fabout);
        et_password = findViewById(R.id.et_password);
        pass_input = findViewById(R.id.pass_input);
        btn_first_submit = findViewById(R.id.btn_first_submit);
        btn_login = findViewById(R.id.btn_login);
        btn_male = findViewById(R.id.btn_male);
        btn_female = findViewById(R.id.btn_female);
        et_mobile = findViewById(R.id.et_mobile);


        spin_reference = findViewById(R.id.spin_reference);
        setupSearchDropDown(spin_reference, "MatchMaker Name", "reference");
        spin_reference.setSelection("Reference By");

        spin_lang = findViewById(R.id.spin_lang);
        setupSearchDropDown(spin_lang, "Spoken Language(s) ", "mothertongue_list");
        spin_lang.setSelection("Language Known");

        spin_designation = findViewById(R.id.spin_designation);
        setupSearchDropDown(spin_designation, "Designation at work", "designation_list");
        spin_designation.setSelection("Designation");

        spin_residence = findViewById(R.id.spin_residence);
        setupSearchDropDown(spin_residence, "Resident Type", "residence");
        spin_residence.setSelection("Residence");


        et_dob = findViewById(R.id.et_dob);
        et_dob.setOnClickListener(v -> {
            selectDate();
        });
//        et_dob.setOnClickListener(v -> {
//            //For above 18 years date
//            Calendar maxDateCalendar = Calendar.getInstance();
//            maxDateCalendar.add(Calendar.YEAR, -18);
//            maxDateCalendar.add(Calendar.DATE, -1);
//
//            //For below 18 years date
//            Calendar minDateCalendar = Calendar.getInstance();
//            minDateCalendar.add(Calendar.YEAR, -90);
//            minDateCalendar.add(Calendar.DATE, 1);
//
//            DatePickerDialog dialog = new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
//            dialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
//            //set max date in date picker
//            dialog.getDatePicker().setMaxDate(maxDateCalendar.getTime().getTime());
//            dialog.getDatePicker().setMinDate(minDateCalendar.getTime().getTime());
//            dialog.show();
//        });

        et_dob.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!et_dob.getText().toString().equals("")) {
                    et_dob.setError(null);
                }
            }
        });
//        btn_choose = findViewById(R.id.btn_choose);
//        btn_choose.setOnClickListener(this);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        btn_first_submit.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_male.setOnClickListener(this);
        btn_female.setOnClickListener(this);
        common.setDrawableLeftButton(R.drawable.male_inactive, btn_male);
//        btn_male.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        common.setDrawableLeftButton(R.drawable.female_inactive, btn_female);
        // common.setDrawableLeftButton(R.drawable.edit_white, btn_choose);

        date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        sheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }

        });
        img_one = findViewById(R.id.img_one);
        img_two = findViewById(R.id.img_two);
        img_three = findViewById(R.id.img_three);
        img_four = findViewById(R.id.img_four);
        btn_photo_submit = findViewById(R.id.btn_photo_submit);
        checkBox = findViewById(R.id.checkBox);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_gallary = findViewById(R.id.tv_gallary);
        tv_camera = findViewById(R.id.tv_camera);

        img_one.setOnClickListener(this);
        img_two.setOnClickListener(this);
        img_three.setOnClickListener(this);
        img_four.setOnClickListener(this);

        btn_photo_submit.setOnClickListener(this);
        tv_gallary.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);


        spin_code = findViewById(R.id.spin_code);
        spin_code.setOnCountryChangeListener(country -> {
            country_code = country.getPhoneCode();
        });

        lblTerms = findViewById(R.id.lblTerms);
        singleTextView(lblTerms);


        llAbout = findViewById(R.id.llAbout);
        llHobby = findViewById(R.id.llHobby);
        llFamilyAbout = findViewById(R.id.llFamilyAbout);
        lay_second = findViewById(R.id.lay_second);
        lay_state = findViewById(R.id.lay_state);
        tvStateTitle = findViewById(R.id.tvStateTitle);
        tvStateHeader = findViewById(R.id.tvStateHeader);

        rvMStatus = findViewById(R.id.rvMStatus);


        rvReligion = findViewById(R.id.rvReligion);


        rvCaste = findViewById(R.id.rvCaste);

        rvMT = findViewById(R.id.rvMT);


        rvCountry = findViewById(R.id.rvCountry);


        rvState = findViewById(R.id.rvState);
        rvCity = findViewById(R.id.rvCity);

        rvHeight = findViewById(R.id.rvHeight);


        rvSkinTone = findViewById(R.id.rvSkinTone);


        rvEducation = findViewById(R.id.rvEducation);


        rvEmployeedIn = findViewById(R.id.rvEmployeedIn);


        rvOccupation = findViewById(R.id.rvOccupation);


        rvIncome = findViewById(R.id.rvIncome);


        rvFamilyType = findViewById(R.id.rvFamilyType);


        rvFamilyStatus = findViewById(R.id.rvFamilyStatus);


        rvBodyType = findViewById(R.id.rvBodyType);


        rvWeight = findViewById(R.id.rvWeight);


        rvEatingH = findViewById(R.id.rvEatingH);


        rvSmoking = findViewById(R.id.rvSmoking);


        rvDrinking = findViewById(R.id.rvDrinking);


        rvHoroscope = findViewById(R.id.rvHoroscope);


        rvStar = findViewById(R.id.rvStar);


        rvMoonSign = findViewById(R.id.rvMoonSign);


        rvMangalik = findViewById(R.id.rvMangalik);


        rvNoOfBrother = findViewById(R.id.rvNoOfBrother);


        rvNoOfBrotherMarried = findViewById(R.id.rvNoOfBrotherMarried);


        rvNoOfSister = findViewById(R.id.rvNoOfSister);


        rvNoOfSisterMarried = findViewById(R.id.rvNoOfSisterMarried);


        rvBlood = findViewById(R.id.rvBlood);


//        rvlanguage = findViewById(R.id.rvlanguage);
//        setrvlanguageRecylerView();

        et_birth_place = findViewById(R.id.et_birth_place);
        et_birth_time = findViewById(R.id.et_birth_time);
        et_sub_caste = findViewById(R.id.et_sub_caste);
        et_gothra = findViewById(R.id.et_gothra);
        et_father_ocu = findViewById(R.id.et_father_ocu);
        et_mother_ocu = findViewById(R.id.et_mother_ocu);
        et_father_name = findViewById(R.id.et_father_name);
        et_mother_name = findViewById(R.id.et_mother_name);
        et_address = findViewById(R.id.et_address);

        et_birth_time.setOnClickListener(v -> {
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(RegisterFirstActivity.this, (timePicker, selectedHour, selectedMinute) -> {
                mcurrentTime.set(Calendar.HOUR, selectedHour);
                mcurrentTime.set(Calendar.MINUTE, selectedMinute);

                if (mFormat == null)
                    mFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

                et_birth_time.setText(Common.get12HrTime(selectedHour, selectedMinute));
            }, hour, minute, false);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isCheckSelect = true;
                } else {
                    isCheckSelect = false;
                }

            }
        });


        setCreatedByRecylerView();
        setrvMangalikRecylerView();
        setrvMoonSignRecylerView();
        setrvStarRecylerView();
        setrvHoroscopeRecylerView();
        setrvDrinkingRecylerView();
        setrvSmokingRecylerView();
        setrvEatingHRecylerView();
        setrvWeightRecylerView();
        setrvBodyTypeRecylerView();
        setrvFamilyStatusRecylerView();
        setrvFamilyTypeRecylerView();
        setrvIncomeRecylerView();
        setrvOccupationRecylerView();
        setrvEmployeedInRecylerView();
        setrvEducationRecylerView();
        setrvSkinToneRecylerView();
        setHeightRecylerView();
        setrvCountryRecylerView();
        setrvMTRecylerView();
        setrvReligionRecylerView();
        setMStatusRecylerView();
        setrvNoOfBrotherRecylerView();
        setrvNoOfBrotherMarriedRecylerView();
        setrvNoOfSisterRecylerView();
        setrvNoOfSisterMarriedRecylerView();
        setrvBloodRecylerView();

    }

    private void setupSearchDropDown(MultiSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void setupSearchDropDown(SingleSpinnerSearch spinner, String hint, String listJsonKey) {
        JsonParser jsonParser = new JsonParser();
        JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
        spinner.setItems(spinner, common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray()), -1, this, hint);
    }

    private void singleTextView(TextView textView) {
        String clickableTextStr = "Terms & Conditions";
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("By continuing you agree to " + clickableTextStr + " of usage");
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openCMSDataDialog();
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(RegisterFirstActivity.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 27, 45, 0);
//            }, spanText.length() - clickableTextStr.length(), spanText.length(), 0);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(RegisterFirstActivity.this, R.color.transparent));
        textView.setText(spanText, TextView.BufferType.SPANNABLE);
        //  lblTerms.setText(Html.fromHtml(getString(R.string.lbl_service_request)), TextView.BufferType.SPANNABLE);

    }

    private void openCMSDataDialog() {
        Intent intent = new Intent(this, AllCmsActivity.class);
        intent.putExtra(AppConstants.KEY_INTENT, "term");
        startActivity(intent);
    }

    private void setCreatedByRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvCreatedBy.setLayoutManager(mLayoutManager);
        setItemDecoration(rvCreatedBy);
        //  rvHeight.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("profileby");
        CreatedByAdapter adapter = new CreatedByAdapter(al, CREATED_BY, this, getContext());
        rvCreatedBy.setAdapter(adapter);

    }

    private void setItemDecoration(RecyclerView rv) {
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.divider));
        rv.addItemDecoration(itemDecoration);
    }

    private List<KeyPairBoolData> setupListForRecyclerView(String listJsonKey) {
        try {
            JsonParser jsonParser = new JsonParser();
            JsonObject gsonObject = (JsonObject) jsonParser.parse(MyApplication.getSpinData().toString());
            return common.getSpinnerListFromArray(gsonObject.get(listJsonKey).getAsJsonArray());
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        } catch (JsonIOException e) {
            e.printStackTrace();
        }

        return null;

    }

    @Override
    public void onItemClick(KeyPairBoolData data, int position, String tag) {
        llProfileCreate.setVisibility(View.GONE);
        lay_first.setVisibility(View.VISIBLE);
        created_id = data.getId();

    }

    public void selectDate() {
        DialogFragment newFragment = new SelectDateFragment();
        newFragment.show(getSupportFragmentManager(), "DatePicker");
    }

    private void updateLabel() {
        String myFormat = "dd-MM-yyyy"; //yyyy-M-dd
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        et_dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_male) {
            gender = "Male";
            btn_male.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            btn_female.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.registration_hint_color)));
            common.setDrawableLeftButton(R.drawable.male_active, btn_male);
            common.setDrawableLeftButton(R.drawable.female_inactive, btn_female);
            btn_male.setTextColor(getResources().getColor(R.color.colorAccent));
            btn_female.setTextColor(getResources().getColor(R.color.registration_hint_color));


        } else if (id == R.id.btn_female) {
            gender = "Female";
            btn_female.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
            btn_male.setCompoundDrawableTintList(ColorStateList.valueOf(getResources().getColor(R.color.registration_hint_color)));
            common.setDrawableLeftButton(R.drawable.male_inactive, btn_male);
            common.setDrawableLeftButton(R.drawable.female_active, btn_female);
            btn_female.setTextColor(getResources().getColor(R.color.colorAccent));
            btn_male.setTextColor(getResources().getColor(R.color.registration_hint_color));

        } else if (id == R.id.btn_first_submit) {
            validFirst();
        } else if (id == R.id.btn_login) {
            startActivity(new Intent(RegisterFirstActivity.this, LoginActivity.class));
            finish();
        } else if (id == R.id.tv_cancel) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            layoutBottomSheet.setVisibility(View.GONE);
        } else if (id == R.id.tv_camera) {

            if (checkPermission()) {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                fromCamera();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                pickImage(200);
            } else {
                requestPermission();
            }
        } else if (id == R.id.tv_gallary) {

            if (checkPermission()) {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                fromGallery();
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                pickImage(100);
            } else {
                requestPermission();
            }

        } else if (id == R.id.btn_choose) {
//            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//
//            } else {
//                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//
//            }
        } else if (id == R.id.img_one) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            image_id = 1;
            sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

        } else if (id == R.id.img_two) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            if (imag_one_seleted) {
                image_id = 2;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else
                common.showToast("Please upload 1st photo first. You can change & update photos later.",swipe);

        } else if (id == R.id.img_three) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            if (imag_one_seleted) {
                image_id = 3;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else
                common.showToast("Please upload 1st photo first. You can change & update photos later.",swipe);

        } else if (id == R.id.img_four) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            if (imag_one_seleted) {
                image_id = 4;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else
                common.showToast("Please upload 1st photo first. You can change & update photos later.",swipe);

        } else if (id == R.id.img_five) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            if (imag_one_seleted) {
                image_id = 5;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else
                common.showToast("Please upload 1st photo first. You can change & update photos later.",swipe);

        } else if (id == R.id.img_six) {
            layoutBottomSheet.setVisibility(View.VISIBLE);
            if (imag_one_seleted) {
                image_id = 6;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else
                common.showToast("Please upload 1st photo first. You can change & update photos later.",swipe);

        } else if (id == R.id.btn_photo_submit) {
            if (!(isImageSelect && isCheckSelect)) {
                common.showToast("Please read the photo guidelines and upload atleast one photo. You can change or upload more photos later.  ",swipe);
                return;
            } else {
                tvSkip.setVisibility(View.GONE);
                lay_photos.setVisibility(View.GONE);
                lay_state.setVisibility(View.VISIBLE);
                rvMStatus.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Marital Status\n");
                page_name = STEP_S;
            }
        }

    }

    private void setUpEasyImage() {
        easyImage = new EasyImage.Builder(RegisterFirstActivity.this)
                .setChooserTitle(getString(R.string.app_name))
                .setCopyImagesToPublicGalleryFolder(false)
                .setChooserType(ChooserType.CAMERA_AND_GALLERY)
                .setFolderName(AppConstants.DIRECTORY_NAME)
                .allowMultiple(false)
                .build();
    }

    private void pickImage(int pickFor) {
        switch (pickFor) {
            case 100:
                easyImage.openGallery(this);
                break;

            case 200:
                easyImage.openCameraForImage(this);
                break;
        }
    }

    private void validFirst() {
        String fname = et_f_name.getText().toString().trim();
        String lname = et_l_name.getText().toString().trim();
        String email = et_email.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String dob = et_dob.getText().toString().trim();
        String mobile = et_mobile.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();
        country_code = spin_code.getSelectedCountryCodeWithPlus();
        String time_call = et_time_call.getText().toString().trim();


        boolean isvalid = true;
        if (gender.equals("")) {
            common.showToast("Please Select your gender",swipe);
            isvalid = false;
        }

        if (TextUtils.isEmpty(fname)) {
            et_f_name.setError("Please enter first name");
            isvalid = false;
        } else if (!common.isValidName(fname)) {

            et_f_name.setError("Please enter valid first name");
            isvalid = false;

        }
        if (fname.contains(" ") || fname.contains(".")) {
            et_f_name.setError("Please enter valid first name");
            isvalid = false;
        }

        if (TextUtils.isEmpty(lname)) {
            et_l_name.setError("Please enter last name");
            isvalid = false;
        } else if (!common.isValidName(lname)) {

            et_l_name.setError("Please enter valid last name");
            isvalid = false;

        }

        if (lname.contains(" ")) {
            et_l_name.setError("Please enter valid last name");
            isvalid = false;
        }


        if (TextUtils.isEmpty(email)) {
            et_email.setError("Please enter email");
            isvalid = false;
        } else {
            if (!common.isValidEmail(email)) {
                et_email.setError("Please enter valid email");
                isvalid = false;
            }
        }

        if (TextUtils.isEmpty(password)) {
            et_password.setError("Please enter password");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (password.length() < 6) {
            et_password.setError("Please enter atleast 6 characters");
            pass_input.setPasswordVisibilityToggleEnabled(false);
            isvalid = false;
        } else
            pass_input.setPasswordVisibilityToggleEnabled(true);

        if (TextUtils.isEmpty(mobile)) {
            et_mobile.setError("Please enter mobile number");
            isvalid = false;
        } else {
            if (mobile.length() < 10 || mobile.contains(".")) {
                et_mobile.setError("Please enter valid mobile number");
                isvalid = false;
            }
        }
//        if (TextUtils.isEmpty(phone)) {
//            et_phone.setError("Please enter your Matchmaker's Mobile Number");
//            isvalid = false;
//        } else {
//            if (phone.length() < 10 || phone.contains(".")) {
//                et_phone.setError("Please enter valid value");
//                isvalid = false;
//            }
//        }
//        if (TextUtils.isEmpty(time_call)) {
//            et_time_call.setError("Please enter your Matchmaker's Name");
//            isvalid = false;
//        } else if (!common.isValidName(time_call)) {
//
//            et_time_call.setError("Please enter valid value");
//            isvalid = false;
//
//        }

//        if (phone.equals(mobile)) {
//            et_phone.setError("Please enter your Matchmaker's Mobile Number, it should be different from mobile number");
//            isvalid = false;
//        }

        if (TextUtils.isEmpty(dob)) {
            et_dob.setError("Please enter date of birth");
            isvalid = false;
        } else {
            age = calculateAge(et_dob.getText().toString());
        }
        if (gender.equals("Male")) {
            if (!(age >= 21)) {
                et_dob.setError("Minimum age should be 21 years");
                common.showToast("Minimum age should be 21 years",swipe);
                isvalid = false;
            }
        } else if (gender.equals("Female")) {
            if (!(age >= 18)) {
                et_dob.setError("Minimum age should be 18 years");
                common.showToast("Minimum age should be 18 years",swipe);
                isvalid = false;
            }
        }
        if (!isValidId(reference_id)) {
            common.spinnerSetError(spin_reference, "Please select reference by");
            isvalid = false;
        }

        if (isvalid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("firstname", fname);
            param.put("lastname", lname);
            param.put("email", email);
            param.put("password", password);
            param.put("country_code", country_code);
            param.put("mobile_number", mobile);
            param.put("birthdate", changeDate(dob));
            param.put("gender", gender);
            param.put("id", ragister_id);
            param.put("fb_id", fb_id);
            param.put("android_device_id", session.getLoginData(SessionManager.KEY_DEVICE_TOKEN));
            //Log.d("resp",param.toString());
//            ApiRequestResponse apiRequestResponse = new ApiRequestResponse();
//            apiRequestResponse.postRequest(this, AppConstants.register_first, param, this, BASIC_DETAIL);

            submitRagister(AppConstants.register_first, BASIC_DETAIL, param);
        }
    }

    private boolean isValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return false;
        }
        return true;
    }

    @SuppressLint("NewApi")
    private int calculateAge(String age) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date d = sdf.parse(age);
            Calendar c = Calendar.getInstance();
            c.setTime(d);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int date = c.get(Calendar.DATE);
            LocalDate l1 = LocalDate.of(year, month, date);
            LocalDate now1 = LocalDate.now();
            Period diff1 = Period.between(l1, now1);
            return diff1.getYears();
            // System.out.println("age:" + diff1.getYears() + "years");
        } catch (Exception e) {
            e.printStackTrace();

        }
        return 0;
    }

    public String changeDate(String time) {
        String inputPattern = "dd-MM-yyyy";
        String outputPattern = "yyyy-M-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public void submitLastDetails(View view) {
        boolean isValid = true;
        String subcst = et_sub_caste.getText().toString().trim();
        String location = et_birth_place.getText().toString().trim();
        String time = et_birth_time.getText().toString().trim();
        String gothra = et_gothra.getText().toString().trim();
        String add = et_address.getText().toString().trim();

        if (!isValidId(lang_id)) {
            common.spinnerSetError(spin_lang, "Please select languages known");
            isValid = false;
        } else if (!isValidId(resi_id)) {
            common.spinnerSetError(spin_residence, "Please select residence type");
            isValid = false;
        } else if (TextUtils.isEmpty(add)) {
            et_address.setError("Please enter Ancestral Origin (Native Place)");
            isValid = false;
        }

//        if (TextUtils.isEmpty(time)) {
//            et_birth_time.setError("Please enter birth time");
//            isValid = false;
//        } else if (TextUtils.isEmpty(location)) {
//            et_birth_place.setError("Please enter city of birth");
//            isValid = false;
//        }


        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
//            param.put("father_occupation", father_occupation);
//            param.put("mother_occupation", mother_occupation);
            param.put("gothra", gothra);
            param.put("subcaste", subcst);
            param.put("birthplace", location);
            param.put("birthtime", time);
            param.put("member_id", ragister_id);
            param.put("languages_known", getValidId(lang_id));
            param.put("designation", getValidId(desig_id));
            param.put("residence", getValidId(resi_id));
            param.put("address", add);
            submitData(STEP_Last, param);
        }
    }

    public void submitFamilyDetails(View view) {
        boolean isValid = true;
        String father_name = et_father_name.getText().toString().trim();
        String father_occupation = et_father_ocu.getText().toString().trim();
        String mother_name = et_mother_name.getText().toString().trim();
        String mother_occupation = et_mother_ocu.getText().toString().trim();

        if (TextUtils.isEmpty(father_name)) {
            et_father_name.setError("Please enter father name");
            isValid = false;
        }
//        else if (TextUtils.isEmpty(father_occupation)) {
//            et_father_ocu.setError("Please enter father's occupation");
//            isValid = false;
//        }
        else if (TextUtils.isEmpty(mother_name)) {
            et_mother_name.setError("Please enter mother name");
            isValid = false;
        }
//        else if (TextUtils.isEmpty(mother_occupation)) {
//            et_mother_ocu.setError("Please enter mother's occupation");
//            isValid = false;
//        }

        if (isValid) {
            HashMap<String, String> param = new HashMap<>();
            param.put("father_name", father_name);
            param.put("father_occupation", father_occupation);
            param.put("mother_name", mother_name);
            param.put("mother_occupation", mother_occupation);
            param.put("member_id", ragister_id);
            submitData(STEP_FAMILYDETAILS, param);
        }
    }


    @Override
    public void onProgressUpdate(int percentage) {
        // set current progress
        if (pd != null && pd.isShowing()) {
            pd.setProgress(percentage);
        }

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        //set finish progress
        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }

    public void onSkipClickFromPhotos(View view) {
        tvSkip.setVisibility(View.GONE);
        lay_photos.setVisibility(View.GONE);
        lay_state.setVisibility(View.VISIBLE);
        rvMStatus.setVisibility(View.VISIBLE);
        tvStateTitle.setText("Marital Status\n");
        page_name = STEP_S;
    }

    public void submitFromAboutPage(View view) {

        if (TextUtils.isEmpty(et_about.getText().toString())) {
            et_about.setError("Please fill this information");
            return;
        }

        HashMap<String, String> param = new HashMap<>();
        param.put("profile_text", et_about.getText().toString());
        param.put("id", ragister_id);
        submitRagister(AppConstants.register_step, STEP_ABOUT, param);


    }

    public void submitFromHobyPage(View view) {
        if (TextUtils.isEmpty(et_hoby.getText().toString())) {
            et_hoby.setError("Please fill this information");
            return;
        }

        HashMap<String, String> param = new HashMap<>();
        param.put("hobby", et_hoby.getText().toString());
        param.put("id", ragister_id);
        submitRagister(AppConstants.register_step, STEP_Hobby, param);


    }

    public void submitFromAbotFamilyPage(View view) {
        if (TextUtils.isEmpty(et_fabout.getText().toString())) {
            et_fabout.setError("Please fill this information");
            return;
        }

        HashMap<String, String> param = new HashMap<>();
        param.put("family_details", et_fabout.getText().toString());
        param.put("member_id", ragister_id);
        submitData(STEP_FAMILYABOUT, param);


    }

    public void onSkipClickFromState(View view) {
        switch (page_name) {
            case STEP_S:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(10);
                rvHeight.setVisibility(View.VISIBLE);
                rvMStatus.setVisibility(View.GONE);
                tvStateTitle.setText("Height\n");
                page_name = STEP_H;
                break;
            case STEP_H:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(20);
                rvReligion.setVisibility(View.VISIBLE);
                rvHeight.setVisibility(View.GONE);
                tvStateTitle.setText("Religion\n");
                page_name = STEP_R;
                break;
            case STEP_R:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(30);
                rvReligion.setVisibility(View.GONE);
                rvCaste.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Caste\n");
                page_name = STEP_C;
                break;

            case STEP_C:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(40);
                rvMT.setVisibility(View.VISIBLE);
                rvCaste.setVisibility(View.GONE);
                tvStateTitle.setText("Mother Tongue\n");
                page_name = STEP_MT;
                break;
            case STEP_MT:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(50);
                rvMT.setVisibility(View.GONE);
                rvCountry.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Country\n");
                page_name = STEP_Country;
                break;
            case STEP_Country:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(60);
                rvCountry.setVisibility(View.GONE);
                rvState.setVisibility(View.VISIBLE);
                tvStateTitle.setText("State\n");
                page_name = STEP_State;
                break;
            case STEP_State:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(70);
                rvState.setVisibility(View.GONE);
                rvCity.setVisibility(View.VISIBLE);
                tvStateTitle.setText("City\n");
                page_name = STEP_City;
                break;
            case STEP_City:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(80);
                rvCity.setVisibility(View.GONE);
                rvEducation.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Highest Education\n");
                page_name = STEP_EDU;
                break;
            case STEP_EDU:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(90);
                rvEducation.setVisibility(View.GONE);
                rvEmployeedIn.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Working Sector\n");
                page_name = STEP_EMP;
                break;
            case STEP_EMP:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(100);
                rvEmployeedIn.setVisibility(View.GONE);
                rvOccupation.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Profession\n");
                page_name = STEP_OCC;
                break;
            case STEP_OCC:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(110);
                rvOccupation.setVisibility(View.GONE);
                rvIncome.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Annual Income\n");
                page_name = STEP_INCOME;
                break;
            case STEP_INCOME:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(120);
                rvIncome.setVisibility(View.GONE);
                rvSkinTone.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Skin Color Tone\n");
                page_name = STEP_SkinTone;
                break;
            case STEP_SkinTone:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(130);
                rvSkinTone.setVisibility(View.GONE);
                rvBodyType.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Body Type\n");
                page_name = STEP_BodyType;
                break;
            case STEP_BodyType:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(140);
                rvBodyType.setVisibility(View.GONE);
                rvWeight.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Weight\n");
                page_name = STEP_Weight;
                break;
            case STEP_Weight:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(150);
                rvWeight.setVisibility(View.GONE);
                rvEatingH.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Eating Habit\n");
                page_name = STEP_EatingH;
                break;
            case STEP_EatingH:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(160);
                rvEatingH.setVisibility(View.GONE);
                rvSmoking.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Smoking\n");
                page_name = STEP_Smoking;
                break;
            case STEP_Smoking:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(170);
                rvSmoking.setVisibility(View.GONE);
                rvDrinking.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Drinking\n");
                page_name = STEP_Drinking;
                break;
            case STEP_Drinking:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(180);
                rvDrinking.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                llAbout.setVisibility(View.VISIBLE);
                tvStateTitle.setText("About\n");
                page_name = STEP_ABOUT;
                break;
            case STEP_ABOUT:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(190);
                llAbout.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                llHobby.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Hobbies and Interests\n");
                page_name = STEP_Hobby;
                break;
            case STEP_Hobby:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(200);
                llHobby.setVisibility(View.GONE);
                rvHoroscope.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Horoscope Belief\n");
                page_name = STEP_Horoscope;
                break;
            case STEP_Horoscope:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(210);
                rvHoroscope.setVisibility(View.GONE);
                rvStar.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Star\n");
                page_name = STEP_Star;
                break;
            case STEP_Star:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(220);
                rvStar.setVisibility(View.GONE);
                rvMoonSign.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Moonsign (Raas)\n");
                page_name = STEP_MoonSign;
                break;
            case STEP_MoonSign:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(230);
                rvMoonSign.setVisibility(View.GONE);
                rvMangalik.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Manglik\n");
                page_name = STEP_Mangalik;
                break;
            case STEP_Mangalik:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(240);
                rvMangalik.setVisibility(View.GONE);
                rvBlood.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Blood Group\n");
                page_name = STEP_Blood;
                break;

            case STEP_Blood:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(240);
                editText.setVisibility(View.GONE);
                rvBlood.setVisibility(View.GONE);
                llLastStep.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Birth Details & Other Info\n");
                page_name = STEP_Last;
                break;

            case STEP_Last:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(250);
                llLastStep.setVisibility(View.GONE);
                rvFamilyStatus.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Status\n");
                page_name = STEP_FS;
                break;

            case STEP_FS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(260);
                rvFamilyStatus.setVisibility(View.GONE);
                rvFamilyType.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Type\n");
                page_name = STEP_FT;
                break;

            case STEP_FT:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(270);
                editText.setVisibility(View.GONE);
                rvFamilyType.setVisibility(View.GONE);
                llFamilyStep.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Details\n");
                page_name = STEP_FAMILYDETAILS;
                break;

            case STEP_FAMILYDETAILS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(280);
                llFamilyStep.setVisibility(View.GONE);
                rvNoOfBrother.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("No. of Brother(s)\n");
                page_name = STEP_NOB;
                break;
            case STEP_NOB:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(290);
                rvNoOfBrother.setVisibility(View.GONE);
                rvNoOfBrotherMarried.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Brother(s) Married\n");
                page_name = STEP_NOBM;
                break;
            case STEP_NOBM:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(300);
                rvNoOfBrotherMarried.setVisibility(View.GONE);
                rvNoOfSister.setVisibility(View.VISIBLE);
                tvStateTitle.setText("No. of Sister(s)\n");
                page_name = STEP_NOS;
                break;

            case STEP_NOS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(310);
                rvNoOfSister.setVisibility(View.GONE);
                rvNoOfSisterMarried.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Sister(s) Married\n");
                page_name = STEP_NOSM;
                break;
            case STEP_NOSM:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(310);
                rvNoOfSisterMarried.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                llFamilyAbout.setVisibility(View.VISIBLE);
                tvStateTitle.setText("About Family\n");
                page_name = STEP_FAMILYABOUT;
                break;
            case STEP_FAMILYABOUT:
                tvSkip.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                lay_state.setVisibility(View.GONE);
                lay_second.setVisibility(View.VISIBLE);
                page_name = STEP_Login;
                break;
        }
    }

    public void onHaveAccountClick(View view) {
        startActivity(new Intent(RegisterFirstActivity.this, LoginActivity.class));
        finish();
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, yy, mm, dd);
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;

        }

        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm + 1, dd);
        }
    }


    public static void populateSetDate(int year, int month, int day) {
        et_dob.setText(day + "-" + month + "-" + year);
    }


    public void continueRegister(View view) {
        if (view.getId() == R.id.btnContinue) {
            lay_welcome_after_basic_regis.setVisibility(View.GONE);
            lay_photos.setVisibility(View.VISIBLE);
            page_name = STEP_Photos;
        }
    }


    private void setMStatusRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMStatus.setLayoutManager(mLayoutManager);
        setItemDecoration(rvMStatus);
        //  rvMStatus.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("marital_status");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_S, this, getContext());
        rvMStatus.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void setrvReligionRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvReligion.setLayoutManager(mLayoutManager);
        setItemDecoration(rvReligion);
        // rvReligion.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("religion_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_R, this, getContext());
        rvReligion.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    private void setrvCasteRecylerView() {
//        casteArrayList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvCaste.setLayoutManager(mLayoutManager);
        setItemDecoration(rvCaste);
        // rvCaste.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        CustomHeightAdapter adapter = new CustomHeightAdapter(casteArrayList, STEP_C, this, getContext());
        rvCaste.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void setrvMTRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMT.setLayoutManager(mLayoutManager);
        setItemDecoration(rvMT);
        // rvMT.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("mothertongue_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_MT, this, getContext());
        rvMT.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        List<KeyPairBoolData> finalAl = al;
//        rvMT.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, rvMT, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        tongue_id = finalAl.get(position).getId();
//                        HashMap<String, String> param = new HashMap<>();
//                        param.put("mother_tongue", getValidId(tongue_id));
//                        param.put("id", ragister_id);
//                        submitRagister(Utils.register_step, STEP_MT, param);
//
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
    }


    private void setrvCountryRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvCountry.setLayoutManager(mLayoutManager);
        setItemDecoration(rvCountry);
        // rvCountry.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("country_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Country, this, getContext());
        rvCountry.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvStateRecylerView() {
//        stateArrayList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvState.setLayoutManager(mLayoutManager);
        setItemDecoration(rvState);
        // rvCaste.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        CustomHeightAdapter adapter = new CustomHeightAdapter(stateArrayList, STEP_State, this, getContext());
        rvState.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void setrvCityRecylerView() {
//        cityArrayList = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvCity.setLayoutManager(mLayoutManager);
        setItemDecoration(rvCity);
        // rvCaste.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        CustomHeightAdapter adapter = new CustomHeightAdapter(cityArrayList, STEP_City, this, getContext());
        rvCity.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void setHeightRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvHeight.setLayoutManager(mLayoutManager);
        setItemDecoration(rvHeight);
        //  rvHeight.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("height_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_H, this, getContext());
        rvHeight.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

//        List<KeyPairBoolData> finalAl = al;
//        rvHeight.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, rvHeight, new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(View view, int position) {
//                        hite_id = finalAl.get(position).getId();
//                        HashMap<String, String> param = new HashMap<>();
//                        param.put("id", ragister_id);
//                        param.put("height", getValidId(hite_id));
//                        submitRagister(Utils.register_step, STEP_H, param);
//                    }
//
//                    @Override
//                    public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );
    }


    private void setrvEducationRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEducation.setLayoutManager(mLayoutManager);
        setItemDecoration(rvEducation);
        //rvEducation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("education_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_EDU, this, getContext());
        rvEducation.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvOccupationRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvOccupation.setLayoutManager(mLayoutManager);
        setItemDecoration(rvOccupation);
        // rvOccupation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("occupation_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_OCC, this, getContext());
        rvOccupation.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvEmployeedInRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEmployeedIn.setLayoutManager(mLayoutManager);
        setItemDecoration(rvEmployeedIn);
        // rvEmployeedIn.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("employee_in");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_EMP, this, getContext());
        rvEmployeedIn.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.GONE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvIncomeRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvIncome.setLayoutManager(mLayoutManager);
        setItemDecoration(rvIncome);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("income");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_INCOME, this, getContext());
        rvIncome.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvSkinToneRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvSkinTone.setLayoutManager(mLayoutManager);
        setItemDecoration(rvSkinTone);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("complexion");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_SkinTone, this, getContext());
        rvSkinTone.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvFamilyStatusRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvFamilyStatus.setLayoutManager(mLayoutManager);
        setItemDecoration(rvFamilyStatus);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("family_status");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_FS, this, getContext());
        rvFamilyStatus.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvFamilyTypeRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvFamilyType.setLayoutManager(mLayoutManager);
        setItemDecoration(rvFamilyType);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("family_type");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_FT, this, getContext());
        rvFamilyType.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvBodyTypeRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvBodyType.setLayoutManager(mLayoutManager);
        setItemDecoration(rvBodyType);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("bodytype");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_BodyType, this, getContext());
        rvBodyType.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvStarRecylerView() {

        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvStar.setLayoutManager(mLayoutManager);
        setItemDecoration(rvStar);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("star_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Star, this, getContext());
        rvStar.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvMangalikRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMangalik.setLayoutManager(mLayoutManager);
        setItemDecoration(rvMangalik);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("manglik");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Mangalik, this, getContext());
        rvMangalik.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvNoOfBrotherRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNoOfBrother.setLayoutManager(mLayoutManager);
        setItemDecoration(rvNoOfBrother);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("no_of_brothers");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_NOB, this, getContext());
        rvNoOfBrother.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvNoOfBrotherMarriedRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNoOfBrotherMarried.setLayoutManager(mLayoutManager);
        setItemDecoration(rvNoOfBrotherMarried);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("no_marri_brother");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_NOBM, this, getContext());
        rvNoOfBrotherMarried.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvNoOfSisterRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNoOfSister.setLayoutManager(mLayoutManager);
        setItemDecoration(rvNoOfSister);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("no_of_brothers");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_NOS, this, getContext());
        rvNoOfSister.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvNoOfSisterMarriedRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvNoOfSisterMarried.setLayoutManager(mLayoutManager);
        setItemDecoration(rvNoOfSisterMarried);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("no_marri_sister");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_NOSM, this, getContext());
        rvNoOfSisterMarried.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvBloodRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvBlood.setLayoutManager(mLayoutManager);
        setItemDecoration(rvBlood);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("blood_group");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Blood, this, getContext());
        rvBlood.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvMoonSignRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvMoonSign.setLayoutManager(mLayoutManager);
        setItemDecoration(rvMoonSign);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("moonsign_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_MoonSign, this, getContext());
        rvMoonSign.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvlanguageRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvlanguage.setLayoutManager(mLayoutManager);
        setItemDecoration(rvlanguage);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("mothertongue_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Lang, this, getContext());
        rvlanguage.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvHoroscopeRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvHoroscope.setLayoutManager(mLayoutManager);
        setItemDecoration(rvHoroscope);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("horoscope");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Horoscope, this, getContext());
        rvHoroscope.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    private void setrvDrinkingRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvDrinking.setLayoutManager(mLayoutManager);
        setItemDecoration(rvDrinking);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("drink");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Drinking, this, getContext());
        rvDrinking.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvSmokingRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvSmoking.setLayoutManager(mLayoutManager);
        setItemDecoration(rvSmoking);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("smoke");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Smoking, this, getContext());
        rvSmoking.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvEatingHRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvEatingH.setLayoutManager(mLayoutManager);
        setItemDecoration(rvEatingH);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("diet");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_EatingH, this, getContext());
        rvEatingH.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setrvWeightRecylerView() {
        List<KeyPairBoolData> al = new ArrayList<>();
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rvWeight.setLayoutManager(mLayoutManager);
        setItemDecoration(rvWeight);
        // rvDesignation.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        al = setupListForRecyclerView("weight_list");
        CustomHeightAdapter adapter = new CustomHeightAdapter(al, STEP_Weight, this, getContext());
        rvWeight.setAdapter(adapter);

        editText = findViewById(R.id.alertSearchEditText);
        editText.setVisibility(View.VISIBLE);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }


    //    API Call  //
    private void getList() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",swipe);
            return;
        }
        common.showProgressRelativeLayout(progressBar);
        common.makePostRequest(AppConstants.common_list, new HashMap<>(), response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                MyApplication.setSpinData(object);
                // initDropDownData();
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),swipe);
            }

        }, error -> {
            Log.d("resp", error.getMessage() + "   ");
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),swipe);
            }
        },swipe);
    }


    private void submitRagister(String url, final String tag, HashMap<String, String> param) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",swipe);
            return;
        }

        Common.hideSoftKeyboard(this);

        common.showProgressRelativeLayout(progressBar);

        common.makePostRequest(url, param, response -> {
            Log.d("resp", response);
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
                if (object.getString("status").equals("success")) {
                    switch (tag) {
                        case BASIC_DETAIL:
                            ragister_id = object.getString("id");
                            HashMap<String, String> params = new HashMap<>();
                            params.put("profileby", created_id);
                            params.put("member_id", ragister_id);
//                            params.put("phone", et_phone.getText().toString());
//                            params.put("time_to_call", et_time_call.getText().toString());
                            params.put("reference", getValidId(reference_id));
                            submitData(BASIC_DETAIL, params);
                            break;

                        case STEP_Photos:
                            ragister_id = object.getString("id");
                            lay_photos.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            lay_state.setVisibility(View.VISIBLE);
                            rvMStatus.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("Marital Status\n");
                            page_name = STEP_S;
                            break;

                        case STEP_S:
                            tvStateTitle.setText("Height\n");
                            rvMStatus.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            rvHeight.setVisibility(View.VISIBLE);
                            page_name = STEP_H;
                            break;

                        case STEP_H:
                            tvStateTitle.setText("Religion\n");
                            rvHeight.setVisibility(View.GONE);
                            rvReligion.setVisibility(View.VISIBLE);
//                            tvSkip.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_R;
                            break;


                        case STEP_R:
                            tvStateTitle.setText("Caste\n");
                            rvReligion.setVisibility(View.GONE);
                            rvCaste.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_C;
                            break;

                        case STEP_C:
                            tvStateTitle.setText("Mother Tongue\n");
                            rvCaste.setVisibility(View.GONE);
                            rvMT.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_MT;
                            break;

                        case STEP_MT:
                            tvStateTitle.setText("Country\n");
                            rvCountry.setVisibility(View.VISIBLE);
                            rvMT.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Country;
                            break;

                        case STEP_Country:
                            tvStateTitle.setText("State\n");
                            rvState.setVisibility(View.VISIBLE);
                            rvCountry.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_State;
                            break;

                        case STEP_State:
                            tvStateTitle.setText("City\n");
                            rvCity.setVisibility(View.VISIBLE);
                            rvState.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_City;
                            break;

                        case STEP_City:
//                            tvStateHeader.setText("A Few More Details");
                            tvStateTitle.setText("Highest Education\n");
                            rvCity.setVisibility(View.GONE);
                            rvEducation.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_EDU;
                            break;

//                        case STEP_H:
//                            tvStateTitle.setText("Highest Education\n");
//                            rvEducation.setVisibility(View.VISIBLE);
//                            rvHeight.setVisibility(View.GONE);
//                            page_name = STEP_EDU;
//                            break;

//                        case STEP_SkinTone:
//                            tvStateTitle.setText("Highest Education\n");
//                            rvEducation.setVisibility(View.VISIBLE);
//                            rvSkinTone.setVisibility(View.GONE);
//                            page_name = STEP_EDU;
//                            break;

                        case STEP_EDU:
                            tvStateTitle.setText("Working Sector\n");
                            rvEmployeedIn.setVisibility(View.VISIBLE);
                            rvEducation.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_EMP;
                            break;

                        case STEP_EMP:
                            tvSkip.setVisibility(View.GONE);
                            tvStateTitle.setText("Profession\n");
                            rvOccupation.setVisibility(View.VISIBLE);
                            rvEmployeedIn.setVisibility(View.GONE);
                            page_name = STEP_OCC;
                            break;

                        case STEP_OCC:
                            tvSkip.setVisibility(View.GONE);
                            tvStateTitle.setText("Annual Income\n");
                            rvIncome.setVisibility(View.VISIBLE);
                            rvOccupation.setVisibility(View.GONE);
                            page_name = STEP_INCOME;
                            break;

                        case STEP_INCOME:
                            tvStateTitle.setText("Skin Color Tone\n");
                            rvSkinTone.setVisibility(View.VISIBLE);
                            rvIncome.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_SkinTone;
                            break;

                        case STEP_SkinTone:
                            tvStateTitle.setText("Body Type\n");
                            rvSkinTone.setVisibility(View.GONE);
                            rvBodyType.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_BodyType;
                            break;

                        case STEP_BodyType:
                            tvStateTitle.setText("Weight\n");
                            rvBodyType.setVisibility(View.GONE);
                            rvWeight.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Weight;
                            break;

                        case STEP_Weight:
                            tvStateTitle.setText("Eating Habit\n");
                            rvWeight.setVisibility(View.GONE);
                            rvEatingH.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_EatingH;
                            break;

                        case STEP_EatingH:
                            tvStateTitle.setText("Smoking\n");
                            rvEatingH.setVisibility(View.GONE);
                            rvSmoking.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Smoking;
                            break;

                        case STEP_Smoking:
                            tvSkip.setVisibility(View.GONE);
                            tvStateTitle.setText("Drinking\n");
                            rvSmoking.setVisibility(View.GONE);
                            rvDrinking.setVisibility(View.VISIBLE);
                            page_name = STEP_Drinking;
                            break;

                        case STEP_Drinking:
                            tvSkip.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("About\n");
                            rvDrinking.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            llAbout.setVisibility(View.VISIBLE);
                            page_name = STEP_ABOUT;
                            break;

                        case STEP_ABOUT:
                            tvSkip.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("Hobbies and Interests\n");
                            llAbout.setVisibility(View.GONE);
                            llHobby.setVisibility(View.VISIBLE);
                            page_name = STEP_Hobby;
                            break;

                        case STEP_Hobby:
                            tvStateTitle.setText("Horoscope Belief\n");
                            llHobby.setVisibility(View.GONE);
                            rvHoroscope.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Horoscope;
                            break;

                        case STEP_Horoscope:
                            tvStateTitle.setText("Star\n");
                            rvHoroscope.setVisibility(View.GONE);
                            rvStar.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Star;
                            break;

                        case STEP_Star:
                            tvStateTitle.setText("Moonsign (Raas)\n");
                            rvStar.setVisibility(View.GONE);
                            rvMoonSign.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_MoonSign;
                            break;

                        case STEP_MoonSign:
                            tvStateTitle.setText("Manglik\n");
                            rvMangalik.setVisibility(View.VISIBLE);
                            rvMoonSign.setVisibility(View.GONE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_Mangalik;

                            break;

                        case STEP_Mangalik:
                            rvMangalik.setVisibility(View.GONE);
                            tvStateTitle.setText("Blood Group\n");
                            rvBlood.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.VISIBLE);
                            page_name = STEP_Blood;
                            break;

                        case STEP_Blood:
                            tvSkip.setVisibility(View.GONE);
                            rvBlood.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            tvStateTitle.setText("Birth & Other Info\n");
                            llLastStep.setVisibility(View.VISIBLE);
                            page_name = STEP_Last;
                            pbState.setProgress(240);
                            break;


                    }
                } else {
                    common.showToast(object.getString("errmessage"),swipe);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),swipe);
            }
        },swipe);
    }


    private void submitData(final String tag, HashMap<String, String> param) {


        common.showProgressRelativeLayout(progressBar);

        common.makePostRequest(AppConstants.edit_profile, param, response -> {
            common.hideProgressRelativeLayout(progressBar);
            try {
                JSONObject object = new JSONObject(response);
//                common.showToast(object.getString("errmessage"),swipe);
                if (object.getString("status").equals("success")) {
                    switch (tag) {
                        case BASIC_DETAIL:
                            lay_first.setVisibility(View.GONE);
                            lay_welcome_after_basic_regis.setVisibility(View.VISIBLE);
                            page_name = STEP_Photos;
                            break;
                        case STEP_FS:
                            tvSkip.setVisibility(View.GONE);
                            tvStateTitle.setText("Family Type\n");
                            rvFamilyStatus.setVisibility(View.GONE);
                            rvFamilyType.setVisibility(View.VISIBLE);
                            page_name = STEP_FT;
                            break;
                        case STEP_FT:
//                            tvStateHeader.setText("Some Personal Details to complete profile");
                            rvFamilyType.setVisibility(View.GONE);
                            editText.setVisibility(View.GONE);
                            tvStateTitle.setText("Family Details\n");
                            llFamilyStep.setVisibility(View.VISIBLE);
                            tvSkip.setVisibility(View.GONE);
                            page_name = STEP_FAMILYDETAILS;
                            break;
//                        case STEP_SkinTone:
//                            tvStateTitle.setText("Highest Education\n");
//                            rvEducation.setVisibility(View.VISIBLE);
//                            rvSkinTone.setVisibility(View.GONE);
//                            page_name = STEP_EDU;
//
//
//                            tvStateTitle.setText("Body Type\n");
//                            rvSkinTone.setVisibility(View.GONE);
//                            rvBodyType.setVisibility(View.VISIBLE);
//                            page_name = STEP_BodyType;
//                            break;

                        case STEP_Last:
                            tvSkip.setVisibility(View.GONE);
                            llLastStep.setVisibility(View.GONE);
                            rvFamilyStatus.setVisibility(View.VISIBLE);
                            editText.setVisibility(View.VISIBLE);
                            tvStateTitle.setVisibility(View.VISIBLE);
//                            lay_state.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("Family Status\n");
                            page_name = STEP_FS;
                            break;

                        case STEP_FAMILYDETAILS:
                            tvSkip.setVisibility(View.GONE);
                            editText.setVisibility(View.VISIBLE);
                            tvStateTitle.setVisibility(View.VISIBLE);
                            llFamilyStep.setVisibility(View.GONE);
                            rvNoOfBrother.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("No. of Brother(s)\n");
                            page_name = STEP_NOB;
                            break;

                        case STEP_NOB:
                            tvSkip.setVisibility(View.GONE);
                            rvNoOfBrother.setVisibility(View.GONE);
                            rvNoOfBrotherMarried.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("Brother(s) Married\n");
                            page_name = STEP_NOBM;
                            break;

                        case STEP_NOBM:
                            tvSkip.setVisibility(View.GONE);
                            rvNoOfBrotherMarried.setVisibility(View.GONE);
                            rvNoOfSister.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("No. of Sister(s)\n");
                            page_name = STEP_NOS;
                            break;

                        case STEP_NOS:
                            tvSkip.setVisibility(View.GONE);
                            rvNoOfSister.setVisibility(View.GONE);
                            rvNoOfSisterMarried.setVisibility(View.VISIBLE);
                            tvStateTitle.setText("Sister(s) Married\n");
                            page_name = STEP_NOSM;
                            break;

                        case STEP_NOSM:
                            tvStateTitle.setText("About Family\n");
                            editText.setVisibility(View.GONE);
                            rvNoOfSisterMarried.setVisibility(View.GONE);
                            llFamilyAbout.setVisibility(View.VISIBLE);
                            page_name = STEP_FAMILYABOUT;
                            break;

                        case STEP_FAMILYABOUT:
                            llFamilyAbout.setVisibility(View.GONE);
                            lay_state.setVisibility(View.GONE);
                            lay_second.setVisibility(View.VISIBLE);
                            page_name = STEP_Login;
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),swipe);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                common.hideProgressRelativeLayout(progressBar);
                if (error.networkResponse != null) {
                    common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),swipe);
                }
            }
        },swipe);
    }


    private void getDependentList(final String tag, String id) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast("Please check your internet connection!",swipe);
            return;
        }

        common.showProgressRelativeLayout(progressBar);

        HashMap<String, String> param = new HashMap<>();
        param.put("get_list", tag);
        param.put("currnet_val", id);
        param.put("multivar", "");
        param.put("retun_for", "");

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
                            casteArrayList.addAll(common.getSpinnerListFromArray(jsonArray));
                            setrvCasteRecylerView();
                            //spin_caste.setItems(spin_caste, common.getSpinnerListFromArray(jsonArray), -1, this, "Caste*");
                            break;
                        case "state_list":
                            JsonArray jsonArray1 = (JsonArray) jsonParser.parse(object.getJSONArray("data").toString());
                            stateArrayList.addAll(common.getSpinnerListFromArray(jsonArray1));
                            setrvStateRecylerView();
                            //spin_state.setItems(spin_state, common.getSpinnerListFromArray(jsonArray1), -1, this, "State*");
                            break;
                        case "city_list":
                            JsonArray jsonArray2 = (JsonArray) jsonParser.parse(object.getJSONArray("data").toString());
                            cityArrayList.addAll(common.getSpinnerListFromArray(jsonArray2));
                            setrvCityRecylerView();
                            // spin_city.setItems(spin_city, common.getSpinnerListFromArray(jsonArray2), -1, this, "City*");
                            break;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            common.hideProgressRelativeLayout(progressBar);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),swipe);
            }
        },swipe);

    }

    @Override
    public void onItemsClick(KeyPairBoolData data, int position, String tag) {
        try {
            editText.setText("");
            HashMap<String, String> param = new HashMap<>();
            switch (tag) {

                case STEP_S:
                    pbState.setProgress(10);
                    mari_id = data.getId();
                    param.put("marital_status", getValidId(mari_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_S, param);
                    break;

                case STEP_R:
                    pbState.setProgress(20);
                    religion_id = data.getId();
                    casteArrayList = new ArrayList<>();
                    getDependentList("caste_list", religion_id);
                    param.put("religion", getValidId(religion_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_R, param);
                    break;

                case STEP_C:
                    pbState.setProgress(30);
                    caste_id = casteArrayList.get(position).getId();
                    param.put("caste", getValidId(caste_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_C, param);
                    break;

                case STEP_MT:
                    pbState.setProgress(40);
                    tongue_id = data.getId();
                    param.put("mother_tongue", getValidId(tongue_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_MT, param);
                    break;

                case STEP_Country:
                    pbState.setProgress(50);
                    country_id = data.getId();
                    stateArrayList = new ArrayList<>();
                    getDependentList("state_list", country_id);
                    param.put("country_id", getValidId(country_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Country, param);
                    break;

                case STEP_State:
                    pbState.setProgress(60);
                    state_id = data.getId();
                    cityArrayList = new ArrayList<>();
                    getDependentList("city_list", state_id);
                    param.put("state_id", getValidId(state_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_State, param);
                    break;

                case STEP_City:
                    pbState.setProgress(70);
                    city_id = data.getId();
                    param.put("city", getValidId(city_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_City, param);
                    break;

                case STEP_H:
                    pbState.setProgress(80);
                    hite_id = data.getId();
                    param.put("id", ragister_id);
                    param.put("height", getValidId(hite_id));
                    submitRagister(AppConstants.register_step, STEP_H, param);

                    break;


                case STEP_EDU:
                    pbState.setProgress(90);
                    edu_id = data.getId();
                    param.put("education_detail", getValidId(edu_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_EDU, param);
                    break;


                case STEP_EMP:
                    pbState.setProgress(100);
                    emp_id = data.getId();
                    param.put("employee_in", getValidId(emp_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_EMP, param);
                    break;


                case STEP_OCC:
                    pbState.setProgress(110);
                    occu_id = data.getId();
                    param.put("occupation", getValidId(occu_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_OCC, param);
                    break;


                case STEP_INCOME:
                    pbState.setProgress(120);
                    income_id = data.getId();
                    param.put("income", getValidId(income_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_INCOME, param);
                    break;


                case STEP_SkinTone:
                    pbState.setProgress(130);
                    skin_id = data.getId();
                    param.put("complexion", getValidId(skin_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_SkinTone, param);
                    break;

                case STEP_BodyType:
                    pbState.setProgress(140);
                    body_id = data.getId();
                    param.put("bodytype", getValidId(body_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_BodyType, param);
                    break;

                case STEP_Weight:
                    pbState.setProgress(150);
                    weight_id = data.getId();
                    param.put("weight", getValidId(weight_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Weight, param);
                    break;

                case STEP_EatingH:
                    pbState.setProgress(160);
                    eat_id = data.getId();
                    param.put("diet", getValidId(eat_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_EatingH, param);
                    break;

                case STEP_Smoking:
                    pbState.setProgress(170);
                    smok_id = data.getId();
                    param.put("smoke", getValidId(smok_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Smoking, param);
                    break;

                case STEP_Drinking:
                    pbState.setProgress(180);
                    drink_id = data.getId();
                    param.put("drink", getValidId(drink_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Drinking, param);
                    break;


                case STEP_Horoscope:
                    pbState.setProgress(190);
                    horo_id = data.getId();
                    param.put("horoscope", getValidId(horo_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Horoscope, param);
                    break;

                case STEP_Star:
                    pbState.setProgress(200);
                    star_id = data.getId();
                    param.put("star", getValidId(star_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Star, param);
                    break;


                case STEP_MoonSign:
                    pbState.setProgress(210);
                    moon_id = data.getId();
                    param.put("moonsign", getValidId(moon_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_MoonSign, param);
                    break;

                case STEP_Mangalik:
                    pbState.setProgress(220);
                    manglik_id = data.getId();
                    param.put("manglik", getValidId(manglik_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Mangalik, param);
                    break;
                case STEP_Blood:
                    pbState.setProgress(230);
                    blood_id = data.getId();
                    param.put("blood_group", getValidId(blood_id));
                    param.put("id", ragister_id);
                    submitRagister(AppConstants.register_step, STEP_Blood, param);
                    break;

                case STEP_FS:
                    pbState.setProgress(250);
                    family_status_id = data.getId();
                    param.put("family_status", getValidId(family_status_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_FS, param);
                    break;

                case STEP_FT:
                    pbState.setProgress(260);
                    family_type_id = data.getId();
                    param.put("family_type", getValidId(family_type_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_FT, param);
                    break;


                case STEP_NOB:
                    pbState.setProgress(270);
                    no_bro_id = data.getId();
                    param.put("no_of_brothers", getValidId(no_bro_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_NOB, param);
                    break;

                case STEP_NOBM:
                    pbState.setProgress(280);
                    no_mari_bro_id = data.getId();
                    param.put("no_of_married_brother", getValidId(no_mari_bro_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_NOBM, param);
                    break;

                case STEP_NOS:
                    pbState.setProgress(290);
                    no_sis_id = data.getId();
                    param.put("no_of_sisters", getValidId(no_sis_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_NOS, param);
                    break;

                case STEP_NOSM:
                    pbState.setProgress(310);
                    no_mari_sis_id = data.getId();
                    param.put("no_of_married_sister", getValidId(no_mari_sis_id));
                    param.put("member_id", ragister_id);
                    submitData(STEP_NOSM, param);
                    break;


            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private String getValidId(String val) {
        if (val == null || val.equals("") || val.equals("0")) {
            return "";
        }
        return val;
    }

    @Override
    public void onBackPressed() {
        switch (page_name) {
            case CREATED_BY:
                finish();
                break;
            case BASIC_DETAIL:
                llProfileCreate.setVisibility(View.VISIBLE);
                lay_first.setVisibility(View.GONE);
                page_name = CREATED_BY;
                break;
//            case STEP_Photos:
//                lay_photos.setVisibility(View.GONE);
//                lay_first.setVisibility(View.VISIBLE);
//                page_name = BASIC_DETAIL;
//                break;
            case STEP_S:
                pbState.setProgress(10);
                lay_photos.setVisibility(View.VISIBLE);
                tvSkip.setVisibility(View.VISIBLE);
                rvMStatus.setVisibility(View.GONE);
                lay_state.setVisibility(View.GONE);
                page_name = STEP_Photos;
                break;
            case STEP_H:
                pbState.setProgress(20);
                rvMStatus.setVisibility(View.VISIBLE);
                tvSkip.setVisibility(View.GONE);
                rvHeight.setVisibility(View.GONE);
                tvStateTitle.setText("Marital Status\n");
                page_name = STEP_S;
                break;
            case STEP_R:
                pbState.setProgress(30);
                rvReligion.setVisibility(View.GONE);
                tvSkip.setVisibility(View.GONE);
                rvHeight.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Height\n");
                page_name = STEP_H;
                break;

            case STEP_C:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(40);
                rvReligion.setVisibility(View.VISIBLE);
                rvCaste.setVisibility(View.GONE);
                tvStateTitle.setText("Religion\n");
                page_name = STEP_R;
                break;

            case STEP_MT:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(50);
                rvMT.setVisibility(View.GONE);
                rvCaste.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Caste\n");
                page_name = STEP_C;
                break;
            case STEP_Country:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(60);
                rvCountry.setVisibility(View.GONE);
                rvMT.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Mother Tongue\n");
                page_name = STEP_MT;
                break;
            case STEP_State:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(70);
                rvState.setVisibility(View.GONE);
                rvCountry.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Country\n");
                page_name = STEP_Country;
                break;
            case STEP_City:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(80);
                rvCity.setVisibility(View.GONE);
                rvState.setVisibility(View.VISIBLE);
                tvStateTitle.setText("State\n");
                page_name = STEP_State;
                break;
            case STEP_EDU:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(90);
                rvEducation.setVisibility(View.GONE);
                rvCity.setVisibility(View.VISIBLE);
                tvStateTitle.setText("City\n");
                page_name = STEP_City;
                break;
            case STEP_EMP:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(100);
                rvEmployeedIn.setVisibility(View.GONE);
                rvEducation.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Highest Education\n");
                page_name = STEP_EDU;
                break;
            case STEP_OCC:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(110);
                rvOccupation.setVisibility(View.GONE);
                rvEmployeedIn.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Working Sector\n");
                page_name = STEP_EMP;
                break;
            case STEP_INCOME:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(120);
                rvIncome.setVisibility(View.GONE);
                rvOccupation.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Profession\n");
                page_name = STEP_OCC;
                break;
            case STEP_SkinTone:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(130);
                rvSkinTone.setVisibility(View.GONE);
                rvIncome.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Annual Income\n");
                page_name = STEP_INCOME;
                break;
            case STEP_BodyType:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(140);
                rvBodyType.setVisibility(View.GONE);
                rvSkinTone.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Skin Color Tone\n");
                page_name = STEP_SkinTone;
                break;
            case STEP_Weight:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(150);
                rvWeight.setVisibility(View.GONE);
                rvBodyType.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Body Type\n");
                page_name = STEP_BodyType;
                break;
            case STEP_EatingH:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(160);
                rvEatingH.setVisibility(View.GONE);
                rvWeight.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Weight\n");
                page_name = STEP_Weight;
                break;
            case STEP_Smoking:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(170);
                rvSmoking.setVisibility(View.GONE);
                rvEatingH.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Eating Habit\n");
                page_name = STEP_EatingH;
                break;
            case STEP_Drinking:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(180);
                rvDrinking.setVisibility(View.GONE);
                rvSmoking.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Smoking\n");
                page_name = STEP_Smoking;
                break;
            case STEP_ABOUT:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(190);
                llAbout.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                rvDrinking.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Drinking\n");
                page_name = STEP_Drinking;
                break;
            case STEP_Hobby:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(200);
                llHobby.setVisibility(View.GONE);
                llAbout.setVisibility(View.VISIBLE);
                tvStateTitle.setText("About\n");
                page_name = STEP_ABOUT;
                break;
            case STEP_Horoscope:
                tvSkip.setVisibility(View.VISIBLE);
                editText.setVisibility(View.GONE);
                pbState.setProgress(210);
                rvHoroscope.setVisibility(View.GONE);
                llHobby.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Hobbies and Interests\n");
                page_name = STEP_Hobby;
                break;
            case STEP_Star:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(220);
                rvStar.setVisibility(View.GONE);
                rvHoroscope.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Horoscope Belief\n");
                page_name = STEP_Horoscope;
                break;
            case STEP_MoonSign:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(230);
                rvMoonSign.setVisibility(View.GONE);
                rvStar.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Star\n");
                page_name = STEP_Star;
                break;
            case STEP_Mangalik:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(240);
                rvMangalik.setVisibility(View.GONE);
                rvMoonSign.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Moonsign (Raas)\n");
                page_name = STEP_MoonSign;
                break;

            case STEP_Blood:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(240);
                rvBlood.setVisibility(View.GONE);
                rvMangalik.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Manglik\n");
                page_name = STEP_Mangalik;
                break;

            case STEP_Last:
                tvSkip.setVisibility(View.VISIBLE);
                pbState.setProgress(250);
                llLastStep.setVisibility(View.GONE);
                rvBlood.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Blood Group\n");
                page_name = STEP_Blood;
                break;

            case STEP_FS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(260);
                editText.setVisibility(View.GONE);
                rvFamilyStatus.setVisibility(View.GONE);
                llLastStep.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Birth Details & Other Info\n");
                page_name = STEP_Last;
                break;

            case STEP_FT:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(270);
                rvFamilyType.setVisibility(View.GONE);
                rvFamilyStatus.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Status\n");
                page_name = STEP_FS;
                break;

            case STEP_FAMILYDETAILS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(280);
                llFamilyStep.setVisibility(View.GONE);
                rvFamilyType.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Type\n");
                page_name = STEP_FT;
                break;
            case STEP_NOB:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(290);
                editText.setVisibility(View.GONE);
                rvNoOfBrother.setVisibility(View.GONE);
                llFamilyStep.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Family Details\n");
                page_name = STEP_FAMILYDETAILS;
                break;
            case STEP_NOBM:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(300);
                rvNoOfBrotherMarried.setVisibility(View.GONE);
                rvNoOfBrother.setVisibility(View.VISIBLE);
                tvStateTitle.setText("No. of Brother(s)\n");
                page_name = STEP_NOB;
                break;

            case STEP_NOS:
                tvSkip.setVisibility(View.GONE);
                pbState.setProgress(310);
                rvNoOfSister.setVisibility(View.GONE);
                rvNoOfBrotherMarried.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Brother(s) Married\n");
                page_name = STEP_NOBM;
                break;
            case STEP_NOSM:
                tvSkip.setVisibility(View.GONE);
                tvStateTitle.setText("No. of Sister(s)\n");
                rvNoOfSister.setVisibility(View.VISIBLE);
                rvNoOfSisterMarried.setVisibility(View.GONE);
                page_name = STEP_NOS;
                break;

            case STEP_FAMILYABOUT:
                tvSkip.setVisibility(View.GONE);
                editText.setVisibility(View.VISIBLE);
                lay_state.setVisibility(View.VISIBLE);
                llFamilyAbout.setVisibility(View.GONE);
                rvNoOfSisterMarried.setVisibility(View.VISIBLE);
                tvStateTitle.setText("Sister(s) Married\n");
                page_name = STEP_NOSM;

                break;
            case STEP_Login:
                tvSkip.setVisibility(View.VISIBLE);
                lay_state.setVisibility(View.VISIBLE);
                rvNoOfSisterMarried.setVisibility(View.GONE);
                lay_second.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                llFamilyAbout.setVisibility(View.VISIBLE);
                tvStateTitle.setText("About Family\n");
                page_name = STEP_FAMILYABOUT;

        }
    }


    public void OpenLoginScreen(View view) {

        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("ragistered_id", ragister_id);
        startActivity(i);

    }



    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA);

        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA,
                ACCESS_NETWORK_STATE}, PERMISSION_REQUEST_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean cameraAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                }
                break;
        }
    }

    //TODO image selection & capture related code
    private void fromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2);
    }

    private void fromCamera() {
        try {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(getApplicationContext(), getPackageName() + ".fileprovider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, 1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File createImageFile() throws IOException {
        File image = null;
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == Activity.RESULT_OK) {
//            image_map.put("id", ragister_id);
//            if (requestCode == 1) {
//                resultUri = Uri.fromFile(new File(mCurrentPhotoPath));
//                org_path = resultUri.getPath();
//                cropImage(resultUri);
//
//            } else if (requestCode == 2) {
//                resultUri = data.getData();
//                org_path = Common.getPath(this, resultUri);
//
//                cropImage(resultUri);
//
//            } else if (requestCode == 3) {
//                resultUri = UCrop.getOutput(data);
//                crop_path = resultUri.getPath();
//
//                final InputStream imageStream;
//                try {
//                    imageStream = getContentResolver().openInputStream(resultUri);
//                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                    isImageSelect = true;
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                Bitmap bitmap = null;
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if (image_id == 1) {
//                    imag_one_seleted = true;
//                    img_one.setImageBitmap(bitmap);
//                    uploadFileToServer();
//                } else if (image_id == 2) {
//                    img_two.setImageBitmap(bitmap);
//                    uploadOthersFileToServer();
//                } else if (image_id == 3) {
//                    img_three.setImageBitmap(bitmap);
//                    uploadOthersFileToServer();
//                } else if (image_id == 4) {
//                    img_four.setImageBitmap(bitmap);
//                    uploadOthersFileToServer();
//                }
//            } else {
//
//            }
//        }

        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            openFileChooser();
        } else if (requestCode == CROP_PIC) {
            if (resultCode == Activity.RESULT_OK) {
                cropUri = UCrop.getOutput(data);
                cropFilePath = cropUri.getPath();

                AppDebugLog.print("cropUri.path : " + cropUri.getPath());
                compressedFile = common.getCompressedImageFile(this, new File(cropUri.getPath()));

                if (image_id == 1) {
                    imag_one_seleted = true;
                    Picasso.get().load(cropUri).error(R.drawable.placeholder).placeholder(
                            R.drawable.placeholder).into(img_one);
                    uploadFileToServer();
                } else if (image_id == 2) {
                    Picasso.get().load(cropUri).error(R.drawable.placeholder).placeholder(
                            R.drawable.placeholder).into(img_two);
                    uploadOthersFileToServer();
                } else if (image_id == 3) {
                    Picasso.get().load(cropUri).error(R.drawable.placeholder).placeholder(
                            R.drawable.placeholder).into(img_three);
                    uploadOthersFileToServer();
                } else if (image_id == 4) {
                    Picasso.get().load(cropUri).error(R.drawable.placeholder).placeholder(
                            R.drawable.placeholder).into(img_four);
                    uploadOthersFileToServer();
                }
                //show image in image view


                isImageSelect = true;

            }
        } else {
//            easyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
//                @Override
//                public void onMediaFilesPicked(MediaFile[] mediaFiles, MediaSource mediaSource) {
//                    for (MediaFile mediaFile : mediaFiles) {
//                        AppDebugLog.print("file : " + mediaFile.getFile().getAbsolutePath());
//                        switch (mediaSource) {
//                            case DOCUMENTS:
//                            case CAMERA_IMAGE:
//                            case GALLERY:
//                                originalFile = mediaFile.getFile();
//                                originalFilePath = mediaFile.getFile().getPath();
//                                cropImage(mediaFile.getFile());
//                                break;
//
//                            default:
//                                cropImage(mediaFile.getFile());
//                        }
//                    }
//                }
//
//                @Override
//                public void onImagePickerError(Throwable error, MediaSource source) {
//                    super.onImagePickerError(error, source);
//                }
//            }, llProfileCreate);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private void openFileChooser() {
        requestPermission();
    }

    private void cropImage(File attachmentFile) {
        Uri uri = Uri.fromFile(attachmentFile);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageExtension = Common.getExtensionFromPath(Common.getPath(RegisterFirstActivity.this, uri));
        AppDebugLog.print("imageExtension : " + imageExtension);

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), timeStamp + imageExtension)));
        uCrop.withAspectRatio(2, 3);
        uCrop.withMaxResultSize(512, 620);
        UCrop.Options options = new UCrop.Options();

        options.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        options.setToolbarWidgetColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

        uCrop.withOptions(options);
        uCrop.start(this, CROP_PIC);


    }


    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private ProgressDialog pd;

    private void uploadOthersFileToServer() {
        if (!ConnectionDetector.isConnectingToInternet(getContext())) {
            common.showToast(getString(R.string.err_msg_no_intenet_connection),swipe);
            return;
        }

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        // setting progress bar to zero
        pd = new ProgressDialog(this);
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), ragister_id);
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Map<String, RequestBody> params = new HashMap<>();
        params.put("member_id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Call<JsonObject> call = null;
        if (image_id == 2 || image_id == 3 || image_id == 4 || image_id == 5 || image_id == 6) {
            String org_param = "profile_photo" + image_id + "_org";
            String crop_param = "profile_photo" + image_id + "_crop";
            AppDebugLog.print("org_param: " + org_param + "  crop_param   " + crop_param);

            File sourceFile_crop = new File(crop_path);
            ProgressRequestBody cropFileBody = new ProgressRequestBody(sourceFile_crop, getMimeType(crop_path), this);
            MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData(crop_param, sourceFile_crop.getName().replaceAll("[^a-zA-Z0-9.]", ""), cropFileBody);

            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(this, new File(Common.getPath(this, resultUri)));
            AppDebugLog.print("profileOriginalImageCompressedFile name : " + profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData(org_param, profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);

            call = appApiService.uploadMyPhotoWithCrop(cropFilePart, originalFilePart, params);
        }

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }

                JsonObject data = response.body();
                AppDebugLog.print("response in submitData : " + response.body());

                if (data != null) {
                    // common.showToast(data.get("errmessage").getAsString(),swipe);
                    if (data.get("status").getAsString().equals("success")) {
                        common.showToast(getString(R.string.upload_photo),swipe);
                    }
                } else {
                    common.showToast(getString(R.string.err_msg_try_again_later),swipe);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                common.showToast(getString(R.string.err_msg_something_went_wrong),swipe);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }

    private void uploadFileToServer() {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast( getString(R.string.err_msg_no_intenet_connection), llProfileCreate);
            return;
        }

        common.hideProgressRelativeLayout(progressBar);

        // setting progress bar to zero
        pd = new ProgressDialog(RegisterFirstActivity.this);
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        try {
            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(this, new File(Common.getPath(this, cropUri)));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(originalFilePath), this);
            MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData("profil_photo_org", profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);

            RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), ragister_id);
            RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
            RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

            Retrofit retrofit = RetrofitClient.getClient();
            AppApiService appApiService = retrofit.create(AppApiService.class);

            Call<JsonObject> call = null;
                File crop = new File(cropFilePath);
                ProgressRequestBody cropFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(cropFilePath), this);
                MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData("profil_photo", crop.getName().replaceAll("[^a-zA-Z0-9.]", ""), cropFileBody);

                Map<String, RequestBody> params = new HashMap<>();
                params.put("id", partParam1);
                params.put("user_agent", partParam2);
                params.put("csrf_new_matrimonial", partParam3);

                long fileSizeMB = common.getFIleSizeInMB(profileOriginalImageCompressedFile);
                if (fileSizeMB > MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD) {
                    common.showToast( "Image size more than " + MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD + " MB", llProfileCreate);
                } else {
                    call = appApiService.uploadPhoto(cropFilePart, originalFilePart, params);
                }




            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }

                    JsonObject data = response.body();
                    AppDebugLog.print("response in submitData : " + response.body());

                    if (data != null) {
                        // common.showToast(data.get("errmessage").getAsString());
                        if (data.get("status").getAsString().equals("success")) {
                            isImageSelect = true;
                            common.showToast(getString(R.string.upload_photo),swipe);
                        }
                    } else {
                        common.showToast(getString(R.string.err_msg_try_again_later),swipe);
    //
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    common.showToast(getString(R.string.err_msg_something_went_wrong),swipe);
                    if (pd != null && pd.isShowing()) {
                        pd.dismiss();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemsSelected(MultiSpinnerSearch singleSpinnerSearch) {
        Common.hideSoftKeyboard(this);
        if (singleSpinnerSearch == null) return;
        if (singleSpinnerSearch.getSelectedIdsInString() == null) return;

        switch (singleSpinnerSearch.getId()) {
            case R.id.spin_lang:
                lang_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
            case R.id.spin_edu:
                edu_id = singleSpinnerSearch.getSelectedIdsInString();
                break;
        }
    }

    @Override
    public void onItemsSelected(SingleSpinnerSearch singleSpinnerSearch, KeyPairBoolData item) {
        Common.hideSoftKeyboard(this);
        if (item == null) return;
        if (item.getId() == null) return;

        switch (singleSpinnerSearch.getId()) {

            case R.id.spin_reference:
                reference_id = item.getId();// reference_map.get(spin_reference.getSelectedItem().toString());
                break;
//            case R.id.spin_lang:
//                lang_id = item.getId();
//                break;

            case R.id.spin_designation:
                desig_id = item.getId();// desig_map.get(spin_designation.getSelectedItem().toString());
                break;

            case R.id.spin_residence:
                resi_id = item.getId();// resi_map.get(spin_residence.getSelectedItem().toString());
                break;

        }
    }


    @Override
    public void onSuccess(JsonObject object, @Nullable String requestTag) {
        common.hideProgressRelativeLayout(progressBar);
        try {
//            JSONObject object = new JSONObject(String.valueOf(data));
            if (object.get("status").equals("success")) {
                switch (requestTag) {

                    case BASIC_DETAIL:
                        ragister_id = object.get("id").getAsString();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("profileby", created_id);
                        params.put("member_id", ragister_id);
                        params.put("phone", et_phone.getText().toString());
                        params.put("time_to_call", et_time_call.getText().toString());
                        params.put("reference", getValidId(reference_id));
                        submitData(BASIC_DETAIL, params);
                        break;

                    case STEP_Photos:
                        ragister_id = object.get("id").getAsString();
                        lay_photos.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        lay_state.setVisibility(View.VISIBLE);
                        rvMStatus.setVisibility(View.VISIBLE);
                        tvStateTitle.setText("Marital Status\n");
                        page_name = STEP_S;
                        break;

                    case STEP_S:
                        tvStateTitle.setText("Height\n");
                        rvMStatus.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        rvHeight.setVisibility(View.VISIBLE);
                        page_name = STEP_H;
                        break;

                    case STEP_H:
                        tvStateTitle.setText("Religion\n");
                        rvHeight.setVisibility(View.GONE);
                        rvReligion.setVisibility(View.VISIBLE);
                        //                            tvSkip.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_R;
                        break;


                    case STEP_R:
                        tvStateTitle.setText("Caste\n");
                        rvReligion.setVisibility(View.GONE);
                        rvCaste.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_C;
                        break;

                    case STEP_C:
                        tvStateTitle.setText("Mother Tongue\n");
                        rvCaste.setVisibility(View.GONE);
                        rvMT.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_MT;
                        break;

                    case STEP_MT:
                        tvStateTitle.setText("Country\n");
                        rvCountry.setVisibility(View.VISIBLE);
                        rvMT.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Country;
                        break;

                    case STEP_Country:
                        tvStateTitle.setText("State\n");
                        rvState.setVisibility(View.VISIBLE);
                        rvCountry.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_State;
                        break;

                    case STEP_State:
                        tvStateTitle.setText("City\n");
                        rvCity.setVisibility(View.VISIBLE);
                        rvState.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_City;
                        break;

                    case STEP_City:
                        //                            tvStateHeader.setText("A Few More Details");
                        tvStateTitle.setText("Highest Education\n");
                        rvCity.setVisibility(View.GONE);
                        rvEducation.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_EDU;
                        break;

                    //                        case STEP_H:
                    //                            tvStateTitle.setText("Highest Education\n");
                    //                            rvEducation.setVisibility(View.VISIBLE);
                    //                            rvHeight.setVisibility(View.GONE);
                    //                            page_name = STEP_EDU;
                    //                            break;

                    //                        case STEP_SkinTone:
                    //                            tvStateTitle.setText("Highest Education\n");
                    //                            rvEducation.setVisibility(View.VISIBLE);
                    //                            rvSkinTone.setVisibility(View.GONE);
                    //                            page_name = STEP_EDU;
                    //                            break;

                    case STEP_EDU:
                        tvStateTitle.setText("Working Sector\n");
                        rvEmployeedIn.setVisibility(View.VISIBLE);
                        rvEducation.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_EMP;
                        break;

                    case STEP_EMP:
                        tvSkip.setVisibility(View.GONE);
                        tvStateTitle.setText("Profession\n");
                        rvOccupation.setVisibility(View.VISIBLE);
                        rvEmployeedIn.setVisibility(View.GONE);
                        page_name = STEP_OCC;
                        break;

                    case STEP_OCC:
                        tvSkip.setVisibility(View.GONE);
                        tvStateTitle.setText("Annual Income\n");
                        rvIncome.setVisibility(View.VISIBLE);
                        rvOccupation.setVisibility(View.GONE);
                        page_name = STEP_INCOME;
                        break;

                    case STEP_INCOME:
                        tvStateTitle.setText("Skin Color Tone\n");
                        rvSkinTone.setVisibility(View.VISIBLE);
                        rvIncome.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_SkinTone;
                        break;

                    case STEP_SkinTone:
                        tvStateTitle.setText("Body Type\n");
                        rvSkinTone.setVisibility(View.GONE);
                        rvBodyType.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_BodyType;
                        break;

                    case STEP_BodyType:
                        tvStateTitle.setText("Weight\n");
                        rvBodyType.setVisibility(View.GONE);
                        rvWeight.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Weight;
                        break;

                    case STEP_Weight:
                        tvStateTitle.setText("Eating Habit\n");
                        rvWeight.setVisibility(View.GONE);
                        rvEatingH.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_EatingH;
                        break;

                    case STEP_EatingH:
                        tvStateTitle.setText("Smoking\n");
                        rvEatingH.setVisibility(View.GONE);
                        rvSmoking.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Smoking;
                        break;

                    case STEP_Smoking:
                        tvSkip.setVisibility(View.GONE);
                        tvStateTitle.setText("Drinking\n");
                        rvSmoking.setVisibility(View.GONE);
                        rvDrinking.setVisibility(View.VISIBLE);
                        page_name = STEP_Drinking;
                        break;

                    case STEP_Drinking:
                        tvSkip.setVisibility(View.VISIBLE);
                        tvStateTitle.setText("About\n");
                        rvDrinking.setVisibility(View.GONE);
                        editText.setVisibility(View.GONE);
                        llAbout.setVisibility(View.VISIBLE);
                        page_name = STEP_ABOUT;
                        break;

                    case STEP_ABOUT:
                        tvSkip.setVisibility(View.VISIBLE);
                        tvStateTitle.setText("Hobbies and Interests\n");
                        llAbout.setVisibility(View.GONE);
                        llHobby.setVisibility(View.VISIBLE);
                        page_name = STEP_Hobby;
                        break;

                    case STEP_Hobby:
                        tvStateTitle.setText("Horoscope Belief\n");
                        llHobby.setVisibility(View.GONE);
                        rvHoroscope.setVisibility(View.VISIBLE);
                        editText.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Horoscope;
                        break;

                    case STEP_Horoscope:
                        tvStateTitle.setText("Star\n");
                        rvHoroscope.setVisibility(View.GONE);
                        rvStar.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Star;
                        break;

                    case STEP_Star:
                        tvStateTitle.setText("Moonsign (Raas)\n");
                        rvStar.setVisibility(View.GONE);
                        rvMoonSign.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_MoonSign;
                        break;

                    case STEP_MoonSign:
                        tvStateTitle.setText("Manglik\n");
                        rvMangalik.setVisibility(View.VISIBLE);
                        rvMoonSign.setVisibility(View.GONE);
                        tvSkip.setVisibility(View.GONE);
                        page_name = STEP_Mangalik;

                        break;

                    case STEP_Mangalik:
                        rvMangalik.setVisibility(View.GONE);
                        tvStateTitle.setText("Blood Group\n");
                        rvBlood.setVisibility(View.VISIBLE);
                        tvSkip.setVisibility(View.VISIBLE);
                        page_name = STEP_Blood;
                        break;

                    case STEP_Blood:
                        tvSkip.setVisibility(View.GONE);
                        rvBlood.setVisibility(View.GONE);
                        editText.setVisibility(View.GONE);
                        tvStateTitle.setText("Birth & Other Info\n");
                        llLastStep.setVisibility(View.VISIBLE);
                        page_name = STEP_Last;
                        pbState.setProgress(240);
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(boolean isError, @Nullable String requestTag) {
        common.hideProgressRelativeLayout(progressBar);
    }
}
