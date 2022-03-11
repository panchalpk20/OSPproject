package com.anngrynerds.ospproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
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

import java.util.Locale;

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

                                loadLanguage();

                                startActivity(new Intent(SplashScreen.this, HomeActivity.class));

                            } else {
                                changeLanguage();
                                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                            }

                            finish();
                        }else{
                            Log.e("LOCATION", "is Null" );
                        }
                    });
        }

    }

    private void changeLanguage() {
        final String languages[]={"English","Hindi","Marathi"};
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreen.this);
        alertDialog.setTitle("Choose Language");
        alertDialog.setSingleChoiceItems(languages, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(which==0){
                    setLocale("en");
                    recreate();
                } else if(which==1){
                    setLocale("hi");
                    recreate();
                } else if(which==2){
                    setLocale("mr");
                    recreate();
                }
            }
        });
        alertDialog.create();
        alertDialog.show();
    }

    private void setLocale(String language) {
        Locale local=new Locale(language);
        Locale.setDefault(local);

        Configuration configuration=new Configuration();
        configuration.locale=local;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor=getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("app_lang",language);
        editor.apply();
    }
    private void loadLanguage(){
        SharedPreferences preferences=getSharedPreferences("Settings",MODE_PRIVATE);
        String lang=preferences.getString("app_lang","");
        setLocale(lang);

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
