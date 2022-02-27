package com.anngrynerds.ospproject.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        checkIfMissingInfo();


        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);



        if (getIntent().getBooleanExtra("toProfile", false)) {

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_frame_layout, new ProfileFragment(), "SUB")
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.bottom_menu_profile);

        } else {

            // feed

            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_activity_frame_layout, FeedFragment.newInstance())
                    .commit();

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

    private void checkIfMissingInfo() {

        //todo
        SharedPreferences prefs = this.getSharedPreferences("com.anngrynerds.ospproject.home", Context.MODE_PRIVATE);
        String phno = prefs.getString("id", "");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
//        startActivity(new Intent(HomeActivity.this, ProfileFill.class));
    }


}