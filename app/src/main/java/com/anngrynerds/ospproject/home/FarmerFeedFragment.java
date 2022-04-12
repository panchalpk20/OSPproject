package com.anngrynerds.ospproject.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.WeatherClasses.Main;
import com.anngrynerds.ospproject.WeatherClasses.SupportClass;
import com.anngrynerds.ospproject.WeatherClasses.Sys;
import com.anngrynerds.ospproject.WeatherClasses.Wind;
import com.anngrynerds.ospproject.WeatherClasses.weatherAPI;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.ml.ModelUnquant;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FarmerFeedFragment extends Fragment {

    private static final String TAG = "FarmerFeed";
    public final String APP_TAG = "crop";
    private final ActivityResultLauncher<String[]> activityResultLaucherForPermissions;
    public String intermediateName = "1.jpg";
    public String resultName = "2.jpg";
    ActivityResultLauncher<Intent> imageSelectActivityLaunch;
    ActivityResultLauncher<Intent> imageCaptureActivityLaunch;
    ActivityResultLauncher<Intent> cropImageActivityResult;
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
    int imageSize = 224;
    String[] names;
    EditText post_item_name;
    String list;
    FrameLayout notificationIcon;
    TextView notificationBadge;
    Uri intermediateProvider;
    Uri resultProvider;
    ArrayList<String> notificationList = new ArrayList<>();


    public FarmerFeedFragment() {
        // Required empty public constructor


        activityResultLaucherForPermissions = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                    Log.e("activityResultLauncher", "" + result.toString());
                    boolean areAllGranted = true;
                    for (Boolean b : result.values()) {
                        areAllGranted = areAllGranted && b;
                    }
                    if (areAllGranted) {
                        takeImagefromCamera();
                    }
                });

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
        context = view.getContext();

        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);
        fab_addPost = view.findViewById(R.id.fragment_farmerfeed_fb_add_post);


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

        notificationIcon = view.findViewById(R.id.farmer_feed_notification);
        notificationBadge = view.findViewById(R.id.farmer_feed_notificationBadge);


        setUpNotifications();

        setImageCoverAccToTime();

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transparent_loading);
        dialog.setCancelable(false);
        pgMsg = dialog.findViewById(R.id.dialog_pgmsg);


        notificationIcon.setOnClickListener(v->{

           // Toast.makeText(context, notificationList.toString(),Toast.LENGTH_LONG).show();
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
            LinearLayout notificationContainer;

            LayoutInflater factory3 = LayoutInflater.from(getActivity());
            View dialogView = factory3.inflate(R.layout.dialog_notifications, null);
            dialogBuilder.setView(dialogView);

            notificationContainer = dialogView.findViewById(R.id.notification_container);

            for (String msg : notificationList){

                TextView textView = new TextView(context);
                textView.setText(msg);
                textView.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_notification_outlined
                        , 0, 0, 0);


                textView.setBackground(ContextCompat.getDrawable(context, R.drawable.style_tv));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                textView.setCompoundDrawablePadding((int) (5 * getResources().getDisplayMetrics().density));
                textView.setPadding(
                        (int) (4 * getResources().getDisplayMetrics().density), //4dp
                        (int) (4 * getResources().getDisplayMetrics().density),
                        (int) (4 * getResources().getDisplayMetrics().density),
                        (int) (4 * getResources().getDisplayMetrics().density)
                );

//                LayoutParams params = notificationContainer.getLayoutParams();

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) container.getLayoutParams();

                params.setMargins(

                        (int) (0 * getResources().getDisplayMetrics().density), //4dp
                        (int) (0 * getResources().getDisplayMetrics().density),
                        (int) (0 * getResources().getDisplayMetrics().density),
                        (int) (4 * getResources().getDisplayMetrics().density)

                );

                textView.setLayoutParams(params);
                notificationContainer.addView(textView);


            }

            AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();


        });



        databaseReference = FirebaseDatabase.getInstance().getReference();
        initBottomSheetDialog();

        lat = mPrefs.getString(Constantss.STR_Latitude, "");
        lang = mPrefs.getString(Constantss.STR_Longitude, "");
        Log.e(TAG, "variables lat:lang " + lat + ":" + lang);
        weatherProcessing();

        fab_addPost.setOnClickListener(v -> {
            bottomSheetDialog.show();
        });


        cropImageActivityResult = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap cropImage = loadFromUri(resultProvider);
                        filePathList.add(resultProvider.toString());

                        ImageView imageView = new ImageView(context);
                        imageView.setImageBitmap(cropImage);
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
                        try {
                            detectImage();
                        } catch (IOException ioException) {
                            ioException.printStackTrace();
                        }


                    }


                });

        imageCaptureActivityLaunch = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {

                    if (result.getResultCode() == Activity.RESULT_OK) {
                        //Bitmap takenImage = loadFromUri(intermediateProvider);
                        onCropImage();
                    }

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
                                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                try {
                                    detectImage();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }

                            }
                        } else {
//                            Log.e(TAG, "onCreateView: "+data.getData() );
                            Uri uri = null;
                            if (data != null) {
                                uri = data.getData();
                                filePathList.add(uri.toString());
                                //Log.e(TAG, "FilePath " + filePath.toString());
                                Bitmap bitmap = null;
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
                                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                                try {
                                    detectImage();
                                } catch (IOException ioException) {
                                    ioException.printStackTrace();
                                }

                            }
                        }

                    }
                });

        return view;
    }

    private void setUpNotifications() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        notificationList.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            //       Log.e(TAG, "Feed" + s.toString());
                            PostObject p = s.getValue(PostObject.class);
                            if (p != null)
                                if (p.getFilePathList() != null)
                                    if (p.getPost_id().split("-")[0].equalsIgnoreCase(user.getMobNo())) {
                                        // Log.e(TAG, "My " + p.toString() );
                                        if (p.getReportCount() > 10) {
                                            notificationList.add("Your post of "+p.getItem_name()
                                                    +"(post id: "+p.getPost_id()+")" +
                                                    "has been disabled because of excessive reporting");
                                        } else if (p.getReportCount() > 0 && p.getReportCount() < 10) {
                                            notificationList.add("Your posts of "+p.getItem_name()
                                                    +"(post id: "+p.getPost_id()+")" +
                                                    "has been reported "+p.getReportCount()+" times for the inaccurate information");
                                        }
                                    }

                        }
                        if (notificationList.isEmpty()) {
                            notificationBadge.setVisibility(View.GONE);
                        } else {
                            notificationBadge.setText("" + notificationList.size());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }

    private void onCropImage() {
        context.grantUriPermission("com.android.camera",
                intermediateProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                        Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(intermediateProvider, "image/*");

        List<ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent, 0);

        int size = 0;

        if (list != null) {
            context.grantUriPermission(list.get(0).activityInfo.packageName, intermediateProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            size = list.size();
        }

        if (size == 0) {
            Toast.makeText(context, "Error, wasn't taken image!", Toast.LENGTH_SHORT).show();
        } else {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            intent.putExtra("crop", "true");

            intent.putExtra("scale", true);

            File photoFile = getPhotoFileUri(resultName);
            resultProvider = FileProvider.getUriForFile(context, "com.photostream.crop.fileprovider", photoFile);

            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, resultProvider);
            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

            Intent cropIntent = new Intent(intent);
            ResolveInfo res = list.get(0);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cropIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            context.grantUriPermission(res.activityInfo.packageName, resultProvider, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            cropIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            cropImageActivityResult.launch(cropIntent);
        }
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
                // on newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(context.getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(context.getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    private void setImageCoverAccToTime() {

        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        Log.e(TAG, "setImageCoverAccToTime: " + timeOfDay);

        if (timeOfDay < 5) {
            img_cover.setImageResource(R.drawable.img_night);
            Toast.makeText(context, "Good Night", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay > 5 && timeOfDay < 12) {
            img_cover.setImageResource(R.drawable.img_morning);
            Toast.makeText(context, "Good Morning", Toast.LENGTH_SHORT).show();
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            img_cover.setImageResource(R.drawable.img_noon);
            Toast.makeText(context, "Good Afternoon", Toast.LENGTH_SHORT).show();

        } else if (timeOfDay >= 16 && timeOfDay < 20) {
            img_cover.setImageResource(R.drawable.img_evening);
            Toast.makeText(context, "Good Evening", Toast.LENGTH_SHORT).show();

        } else if (timeOfDay >= 20) {
            img_cover.setImageResource(R.drawable.img_night);
            Toast.makeText(context, "Good Night", Toast.LENGTH_SHORT).show();

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
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.dialog_for_imageandcamera, null);
            builder.setCancelable(false);
            builder.setView(view);

            ImageView imageView_Camera = view.findViewById(R.id.image_Camera);
            ImageView imageView_Gallery = view.findViewById(R.id.image_gallery);


            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            imageView_Camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //if(checkCameraPermission())
                    takeImagefromCamera();
                    alertDialog.cancel();

                }
            });

            imageView_Gallery.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                    alertDialog.cancel();
                }
            });


            //selectImage();
        });




                /*bottomSheetDialog.findViewById(R.id.bs_post_btn_takeImage).setOnClickListener(v->{

                        if (ActivityCompat.checkSelfPermission(context,
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                                String[] appPerms = new String[]{Manifest.permission.CAMERA};
                                this.activityResultLaucherForPermissions.launch(appPerms);

                        }else {

                                takeImagefromCamera();

                        }
                });*/

        bottomSheetDialog.findViewById(R.id.bs_post_btn_submit).setOnClickListener(v -> {

            Log.e(TAG, "FilePath " + filePathList.toString());

            if (post_item_name.getText().toString().isEmpty()) {
                inputLayout_item_name.setError("Enter Name of the item");
            } else if (post_item_qty.getText().toString().isEmpty()) {
                inputLayout_item_qty.setError("Enter Name of the item");
            } else if (post_item_price.getText().toString().isEmpty()) {
                inputLayout_item_price.setError("Enter Name of the item");
            } else if (filePathList == null) {
                Toast.makeText(context, "Select Atlas one Image", Toast.LENGTH_LONG).show();
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

    private void takeImagefromCamera() {


        Toast.makeText(context, "camera permission granted", Toast.LENGTH_LONG).show();
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = getPhotoFileUri(intermediateName);
        intermediateProvider = FileProvider.getUriForFile(context,
                "com.photostream.crop.fileprovider",
                photoFile);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, intermediateProvider);
        imageCaptureActivityLaunch.launch(cameraIntent);
    }


    public File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(""), APP_TAG);
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("APP_TAG", "failed to create directory");
        }
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
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
                "",   //setting initial distance to 0
                0 //setting initial reports count 0
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
        Intent takePhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


        takePhoto.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        imageSelectActivityLaunch.launch(takePhoto);

    }

    private void closepg() {
        dialog.dismiss();
    }

    private void showpg() {
        pgMsg.setText("Fetching weather data");
        dialog.show();
    }

    private void detectImage() throws IOException {
        // bm=((BitmapDrawable)imageView.getDrawable()).getBitmap();
        for (int i = 0; i < filePathList.size(); i++) {
            list = filePathList.get(i);

        }
        Bitmap bm = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.parse(list));

        int dimension = Math.min(bm.getWidth(), bm.getHeight());

        bm = ThumbnailUtils.extractThumbnail(bm, dimension, dimension);

        bm = Bitmap.createScaledBitmap(bm, imageSize, imageSize, false);

        try {
            //  Model model = Model.newInstance(getApplicationContext());
            ModelUnquant model = ModelUnquant.newInstance(context);


            // Creates inputs for reference.
            TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3);
            byteBuffer.order(ByteOrder.nativeOrder());

            // get 1D array of 224 * 224 pixels in image
            int[] intValues = new int[imageSize * imageSize];
            bm.getPixels(intValues, 0, bm.getWidth(), 0, 0, bm.getWidth(), bm.getHeight());

            // iterate over pixels and extract R, G, and B values. Add to bytebuffer.
            int pixel = 0;
            for (int i = 0; i < imageSize; i++) {
                for (int j = 0; j < imageSize; j++) {
                    int val = intValues[pixel++]; // RGB
                    byteBuffer.putFloat(((val >> 16) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat(((val >> 8) & 0xFF) * (1.f / 255.f));
                    byteBuffer.putFloat((val & 0xFF) * (1.f / 255.f));
                }
            }

            inputFeature0.loadBuffer(byteBuffer);

            // Runs model inference and gets result.

            ModelUnquant.Outputs outputs = model.process(inputFeature0);
            TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

            float[] confidences = outputFeature0.getFloatArray();
            // find the index of the class with the biggest confidence.
            int maxPos = 0;
            float maxConfidence = 0;
            for (int i = 0; i < confidences.length; i++) {
                if (confidences[i] > maxConfidence) {
                    maxConfidence = confidences[i];
                    maxPos = i;
                }
            }
            String[] classes = {"tomato", "onion", "brinjal",
                    "cabbage",
                    "carrot",
                    "drumstick",
                    "lady finger"};
            String max = classes[maxPos];
            try {
                String[] stringArray = new String[classes.length];   //Declarartion by specifying the size
                for (int j = 0; j < classes.length; j++) {
                    stringArray[j] = max;
                }
                for (int i = 0; i < stringArray.length; i++) {
                    if (stringArray[0] == stringArray[i + 1]) {
                        Toast.makeText(context, stringArray[0], Toast.LENGTH_SHORT).show();
                        post_item_name.setText(stringArray[0]);

                    } else {
                        Toast.makeText(context, "errorrrrrrrrrrrrrrrr", Toast.LENGTH_SHORT).show();

                    }
                }
            } catch (Exception e) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show();
            }

            // names=new String[classes.length];
            //  Toast.makeText(context, max, Toast.LENGTH_SHORT).show();
            // textView.setText(classes[maxPos]);


            String s = "";
            for (int i = 0; i < classes.length; i++) {
                s += String.format("%s: %.1f%%\n", classes[i], confidences[i] * 100);
            }
            // textView1.setText(s);


            // Releases model resources if no longer used.
            model.close();
        } catch (IOException e) {
            // TODO Handle the exception
        }
    }
}