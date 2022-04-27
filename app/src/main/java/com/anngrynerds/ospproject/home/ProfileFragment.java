package com.anngrynerds.ospproject.home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.Select_language;
import com.anngrynerds.ospproject.adapters.FeedAdapter;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.login.LoginActivity;
import com.anngrynerds.ospproject.login.ProfileFill;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.util.ArrayList;


public class ProfileFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "ProfileFrag";
    DatabaseReference myRef;
    String str_phno;
    TextView tv_name;
    TextView tv_mobileNo;
    TextView tv_address;
    ProgressBar pg;
    Button btn_edit;
    ImageView op;
    User user;
    FeedAdapter adapter;
    ImageView profileimg;
    ImageButton imageButton;
    String uri;
    StorageReference storageReference;
    TextView editprofile,logoutprofie;


    //for farmer posts
    CardView postsContainer;
    RecyclerView rc;
    Context context;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        /*  Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = view.getContext();
        pg = view.findViewById(R.id.profile_page_pg);
        tv_name = view.findViewById(R.id.profile_page_tv_name);
        tv_address = view.findViewById(R.id.profile_page_tv_address);
        tv_mobileNo = view.findViewById(R.id.profile_page_tv_mobile_no);
        profileimg=view.findViewById(R.id.profile_imageView);
        imageButton=view.findViewById(R.id.Btlanguage);
        myRef = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = this.requireActivity().getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        str_phno = prefs.getString("id", "");


        btn_edit = view.findViewById(R.id.profile_page_btn_edit);
        op=view.findViewById(R.id.options);

        Gson gson = new Gson();
        String json1 = prefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json1, User.class);
        if(user.getRole().equalsIgnoreCase("Farmer")) {
            btn_edit.setText(R.string.farmer);
        }else {
            btn_edit.setText(R.string.Customer);
        }
        tv_name.setText(user.getName());
        tv_address.setText(user.getAddress());
        tv_mobileNo.setText(user.getMobNo());
        storageReference = FirebaseStorage.getInstance().getReference();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid()+"jpg";
       // StorageReference storageReference1=storageReference.child("profileImages/"+uid);
      /*  storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(context).load(uri);
            }
        });


       /* if (firebaseUser != null) {
            Log.d(TAG, "onCreate: " + firebaseUser.getDisplayName());
            if (firebaseUser.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(firebaseUser.getPhotoUrl())
                        .into(profileimg);
            }
        }*/

        //uri=user.getUserProfile();
        //tv_address.setText(user.getUserProfile());
        //Toast.makeText(context,uri,Toast.LENGTH_SHORT).show();
      // Glide.with(context).load(Uri.parse(uri)).into(profileimg);

        postsContainer = view.findViewById(R.id.profile_posts_container);
        rc = view.findViewById(R.id.profile_rc);
        rc.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        if (user.getRole().equalsIgnoreCase(Constantss.ROLE_CUSTOMER)) {
            postsContainer.setVisibility(View.GONE);
            setMargins(profileimg, 0, 250, 0, 0);
            setMargins(tv_name,0,10,0,0);
            setMargins(tv_mobileNo,0,10,0,0);
            setMargins(tv_address,0,10,0,0);



        } else {
            loadFarmerPosts();
        }
        if(user.getRole().equalsIgnoreCase((Constantss.ROLE_CUSTOMER)))
        {
            profileimg.setImageResource(R.drawable.market_trader);
        }
        else{
            profileimg.setImageResource(R.drawable.farmer3);
        }

        view.findViewById(R.id.profile_page_btn_myorders).setOnClickListener(v->{

            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_activity_frame_layout, OrdersFragment.newInstance())
                    .addToBackStack(null)
                    .commit();
        });

      /*  view.findViewById(R.id.profile_page_btn_).setOnClickListener(v -> {

            Intent i = new Intent(context, ProfileFill.class);
            i.putExtra("fromProfile", true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        });*/

       /* logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder= new AlertDialog.Builder(context);

                builder.setMessage("Are you sure?").setTitle("Logout?");

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                logoutt();

                            }
                        }) .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        dialog.cancel();

                    }
                });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.show();

        }
            });*/

//ratePw.addView(layout,parm1);  // removed it
        //ratePw.setContentView(layout);
        LayoutInflater layoutInflater= (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.dropdownmenulayout, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,250, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        editprofile=popupView.findViewById(R.id.profile_page_btn_);
        logoutprofie=popupView.findViewById(R.id.logoutuser);
         op.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                else {
                    popupWindow.showAsDropDown(op, 50, 0);
                }
            }
        });
         imageButton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i=new Intent(context, Select_language.class);
                 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 context.startActivity(i);
             }
         });

         editprofile.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent i = new Intent(context, ProfileFill.class);
                 i.putExtra("fromProfile", true);
                 i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                 context.startActivity(i);
             }
         });

         logoutprofie.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 AlertDialog.Builder builder= new AlertDialog.Builder(context);

                 builder.setMessage(R.string.rusure).setTitle(R.string.logoutt);

                 //Setting message manually and performing action on button click
                 builder.setMessage(R.string.rusure)
                         .setCancelable(false)
                         .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                             public void onClick(DialogInterface dialog, int id) {
                                 logoutt();

                             }
                         }) .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                     public void onClick(DialogInterface dialog, int id) {
                         //  Action for 'NO' Button
                         dialog.cancel();

                     }
                 });
                 //Creating dialog box
                 AlertDialog alert = builder.create();
                 //Setting the title manually
                 alert.show();

             }

         });

        return view;
    }


    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    private void logoutt() {
        FirebaseAuth.getInstance().signOut();
        context.startActivity(new Intent(context, LoginActivity.class));

    }

    private void loadFarmerPosts() {

        ArrayList<PostObject> list = new ArrayList<>();

        myRef.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        //   Log.e(TAG, "loading farmers posts: "+snapshot.getChildrenCount() );
                        list.clear();
                        for (DataSnapshot s : snapshot.getChildren()) {
                     //       Log.e(TAG, "Feed" + s.toString());
                            PostObject p = s.getValue(PostObject.class);
                                if (p != null)
                                    if (p.getFilePathList() != null)
                                        if (p.getPost_id().split("-")[0].equalsIgnoreCase(user.getMobNo())) {
                                        // Log.e(TAG, "My " + p.toString() );
                                        list.add(p);
                                    }

                        }

                        adapter = new FeedAdapter(list, context, true);

                        rc.setAdapter(adapter);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


    }
}