package com.anngrynerds.ospproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.anngrynerds.ospproject.login.ProfileFill;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        prefs = this.getSharedPreferences("com.anngrynerds.ospproject.home", Context.MODE_PRIVATE);

        String phno = prefs.getString("id", "");

        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !phno.isEmpty()) {

            //   Toast.makeText(SplashScreen.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));

        } else {

            startActivity(new Intent(SplashScreen.this, LoginActivity.class));

        }
        finish();
    }
}
