package com.anngrynerds.ospproject.home;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

public class HomeActivity extends AppCompatActivity implements LocationListener {

    BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;
    LocationManager locationManager;
    private String latitude;
    private String longitude;
    User u;
    SharedPreferences pref;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkIfMissingInfo();
        pref = getApplication()
                .getSharedPreferences(Constantss.sharedPrefID, MODE_PRIVATE);

        gson = new Gson();
        String json1 = pref.getString(Constantss.sharedPrefUserKey, "");
        u = gson.fromJson(json1, User.class);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000000, 0, this);

        if (getIntent().getBooleanExtra("toProfile", false)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_frame_layout, new ProfileFragment(), "SUB")
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_profile);

        } else {

            // loading feeds acc to user

            if(u.getRole().equalsIgnoreCase(Constantss.ROLE_CUSTOMER)){
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frame_layout, FeedFragment.newInstance())
                        .commit();
            }else{
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frame_layout, FarmerFeedFragment.newInstance())
                        .commit();
            }



        }



        bottomNavigationView.setSelectedItemId(R.id.bottom_menu_show_feed);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.bottom_menu_profile) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frame_layout, ProfileFragment.newInstance(), "SUB")
                        .commit();

                return true;

            } else if (item.getItemId() == R.id.bottom_menu_ai) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frame_layout, AIFragment.newInstance())
                        .commit();

                return true;

            }else if (item.getItemId() == R.id.bottom_menu_show_feed) {

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_activity_frame_layout, FeedFragment.newInstance())
                        .commit();

                return true;

            } else {
                return false;
            }
        });



    }

    private void updateCoordinatesInDatabasee(String lat, String lang) {


        SharedPreferences.Editor prefsEditor = pref.edit();
        prefsEditor.putString(Constantss.STR_Longitude, longitude + "");
        prefsEditor.putString(Constantss.STR_Latitude, latitude + "");
        prefsEditor.apply();


        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
                .child(u.getCity()).child(u.getRole()).child(u.getMobNo());

        ref.child("lang").setValue(lang);
        ref.child("lat").setValue(lat);

    }

    private void checkIfMissingInfo() {

        //todo
        SharedPreferences prefs = this.getSharedPreferences("com.anngrynerds.ospproject.home", Context.MODE_PRIVATE);
        String phno = prefs.getString("id", "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        startActivity(new Intent(HomeActivity.this, ProfileFill.class));
    }


    @Override
    public void onLocationChanged(Location location) {

        latitude = String.valueOf(location.getLatitude());
        longitude = String.valueOf(location.getLongitude());
        Log.e("Location ", "Lat: "+latitude+" | long: "+longitude );
        updateCoordinatesInDatabasee(latitude, longitude);

    }

    @Override
    public void onProviderDisabled(String provider) {
        // Log.d("Latitude","disable");
    }

    @Override
    public void onProviderEnabled(String provider) {
        //Log.d("Latitude","enable");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        //Log.d("Latitude","status");
    }





}