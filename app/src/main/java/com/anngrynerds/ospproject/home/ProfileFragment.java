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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.login.ProfileFill;
import com.anngrynerds.ospproject.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
        Context context = view.getContext();
        pg = view.findViewById(R.id.profile_page_pg);
        tv_name = view.findViewById(R.id.profile_page_tv_name);
        tv_address = view.findViewById(R.id.profile_page_tv_address);
        tv_mobileNo = view.findViewById(R.id.profile_page_tv_mobile_no);
        myRef = FirebaseDatabase.getInstance().getReference();
        SharedPreferences prefs = this.requireActivity().getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        str_phno = prefs.getString("id", "");

        if (!str_phno.isEmpty()) {
            pg.setVisibility(View.VISIBLE);
            myRef.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child(str_phno).exists()) {

                        User u = snapshot.child(str_phno).getValue(User.class);
                        if (u != null) {
                            tv_name.setText(u.getName());
                            tv_address.setText(u.getAddress());
                            tv_mobileNo.setText(u.getMobNo());
                        }
                    }
                    pg.setVisibility(View.INVISIBLE);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(view.getContext(),
                            "Error Occurred: " + error.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    pg.setVisibility(View.INVISIBLE);
                }
            });
        }


        view.findViewById(R.id.profile_page_btn_).setOnClickListener(v -> {

            Intent i = new Intent(context, ProfileFill.class);
            i.putExtra("fromProfile", true);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(i);
        });

        return view;
    }
}