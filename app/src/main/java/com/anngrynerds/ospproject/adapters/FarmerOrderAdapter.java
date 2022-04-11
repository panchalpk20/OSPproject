package com.anngrynerds.ospproject.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.pojo.Order;

import java.util.ArrayList;

public class FarmerOrderAdapter extends RecyclerView.Adapter<FarmerOrderAdapter.ViewHolder>{
    Context context;
    ArrayList<Order> list;

    public FarmerOrderAdapter(Context context, ArrayList<Order> list) {
        this.context = context;
        this.list = list;
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
        //holder.orderPhoto.setImageBitmap(order.getFilePathList().get(0));
        //holder.tv.setText(list.get(position).toString());



    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView itemName,itemQuantity,userName;
        Button completeOrder;
        ImageView orderPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.post_item_name);
            itemQuantity = itemView.findViewById(R.id.post_item_count);
            userName=itemView.findViewById(R.id.user_name);
            completeOrder=itemView.findViewById(R.id.order_completed);
            orderPhoto=itemView.findViewById(R.id.image_post);




        }
    }

}
