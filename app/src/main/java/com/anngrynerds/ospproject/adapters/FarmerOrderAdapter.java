package com.anngrynerds.ospproject.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Objects;

public class FarmerOrderAdapter extends RecyclerView.Adapter<FarmerOrderAdapter.ViewHolder>{
    Context context;
    ArrayList<Order> list;
    User user, buyer;
    PostObject p;

    public FarmerOrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
        SharedPreferences mPrefs =
                context.getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString(Constantss.sharedPrefUserKey, "");
        user = gson.fromJson(json, User.class);
    }

    @NonNull
    @Override
    public FarmerOrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_farmer_order, parent, false);
        return new FarmerOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmerOrderAdapter.ViewHolder holder, int position) {
        Order order = list.get(position);
        int totalCost = 0;
        totalCost = Integer.parseInt(order.getCost())*Integer.parseInt(order.getQty());

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(user.getCity()).child(Constantss.postsNode).child(order.getPostId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.e("TAG", "onDataChange: "+snapshot.getKey());
                        p = snapshot.getValue(PostObject.class);
                        if (p != null) {
                            Glide.with(context).load(p.getFilePathList().get(0)).into(holder.img);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        ref.child("Pune").child(Constantss.ROLE_CUSTOMER)
                .child(order.getToId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.e("TAG", "onDataChange: "+snapshot.getKey()+":::"+ snapshot.getChildrenCount() );
                buyer = snapshot.getValue(User.class);

                if (buyer != null) {
                    holder.tv_soldTo.setText(
                            MessageFormat.format(
                                    "Sold to the - {0}", buyer.getName()));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        holder.tv_orderStatus.setText(order.getOrderStatus());

        if(order.getOrderStatus().equalsIgnoreCase(Constantss.ORDER_STATUS_CANCELLED)){
            holder.tv_orderStatus.setTextColor(Color.RED);
        }else if(order.getOrderStatus().equalsIgnoreCase(Constantss.ORDER_STATUS_COMPLETED_CUSTOMER)){
            //green
            holder.tv_orderStatus.setTextColor(Color.GREEN);

        }if(order.getOrderStatus().equalsIgnoreCase(Constantss.ORDER_STATUS_COMPLETED_FARMER)){
            //blue
            holder.tv_orderStatus.setTextColor(Color.BLUE);

        }if(order.getOrderStatus().equalsIgnoreCase(Constantss.ORDER_STATUS_PROCESSING)){
            //orange
            holder.tv_orderStatus.setTextColor(Color.parseColor("#FFAB00"));

        }


        holder.tv_name.setText(order.getName());
        holder.tv_price.setText(order.getCost());
        holder.tv_qty.setText(order.getQty());

        holder.tv_totalCost.setText(MessageFormat.format("Total Cost Rs {0}", totalCost));

        holder.btn_completed.setOnClickListener(v->{
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        ref.child(user.getCity())
                                .child(Constantss.orderNode)
                                .child(order.getOrderId()).child("orderStatus").setValue(Constantss.ORDER_STATUS_COMPLETED_FARMER);


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Order shipment");
            builder.setMessage("is Order successfully shipped from you?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        holder.btn_cancel.setOnClickListener(v->{
            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:

                        ref.child(user.getCity())
                                .child(Constantss.orderNode)
                                .child(order.getOrderId()).child("orderStatus").setValue(Constantss.ORDER_STATUS_CANCELLED);


                        break;

                    case DialogInterface.BUTTON_NEGATIVE:

                        break;
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Confirm Order Cancel");
            builder.setMessage("Are you sure you want to cancel order").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        });

        int finalTotalCost = totalCost;
        holder.itemView.setOnClickListener(v->{
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_cust_order);

            TextView bs_soldBy = bottomSheetDialog.findViewById(R.id.bs_cust_order_soldby);
            TextView bs_itemName = bottomSheetDialog.findViewById(R.id.bs_cust_order_itemname);
            ProgressBar bs_pg = bottomSheetDialog.findViewById(R.id.bs_cust_order_pg);
            LinearLayout bs_imageContainer = bottomSheetDialog.findViewById(R.id.bs_cust_order_imagecontainer);
            TextView bs_itemQty = bottomSheetDialog.findViewById(R.id.bs_cust_order_itemqty);
            TextView bs_itemPrice = bottomSheetDialog.findViewById(R.id.bs_cust_order_itemprice);
            TextView bs_totalPrice = bottomSheetDialog.findViewById(R.id.bs_cust_order_totalprice);
            TextView bs_sellerAddress = bottomSheetDialog.findViewById(R.id.bs_cust_order_sellerAddress);
            Button bs_callSeller = bottomSheetDialog.findViewById(R.id.bs_cust_order_btn_call);
            TextView bs_addressLabel = bottomSheetDialog.findViewById(R.id.bs_cust_order_AddressLabel);

            assert bs_addressLabel != null;
            bs_addressLabel.setText(R.string.address_of_buyer);

            Objects.requireNonNull(bs_itemName).setText(MessageFormat.format("Item Name {0}", order.getName()));
            Objects.requireNonNull(bs_soldBy).setText(String.format("This item is getting sold to %s", buyer.getName()));
            Objects.requireNonNull(bs_itemPrice).setText(order.getCost());
            Objects.requireNonNull(bs_itemQty).setText(order.getQty());
            Objects.requireNonNull(bs_totalPrice).setText(MessageFormat.format("Rs. {0}", finalTotalCost));
            Objects.requireNonNull(bs_sellerAddress).setText(String.format("%s, %s",
                    buyer.getAddress(), buyer.getCity()));

            if(p!=null && p.getFilePathList()!=null){
                for(String url: p.getFilePathList()){

                    ImageView imageView = new ImageView(context);
                    Glide.with(context)
                            .load(url)
                            .into(imageView);

                    assert bs_imageContainer != null;
                    ViewGroup.LayoutParams params = bs_imageContainer.getLayoutParams();
                    params.height = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            250,
                            context.getResources().getDisplayMetrics());
                    params.width = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            400,
                            context.getResources().getDisplayMetrics());

                    imageView.setLayoutParams(params);
                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bs_imageContainer.addView(imageView);

                }
            }

            assert bs_callSeller != null;
            bs_callSeller.setText(R.string.call_buyer);
            bs_callSeller.setOnClickListener(v1->{

                String mobno = buyer.getMobNo();
                Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+mobno));
                context.startActivity(i);

            });

            bottomSheetDialog.show();

        });

    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_soldTo;
        TextView tv_totalCost;
        Button btn_completed;
        Button btn_cancel;
        ImageView img;
        TextView tv_name;
        TextView tv_qty;
        TextView tv_price;
        TextView tv_orderStatus;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_soldTo = itemView.findViewById(R.id.item_farmer_order_soldby);
            tv_totalCost = itemView.findViewById(R.id.totalCost);
            btn_cancel = itemView.findViewById(R.id.item_farmer_order_btn_cancel);
            btn_completed = itemView.findViewById(R.id.item_farmer_order_btn_completed);
            img = itemView.findViewById(R.id.item_farmer_order_image);
            tv_name = itemView.findViewById(R.id.item_farmer_order_name);
            tv_qty = itemView.findViewById(R.id.item_farmer_order_qty);
            tv_price = itemView.findViewById(R.id.item_farmer_order_price);
            tv_orderStatus = itemView.findViewById(R.id.item_farmer_order_status);


        }
    }

}
