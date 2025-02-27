package com.matriapp.mobile.activities.Registration;

import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
//import static android.Manifest.permission.READ_MEDIA_IMAGES;
//import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.matriapp.mobile.utility.AppConstants.MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;
import com.matriapp.mobile.R;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.retrofit.AppApiService;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.retrofit.RetrofitClient;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.utility.SessionManager;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class RegistrationPhotoActivity extends AppCompatActivity implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {

    private Common common;
    private RelativeLayout lay_photos;
    private SessionManager session;
    private RelativeLayout loader;
    private ProgressDialog progressBar;
    private String ragister_id;
    private ImageView img_plus_one, img_profile, img_two, img_three, img_four,ivPhotoGuide,ivPhotoGuideInDialog;
    private int image_id;

    private final int CROP_PIC = 3;
    private final int C_PERMISSION_REQUEST_CODE = 122;
    private final int G_PERMISSION_REQUEST_CODE = 123;
    //    private EasyImage easyImage = null;
    private File compressedFile = null;
    private File originalFile = null;
    private String originalFilePath = "", cropFilePath = "";
    private Uri cropUri;
    private HashMap<String, String> paramsForAPI;
    private ArrayList<Uri> imagesList = new ArrayList<Uri>();
    private BottomSheetBehavior sheetBehavior;
    private CardView layoutBottomSheet;
    private RelativeLayout tv_cancel;

    private Button btn_photo_submit;
    private CheckBox checkBox;
    private TextView lblTerms,  tv_gallary, tv_camera;
    boolean isImageSelect = false;
    boolean isCheckSelect = false;
    private static final String CHECKBOX_STATE = "checkbox_state";


    private int smallImageW = 0;
    private int smallImageH = 0;

    private String[] REQUIRED_PERMISSIONS = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            ? new String[] {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            Manifest.permission.MANAGE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    }
            : new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_photo);
        common = new Common(this);
        session = new SessionManager(this);
        setSize();
        paramsForAPI = new HashMap<>();
        ragister_id = getIntent().getStringExtra("ragister_id");
        img_plus_one = findViewById(R.id.img_plus_one);
        img_profile = findViewById(R.id.img_profile);
        img_two = findViewById(R.id.img_two);
        img_three = findViewById(R.id.img_three);
        img_four = findViewById(R.id.img_four);
        ivPhotoGuide = findViewById(R.id.ivPhotoGuide);
        ivPhotoGuideInDialog = findViewById(R.id.ivPhotoGuideInDialog);
        lay_photos = findViewById(R.id.lay_photos);

//        common.setImageGuideLine(ivPhotoGuide, R.drawable.photo_guidelines_image_one);
//        common.setImageGuideLine(ivPhotoGuideInDialog, R.drawable.photo_guidelines_image_one);

        loader = findViewById(R.id.loader);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);
        btn_photo_submit = findViewById(R.id.btn_photo_submit);
        checkBox = findViewById(R.id.checkBox);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_gallary = findViewById(R.id.tv_gallary);
        tv_camera = findViewById(R.id.tv_camera);

        if (savedInstanceState != null) {
            isCheckSelect = savedInstanceState.getBoolean(CHECKBOX_STATE);
            checkBox.setChecked(isCheckSelect);
        }
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isCheckSelect = isChecked;

            }
        });
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
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
        img_profile.setOnClickListener(this);
        img_two.setOnClickListener(this);
        img_three.setOnClickListener(this);
        img_four.setOnClickListener(this);

        btn_photo_submit.setOnClickListener(this);
        tv_gallary.setOnClickListener(this);
        tv_camera.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);


    }

    private void setSize() {
        int deviceWidth = Common.getDisplayWidth(this);
        int smallWidth = deviceWidth / 3;
        smallImageW = smallWidth;
        smallImageH = (int) (smallImageW * 1.210);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(CHECKBOX_STATE, isCheckSelect);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isCheckSelect = savedInstanceState.getBoolean(CHECKBOX_STATE);
        checkBox.setChecked(isCheckSelect);
    }


    private boolean checkPermissions() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }


    private void goToNextActivity() {
        Intent in = new Intent(this, RegistrationCountryActivity.class);
        in.putExtra("ragister_id", ragister_id);
        startActivity(in);
    }

    public void goBack(View view) {
        onBackPressed();
    }


    private void openFileChooser() {
        sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        ) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            pickImage(100);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, CAMERA,
                    ACCESS_NETWORK_STATE}, C_PERMISSION_REQUEST_CODE);

        }

    }

    private void requestGalleryPermission() {
        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14+
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) { // Android 13+
            permissions = new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO};
        } else { // Below Android 13
            permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        requestPermissions.launch(permissions);
    }
    ActivityResultLauncher<String[]> requestPermissions =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), results -> {
                boolean hasReadMediaVideoPermission = results.getOrDefault("android.permission.READ_MEDIA_VIDEO", false);
                boolean hasReadMediaImagesPermission = results.getOrDefault("android.permission.READ_MEDIA_IMAGES", false);
                boolean hasReadMediaVisualPermission = results.getOrDefault("android.permission.READ_MEDIA_VISUAL_USER_SELECTED", false);

                if (hasReadMediaVideoPermission && hasReadMediaImagesPermission) {
                    openGallery(200);
                } else if (hasReadMediaVideoPermission || hasReadMediaImagesPermission) {
                    openGallery(200);
                }
                else{
                    openGallery(200);
                }
//                common.showToast("Permission denied. Cannot access gallery.", lay_photos);
            });



    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btn_photo_submit) {
            if (!(isCheckSelect && imagesList.size()>0)) {
                common.showToast("Please read the photo guidelines and upload atleast one photo. You can change or upload more photos later.",lay_photos);
                return;
            }
            goToNextActivity();

        } else if (id == R.id.tv_cancel) {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (id == R.id.tv_camera) {
            requestCameraPermission();

        } else if (id == R.id.tv_gallary) {
            requestGalleryPermission();
//            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            pickImage(100);
        } else if (id == R.id.img_profile) {
            image_id = 1;
            openFileChooser();
        } else if (id == R.id.img_two) {
            image_id = 2;
            openFileChooser();
        } else if (id == R.id.img_three) {
            image_id = 3;
            openFileChooser();
        } else if (id == R.id.img_four) {
            image_id = 4;
            openFileChooser();
        }
    }

//    private void uploadImages() {
//        for (int i = 0; i < imagesList.size(); i++) {
//            uploadFileToServer(imagesList.get(i));
//        }
//    }

    private void pickImage(int pickFor) {
        switch (pickFor) {
            case 100:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 100);
//                easyImage.openGallery(this);
                break;

            case 200:
//                openGallery(200);
//                easyImage.openCameraForImage(this);
                break;
        }
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


    private void uploadFileToServer(Uri cropUri) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast(getString(R.string.err_msg_no_intenet_connection), lay_photos);
            return;
        }

        common.hideProgressRelativeLayout(loader);

        // setting progress bar to zero
        progressBar = new ProgressDialog(RegistrationPhotoActivity.this);
        progressBar.setTitle("Uploading...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setCancelable(false);
        progressBar.show();

        File profileOriginalImageCompressedFile = new File(Common.getPath(this, cropUri));
        ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
        MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData("profil_photo", profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);

        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), ragister_id);
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Call<JsonObject> call = null;

        Map<String, RequestBody> params = new HashMap<>();
        params.put("id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);

        long fileSizeMB = common.getFIleSizeInMB(profileOriginalImageCompressedFile);
        if (fileSizeMB > MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD) {
            common.showToast("Image size more than " + MAX_IMAGE_SIZE_IN_MB_TO_UPLOAD + " MB", lay_photos);
        } else {
            call = appApiService.uploadPhoto(originalFilePart, originalFilePart, params);
        }


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }

                JsonObject data = response.body();

                if (data != null) {
                    if (data.get("status").getAsString().equals("success")) {

                        common.showToast(getString(R.string.profile_details_added),lay_photos);


                    } else {
                        common.showToast(data.get("errmessage").getAsString(),lay_photos);
                    }
                } else {
                    common.showToast(getString(R.string.err_msg_try_again_later), lay_photos);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                common.showToast( getString(R.string.err_msg_something_went_wrong), lay_photos);
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }


    private void uploadFileToServerOthers(Uri cropUri) {
        if (!ConnectionDetector.isConnectingToInternet(this)) {
            common.showToast(getString(R.string.err_msg_no_intenet_connection), lay_photos);
            return;
        }

        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
        // setting progress bar to zero
        progressBar = new ProgressDialog(this);
        progressBar.setTitle("Uploading...");
        progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);
        progressBar.setCancelable(false);
        progressBar.show();

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
        if (image_id == 2 || image_id == 3 || image_id == 4) {
            String org_param = "profile_photo" + image_id + "_org";
            String crop_param = "profile_photo" + image_id + "_crop";
            File sourceFile_crop = new File(cropFilePath);
            File sourceFile = new File(originalFilePath);
            ProgressRequestBody cropFileBody = new ProgressRequestBody(sourceFile_crop, getMimeType(cropFilePath), this);
            MultipartBody.Part cropFilePart = MultipartBody.Part.createFormData(crop_param, sourceFile_crop.getName().replaceAll("[^a-zA-Z0-9.]", ""), cropFileBody);

            File profileOriginalImageCompressedFile = sourceFile;
            AppDebugLog.print("profileOriginalImageCompressedFile name : " + profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""));
            ProgressRequestBody originalFileBody = new ProgressRequestBody(profileOriginalImageCompressedFile, getMimeType(profileOriginalImageCompressedFile.getAbsolutePath()), this);
            MultipartBody.Part originalFilePart = MultipartBody.Part.createFormData(org_param, profileOriginalImageCompressedFile.getName().replaceAll("[^a-zA-Z0-9.]", ""), originalFileBody);
            call = appApiService.uploadMyPhotoWithCrop(cropFilePart, originalFilePart, params);


        }



        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }

                JsonObject data = response.body();
                AppDebugLog.print("response in submitData : " + response.body());

                if (data != null) {
                    common.showToast(data.get("errmessage").getAsString(),lay_photos);

                } else {
                    common.showToast(getString(R.string.err_msg_try_again_later),lay_photos);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                common.showToast(getString(R.string.err_msg_something_went_wrong), lay_photos);
                if (progressBar != null && progressBar.isShowing()) {
                    progressBar.dismiss();
                }
            }
        });
    }

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
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.setProgress(percentage);
        }

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {
        //set finish progress
        if (progressBar != null && progressBar.isShowing()) {
            progressBar.dismiss();
        }
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
//            openFileChooser();
//        }
        if (requestCode == 100 && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            File mediaFile = convertBitmapToFile(imageBitmap);
            originalFile = mediaFile.getAbsoluteFile();
            originalFilePath = mediaFile.getAbsoluteFile().getAbsolutePath();
            cropImage(originalFile);
        }
        else if (requestCode == 200 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    File mediaFile = createFileFromUri(uri);
                    originalFile = mediaFile.getAbsoluteFile();
                    originalFilePath = mediaFile.getAbsolutePath();
                    cropImage(originalFile);
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception appropriately
                }
            }
        }
        else if (requestCode == CROP_PIC) {
            if (resultCode == Activity.RESULT_OK) {

                cropUri = UCrop.getOutput(data);
                imagesList.add(cropUri);
                cropFilePath = cropUri.getPath();

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), cropUri);
                    if (image_id == 1) {
                        isImageSelect = true;
                    img_profile.setImageBitmap(bitmap);
                        img_profile.setScaleType(ImageView.ScaleType.MATRIX);
                        uploadFileToServer(cropUri);

//                        Uri uri = setImageURI(bitmap);
//                        Picasso.get().load(uri).resize(smallImageW, smallImageH).centerCrop(Gravity.TOP | Gravity.START).into(img_profile);

                    } else if (image_id == 2) {
                        img_two.setImageBitmap(bitmap);
                        img_two.setScaleType(ImageView.ScaleType.MATRIX);
                        uploadFileToServerOthers(cropUri);
                    } else if (image_id == 3) {
                        img_three.setImageBitmap(bitmap);
                        img_three.setScaleType(ImageView.ScaleType.MATRIX);
                        uploadFileToServerOthers(cropUri);

                    } else if (image_id == 4) {
                        img_four.setImageBitmap(bitmap);
                        img_four.setScaleType(ImageView.ScaleType.MATRIX);
                        uploadFileToServerOthers(cropUri);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Uri setImageURI(Bitmap bitmap) {


        try {
            // Save the Bitmap to the file
            File file = new File(getCacheDir(), "image.jpg");
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            Uri uri = Uri.fromFile(file);
            return uri;
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
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

    private void cropImage(File attachmentFile) {
        try {
            sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            Uri uri = Uri.fromFile(attachmentFile);

            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            String imageExtension = Common.getExtensionFromPath(Common.getPath(RegistrationPhotoActivity.this, uri));
            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), timeStamp + imageExtension)));
            uCrop.withAspectRatio(2, 3);
//        uCrop.withMaxResultSize(512, 620);
            uCrop.withMaxResultSize(720, 1080);
            UCrop.Options options = new UCrop.Options();

            options.setToolbarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
            options.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
            options.setToolbarWidgetColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            options.setRootViewBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));

            uCrop.withOptions(options);
            uCrop.start(this, CROP_PIC);
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    private File createFileFromUri(Uri uri) throws IOException {
        ContentResolver contentResolver = getContentResolver();
        String fileExtension = getFileExtension(Uri.parse(contentResolver.getType(uri)));

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getPackageName());

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

    public void onSkipClick(View view) {
        goToNextActivity();
    }

}