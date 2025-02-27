package com.matriapp.mobile.fragments.ProfileVerification;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED;
import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.matriapp.mobile.R;
import com.matriapp.mobile.fragments.PhotoPreviewDialog;
import com.matriapp.mobile.network.ConnectionDetector;
import com.matriapp.mobile.retrofit.AppApiService;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.retrofit.RetrofitClient;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;


public class PhotoVerificationFragment extends Fragment  implements ProgressRequestBody.UploadCallbacks {

    private Common common;
    private ProgressDialog pd;

    private SessionManager session;
    private Context context;
    private RelativeLayout loader,llView;
    private TextView btnNeedHelp, tvDescription;
    private ImageView img_edit_self, img_id_self;
    private String cover_photo_approve, cover_photo;

    private int coverImageHeight = 0;
    private int coverImageWidth = 0;

    private final int C_PERMISSION_REQUEST_CODE = 122;
    private String originalFilePath = "", cropFilePath = "";
    private Uri cropUri;
    ;
    private File originalFile = null;
    private final int CROP_PIC = 3;
    private int image_id=0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_photo_verification, container, false);
        
        context = getContext();
        session = new SessionManager(context);
        common = new Common(getActivity());

        int deviceWidth = Common.getDisplayWidth(getActivity());
        coverImageHeight = Common.convertDpToPixels(200, getActivity());
        coverImageWidth = deviceWidth - Common.convertDpToPixels(10, getActivity());


        llView = view.findViewById(R.id.llView);
        loader = view.findViewById(R.id.loader);
        btnNeedHelp = view.findViewById(R.id.btnNeedHelp);
        tvDescription = view.findViewById(R.id.tvDescription);
        img_edit_self = view.findViewById(R.id.img_edit_self);
        img_id_self = view.findViewById(R.id.img_id_self);

        img_id_self.setOnClickListener(v1 -> {
            if (!isValidImage(cover_photo)) {
                requestGalleryPermission();
            } else showPhotoPreviewDialog(cover_photo);
        });

        img_edit_self.setOnClickListener(vm -> {
            editClick(vm);
        });

        btnNeedHelp.setOnClickListener(v -> {
            common.callWhatsApp(context);
        });



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getMyProfile();
    }

    private void editClick(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        popup.inflate(R.menu.photo_edit_menu);
        popup.getMenu().getItem(2).setVisible(false);
        popup.getMenu().getItem(1).setVisible(false);

        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.edit:
                    requestGalleryPermission();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
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

                    cover_photo_approve = data.getString("cover_photo_approve");
                    cover_photo = data.getString("cover_photo");

                    setIdProofPhoto();
                    Log.d("TAG", "getMyProfile: " + data);

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

    private void setIdProofPhoto() {
        if (cover_photo_approve.equalsIgnoreCase("UNAPPROVED") && isValidImage(cover_photo)) {
            tvDescription.setText("Verification in Process");
            tvDescription.setTextColor(context.getColor(R.color.quantum_orange900));
        }
        else if (cover_photo_approve.equalsIgnoreCase("APPROVED") && isValidImage(cover_photo)) {
            tvDescription.setText("Verified Successfully");
            tvDescription.setTextColor(context.getColor(R.color.positive_green));
        }
        else {
//            tvDescription.setText(context.getString(R.string.upload_a_clearly_visible_photo_of_your_aadhar_card_or_pan_n_other_members_cannot_see_this_photo));
//            tvDescription.setTextColor(context.getColor(R.color.seeting_detail));
        }

        if (isValidImage(cover_photo)) {
            Picasso.get().load(cover_photo).resize(coverImageWidth, coverImageHeight).centerInside().placeholder(R.drawable.add_image_shape_cover).error(R.drawable.add_image_shape_cover).into(img_id_self);
            img_edit_self.setVisibility(View.VISIBLE);
        } else {
            img_edit_self.setVisibility(View.GONE);
            img_id_self.setImageResource(R.drawable.add_image_shape_cover);
        }

    }

    private boolean isValidImage(String url) {
        return !url.equals("") && !url.equals("null");
    }

    private void showPhotoPreviewDialog(String imageUri) {
        if (getContext() != null) {
            PhotoPreviewDialog dialog = new PhotoPreviewDialog(getContext(), imageUri);
            dialog.show();
        } else {
            // Handle the case where the fragment is not attached to the activity
        }
    }


    private void requestGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            requestPermissions.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED});
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions.launch(new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO});
        } else {
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
        } else if (hasReadExternalStorageImagesPermission) {

            openGallery(200);
        } else {
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

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // If you also want to include videos, uncomment the following line
        // galleryIntent.setType("image/* video/*");
        startActivityForResult(intent, code);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        AppDebugLog.print("resultCode : " + resultCode);
        AppDebugLog.print("requestCode : " + requestCode);
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
            AppDebugLog.print("resultCode in CROP_PIC: " + resultCode);
            AppDebugLog.print("requestCode  in CROP_PIC: " + requestCode);
            if (resultCode == Activity.RESULT_OK) {
                cropUri = UCrop.getOutput(data);
                cropFilePath = cropUri.getPath();

                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), cropUri);
                    img_id_self.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                uploadFileToServer();
            }
        }

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

    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
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
            getMyProfile();
        }
    }

    private void cropImage(File attachmentFile) {

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
}