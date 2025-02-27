package com.matriapp.mobile.activities.Registration;

import android.content.Intent;
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
}