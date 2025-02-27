package com.matriapp.mobile.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.Registration.RegistrationReferredByActivity;
import com.matriapp.mobile.utility.AppConstants;

public class NewWelcomeTwoActivity extends AppCompatActivity {

    private TextView tvVersion, tvBuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_welcome_two);
        singleTextView1(findViewById(R.id.lblTerms));
        tvBuild = findViewById(R.id.tvBuild);
        tvVersion = findViewById(R.id.tvVersion);

        getVersion();

        findViewById(R.id.btnNewUser).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeTwoActivity.this, RegistrationReferredByActivity.class));
        });

        findViewById(R.id.btnOtp).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeTwoActivity.this, LoginWithOtpActivityWithFirebase.class));
        });
       findViewById(R.id.btnPassword).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeTwoActivity.this, LoginActivity.class));
        });
        findViewById(R.id.tvOption).setOnClickListener(view -> {
            startActivity(new Intent(NewWelcomeTwoActivity.this, NewWelcomeActivity.class));
        });

    }

    private void getVersion() {
        PackageManager manager = getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(getPackageName(), 0);
            String version = info.versionName;
            String build = String.valueOf(info.versionCode);
            tvVersion.setText(getString(R.string.app_version) + " " + version);
            tvBuild.setText(getString(R.string.built_version) + " " + build);
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
                textPaint.setColor(ContextCompat.getColor(NewWelcomeTwoActivity.this, R.color.blue_color));    // you can use custom color
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
                textPaint.setColor(ContextCompat.getColor(NewWelcomeTwoActivity.this, R.color.blue_color));    // you can use custom color
                textPaint.setUnderlineText(false);    // this remove the underline
            }
        }, 39, 54, 0);

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setHighlightColor(ContextCompat.getColor(NewWelcomeTwoActivity.this, R.color.transparent));
        textView.setText(spanText, TextView.BufferType.SPANNABLE);
        //  lblTerms.setText(Html.fromHtml(getString(R.string.lbl_service_request)), TextView.BufferType.SPANNABLE);

    }

    private void openCMSDataDialog(String tag) {
        Intent intent = new Intent(this, AllCmsActivity.class);
        intent.putExtra(AppConstants.KEY_INTENT, tag);
        startActivity(intent);
    }

}