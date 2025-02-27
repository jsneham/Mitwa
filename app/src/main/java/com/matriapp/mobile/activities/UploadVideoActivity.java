package com.matriapp.mobile.activities;

import static com.matriapp.mobile.utility.AppConstants.MAX_VIDEO_SIZE_IN_MB_TO_UPLOAD;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.matriapp.mobile.retrofit.AppApiService;
import com.matriapp.mobile.retrofit.ProgressRequestBody;
import com.matriapp.mobile.retrofit.RetrofitClient;
import com.matriapp.mobile.utility.AppDebugLog;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.Common;
import com.matriapp.mobile.R;
import com.matriapp.mobile.utility.SessionManager;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class UploadVideoActivity extends AppCompatActivity implements View.OnClickListener, ProgressRequestBody.UploadCallbacks {//implements YouTubePlayer.OnInitializedListener
    private TextView tv_delete_id;
    private Button btn_upload;
    private ImageView btnBack;
    private VideoView videoview,videoview2;
    MediaController mc;
    private String url = "https://www.google.com";
    private Common common;
    private SessionManager session;
    private RelativeLayout loader;
    private ConstraintLayout llProfileCreate;
    private Toolbar toolbar;
    private String filePath = "";
    private final int PERMISSION_REQUEST_CODE = 122;
    Uri selectedVideoUri;

    String api_key = "AIzaSyCaRnEejqznEv597IjwwTX9nUBfs8AyZg4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_video);

        common = new Common(this);
        session = new SessionManager(this);

        videoview = findViewById(R.id.videoview);
        videoview2 = findViewById(R.id.videoview2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Upload Videos");
        toolbar.setNavigationOnClickListener(v -> finish());
        common.setGradient(getWindow());

        mc = new MediaController(UploadVideoActivity.this);
        videoview.setMediaController(mc);

        loader = findViewById(R.id.loader);

        btn_upload = findViewById(R.id.btn_upload);

        tv_delete_id = findViewById(R.id.tv_delete_id);

        btn_upload.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        tv_delete_id.setOnClickListener(view -> {
            deleteAlert();
        });

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
            }
        });



        getMyprofile();
    }

    @AfterPermissionGranted(122)
    private void requestPermission() {
        if (!checkPermission()) {
            // Ask for one permission
            EasyPermissions.requestPermissions(
                    this,
                    "This needs permission to use feature. You can grant them in app settings.",
                    PERMISSION_REQUEST_CODE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK);
            pickIntent.setType("video/*");
            startActivityForResult(pickIntent, 1);
        }
    }

    //TODO Permission related
    private boolean checkPermission() {
        String[] perms = {Manifest.permission.CAMERA};

        if (EasyPermissions.hasPermissions(this, perms)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent data) {
        super.onActivityResult(requestCode, resultcode, data);
        if (requestCode == 1) {
            if (data != null) {
                selectedVideoUri = data.getData();

                //filePath = getFilePathFromURI(this,selectedVideoUri);


                String[] filePathColumn = {MediaStore.Video.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedVideoUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                filePath = cursor.getString(columnIndex);
                cursor.close();

                AppDebugLog.print("video path : " + filePath);

                uploadVideo(filePath);
            }

        }
    }

    private void uploadVideo(String path) {
        RequestBody partParam1 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.KEY_USER_ID));
        RequestBody partParam2 = RequestBody.create(MediaType.parse("text/plain"), "NI-AAPP");
        RequestBody partParam3 = RequestBody.create(MediaType.parse("text/plain"), session.getLoginData(SessionManager.TOKEN));

        //Create a file object using file path
        File file = new File(path);
        // Parsing any Media type file
        ProgressRequestBody fileBody = new ProgressRequestBody(file, getMimeType(path), this);
        MultipartBody.Part fileToUpload =
                MultipartBody.Part.createFormData
                        ("video", file.getName(), fileBody);

        Retrofit retrofit = RetrofitClient.getClient();
        AppApiService appApiService = retrofit.create(AppApiService.class);

        Map<String, RequestBody> params = new HashMap<>();
        params.put("member_id", partParam1);
        params.put("user_agent", partParam2);
        params.put("csrf_new_matrimonial", partParam3);
        AppDebugLog.print("member_id:" + session.getLoginData(SessionManager.KEY_USER_ID) + " csrf_new_matrimonial:" + session.getLoginData(SessionManager.TOKEN));

        long fileSizeMB = common.getFIleSizeInMB(file);
        AppDebugLog.print("fileSizeMB : " + fileSizeMB);
        if (fileSizeMB > MAX_VIDEO_SIZE_IN_MB_TO_UPLOAD) {
            common.showToast( "Video size more than " + MAX_VIDEO_SIZE_IN_MB_TO_UPLOAD + "MB", llProfileCreate);
        } else {
            common.showProgressRelativeLayout(loader);
            Call<JsonObject> call = appApiService.uploadVideoNew(AppConstants.add_video, fileToUpload, params);
            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                    common.hideProgressRelativeLayout(loader);
                    JsonObject data = response.body();
                    AppDebugLog.print("response in submitData : " + data);
                    common.showToast( data.get("errmessage").getAsString(), llProfileCreate);
                    if (data.get("status").getAsString().equalsIgnoreCase("success")) {
                            videoview2.setVisibility(View.GONE);
                        videoview.setVisibility(View.VISIBLE);
                        videoview.setVideoURI(selectedVideoUri);
//                        videoview.start();
                        tv_delete_id.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    common.hideProgressRelativeLayout(loader);
                    t.printStackTrace();
                    AppDebugLog.print("error : " + t.fillInStackTrace());
                    AppDebugLog.print("error : " + t.getMessage());
                    AppDebugLog.print("error : " + t.getStackTrace());
                    common.showToast( getString(R.string.err_msg_try_again_later), llProfileCreate);
                }
            });
        }
    }

    // url = file path or whatever suitable URL you want.
    public String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        AppDebugLog.print("File Mime Type : " + type);
        return type;
    }

    @Override
    public void onClick(View v) {

    }

    private void getMyprofile() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        common.makePostRequest(AppConstants.get_my_profile, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                JSONObject data = object.getJSONObject("data");

                if (!data.getString("video").equals("") && !data.getString("video").equals("null")) {
                    videoview.setVideoPath(data.getString("video"));
                    //videoview.start();
                    tv_delete_id.setVisibility(View.VISIBLE);
                } else {
                    tv_delete_id.setVisibility(View.GONE);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llProfileCreate);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);
    }

    public static String getFilePathFromURI(Context context, Uri contentUri) {
        //copy file and send new file path

        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/" + AppConstants.DIRECTORY_NAME);
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        File copyFile = new File(wallpaperDirectory + File.separator + Calendar.getInstance()
                .getTimeInMillis() + ".mp4");
        // create folder if not exists

        copy(context, contentUri, copyFile);
        AppDebugLog.print("vPath---> " + copyFile.getAbsolutePath());

        return copyFile.getAbsolutePath();

    }

    public static void copy(Context context, Uri srcUri, File dstFile) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return;
            OutputStream outputStream = new FileOutputStream(dstFile);
            copystream(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int BUFFER_SIZE = 1024 * 2;

    public static int copystream(InputStream input, OutputStream output) throws Exception, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
        BufferedOutputStream out = new BufferedOutputStream(output, BUFFER_SIZE);
        int count = 0, n = 0;
        try {
            while ((n = in.read(buffer, 0, BUFFER_SIZE)) != -1) {
                out.write(buffer, 0, n);
                count += n;
            }
            out.flush();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
            try {
                in.close();
            } catch (IOException e) {
                Log.e(e.getMessage(), String.valueOf(e));
            }
        }
        return count;
    }

    private void deleteAlert() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setMessage("Are you sure you want to delete video?");
        alert.setNegativeButton("Cancel", null);
        alert.setPositiveButton("Delete", (dialogInterface, i) -> deleteVideo_api());
        alert.show();
    }

    private void deleteVideo_api() {
        common.showProgressRelativeLayout(loader);

        HashMap<String, String> param = new HashMap<>();
        param.put("member_id", session.getLoginData(SessionManager.KEY_USER_ID));
        param.put("delete_video_photo", "delete");

        common.makePostRequest(AppConstants.delete_video_proof_photo, param, response -> {
            common.hideProgressRelativeLayout(loader);
            try {
                JSONObject object = new JSONObject(response);
                common.showToast(object.getString("errmessage"),llProfileCreate);
                if (object.getString("status").equals("success")) {
                    videoview.setVisibility(View.GONE);
                    videoview2.setVisibility(View.VISIBLE);
                    getMyprofile();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                common.showToast(getString(R.string.err_msg_try_again_later),llProfileCreate);
            }
        }, error -> {
            common.hideProgressRelativeLayout(loader);
            if (error.networkResponse != null) {
                common.showToast(Common.getErrorMessageFromErrorCode(error.networkResponse.statusCode),llProfileCreate);
            }
        },llProfileCreate);

    }

    @Override
    public void onProgressUpdate(int percentage) {

    }

    @Override
    public void onError() {

    }

    @Override
    public void onFinish() {

    }
    public void onBackPressed(){
        super.onBackPressed();

    }

}