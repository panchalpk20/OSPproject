package com.anngrynerds.ospproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity{
    SharedPreferences prefs;
    String longitude;
    String latitude;
    String phno;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);

        phno = prefs.getString("id", "");
        FusedLocationProviderClient fusedLocationClient
                = LocationServices.getFusedLocationProviderClient(this);


        if (ContextCompat.checkSelfPermission(SplashScreen.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                &&
                ContextCompat.checkSelfPermission(SplashScreen.this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            askForLocationPermissions();
        } else {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            latitude = String.valueOf(location.getLatitude());
                            longitude = String.valueOf(location.getLongitude());
                            Log.e("Location ", "Lat: "+latitude+" | long: "+longitude );
                            SharedPreferences.Editor prefsEditor = prefs.edit();
                            prefsEditor.putString(Constantss.STR_Longitude, longitude + "");
                            prefsEditor.putString(Constantss.STR_Latitude, latitude + "");
                            prefsEditor.apply();

                            if (FirebaseAuth.getInstance().getCurrentUser()!=null && !phno.isEmpty()) {
                                //   Toast.makeText(SplashScreen.this, "Already Logged in", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SplashScreen.this, HomeActivity.class));

                            } else {
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                            }

                            finish();
                        }else{
                            Log.e("LOCATION", "is Null" );
                        }
                    });
        }

    }



    private void askForLocationPermissions() {


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Location permissions needed")
                    .setMessage("you need to allow this permission!")
                    .setPositiveButton("Sure", (dialog, which) ->
                            ActivityCompat.requestPermissions(SplashScreen.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            101))
                    .setNegativeButton("Not now", (dialog, which) -> {
//                                        //Do nothing

                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                101);


                    })
                    .show();

        } else {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    101);


        }

    }
}
