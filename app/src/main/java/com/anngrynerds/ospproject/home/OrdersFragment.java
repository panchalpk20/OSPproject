package com.anngrynerds.ospproject.home;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.adapters.CustomerOrderAdapter;
import com.anngrynerds.ospproject.adapters.FarmerOrderAdapter;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;


public class OrdersFragment extends Fragment {


    public OrdersFragment() {
        // Required empty public constructor
    }


    public static OrdersFragment newInstance() {

        return new OrdersFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    ArrayList<Order> list = new ArrayList<>();
    RecyclerView rc;
    View view;
    Context context;
    User u;
    CustomerOrderAdapter adapter;
    FarmerOrderAdapter adapter1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_orders, container, false);
        context = view.getContext();
        SharedPreferences pref = view.getContext()
                .getSharedPreferences(Constantss.sharedPrefID, MODE_PRIVATE);

        Gson gson = new Gson();
        String json1 = pref.getString(Constantss.sharedPrefUserKey, "");
        u = gson.fromJson(json1, User.class);




        if(u.getRole().equalsIgnoreCase(Constantss.ROLE_CUSTOMER)){
            loadCustomerOrders();
            adapter = new CustomerOrderAdapter(context, list);
            rc = view.findViewById(R.id.order_fragment_recyclerView);
            rc.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rc.setAdapter(adapter);
        }else{
            loadFarmersOrders();
            adapter1 = new FarmerOrderAdapter(context, list);
            rc = view.findViewById(R.id.order_fragment_recyclerView);
            rc.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
            rc.setAdapter(adapter1);
        }


        return view;
    }

    private void loadFarmersOrders() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(u.getCity())
                .child(Constantss.orderNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot s: snapshot.getChildren()) {
                            Order o = s.getValue(Order.class);
                            if (o != null && o.getFromId().equalsIgnoreCase(u.getMobNo())) {
                                list.add(o);
                            }
                        }

                        adapter1.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void loadCustomerOrders() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(u.getCity())
                .child(Constantss.orderNode)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        list.clear();
                        for (DataSnapshot s: snapshot.getChildren()) {
                            Order o = s.getValue(Order.class);
                            if (o != null && o.getToId().equalsIgnoreCase(u.getMobNo())) {
                                list.add(o);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}