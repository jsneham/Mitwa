package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.matriapp.mobile.R;
import com.matriapp.mobile.activities.LoginActivity;

public class RegisterWelcomeActivity extends AppCompatActivity {

    private  String ragister_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_welcome);
        ragister_id = getIntent().getStringExtra("ragister_id");
    }

    public void OpenLoginScreen(View view) {
        finish();
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        i.putExtra("ragistered_id", ragister_id);
        startActivity(i);


    }

    @Override
    public void onBackPressed() {

    }

    public void onWhatsAppCall(View view) {

        String phoneNumber = getString(R.string.phone_number_help); // Phone number with country code
        Uri uri = Uri.parse(getString(R.string.whatsapp) + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}