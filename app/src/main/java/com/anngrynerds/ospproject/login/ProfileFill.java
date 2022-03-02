package com.anngrynerds.ospproject.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.pojo.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.util.HashMap;
import java.util.Map;

public class ProfileFill extends AppCompatActivity {

    private static final String TAG = "PROFILE_FRAG";
    TextView temp;
    String lat, longitude;
    EditText et_phno;
    EditText et_name;
    EditText et_address;

    TextInputLayout ipl_name;
    TextInputLayout ipl_number;
    TextInputLayout ipl_address;

    TextView pgMsg;

    ProgressBar pg;
    Button btn_continue;

    String str_phno;
    String str_name;
    String str_address;
    String str_role;
    String str_city;

    Spinner spinner_citySelect;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RadioGroup rg_role;

    boolean fromProfile;
    Dialog dialog;
    String[] cities;

    String str_lat, str_long;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fill);

        temp = findViewById(R.id.temp);

        // getCurrentAddress();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SharedPreferences prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        cities = getResources().getStringArray(R.array.cities);

        str_lat = prefs.getString(Constantss.STR_Latitude, "");
        str_long = prefs.getString(Constantss.STR_Longitude, "");
        str_phno = prefs.getString("id", "");

        fromProfile = getIntent().getBooleanExtra("fromProfile", false);
        rg_role = findViewById(R.id.profile_rg_role);
        ipl_number = findViewById(R.id.profile_ipl_mobile_number);
        ipl_name = findViewById(R.id.profile_ipl_name);
        ipl_address = findViewById(R.id.profile_ipl_address);

        str_phno = prefs.getString("id", "");
        et_phno = findViewById(R.id.profile_mobile_number);
        et_address = findViewById(R.id.profile_address);
        et_name = findViewById(R.id.profile_name);
        btn_continue = findViewById(R.id.profile_btn_continue);

        spinner_citySelect = findViewById(R.id.profile_spinner_city);
        spinner_citySelect.setPrompt("Select you are in");
        spinner_citySelect.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str_city = cities[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> spinnerAdapter
                = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                cities
        );

        spinnerAdapter.setDropDownViewResource(
                android.R.layout
                        .simple_spinner_dropdown_item);
        spinner_citySelect.setAdapter(spinnerAdapter);

        dialog = new Dialog(ProfileFill.this);
        dialog.setContentView(R.layout.dialog_transparent_loading);
        dialog.setCancelable(false);
        pgMsg = dialog.findViewById(R.id.dialog_pgmsg);

//        Log.e(TAG, "onCreate: "+str_phno );
        et_phno.setText(str_phno);
        et_phno.setKeyListener(null);

        rg_role.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.profile_rb_role_farmer) str_role = "farmer";
            if (checkedId == R.id.profile_rb_role_customer) str_role = "customer";
        });


        Gson gson = new Gson();
        String json1 = prefs.getString(Constantss.sharedPrefUserKey, "");
        User user1 = gson.fromJson(json1, User.class);

        if (user1 != null) {

            et_phno.setText(user1.getMobNo());
            et_name.setText(user1.getName());
            if (user1.getRole().equalsIgnoreCase("farmer"))
                rg_role.check(R.id.profile_rb_role_farmer);
            else
                rg_role.check(R.id.profile_rb_role_customer);

            et_address.setText(user1.getAddress());


        }


        btn_continue.setOnClickListener(v -> {

            showpg("Updating data, please wait");

            str_phno = et_phno.getText().toString();
            str_name = et_name.getText().toString();
            str_address = et_address.getText().toString();

            if (str_name.isEmpty()) {
                ipl_name.setError("Enter Name");
                et_name.requestFocus();
                closepg();
            } else if (str_name.length() < 4) {
                ipl_name.setError("Name should contain at least 4 characters");
                et_name.requestFocus();
                closepg();
            } else if (str_address.isEmpty()) {
                ipl_address.setError("Enter Address");
                et_address.requestFocus();
                closepg();
            } else if (str_address.length() < 10) {
                ipl_address.setError("Address should be minimum of 10 characters");
                et_address.requestFocus();
                closepg();
            } else if (str_city.isEmpty()) {
                Snackbar.make(v, "Please Select City", BaseTransientBottomBar.LENGTH_LONG).show();

            } else {

                Map<String, Object> u = new HashMap<>();
                u.put("mobNo", str_phno);
                u.put("address", str_address);
                u.put("name", str_name);
                u.put("role", str_role);
                u.put("lat", str_lat);
                u.put("lang", str_long);
                u.put("city", str_city);

                myRef.child(str_city).child(str_role).child(str_phno).updateChildren(u).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        closepg();

                        Snackbar.make(v, "Data Updated", BaseTransientBottomBar.LENGTH_SHORT).show();

                        //todo add u in sharedPref
                        SharedPreferences.Editor editor = prefs.edit();
                        JsonElement jsonElement = gson.toJsonTree(u);
                        User user = gson.fromJson(jsonElement, User.class);
                        String json = gson.toJson(user);
                        editor.putString(Constantss.sharedPrefUserKey, json);
                        editor.apply();

                        Intent i = new Intent(ProfileFill.this, HomeActivity.class);
                        if (fromProfile) {
                            i.putExtra("toProfile", true);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Snackbar.make(v, "Error Occurred while saving information: " + task.getException().getMessage(),
                                BaseTransientBottomBar.LENGTH_INDEFINITE).show();

                        Log.e(TAG, "onCreate: " + task.getException().getMessage());
                    }
                });
            }

        });

    }


    private void closepg() {
        dialog.dismiss();
    }

    private void showpg(String message) {
        if (!message.isEmpty()) {
            pgMsg.setText(message);
        } else {
            pgMsg.setText(" ");
        }
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        btn_continue.callOnClick();
    }

}