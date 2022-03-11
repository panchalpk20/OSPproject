package com.anngrynerds.ospproject.CheckOut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.HomeActivity;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.OrderItem;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.MessageFormat;

public class CheckOutActivity extends AppCompatActivity {

    private static final String TAG = "CheckOut";
    public static String EXTRA_OrderObject = "OrderObjectForCheckOut";
    Order order;

    TableLayout tableLayout;
    Context context;
    int totalCost;
    TextView tv_totalCost;
    Button btn_pay;
    Button btn_cod;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        context = CheckOutActivity.this;

        tableLayout = findViewById(R.id.checkout_tableLayout);
        tv_totalCost = findViewById(R.id.checkout_tv_totalcost);
        btn_pay = findViewById(R.id.checkout_btn_pay);
        btn_cod = findViewById(R.id.checkout_btn_cod);

        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);


        String json1 = getIntent().getStringExtra(EXTRA_OrderObject);
        order = new Gson().fromJson(json1, Order.class);

        order.setToId(user.getMobNo());

        totalCost = 0;
        for (OrderItem item : order.getList()) {

            TableRow tableRow = new TableRow(context);
            TextView name = new TextView(context);
            name.setText(item.getName());

            name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            name.setTextColor(getResources().getColor(R.color.textColor));
            name.setGravity(Gravity.START);

            TextView qty = new TextView(context);
            qty.setText(item.getQty());
            qty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            qty.setTextColor(getResources().getColor(R.color.textColor));
            qty.setGravity(Gravity.CENTER);


            TextView cost = new TextView(context);
            cost.setText(item.getCost());
            cost.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            cost.setTextColor(getResources().getColor(R.color.textColor));
            cost.setGravity(Gravity.CENTER);


            totalCost = totalCost + (Integer.parseInt(item.getCost()) * Integer.parseInt(item.getQty()));

            tableRow.addView(name);
            tableRow.addView(qty);
            tableRow.addView(cost);

            tableLayout.addView(tableRow);
        }

        tv_totalCost.setText(String.valueOf(totalCost));
        btn_pay.setText(MessageFormat.format("Pay{0}", totalCost));


        btn_pay.setOnClickListener(v -> {

            //implement Payment

            AlertDialog.Builder builder = new AlertDialog.Builder(context);

            builder.setTitle("Payment successful?");
            builder.setPositiveButton("yes", (dialog, which) -> {
                makeOrder();
            });

            builder.setNegativeButton("No", (dialog, which) -> {
                paymentFailed();
            });


        });

        btn_cod.setOnClickListener(v->{

            makeOrder();


        });

    }

    private void paymentFailed() {



    }

    private void showFailedDialog() {
    }

    private void reflectChangesInDb() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        for (OrderItem oi : order.getList()) {
            String id = oi.getPostId();
            ref.child(user.getCity()).child(Constantss.postsNode).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot s) {
                    PostObject postObj = s.getValue(PostObject.class);

                //    Log.e(TAG, "onDataChange: "+ (postObj != null ? postObj.toString() : "null"));


                    if (postObj != null && postObj.getPost_id().equalsIgnoreCase(id)) {
                        int newValue = Integer.parseInt(postObj.getItem_qty()) ;
                        newValue = newValue - Integer.parseInt(oi.getQty());

                     //   Log.e(TAG, "onDataChange: "+newValue );

                        ref.child(user.getCity())
                                .child(Constantss.postsNode)
                                .child(id)
                                .child("item_qty")
                                .setValue(String.valueOf(newValue)).addOnCompleteListener(task->{

                                    if(task.isSuccessful()){
                                        new AlertDialog.Builder(context)
                                                .setTitle("Order Successful!!")
                                                .setMessage("Your order has been placed successfully!")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        dialog.dismiss();

                                                        Intent intent = new Intent(context, HomeActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        startActivity(intent);

                                                    }
                                                })
                                                .show();
                                    }

                        });
                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //  ref.child(Constantss.postsNode).child(id).child("item_qty").setValue(newValue);
        }


    }

    private void makeOrder() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

        ref.child(user.getCity())
                .child(Constantss.orderNode)
                .child(order.getOrderId())
                .setValue(order)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {
                        reflectChangesInDb();
                    } else {
                        showFailedDialog();
                    }

                });

    }
}