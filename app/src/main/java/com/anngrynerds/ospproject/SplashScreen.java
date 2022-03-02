package com.anngrynerds.ospproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity  {

    SharedPreferences prefs;
    String longitude;
    String latitude;
    String phno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);

        phno = prefs.getString("id", "");





        //checkIfGPSisOn();

        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !phno.isEmpty()) {

            //   Toast.makeText(SplashScreen.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));

        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }

        finish();


    }

    /*private void checkIfGPSisOn() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.e("Location ", "No GPS");
            OnGPS();
        } else {
            Log.e("Location ", "GPS");
            getLocation();
        }
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                checkIfGPSisOn();
            }
        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                SplashScreen.this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SplashScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);

            Log.e("Location ", "here 1");

        } else {

            Log.e("Location ", "here 2");
            List<String> providers = locationManager.getProviders(true);
            Location bestLocation = null;
            for (String provider : providers) {
                Location l = locationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }

            Log.e("Location ", bestLocation != null ? bestLocation.toString() : "null");
            
            if (bestLocation != null) {
                latitude = String.valueOf(bestLocation.getLatitude());
                longitude = String.valueOf(bestLocation.getLongitude());
                Log.e("Location ", "Lat: "+latitude+" | long: "+longitude );


                SharedPreferences locationpref = getApplication()
                        .getSharedPreferences(Constantss.sharedPrefID, MODE_PRIVATE);
                SharedPreferences.Editor prefsEditor = locationpref.edit();
                prefsEditor.putString(Constantss.STR_Longitude, longitude + "");
                prefsEditor.putString(Constantss.STR_Latitude, latitude + "");
                prefsEditor.apply();


            }

        }
    }*/





}
