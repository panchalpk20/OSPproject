package com.anngrynerds.ospproject.home;

import android.app.Activity;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
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


        context = view.getContext();
        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);
        fab_addPost = view.findViewById(R.id.fragment_farmerfeed_fb_add_post);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        initBottomSheetDialog();

        lat = user.getLat();
        lang = user.getLang();
        Log.e(TAG, "variables lat:lang "+lat+":"+lang );

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







}