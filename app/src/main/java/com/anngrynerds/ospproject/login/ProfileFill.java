package com.anngrynerds.ospproject.login;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.pojo.User;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProfileFill extends AppCompatActivity {

    TextView temp;
    String lat, longitude;
    private static final String TAG = "PROFILE_FRAG";
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

        fromProfile = getIntent().getBooleanExtra("fromProfile",false);
        rg_role = findViewById(R.id.profile_rg_role);
        ipl_number = findViewById(R.id.profile_ipl_mobile_number);
        ipl_name = findViewById(R.id.profile_ipl_name);
        ipl_address = findViewById(R.id.profile_ipl_address);

        str_phno = prefs.getString("id","");
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

        if (!str_phno.isEmpty()) {
            showpg("Getting User Data, please wait");

            myRef.child(str_role).child(str_city).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(str_phno).exists()) {

                        User u = snapshot.child(str_phno).getValue(User.class);
                        if (u != null) {
                            et_phno.setText(u.getMobNo());
                            et_address.setText(u.getAddress());
                            et_name.setText(u.getName());
                            str_role = u.getRole();
                            if(str_role.equalsIgnoreCase("farmer")){
                                rg_role.check(R.id.profile_rb_role_farmer);
                            }else {
                                rg_role.check(R.id.profile_rb_role_customer);
                            }
                        }

                    }
                    closepg();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ProfileFill.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, ""+error.getDetails() );
                    closepg();

                }
            });

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
            } if(str_city.isEmpty()){
                Snackbar.make(v,"Please Select City",BaseTransientBottomBar.LENGTH_LONG).show();

            }else {

                Map<String, Object> u = new HashMap<>();
                u.put("mobNo", str_phno);
                u.put("address", str_address);
                u.put("name", str_name);
                u.put("role", str_role);
                u.put("lat", "lat");
                u.put("lang", "longitude");
                u.put("city", str_city);


                myRef.child("users").child(str_phno).updateChildren(u).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        closepg();

                        Snackbar.make(v, "Data Updated", BaseTransientBottomBar.LENGTH_SHORT).show();

                        Intent i = new Intent(ProfileFill.this, HomeActivity.class);
                        if (fromProfile) {
                            i.putExtra("toProfile", true);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    }else {
                        Snackbar.make(v,"Error Occurred while saving information: "+task.getException().getMessage(),
                                BaseTransientBottomBar.LENGTH_INDEFINITE).show();

                        Log.e(TAG, "onCreate: "+task.getException().getMessage());
                    }
                });
            }

        });

    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, final int id) {
                            dialog.cancel();
                        }
                    });
            final AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void getCurrentAddress() {

        statusCheck();
        // Get the location manager
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (locationManager != null) {

            try {

                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(ProfileFill.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);

                    return;
                }
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000,
                        100, (LocationListener) this);
            } catch (Exception ex) {
                Log.i("msg", "fail to request location update, ignore", ex);
            }
            Location location = locationManager
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            lat = String.valueOf(location.getLatitude());
            longitude = String.valueOf(location.getLongitude());

            Geocoder gcd = new Geocoder(getBaseContext(),
                    Locale.getDefault());

            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String locality = addresses.get(0).getLocality();
                    String subLocality = addresses.get(0).getSubLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();
                    String currentLocation;
                    if (subLocality != null) {

                        currentLocation = locality + "," + subLocality;
                    } else {

                        currentLocation = locality;
                    }

                    et_address.setText(String.format("%s\n%s", address, postalCode));
                }


            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(ProfileFill.this, "Error Fetching Location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void closepg(){
        dialog.dismiss();
    }
    private void showpg(String message){
        if(!message.isEmpty()){
            pgMsg.setText(message);
        }else{
            pgMsg.setText(" ");
        }
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        btn_continue.callOnClick();
    }

}