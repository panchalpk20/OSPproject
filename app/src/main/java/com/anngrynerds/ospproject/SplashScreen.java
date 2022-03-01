package com.anngrynerds.ospproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    SharedPreferences prefs;
    String longitude;
    String latitude;
    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);

        String phno = prefs.getString("id", "");



        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE
                }, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            getLocation();
        }



/*

        SharedPreferences locationpref = getApplication()
                .getSharedPreferences(Constantss.sharedPrefID, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = locationpref.edit();
        prefsEditor.putString("Longitude", longitude + "");
        prefsEditor.putString("Latitude", latitude + "");
        prefsEditor.apply();

*/

        if (FirebaseAuth.getInstance().getCurrentUser()!=null && !phno.isEmpty()) {

            //   Toast.makeText(SplashScreen.this, "Already Logged in", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(SplashScreen.this, HomeActivity.class));

        } else {
            startActivity(new Intent(SplashScreen.this, LoginActivity.class));
        }
        finish();
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new  DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", (dialog, which) -> dialog.cancel());
        final AlertDialog alertDialog = builder.create();
     //   alertDialog.show();
    }


    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(
                SplashScreen.this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                SplashScreen.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
        } else {
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
    }

}
