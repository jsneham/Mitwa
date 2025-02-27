package com.matriapp.mobile.fragments;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.matriapp.mobile.R;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.retrofit.AppApiService;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.retrofit.RetrofitClient;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pl.aprilapps.easyphotopicker.ChooserType;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class MyIdProofFragment extends Fragment implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {
    private Common common;
    private SessionManager session;
    private Context context;
    private ProgressDialog pd;
    private RelativeLayout loader;
    private TextView tv_cancel, tv_gallary, tv_camera;
    private CardView layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;
    private int image_id;
    private Uri resultUri;
    private long totalSize = 0;
    private boolean isfirst = true;
    private ImageView img_one, img_two, img_three, img_four,
            img_plus_one, img_plus_two, img_plus_three, img_plus_four, img_plus_five, img_plus_six,
            img_edit_one, img_edit_two, img_edit_three, img_edit_four, img_edit_five, img_edit_six, img_cover, img_edit_cover, img_plus_cover, img_id_proof,img_edit_proof,img_edit_horo,img_horoscope;
    private int placeHolder, photoProtectPlaceHolder, fourPhoto= R.drawable.add_image_shape_cover;

    private int coverImageHeight = 0;
    private int coverImageWidth = 0;
    private int smallImageW = 0;
    private int smallImageH = 0;
    private int bigImageW = 0;
    private int bigImageH = 0;

    private EasyImage easyImage = null;
    private final int C_PERMISSION_REQUEST_CODE = 122;
    private final int G_PERMISSION_REQUEST_CODE = 123;
    private File compressedFile = null;
    private File originalFile = null;
    private String originalFilePath = "", cropFilePath = "";
    private Uri cropUri;
    private final int CROP_PIC = 3;
    private String cover_photo, horoscope_photo, id_proof, photo1,photo2,photo3,photo4;
    private CoordinatorLayout llView;

    public MyIdProofFragment() {
        // Required empty public constructor
    }

    public static MyIdProofFragment newInstance(String param1, String param2) {
        MyIdProofFragment fragment = new MyIdProofFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_photo_id_proof, container, false);

        context = getActivity();
        session = new SessionManager(context);
        common = new Common(getActivity());

        if (session.getLoginData(SessionManager.KEY_GENDER).equals("Female")) {
            placeHolder = R.drawable.add_image_shape;
        } else if (session.getLoginData(SessionManager.KEY_GENDER).equals("Male")) {
            placeHolder = R.drawable.add_image_shape;
        }

//        setUpEasyImage();

        int deviceWidth = Common.getDisplayWidth(getActivity());
        int smallWidth = deviceWidth / 3;
        int bigWidth = (int) ((deviceWidth / 3) * 2);
        coverImageHeight = Common.convertDpToPixels(200, getActivity());
        coverImageWidth = deviceWidth - Common.convertDpToPixels(10, getActivity());
        smallImageW = smallWidth; //Common.convertDpToPixels(smallWidth, getActivity());
        bigImageW = bigWidth; //Common.convertDpToPixels(bigWidth, getActivity());
        smallImageH = (int) (smallImageW * 1.210);
        bigImageH = (int) (bigImageW * 1.210);

        llView = view.findViewById(R.id.llView);
        loader = view.findViewById(R.id.loader);
        img_cover = view.findViewById(R.id.img_cover);
        img_edit_cover = view.findViewById(R.id.img_edit_cover);
        img_edit_cover.setOnClickListener(this);
        img_plus_cover = view.findViewById(R.id.img_plus_cover);
        img_cover.setOnClickListener(this);

        img_one = view.findViewById(R.id.img_one);
        img_two = view.findViewById(R.id.img_two);
        img_three = view.findViewById(R.id.img_three);
        img_four = view.findViewById(R.id.img_four);
//        img_five = view.findViewById(R.id.img_five);
//        img_six = view.findViewById(R.id.img_six);
        img_id_proof = view.findViewById(R.id.img_id_proof);
        img_id_proof.setOnClickListener(this);
        img_edit_proof = view.findViewById(R.id.img_edit_proof);
        img_edit_proof.setOnClickListener(this);

        img_horoscope = view.findViewById(R.id.img_horoscope);
        img_horoscope.setOnClickListener(this);
        img_edit_horo = view.findViewById(R.id.img_edit_horo);
        img_edit_horo.setOnClickListener(this);


        img_plus_one = view.findViewById(R.id.img_plus_one);
        img_one.setOnClickListener(this);
        img_plus_two = view.findViewById(R.id.img_plus_two);
        img_two.setOnClickListener(this);
        img_plus_three = view.findViewById(R.id.img_plus_three);
        img_three.setOnClickListener(this);
        img_plus_four = view.findViewById(R.id.img_plus_four);
        img_four.setOnClickListener(this);
        img_plus_five = view.findViewById(R.id.img_plus_five);
        img_plus_five.setOnClickListener(this);
        img_plus_six = view.findViewById(R.id.img_plus_six);
        img_plus_six.setOnClickListener(this);

        img_edit_one = view.findViewById(R.id.img_edit_one);
        img_edit_one.setOnClickListener(this);
        img_edit_two = view.findViewById(R.id.img_edit_two);
        img_edit_two.setOnClickListener(this);
        img_edit_three = view.findViewById(R.id.img_edit_three);
        img_edit_three.setOnClickListener(this);
        img_edit_four = view.findViewById(R.id.img_edit_four);
        img_edit_four.setOnClickListener(this);
        img_edit_five = view.findViewById(R.id.img_edit_five);
        img_edit_five.setOnClickListener(this);
        img_edit_six = view.findViewById(R.id.img_edit_six);
        img_edit_six.setOnClickListener(this);

        layoutBottomSheet = view.findViewById(R.id.bottom_sheet);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_gallary = view.findViewById(R.id.tv_gallary);
        tv_camera = view.findViewById(R.id.tv_camera);
        tv_gallary.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
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
        getMyProfile();
        return view;
    }

    private void setUpEasyImage() {
        easyImage = new EasyImage.Builder(getActivity())
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
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                easyImage.openGallery(this);
                break;

//            case 200:
//                easyImage.openCameraForImage(this);
//                break;
        }
    }

    private void openFileChooser(int pickFor) {
//        requestPermission(pickFor);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        ) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            pickImage(100);
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{WRITE_EXTERNAL_STORAGE, CAMERA,
                    ACCESS_NETWORK_STATE}, C_PERMISSION_REQUEST_CODE);

        }

    }

    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED});
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO});
        }
        else {
            requestPermissions.launch(new String[]{READ_EXTERNAL_STORAGE});
        }


    }
    ActivityResultLauncher<String[]> requestPermissions = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
        boolean hasReadMediaVisualUserSelectedPermission = results.getOrDefault("android.permission.READ_MEDIA_VISUAL_USER_SELECTED", false);
        boolean hasReadMediaVideoPermission = results.getOrDefault("android.permission.READ_MEDIA_VIDEO", false);
        boolean hasReadMediaImagesPermission = results.getOrDefault("android.permission.READ_MEDIA_IMAGES", false);
        boolean hasReadExternalStorageImagesPermission = results.getOrDefault("android.permission.READ_EXTERNAL_STORAGE", false);

        if (hasReadMediaVideoPermission && hasReadMediaImagesPermission && hasReadMediaVisualUserSelectedPermission) {
            openGallery(200);
        } else if (hasReadMediaVideoPermission || hasReadMediaImagesPermission) {
            openGallery(200);
        }
        else if(hasReadExternalStorageImagesPermission){

            openGallery(200);
        }
        else {
            showPermissionSettingsDialog();
        }
    });

    private void showPermissionSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Permission Required");
        builder.setMessage("This app requires certain permissions to function properly. Please go to Settings to grant the necessary permissions.");
        builder.setPositiveButton("Go to Settings", (dialog, which) -> openAppSettings());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void openGallery(int code) {
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.setType("image/* video/*");
//        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//        startActivityForResult(intent, code);

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // If you also want to include videos, uncomment the following line
        // galleryIntent.setType("image/* video/*");
        startActivityForResult(intent, code);
    }

    private void getMyProfile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));

        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                session.setUserData(SessionManager.TOKEN, object.getString("tocken"));
                if (object.getString("status").equals("success")) {
                    JSONObject data = object.getJSONObject("data");

                    photo1 = data.getString("photo1");
                    photo2 = data.getString("photo2");
                    photo3 = data.getString("photo3");
                    photo4 = data.getString("photo4");
                    String photo5 = data.getString("photo5");
                    String photo6 = data.getString("photo6");
                    cover_photo = data.getString("cover_photo");
                    horoscope_photo = data.getString("horoscope_photo");
                    id_proof = data.getString("id_proof");
                    if (isValidImage(horoscope_photo)) {
                        Picasso.get().load(horoscope_photo).resize(coverImageWidth, coverImageHeight).centerInside().placeholder(R.drawable.add_image_shape_cover).error(R.drawable.add_image_shape_cover).into(img_horoscope);
                        //img_plus_proof.setVisibility(View.GONE);
                        img_edit_horo.setVisibility(View.VISIBLE);
                    } else {
//                    img_plus_proof.setVisibility(View.VISIBLE);
                        img_edit_horo.setVisibility(View.GONE);
                        img_horoscope.setImageResource(R.drawable.add_image_shape_cover);
                    }

                    if (isValidImage(id_proof)) {
                        Picasso.get().load(id_proof).resize(coverImageWidth, coverImageHeight).centerInside().placeholder(R.drawable.add_image_shape_cover).error(R.drawable.add_image_shape_cover).into(img_id_proof);
                        //img_plus_proof.setVisibility(View.GONE);
                        img_edit_proof.setVisibility(View.VISIBLE);
                    } else {
//                    img_plus_proof.setVisibility(View.VISIBLE);
                        img_edit_proof.setVisibility(View.GONE);
                        img_id_proof.setImageResource(R.drawable.add_image_shape_cover);
                    }
                    if (isValidImage(cover_photo)) {
                        Picasso.get().load(cover_photo).resize(coverImageWidth, coverImageHeight).centerInside().placeholder(R.drawable.ic_coverphoto_notuploaded).into(img_cover);
//                        img_plus_cover.setVisibility(View.GONE);
                        img_edit_cover.setVisibility(View.VISIBLE);
                    } else {
//                        img_plus_cover.setVisibility(View.VISIBLE);
                        img_edit_cover.setVisibility(View.VISIBLE);
                        img_cover.setImageResource(R.drawable.ic_coverphoto_notuploaded);
                    }

                    if (isValidImage(photo1)) {
                        Picasso.get().load(photo1).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(fourPhoto).error(fourPhoto).into(img_one);
                        img_plus_one.setVisibility(View.GONE);
                        img_edit_one.setVisibility(View.VISIBLE);
                    } else {
//                        img_plus_one.setVisibility(View.VISIBLE);
                        img_edit_one.setVisibility(View.GONE);
                        img_one.setImageResource(fourPhoto);
                        //img_one.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    }
                    if (isValidImage(photo2)) {
                        Picasso.get().load(photo2).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(fourPhoto).error(fourPhoto).into(img_two);
                        img_plus_two.setVisibility(View.GONE);
                        img_edit_two.setVisibility(View.VISIBLE);
                    } else {
//                        img_plus_two.setVisibility(View.VISIBLE);
                        img_edit_two.setVisibility(View.GONE);
                        img_two.setImageResource(fourPhoto);
                        //img_two.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    }
                    if (isValidImage(photo3)) {
                        Picasso.get().load(photo3).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(fourPhoto).error(fourPhoto).into(img_three);
                        img_plus_three.setVisibility(View.GONE);
                        img_edit_three.setVisibility(View.VISIBLE);
                    } else {
//                        img_plus_three.setVisibility(View.VISIBLE);
                        img_edit_three.setVisibility(View.GONE);
                        img_three.setImageResource(fourPhoto);
                        //img_three.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    }
                    if (isValidImage(photo4)) {
                        Picasso.get().load(photo4).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(fourPhoto).error(fourPhoto).into(img_four);
                        img_plus_four.setVisibility(View.GONE);
                        img_edit_four.setVisibility(View.VISIBLE);
                    } else {
//                        img_plus_four.setVisibility(View.VISIBLE);
                        img_edit_four.setVisibility(View.GONE);
                        img_four.setImageResource(fourPhoto);
                        //img_four.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                    }
//                    if (isValidImage(photo5)) {
//                        Picasso.get().load(photo5).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(placeHolder).error(placeHolder).into(img_five);
//                        img_plus_five.setVisibility(View.GONE);
//                        img_edit_five.setVisibility(View.VISIBLE);
//                    } else {
//                        img_plus_five.setVisibility(View.VISIBLE);
//                        img_edit_five.setVisibility(View.GONE);
//                        img_five.setImageResource(placeHolder);
//                        //img_five.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
//                    }
//                    if (isValidImage(photo6)) {
//                        Picasso.get().load(photo6).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).placeholder(placeHolder).error(placeHolder).into(img_six);
//                        img_plus_six.setVisibility(View.GONE);
//                        img_edit_six.setVisibility(View.VISIBLE);
//                    } else {
//                        img_plus_six.setVisibility(View.VISIBLE);
//                        img_edit_six.setVisibility(View.GONE);
//                        img_six.setImageResource(placeHolder);
//                        //img_six.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_IN);
//                    }

                }
                isfirst = false;

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

    private boolean isValidImage(String url) {
        return !url.equals("") && !url.equals("null");
    }

    private void editClick(final int img_id, View v) {
        image_id = img_id;
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.photo_edit_menu);
        if (img_id == 1 || img_id == 0 || img_id==7 ||img_id==8)
            popup.getMenu().getItem(2).setVisible(false);
        if(img_id==8 || img_id==1)
            popup.getMenu().getItem(1).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                    return true;
                case R.id.delete:
                    deletePhotoAlert();
                    return true;
                case R.id.profile:
                    setProfilePhotoApi();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }

    private void deletePhotoAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage("Are you sure you want to delete photo?");
        alert.setNegativeButton("No", null);
        alert.setPositiveButton("Yes", (dialogInterface, i) -> {
            if (image_id == 0 || image_id == 1 || image_id == 2 || image_id == 3 || image_id == 4) {
                deletePhotoApi();
            } else if (image_id == 0) {
                deleteCoverPhotoApi();
            } else {
                deleteId_api();
            }

        });
        alert.show();
    }

    private void deleteCoverPhotoApi() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("user_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("delete_cover_photo", "delete");

        common.makePostRequest(AppConstants.delete_cover_photo, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);
                if (object.getString("status").equals("success")) {
                    getMyProfile();
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

    private void deletePhotoApi() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("photo_number", String.valueOf(image_id));
        param.put("delete_photo", "delete");

        common.makePostRequest(AppConstants.delete_photo, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);
                if (object.getString("status").equals("success")) {
                    getMyProfile();
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

    private void setProfilePhotoApi() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("photo_number", String.valueOf(image_id));
        param.put("set_profile", "set_profile");

        common.makePostRequest(AppConstants.set_profile_pic, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);
                if (object.getString("status").equals("success")) {
                    getMyProfile();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppDebugLog.print("resultCode : " + resultCode);
        AppDebugLog.print("requestCode : " + requestCode);
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            openFileChooser(200);
//        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            File mediaFile = convertBitmapToFile(imageBitmap);
            originalFile = mediaFile.getAbsoluteFile();
            originalFilePath = mediaFile.getAbsoluteFile().getAbsolutePath();
            if (image_id == 1 || image_id == 2 || image_id == 3 || image_id == 4) {
                cropImage(originalFile);
            }else{
                uploadFileToServer();
            }
        }
        else if (requestCode == 200 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    File mediaFile = createFileFromUri(uri);
                    originalFile = mediaFile.getAbsoluteFile();
                    originalFilePath = mediaFile.getAbsolutePath();
                    if (image_id == 1 || image_id == 2 || image_id == 3 || image_id == 4) {
                        cropImage(originalFile);
                    }else{
                        uploadFileToServer();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception appropriately
                }
            }
        }
        else if (requestCode == CROP_PIC) {
            AppDebugLog.print("resultCode in CROP_PIC: " + resultCode);
            AppDebugLog.print("requestCode  in CROP_PIC: " + requestCode);
            if (resultCode == Activity.RESULT_OK) {
                cropUri = UCrop.getOutput(data);
                cropFilePath = cropUri.getPath();

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cropUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                switch (image_id) {
                    case 1:
                        img_one.setImageBitmap(bitmap);
                        break;
                    case 2:
                        img_two.setImageBitmap(bitmap);
                        break;
                    case 3:
                        img_three.setImageBitmap(bitmap);
                        break;
                    case 4:
                        img_four.setImageBitmap(bitmap);
                        break;
//                    case 5:
//                        img_five.setImageBitmap(bitmap);
//                        break;
//                    case 6:
//                        img_six.setImageBitmap(bitmap);
//                        break;
                }
                uploadFileToServer();
            }
        } else {
//            easyImage.handleActivityResult(requestCode, resultCode, data, getActivity(), new DefaultCallback() {
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
//                                if (image_id == 1 || image_id == 2 || image_id == 3 || image_id == 4) {
//                                    cropImage(originalFile);
//                                }else{
//                                    uploadFileToServer();
//                                }
//                                break;
//                        }
//                    }
//                }
//
//                @Override
//                public void onImagePickerError(Throwable error, MediaSource source) {
//                    super.onImagePickerError(error, source);
//                }
//            });
        }
    }

    private void uploadFileToServer() {
        if (!ConnectionDetector.isConnectingToInternet(getActivity())) {
            common.showToast( getString(R.string.err_msg_no_intenet_connection), llView);
            return;
        }

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
        // setting progress bar to zero
        pd = new ProgressDialog(getActivity());
        pd.setTitle("Uploading...");
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setProgress(0);
        pd.setCancelable(false);
        pd.show();

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.KEY_USER_ID));
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Map<String, RequestBody> params = new HashMap<>();
        params.put("member_id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Call<JsonObject> call = null;
        if (image_id == 1 || image_id == 2 || image_id == 3 || image_id == 4) {
            String org_param = "profile_photo" + image_id + "_org";
            String crop_param = "profile_photo" + image_id + "_crop";
            File sourceFile_crop = new File(cropFilePath);
            File sourceFile = new File(originalFilePath);
            ProgressRequestBody cropFileBody = new ProgressRequestBody(sourceFile_crop, getMimeType(cropFilePath), this);
            MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData(crop_param, sourceFile_crop.getName().replaceAll("[^a-zA-Z0-9.]", ""), cropFileBody);

//            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), sourceFile);
            File profileOriginalImageCompressedFile = sourceFile;
            AppDebugLog.print("profileOriginalImageCompressedFile name : " + profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData(org_param, profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);
            call = appApiService.uploadMyPhotoWithCrop(cropFilePart, originalFilePart, params);



        }  else if (image_id == 0) {
            File sourceFile = new File(originalFilePath);
//            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), sourceFile);
            File profileOriginalImageCompressedFile = sourceFile;
            ProgressRequestBody orgFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part orgFilePart = MultipartBody.Part.createFormData("cover_photo", profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), orgFileBody);

            call = appApiService.uploadMyPhotoWithoutCrop(orgFilePart, params);
        }
        else if (image_id == 7) {
            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), new File(originalFilePath));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part originalFilePart = null;
            originalFilePart = MultipartBody.Part.createFormData("id_proof", profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);
            call = appApiService.uploadIdProof(originalFilePart, params);
        } else {
            File profileOriginalImageCompressedFile = Common.getCompressedImageFile(getActivity(), new File(originalFilePath));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part originalFilePart = null;
            originalFilePart = MultipartBody.Part.createFormData("horoscope_photo", profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);
            call = appApiService.uploadHoroscopePhoto(originalFilePart, params);
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
                    common.showToast(data.get("errmessage").getAsString(),llView);
                    if (data.get("status").getAsString().equals("success")) {
                        getMyProfile();
                    }
                } else {
                    common.showToast( getString(R.string.err_msg_try_again_later), llView);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                common.showToast( getString(R.string.err_msg_something_went_wrong), llView);
                if (pd != null && pd.isShowing()) {
                    pd.dismiss();
                }
            }
        });
    }




    // url = file path or whatever suitable URL you want.
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
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

    private void cropImage(File attachmentFile) {
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        Uri uri = Uri.fromFile(attachmentFile);

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageExtension = Common.getExtensionFromPath(Common.getPath(getActivity(), uri));
        AppDebugLog.print("imageExtension : " + imageExtension);

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), timeStamp + imageExtension)));
        uCrop.withAspectRatio(2, 3);
        uCrop.withMaxResultSize(720, 1080);
        UCrop.Options options = new UCrop.Options();

        options.setToolbarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
        options.setStatusBarColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        options.setToolbarWidgetColor(ContextCompat.getColor(getActivity(), R.color.white));
        options.setRootViewBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

        uCrop.withOptions(options);
        uCrop.start(context, this, CROP_PIC);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            case R.id.tv_camera:
                requestCameraPermission();
//                openFileChooser(200);
                break;
            case R.id.tv_gallary:
                requestGalleryPermission();
//                openFileChooser(100);
                break;
            case R.id.img_one:
                if (!isValidImage(photo1)) {
                    image_id = 1;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else showPhotoPreviewDialog(photo1);

                break;
            case R.id.img_two:
                if (!isValidImage(photo2)) {
                    image_id = 2;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else showPhotoPreviewDialog(photo2);

                break;
            case R.id.img_three:
                if (!isValidImage(photo3)) {
                    image_id = 3;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else showPhotoPreviewDialog(photo3);
                break;
            case R.id.img_four:
                if (!isValidImage(photo4)) {
                    image_id = 4;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else showPhotoPreviewDialog(photo4);
                break;
            case R.id.img_plus_five:
                image_id = 5;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_plus_six:
                image_id = 6;
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_cover:
                if (!isValidImage(cover_photo)) {
                    image_id = 0;
//                    openFileChooser(100);
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                else showPhotoPreviewDialog(cover_photo);
//                image_id = 0;

                //sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                break;
            case R.id.img_edit_cover:
                editClick(0, view);
                break;
            case R.id.img_edit_one:
                editClick(1, view);
                break;
            case R.id.img_edit_two:
                editClick(2, view);
                break;
            case R.id.img_edit_three:
                editClick(3, view);
                break;
            case R.id.img_edit_four:
                editClick(4, view);
                break;
            case R.id.img_edit_five:
                editClick(5, view);
                break;
            case R.id.img_edit_six:
                editClick(6, view);
                break;
            case R.id.img_id_proof:
                if (!isValidImage(id_proof)) {
                    image_id = 7;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else showPhotoPreviewDialog(id_proof);
                break;
            case R.id.img_edit_proof:
                editClick(7, view);
                break;
            case R.id.img_horoscope:
                if (!isValidImage(horoscope_photo)) {
                    image_id = 8;
                    sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else showPhotoPreviewDialog(horoscope_photo);
                break;
            case R.id.img_edit_horo:
                editClick(8, view);
                break;
        }

    }


    private void showPhotoPreviewDialog(String imageUri) {
        if (getContext() != null) {
//            Bitmap photoBitmap = BitmapFactory.decodeFile(imageUri);
            PhotoPreviewDialog dialog = new PhotoPreviewDialog(getContext(), imageUri);
            dialog.show();
        } else {
            // Handle the case where the fragment is not attached to the activity
        }
    }

    private void openPhotoOnPopUp(String imageUri) {
        Dialog builder = new Dialog(context);
        builder.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        builder.setContentView(getLayoutInflater().inflate(R.layout.image_layout
                , null));
        builder.show();


        ImageView imageView = builder.findViewById(R.id.imageView);
        Glide.with(context).load(imageUri).into(imageView);



        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });



    }

    private void deleteId_api() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("delete_id_proof_photo", "delete");

        common.makePostRequest(AppConstants.delete_id_proof_photo, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llView);
                if (object.getString("status").equals("success")) {
                    getMyProfile();
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

    private File convertBitmapToFile(Bitmap bitmap) {
        // Create a file in the Pictures directory
        File picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(picturesDir, "image.jpg");

        try (FileOutputStream fos = new FileOutputStream(imageFile)) {
            // Compress the bitmap to JPEG format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageFile;
    }


    private File createFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = context.getContentResolver();
        String fileExtension = getFileExtension(Uri.parse(contentResolver.getType(uri)));

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), context.getPackageName());

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("YourAppDirectoryName", "failed to create directory");
        }

        // Create a media file name with timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String mediaFileName = "IMG_" + timeStamp + "." + fileExtension;

        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + mediaFileName);

        try (InputStream inputStream = contentResolver.openInputStream(uri);
             OutputStream outputStream = new FileOutputStream(mediaFile)) {

            // Copy the content to the new file
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }

        return mediaFile;
    }



    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = contentResolver.query(uri, projection, null, null, null);

        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (cursor.moveToFirst()) {
                String filePath = cursor.getString(columnIndex);
                if (filePath != null) {
                    return MimeTypeMap.getFileExtensionFromUrl(filePath);
                }
            }
            cursor.close();
        }

        // If all else fails, return a default extension or handle the case accordingly
        return "jpg";
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == C_PERMISSION_REQUEST_CODE) {
            // Check if all permissions are granted
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Log.d("TAG", "onRequestPermissionsResult: ");
                // All permissions granted, proceed with your logic
                // e.g., start the camera or access storage
            } else {
                Log.d("TAG", "onRequestPermissionsResult: ");
                // Some permissions were denied, handle accordingly
                // You may show a message or disable functionality
            }
        }
        else if (requestCode == C_PERMISSION_REQUEST_CODE) {

        }
    }


}