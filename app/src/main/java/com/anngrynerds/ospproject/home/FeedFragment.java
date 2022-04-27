package com.anngrynerds.ospproject.home;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
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
import java.util.Locale;

public class

FeedFragment extends Fragment {

    private static final String TAG = "FeedFragment";

    Context context;
    DatabaseReference databaseReference;
    User user;
    RecyclerView rc,rc1;
    FeedAdapter adapter;
    EditText et_searchQry;
    DatabaseReference ref;
    ArrayList<PostObject> list = new ArrayList<>();
//    TextView searchDesc;
    CheckBox checkBox_sort;
    SharedPreferences mPrefs;
    TextView pgMsg;
    Dialog dialog;
    Spinner spinner;
    ImageView spinnerimg;
    int chk=1;
    boolean flag=true;



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

        mPrefs = context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_transparent_loading);
        dialog.setCancelable(false);
        pgMsg = dialog.findViewById(R.id.dialog_pgmsg);

        //spinner=view.findViewById(R.id.spinner);
        checkBox_sort = view.findViewById(R.id.feed_checkbox_sort);

        et_searchQry = view.findViewById(R.id.feed_et_searchqry);
        spinnerimg=view.findViewById(R.id.spinnerImg);

        LayoutInflater layoutInflater= (LayoutInflater)context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = layoutInflater.inflate(R.layout.filtterdropdown, null);
        final PopupWindow popupWindow = new PopupWindow(popupView,250, ViewGroup.LayoutParams.WRAP_CONTENT,true);
        checkBox_sort=popupView.findViewById(R.id.feed_checkbox_sort);
        spinnerimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(popupWindow.isShowing()) {
                    popupWindow.dismiss();
                }
                else {
                    popupWindow.showAsDropDown(spinnerimg, 50, 0);
                }
            }
        });

//        searchDesc = view.findViewById(R.id.feed_t_searchdesc);
//        searchDesc.setVisibility(View.GONE);

        checkBox_sort.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if(isChecked){
                sortByDistance();
                popupWindow.dismiss();
            }else{
                dontSort();
                popupWindow.dismiss();
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


//        searchDesc.setOnClickListener(v -> {
//            noSearch();
//            searchDesc.setVisibility(View.GONE);
//        });

        //recyclerView
        rc = view.findViewById(R.id.fragment_feed_rc);
        rc.setLayoutManager(
                new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        ref = FirebaseDatabase.getInstance()
                .getReference();

        adapter = new FeedAdapter(list, context, false);
        rc.setAdapter(adapter);

       // Log.e(TAG, "onCreateView: " + user.getCity());



        //float[] results = new float[]{};

        LoadPosts();

        return view;
    }

    private void LoadPosts() {

        showpg();

        double startLatitude = Double.parseDouble(mPrefs.getString(Constantss.STR_Latitude,""));
        double startLongitude = Double.parseDouble(mPrefs.getString(Constantss.STR_Longitude,""));

        ref.child(user.getCity())
                .child(Constantss.postsNode)
                .addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

       //         Log.e(TAG, "onDataChange: " + snapshot.getChildrenCount());

                list.clear();

                for (DataSnapshot s : snapshot.getChildren()) {
                    PostObject postObj = s.getValue(PostObject.class);
                    if(postObj!=null && Integer.parseInt(postObj.getItem_qty()) != 0){
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
                closepg();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void dontSort() {
        LoadPosts();
    }

    private void sortByDistance() {
try {
    list.sort((o1, o2) -> Float.compare(Float.parseFloat(o1.getDistance()),
            Float.parseFloat(o2.getDistance())));

    adapter.notifyDataSetChanged();
}catch (Exception e){

}

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
                            if (postObj != null
                                    && Integer.parseInt(postObj.getItem_qty()) != 0
                                    && postObj.getItem_name()
                                    .toLowerCase(Locale.ROOT)
                                    .contains(searchWord.toLowerCase(Locale.ROOT)))
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



    private void closepg(){
        dialog.dismiss();
    }
    private void showpg(){
        pgMsg.setText(R.string.loading);
        dialog.show();
    }



}