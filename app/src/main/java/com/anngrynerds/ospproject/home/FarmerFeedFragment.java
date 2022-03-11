package com.anngrynerds.ospproject.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.WeatherClasses.Main;
import com.anngrynerds.ospproject.WeatherClasses.SupportClass;
import com.anngrynerds.ospproject.WeatherClasses.Sys;
import com.anngrynerds.ospproject.WeatherClasses.Wind;
import com.anngrynerds.ospproject.WeatherClasses.weatherAPI;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FarmerFeedFragment extends Fragment {

    private static final String TAG = "FarmerFeed";

    ActivityResultLauncher<Intent> imageSelectActivityLaunch;
    ArrayList<String> filePathList = new ArrayList<>();
    ArrayList<String> downloadUrl = new ArrayList<>();
    Context context;
    BottomSheetDialog bottomSheetDialog;
    LinearLayout bs_imageContainer;
    StorageReference storageReference;
    String lat;
    String lang;
    ExtendedFloatingActionButton fab_addPost;
    ProgressBar bs_progressBar;
    User user;
    DatabaseReference databaseReference;
    TextView tvCity;
    TextView tvTemp;
    TextView tvFeelslike;
    TextView tvMin;
    TextView tvMax;
    TextView tvWind;
    TextView tvHumidity;
    TextView tvPressure;
    TextView tvSunrise;
    TextView tvSunset;
    TextView tvClouds;
    ImageView img_cover;
    ImageView img_cloudIcon;
    TextView time;
    LinearLayout layout;
    String api_key = "d3ba099653be9ae93894fd0c30529ed3";
    //  String city="sangli";
    TextView pgMsg;
    Dialog dialog;

    public FarmerFeedFragment() {
        // Required empty public constructor
    }

    public static FarmerFeedFragment newInstance() {
        return new FarmerFeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_farmer_feed, container, false);

        tvCity = view.findViewById(R.id.tv_city);
        tvTemp = view.findViewById(R.id.tv_temp);
        tvFeelslike = view.findViewById(R.id.tv_feelslike);
        tvMin = view.findViewById(R.id.tv_mintemp);
        tvMax = view.findViewById(R.id.tv_maxtemp);
        tvWind = view.findViewById(R.id.tv_wind);
        tvHumidity = view.findViewById(R.id.tv_humidity);
        tvPressure = view.findViewById(R.id.tv_pressure);
        tvSunrise = view.findViewById(R.id.tv_sunrise);
        tvSunset = view.findViewById(R.id.tv_sunset);
        layout = view.findViewById(R.id.linear);
        tvClouds = view.findViewById(R.id.tv_clouds);
        img_cloudIcon = view.findViewById(R.id.iv_cloudIcon);
        img_cover = view.findViewById(R.id.weather_iv_cover);
        time = view.findViewById(R.id.tv_time);

        setImageCoverAccToTime();
        context = view.getContext();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transparent_loading);
        dialog.setCancelable(false);
        pgMsg = dialog.findViewById(R.id.dialog_pgmsg);


        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);
        fab_addPost = view.findViewById(R.id.fragment_farmerfeed_fb_add_post);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        initBottomSheetDialog();

        lat = mPrefs.getString(Constantss.STR_Latitude, "");
        lang = mPrefs.getString(Constantss.STR_Longitude, "");
        Log.e(TAG, "variables lat:lang " + lat + ":" + lang);
        weatherProcessing();

        fab_addPost.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });

        imageSelectActivityLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null && data.getClipData() != null) {
                            ClipData mClipData = data.getClipData();
                            for (int i = 0; i < mClipData.getItemCount(); i++) {

                                ClipData.Item item = mClipData.getItemAt(i);
                                Uri uri = item.getUri();
                                filePathList.add(uri.toString());

                                //Log.e(TAG, "FilePath " + filePath.toString());

                                Bitmap bitmap = null;

                                //Log.e(TAG, "onCreateView: " + filePath.toString());

                                try {
                                    bitmap = MediaStore
                                            .Images
                                            .Media
                                            .getBitmap(
                                                    context.getContentResolver(),
                                                    uri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                ImageView imageView = new ImageView(context);
                                imageView.setImageBitmap(bitmap);
                                ViewGroup.LayoutParams params = bs_imageContainer.getLayoutParams();
                                params.height = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        250,
                                        getResources().getDisplayMetrics());
                                params.width = (int) TypedValue.applyDimension(
                                        TypedValue.COMPLEX_UNIT_DIP,
                                        400,
                                        getResources().getDisplayMetrics());

                                imageView.setLayoutParams(params);
                                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                bs_imageContainer.addView(imageView);

                            }
                        }

                    }
                });

        return view;
    }

    private void setImageCoverAccToTime() {

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay < 12) {
            img_cover.setImageResource(R.drawable.img_morning);
        } else if (timeOfDay < 16) {
            img_cover.setImageResource(R.drawable.img_noon);
        } else if (timeOfDay < 21) {
            img_cover.setImageResource(R.drawable.img_evening);
        } else {
            img_cover.setImageResource(R.drawable.img_night);
        }


    }

    private void weatherProcessing() {
//        String url="https://api.openweathermap.org/data/2.5/weather?q="
//                +city+"&appid=b5a7dde7feb96ae2a83514dfa7ce965b";


//        api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={API key}

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherAPI weatherAPI = retrofit.create(weatherAPI.class);

        Call<SupportClass> supportClassCall = weatherAPI.getweather(String.valueOf(lat),
                String.valueOf(lang),
                api_key);

        showpg();

        supportClassCall.enqueue(new Callback<SupportClass>() {
            @Override
            public void onResponse(@NonNull Call<SupportClass> call, @NonNull Response<SupportClass> response) {
                if (response.code() == 404) {
                    Toast.makeText(context, "Please enter valid data", Toast.LENGTH_LONG).show();
                    closepg();

                } else if (!(response.isSuccessful())) {
                    Toast.makeText(context, response.code(), Toast.LENGTH_LONG).show();

                }
                closepg();

                    //location + temperature + feels like-min, max tmp + wind + humidity + pressure
                    SupportClass supportClass = response.body();

                    Main main = supportClass.getMain();

                    tvCity.setText(supportClass.getName());

//                Log.e(TAG, "onResponse: "+supportClass.getName());

                    Double temp = main.getTemp();
                    Integer temperature = (int) (temp - 273.15);
                    tvTemp.setText(String.valueOf(temperature));

                    Double Feellike = main.getTempMin();
                    Integer feel = (int) (Feellike - 273.15);
                    tvFeelslike.setText(String.valueOf(feel));

                    Double mintemp = main.getTempMin();
                    Integer MinTemp = (int) (mintemp - 273.15);
                    tvMin.setText(String.valueOf(MinTemp));

                    Double maxtemp = main.getTempMin();
                    Integer MaxTemp = (int) (maxtemp - 273.15);
                    tvMax.setText(String.valueOf(MaxTemp));

                    Wind wind = supportClass.getWind();
                    Double speed = wind.getSpeed();
                    tvWind.setText(String.valueOf(speed));

                    tvHumidity.setText(String.valueOf(main.getHumidity()));
                    tvPressure.setText(String.valueOf(main.getPressure()));

                    Sys sys = supportClass.getSys();

                    long sunrise = sys.getSunrise();
                    Date date1 = new Date(sunrise * 1000);

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                    tvSunrise.setText(sdf.format(date1));

                    time.setText(sdf.format(Calendar.getInstance().getTime()));


                    long sunset = sys.getSunset();
                    Date date2 = new Date(sunset * 1000);
                    tvSunset.setText(sdf.format(date2));

                    tvClouds.setText(supportClass.getWeather().get(0).getDescription());
                    String weatherIcon = supportClass.getWeather().get(0).getIcon();
                    String iconUrl = "https://openweathermap.org/img/wn/" + weatherIcon + "@4x.png";

                    Log.e(TAG, "onResponse: " + weatherIcon);

                    Glide.with(context)
                            .load(iconUrl)
                            .into(img_cloudIcon);

                    // String icon=weather.getIcon();
                    //String com=icon+cloud;


            }

            @Override
            public void onFailure(@NonNull Call<SupportClass> call, @NonNull Throwable t) {
                Toast.makeText(context, t.getMessage(), Toast.LENGTH_LONG).show();
                //  tvClouds.setText(t.getMessage());

            }
        });

    }

    private void initBottomSheetDialog() {


        storageReference = FirebaseStorage.getInstance().getReference();

        bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_add_post);
        EditText post_item_name = bottomSheetDialog.findViewById(R.id.bs_post_et_item_name);
        EditText post_item_qty = bottomSheetDialog.findViewById(R.id.bs_post_et_item_count);
        EditText post_item_price = bottomSheetDialog.findViewById(R.id.bs_post_et_item_price);

        bs_progressBar = bottomSheetDialog.findViewById(R.id.bs_progressBar);
        assert bs_progressBar != null;
        bs_progressBar.setVisibility(View.GONE);

        TextInputLayout inputLayout_item_name =
                bottomSheetDialog.findViewById(R.id.bs_post_ipl_item_name);
        TextInputLayout inputLayout_item_qty =
                bottomSheetDialog.findViewById(R.id.bs_post_ipl_item_count);
        TextInputLayout inputLayout_item_price =
                bottomSheetDialog.findViewById(R.id.bs_post_ipl_item_price);

        assert post_item_name != null;
        assert inputLayout_item_name != null;
        assert post_item_qty != null;
        assert inputLayout_item_qty != null;
        assert inputLayout_item_price != null;
        assert post_item_price != null;

        bs_imageContainer = bottomSheetDialog.findViewById(R.id.bs_image_container);

        bottomSheetDialog.findViewById(R.id.bs_post_btn_uploadImage).setOnClickListener(v -> {
            selectImage();
        });

        bottomSheetDialog.findViewById(R.id.bs_post_btn_submit).setOnClickListener(v -> {

            Log.e(TAG, "FilePath " + filePathList.toString());

            if (post_item_name.getText().toString().isEmpty()) {
                inputLayout_item_name.setError("Enter Name of the item");
            } else if (post_item_qty.getText().toString().isEmpty()) {
                inputLayout_item_qty.setError("Enter Name of the item");
            } else if (post_item_price.getText().toString().isEmpty()) {
                inputLayout_item_price.setError("Enter Name of the item");
            } else if (filePathList == null) {
                Toast.makeText(context, "Select Atleast one Image", Toast.LENGTH_LONG).show();
            } else {

                //good to go, creating post
                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy", Locale.getDefault());
                String str_date = df.format(c);

                createPost(
                        post_item_name.getText().toString(),
                        post_item_qty.getText().toString(),
                        post_item_price.getText().toString(),
                        filePathList,
                        str_date,
                        user.getMobNo()
                );

            }
        });

    }

    private void createPost(String str_item_name,
                            String str_item_qty,
                            String str_item_price,
                            ArrayList<String> filePathList,
                            String str_date,
                            String str_mobno
    ) {
        downloadUrl.clear();

        bs_progressBar.setVisibility(View.VISIBLE);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy-hhmmss", Locale.getDefault());
        String code = df.format(c);
        String post_id = user.getMobNo() + "-" + code;

        PostObject newPost = new PostObject(
                str_item_name,
                str_item_qty,
                str_item_price,
                downloadUrl,
                str_date,
                str_mobno,
                post_id,
                lat,
                lang,
                ""   //setting initial distance to 0
        );


        databaseReference.child(user.getCity()).
                child(Constantss.postsNode)
                .child(post_id)
                .setValue(newPost)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        bs_progressBar.setVisibility(View.VISIBLE);
//                        bottomSheetDialog.dismiss();
//                        Toast
//                                .makeText(context,
//                                        "Post Created!!",
//                                        Toast.LENGTH_SHORT)
//                                .show();


                        for (String filePath : filePathList) {


                            StorageReference storageRef
                                    = storageReference
                                    .child(
                                            "postImages/"
                                                    + UUID.randomUUID().toString());

                            storageRef.putFile(Uri.parse(filePath))
                                    .addOnSuccessListener(taskSnapshot -> {


                                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                            downloadUrl.add(uri.toString());
                                            databaseReference.child(user.getCity()).
                                                    child(Constantss.postsNode)
                                                    .child(post_id)
                                                    .child("filePathList")
                                                    .setValue(downloadUrl);

                                        });


                                    });

                        }

                        bottomSheetDialog.dismiss();


                    } else {
                        bs_progressBar.setVisibility(View.GONE);
                        //bottomSheetDialog.dismiss();
                        Toast
                                .makeText(context,
                                        "Unable to create post " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT)
                                .show();

                        Log.e(TAG, "createPost: " + task.getException().getMessage());
                    }


                });
    }


    private void selectImage() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageSelectActivityLaunch.launch(intent);

    }

    private void closepg(){
        dialog.dismiss();
    }
    private void showpg(){
        pgMsg.setText("Fetching weather data");
        dialog.show();
    }
}