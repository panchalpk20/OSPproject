package com.anngrynerds.ospproject.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.AIChatBot.AIFragment;
import com.anngrynerds.ospproject.pojo.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    User u;
    SharedPreferences pref;
    Gson gson;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLanguage();
        setContentView(R.layout.activity_home);
        getWindow().setFlags(
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW,
                WindowManager.LayoutParams.FIRST_APPLICATION_WINDOW);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        //getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadLanguage();
        checkIfMissingInfo();
        pref = getApplication()
                .getSharedPreferences(Constantss.sharedPrefID, MODE_PRIVATE);

        gson = new Gson();
        String json1 = pref.getString(Constantss.sharedPrefUserKey, "");
        u = gson.fromJson(json1, User.class);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        updateCoordinatesInDatabasee();






       /* if (getIntent().getBooleanExtra("toProfile", false)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_frame_layout, new ProfileFragment(), "SUB")
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_profile);

        } else {*/

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



//        }



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
                    //getSupportFragmentManager().beginTransaction().add(R.id.linearL,FarmerFeedFragment.newInstance()).commit();
                }

                return true;

            } else {
                return false;
            }
        });



    }


    private void updateCoordinatesInDatabasee() {

        String lat = pref.getString(Constantss.STR_Longitude, "");
        String lang = pref.getString(Constantss.STR_Latitude, "");

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

    private void setLocale(String language) {
        Locale local=new Locale(language);
        Locale.setDefault(local);

        Configuration configuration=new Configuration();
        configuration.locale=local;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());
        //recreate();
    }
    private void loadLanguage(){
        SharedPreferences preferences=getSharedPreferences(Constantss.sharedPrefID,MODE_PRIVATE);
        String lang=preferences.getString("app_lang","");
        if(!lang.isEmpty())
        setLocale(lang);

    }



}