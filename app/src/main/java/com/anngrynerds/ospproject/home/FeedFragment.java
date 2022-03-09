package com.anngrynerds.ospproject.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.adapters.FeedAdapter;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    Context context;
    DatabaseReference databaseReference;
    User user;
    RecyclerView rc;
    FeedAdapter adapter;
    EditText et_searchQry;
    Button btn_search;
    DatabaseReference ref;
    ArrayList<PostObject> list = new ArrayList<>();
    TextView searchDesc;
    CheckBox checkBox_sort;

    public FeedFragment() {
        // Required empty public constructor
    }

    public static FeedFragment newInstance() {
        return new FeedFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        context = view.getContext();

        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);

        checkBox_sort = view.findViewById(R.id.feed_checkbox_sort);

        et_searchQry = view.findViewById(R.id.feed_et_searchqry);
        btn_search = view.findViewById(R.id.btn_search);

        searchDesc = view.findViewById(R.id.feed_t_searchdesc);
        searchDesc.setVisibility(View.GONE);


        checkBox_sort.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                sortByDistance();
            }else{
               // dontSort();
            }

        });

        et_searchQry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                fireBaseSearch(s.toString());
            }
        });


        searchDesc.setOnClickListener(v -> {
            noSearch();
            searchDesc.setVisibility(View.GONE);
        });

        //recyclerView
        rc = view.findViewById(R.id.fragment_feed_rc);
        rc.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

        ref = FirebaseDatabase.getInstance()
                .getReference();

        adapter = new FeedAdapter(list, context, false);

       // Log.e(TAG, "onCreateView: " + user.getCity());

        double startLatitude = Double.parseDouble(mPrefs.getString(Constantss.STR_Latitude,""));
        double startLongitude = Double.parseDouble(mPrefs.getString(Constantss.STR_Longitude,""));

        //float[] results = new float[]{};

        ref.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

       //         Log.e(TAG, "onDataChange: " + snapshot.getChildrenCount());

                list.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    PostObject postObj = s.getValue(PostObject.class);
                    if(postObj!=null){
                        double postLat = Double.parseDouble(postObj.getLat());
                        double postLong = Double.parseDouble(postObj.getLang());

                        Location startPoint=new Location("locationA");
                        Location endPoint=new Location("locationA");

                        endPoint.setLatitude(postLat);
                        endPoint.setLongitude(postLong);
                        startPoint.setLatitude(startLatitude);
                        startPoint.setLongitude(startLongitude);

                        //distance in meter
                        double distance=startPoint.distanceTo(endPoint);

                        Log.e(TAG, "onDataChange: Distance "+distance );
                        postObj.setDistance(String.valueOf(distance));

                        list.add(postObj);
//                    Log.e(TAG, "onDataChange: " + s.toString());
                    }

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        rc.setAdapter(adapter);


        /*double startLatitude = Double.parseDouble(user.getLang());
        double startLongitude = Double.parseDouble(user.getLang());
        double endLatitude = startLatitude;
        double endLongitude = startLongitude;
        float[] results = new float[]{};
        Location.distanceBetween(
                startLatitude,
                startLongitude,
                endLatitude,
                endLongitude,
                results
        );*/

        return view;
    }

    private void sortByDistance() {

        list.sort((o1, o2) -> Float.compare(Float.parseFloat(o1.getDistance()),
                Float.parseFloat(o2.getDistance())));

        adapter.notifyDataSetChanged();

    }

    private void noSearch() {

        ref.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        //         Log.e(TAG, "onDataChange: " + snapshot.getChildrenCount());

                        list.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            PostObject postObj = s.getValue(PostObject.class);
                            list.add(postObj);
//                    Log.e(TAG, "onDataChange: " + s.toString());

                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void fireBaseSearch(String searchWord) {

        ref.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // Log.e(TAG, "onDataChange: " + snapshot.getChildrenCount());

                        list.clear();

                        for (DataSnapshot s : snapshot.getChildren()) {
                            PostObject postObj = s.getValue(PostObject.class);
                            if (postObj.getItem_name().contains(searchWord))
                                list.add(postObj);
//                    Log.e(TAG, "onDataChange: " + s.toString());

                        }
                        adapter.notifyDataSetChanged();

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

}