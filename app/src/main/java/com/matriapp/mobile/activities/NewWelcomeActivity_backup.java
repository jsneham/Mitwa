package com.matriapp.mobile.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.Registration.RegistrationReferredByActivity;
import com.matriapp.mobile.utility.AppConstants;
import com.matriapp.mobile.utility.AppDebugLog;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class NewWelcomeActivity_backup extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks{

    private TextView tvVersion, tvBuild;
    private final int GPS_REQUEST = 333;
    private final int TAG_PERMISSIONS = 124;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_welcome);

        singleTextView1(findViewById(R.id.lblTerms));
        tvBuild = findViewById(R.id.tvBuild);
        tvVersion = findViewById(R.id.tvVersion);

        getVersion();
        getLocationAccess();

        findViewById(R.id.btnNewUser).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeActivity_backup.this, RegistrationReferredByActivity.class));
        });

        findViewById(R.id.btnExistingAccount).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeActivity_backup.this, LoginActivity.class));
        });
        findViewById(R.id.tvOption).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeActivity_backup.this, NewWelcomeTwoActivity.class));
        });

    }

    private void getVersion() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            String build = String.valueOf(info.versionCode);
            tvVersion.setText(getString(R.string.app_version) + " " + build);
            tvBuild.setText(" ( " + version + " )");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void singleTextView1(TextView textView) {
        String clickableTextStr = "terms";
        String clickableTextStr1 = "privacy policy";
        SpannableStringBuilder spanText = new SpannableStringBuilder();
        spanText.append("By continuing, you accept the " + clickableTextStr + " and " + clickableTextStr1);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openCMSDataDialog("term");
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(NewWelcomeActivity_backup.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 30, 35, 0);

        SpannableStringBuilder spanText1 = new SpannableStringBuilder();
        spanText1.append(" and " + clickableTextStr1);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                openCMSDataDialog("privacy");
            }

            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setColor(ContextCompat.getColor(NewWelcomeActivity_backup.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 39, 54, 0);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(NewWelcomeActivity_backup.this, R.color.transparent));
        textView.setText(spanText, TextView.BufferType.SPANNABLE);
        //  lblTerms.setText(Html.fromHtml(getString(R.string.lbl_service_request)), TextView.BufferType.SPANNABLE);

    }

    private void openCMSDataDialog(String tag) {
        Intent intent = new Intent(this, AllCmsActivity.class);
        intent.putExtra(AppConstants.KEY_INTENT, tag);
        startActivity(intent);
    }


    @AfterPermissionGranted(TAG_PERMISSIONS)
    private void getLocationAccess() {
        String[] perms = {
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};

        if (EasyPermissions.hasPermissions(this, perms)) {
            createLocationRequest();
        } else {
            // Do not have permissions, request them now
            ActivityCompat.requestPermissions(this, perms, TAG_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
//            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {

            }
            if (requestCode == GPS_REQUEST) {
                getCurrentLocationFromFusedLocationLibrary();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            getCurrentLocationFromFusedLocationLibrary();
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                // Show the dialog by calling startResolutionForResult(),
                // and check the result in onActivityResult().
                try {
                    ((ResolvableApiException) e).startResolutionForResult(this, GPS_REQUEST);
                } catch (IntentSender.SendIntentException sendIntentException) {
                    sendIntentException.printStackTrace();
                }
            }
        });
    }

    private void getCurrentLocationFromFusedLocationLibrary() {
        //fusion library
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                if (locationResult.getLastLocation() != null) {
                    currentLocation = locationResult.getLastLocation();
                    AppDebugLog.print("narjis location : " + currentLocation.getLatitude() + "," + currentLocation.getLongitude());
                    //getMyprofile();
                    removeLocationUpdateCallback();
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    private void removeLocationUpdateCallback() {
        if (fusedLocationClient != null)
            fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}