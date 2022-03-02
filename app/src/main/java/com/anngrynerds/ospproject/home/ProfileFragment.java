package com.anngrynerds.ospproject.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.adapters.FeedAdapter;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.login.ProfileFill;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    User user;
    FeedAdapter adapter;

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
        myRef = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = this.requireActivity().getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        str_phno = prefs.getString("id", "");

        Gson gson = new Gson();
        String json1 = prefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json1, User.class);

        tv_name.setText(user.getName());
        tv_address.setText(user.getAddress());
        tv_mobileNo.setText(user.getMobNo());

        postsContainer = view.findViewById(R.id.profile_posts_container);
        rc = view.findViewById(R.id.profile_rc);
        rc.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        if(user.getRole().equalsIgnoreCase(Constantss.ROLE_CUSTOMER)){
            postsContainer.setVisibility(View.GONE);
        }else{
            loadFarmerPosts();
        }


        view.findViewById(R.id.profile_page_btn_).setOnClickListener(v -> {

            Intent i = new Intent(context, ProfileFill.class);
            i.putExtra("fromProfile", true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        });

        return view;
    }

    private void loadFarmerPosts() {

        ArrayList<PostObject> list = new ArrayList<>();

        myRef.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
             //   Log.e(TAG, "loading farmers posts: "+snapshot.getChildrenCount() );

                for (DataSnapshot s: snapshot.getChildren()){
                    Log.e(TAG, "Feed" + s.toString() );
                    PostObject p = s.getValue(PostObject.class);
                    if(p!=null)
                    if(p.getPost_id().split("-")[0].equalsIgnoreCase(user.getMobNo())){
                       // Log.e(TAG, "My " + p.toString() );
                        list.add(p);
                    }

                }

                adapter = new FeedAdapter(list,context, true);
                rc.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }
}