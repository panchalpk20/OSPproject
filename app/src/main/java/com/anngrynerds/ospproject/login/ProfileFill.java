package com.anngrynerds.ospproject.login;

import static android.provider.MediaStore.MediaColumns.DISPLAY_NAME;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileFill extends AppCompatActivity {

    private static final String TAG = "PROFILE_FRAG";
    TextView temp;
    EditText et_phno;
    EditText et_name;
    EditText et_address;
    Uri uri = null;
    ActivityResultLauncher<Intent> imageCaptureActivityLaunch;
    StorageReference storageReference;





    TextInputLayout ipl_name;
    TextInputLayout ipl_number;
    TextInputLayout ipl_address;

    TextView pgMsg;

    ProgressBar pg;
    Button btn_continue;

    String str_phno;
    String str_name;
    String str_address;
    String str_role= "";
    String str_city;

    Spinner spinner_citySelect;
    FirebaseDatabase database;
    DatabaseReference myRef;
    RadioGroup rg_role;

    boolean fromProfile;
    Dialog dialog;
    String[] cities;

    String str_lat, str_long;
    LinearLayout l1,l2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_fill);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        temp = findViewById(R.id.temp);

        // getCurrentAddress();
        storageReference = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        SharedPreferences prefs = this.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        cities = getResources().getStringArray(R.array.cities);

        str_lat = prefs.getString("Latitude", "");
        str_long = prefs.getString("Longitude", "");
        str_phno = prefs.getString("id", "");

        fromProfile = getIntent().getBooleanExtra("fromProfile", false);


        rg_role = findViewById(R.id.profile_rg_role);
        ipl_number = findViewById(R.id.profile_ipl_mobile_number);
        ipl_name = findViewById(R.id.profile_ipl_name);
        ipl_address = findViewById(R.id.profile_ipl_address);

        et_phno = findViewById(R.id.profile_mobile_number);
        et_address = findViewById(R.id.profile_address);
        et_name = findViewById(R.id.profile_name);
        btn_continue = findViewById(R.id.profile_btn_continue);
        //l1=findViewById(R.id.r1);
        //l2=findViewById(R.id.r2);

        et_phno.setText(str_phno);




        


        if(fromProfile){

            //todo uncomment following line
//            rg_role.setVisibility(View.GONE);
        }
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

            showpg(getString(R.string.updatingdata));

            str_phno = et_phno.getText().toString();
            str_name = et_name.getText().toString();
            str_address = et_address.getText().toString();

            if (str_name.isEmpty()) {
                ipl_name.setError(getString(R.string.entername));
                et_name.requestFocus();
                closepg();
            } else if (str_name.length() < 4) {
                ipl_name.setError(getString(R.string.nameshould));
                et_name.requestFocus();
                closepg();
            } else if (str_address.isEmpty()) {
                ipl_address.setError(getString(R.string.enteraddress));
                et_address.requestFocus();
                closepg();
            } else if (str_address.length() < 10) {
                ipl_address.setError(getString(R.string.addressshould));
                et_address.requestFocus();
                closepg();
            } else if (str_city.isEmpty()) {
                Snackbar.make(v, R.string.selectcity, BaseTransientBottomBar.LENGTH_LONG).show();
                closepg();

            } else if (str_role.isEmpty()) {
                Snackbar.make(v, R.string.selectrole, BaseTransientBottomBar.LENGTH_LONG).show();
                closepg();

            } else {
                //String uid = FirebaseAuth.getInstance().getCurrentUser().getUid()+"jpeg";
                String pune=Constantss.citypune;
                String mumabi=Constantss.citymumbai;

                Map<String, Object> u = new HashMap<>();
                u.put("mobNo", str_phno);
                u.put("address", str_address);
                u.put("name", str_name);
                u.put("role", str_role);
                u.put("lat", str_lat);
                u.put("lang", str_long);
                if(str_city.equalsIgnoreCase("Pune") || str_city.equals("पुणे")){
                    str_city=pune;
                    u.put("city", str_city);
                }
                else {
                    str_city=mumabi;
                    u.put("city",str_city);
                }
                u.put("imgProfile",uri);

                myRef.child(str_city).child(str_role).child(str_phno).updateChildren(u).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        closepg();

                        Snackbar.make(v, "Data Updated", BaseTransientBottomBar.LENGTH_SHORT).show();

                        SharedPreferences.Editor editor = prefs.edit();
                        JsonElement jsonElement = gson.toJsonTree(u);
                        User user = gson.fromJson(jsonElement, User.class);
                        String json = gson.toJson(user);
                        editor.putString(Constantss.sharedPrefUserKey, json);
                        editor.apply();

                        closepg();

                        Intent i = new Intent(ProfileFill.this, HomeActivity.class);
                        if (fromProfile) {
                            i.putExtra("toProfile", true);
                        }
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                    } else {
                        Snackbar.make(v, getString(R.string.errorsnak) + task.getException().getMessage(),
                                BaseTransientBottomBar.LENGTH_INDEFINITE).show();

                        Log.e(TAG, "onCreate: " + task.getException().getMessage());
                    }
                });
            }

        });

    }


    /*private void handleUpload(Uri uri) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid()+"jpg";
        StorageReference fileref=storageReference.child("profileImages/"+uid);
        fileref.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ProfileFill.this).load(uri).into(profileImg);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }*/

    private void getDownloadUrl(StorageReference reference) {
        reference.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.d(TAG, "onSuccess: " + uri);
                        setUserProfileUrl(uri);
                    }
                });
    }

    private void setUserProfileUrl(Uri uri) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build();

        user.updateProfile(request)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(ProfileFill.this, "Updated succesfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileFill.this, "Profile image failed...", Toast.LENGTH_SHORT).show();
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