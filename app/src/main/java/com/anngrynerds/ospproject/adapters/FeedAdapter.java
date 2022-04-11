package com.anngrynerds.ospproject.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.CheckOut.CheckOutActivity;
import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.OrderItem;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.ViewHolder> {

    Context context;
    ArrayList<PostObject> list;
    boolean isFarmer;
    //    User resultUser;
    private String TAG = "FeedAdapter";
    User u;
    String name;

    public FeedAdapter(ArrayList<PostObject> list, Context context, boolean isFarmer) {
        this.list = list;
        this.context = context;
        this.isFarmer = isFarmer;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        PostObject model = list.get(position);
        holder.tv_date.setText(model.getDate());
        holder.tv_name.setText(model.getItem_name());
        holder.tv_cost.setText(model.getItem_price());
        holder.tv_qty.setText(model.getItem_qty());

        holder.imageViewContainer.removeAllViews();


        fetchUserForPost(model);
//
        SharedPreferences prefs = context.
                getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(Constantss.sharedPrefUserKey, "");
        User currentUser = gson.fromJson(json, User.class);
        String phno = model.getPost_id().split("-")[0];
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(currentUser.getCity())
                .child(Constantss.ROLE_FARMER)
                .child(phno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
//                // Log.e(TAG, "Fetching user : " + (resultUser != null ? resultUser.toString() : snapshot.getChildrenCount()));
                holder.tv_username.setText(u.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //pop-up menu code here

        holder.tv_options.setOnClickListener(v->{

            PopupMenu popup = new PopupMenu(context, holder.tv_options);

            popup.inflate(R.menu.post_options_menu);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {

                //todo add ids in menu xml and complete below

//                    switch (item.getItemId()) {
//                        case R.id.menu1:
//                            //handle menu1 click
//                            return true;
//                        case R.id.menu2:
//                            //handle menu2 click
//                            return true;
//                        case R.id.menu3:
//                            //handle menu3 click
//                            return true;
//                        default:
//                            return false;
//                    }

                Toast.makeText(context,
                        "item clicked "+ item.getTitle(),
                        Toast.LENGTH_SHORT).show();
                return false;
            });
            //displaying the popup
            popup.show();


        });



        if(model.getDistance().isEmpty()){
            holder.tv_distance.setVisibility(View.GONE);
        }else{
            float d_km=Float.parseFloat(model.getDistance())/1000;
            holder.tv_distance.setText(MessageFormat.format("{0} KM away from you",
                    new DecimalFormat("##.##").format(d_km)));
        }


        holder.pg.setVisibility(View.VISIBLE);
        if(model.getFilePathList() != null)
            for (String url : model.getFilePathList()) {

//               // Log.e("feedAdp: ", "url: " + url);


                ImageView imageView = new ImageView(context);
                Glide.with(context)
                        .load(url)
                        .addListener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e,
                                                        Object model, Target<Drawable> target,
                                                        boolean isFirstResource) {
                                holder.pg.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource,
                                                           Object model,
                                                           Target<Drawable> target,
                                                           DataSource dataSource,
                                                           boolean isFirstResource) {

                                holder.pg.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transition(withCrossFade())
                        .thumbnail(Glide.with(context)
                                .load(url)
                                .override(2))
                        .error(R.drawable.ic_launcher_foreground)
                        .into(imageView);

                ViewGroup.LayoutParams params = holder.imageViewContainer.getLayoutParams();
                params.height = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        200,
                        context.getResources().getDisplayMetrics());
                params.width = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        400,
                        context.getResources().getDisplayMetrics());

                imageView.setLayoutParams(params);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                holder.imageViewContainer.addView(imageView);
            }


//    //    Log.e(TAG, "onBindViewHolder: " + isFarmer);
        if (isFarmer) {
            holder.btn_buy.setText(R.string.Delete);
            holder.btn_buy.setOnClickListener(v -> {
                //implement delete post
                int newPosition = holder.getAdapterPosition();
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(newPosition, list.size());

            });
        } else {

            holder.btn_buy.setText("Buy");
            holder.btn_buy.setOnClickListener(v -> {
                //todo implement buy

//                User u = null;
//
//                while(u == null){
//                    u = fetchUserForPost(model);
//                }

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_buy);

                TextView bs_tv_soldby = bottomSheetDialog.findViewById(R.id.bs_buy_tv_soldby);
                TextView bs_tv_itemName = bottomSheetDialog.findViewById(R.id.bs_buy_tv_itemname);
                TextView bs_tv_stock = bottomSheetDialog.findViewById(R.id.bs_buy_tv_itemqty);
                TextView bs_tv_price = bottomSheetDialog.findViewById(R.id.bs_buy_tv_itemprice);
                TextView bs_tv_qtyTobuy = bottomSheetDialog.findViewById(R.id.bs_buy_tv_buyqty);

                ProgressBar bs_pg = bottomSheetDialog.findViewById(R.id.bs_buy_pg);

                LinearLayout bs_imageContainer = bottomSheetDialog.findViewById(R.id.bs_buy_imagecontainer);
                Button bs_btn_inc = bottomSheetDialog.findViewById(R.id.bs_buy_btn_inc);
                Button bs_btn_dec = bottomSheetDialog.findViewById(R.id.bs_buy_btn_dec);
                Button bs_btn_addToCart = bottomSheetDialog.findViewById(R.id.bs_buy_btn_addtocart);
                Button bs_btn_putOrder = bottomSheetDialog.findViewById(R.id.bs_buy_btn_buy);

                 name = u != null ? u.getName() : "";
                String userMobileNo = u != null ? u.getMobNo() : "null";

                assert bs_pg != null;
                for (String url : model.getFilePathList()) {

//                    Log.e("feedAdp: ", "url: " + url);

                    ImageView imageView1 = new ImageView(context);
                    Glide.with(context)
                            .load(url)
                            .addListener(new RequestListener<Drawable>() {
                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e,
                                                            Object model, Target<Drawable> target,
                                                            boolean isFirstResource) {
                                    bs_pg.setVisibility(View.GONE);
                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Drawable resource,
                                                               Object model,
                                                               Target<Drawable> target,
                                                               DataSource dataSource,
                                                               boolean isFirstResource) {

                                    bs_pg.setVisibility(View.GONE);
                                    return false;
                                }
                            })
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transition(withCrossFade())
                            .thumbnail(Glide.with(context)
                                    .load(url)
                                    .override(2))
                            .error(R.drawable.ic_launcher_foreground)
                            .into(imageView1);

                    assert bs_imageContainer != null;
                    ViewGroup.LayoutParams params1 = bs_imageContainer.getLayoutParams();
                    params1.height = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            200,
                            context.getResources().getDisplayMetrics());
                    params1.width = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            400,
                            context.getResources().getDisplayMetrics());

                    imageView1.setLayoutParams(params1);
                    imageView1.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    bs_imageContainer.addView(imageView1);

                }


                assert bs_tv_soldby != null;
                bs_tv_soldby.setText(MessageFormat.format("Sold By {0}", name));
                //holder.tv_username.setText(MessageFormat.format("Sold By {0}", name));
                assert bs_tv_itemName != null;
                bs_tv_itemName.setText(model.getItem_name());
                assert bs_tv_stock != null;
                bs_tv_stock.setText(model.getItem_qty());
                assert bs_tv_price != null;
                bs_tv_price.setText(model.getItem_price());

                assert bs_btn_inc != null;
                bs_btn_inc.setOnClickListener(v1 -> {
                    assert bs_tv_qtyTobuy != null;
                    int count = Integer.parseInt(bs_tv_qtyTobuy.getText().toString());
                    if (count < Integer.parseInt(model.getItem_qty())) {
                        bs_tv_qtyTobuy.setText(MessageFormat.format("{0}", count + 1));
                    }
                });


                assert bs_btn_dec != null;
                bs_btn_dec.setOnClickListener(v1 -> {
                    assert bs_tv_qtyTobuy != null;
                    int count = Integer.parseInt(bs_tv_qtyTobuy.getText().toString());
                    if (count > 1) {
                        bs_tv_qtyTobuy.setText(MessageFormat.format("{0}", count - 1));
                    }
                });

                assert bs_btn_addToCart != null;
                bs_btn_addToCart.setOnClickListener(v1 -> {
//                        todo Implement method
//                        addToCart()

                });

                assert bs_btn_putOrder != null;
                bs_btn_putOrder.setOnClickListener(v1 -> {
//                        todo Implement method
                    putOrder(model,
                            bs_tv_qtyTobuy.getText().toString(),
                            userMobileNo);

                    bottomSheetDialog.dismiss();
                });

                bottomSheetDialog.show();


            });
        }


    }

    private void putOrder(PostObject model, String qtyToBuy , String sellerNumber) {


        SharedPreferences prefs = context.
                getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(Constantss.sharedPrefUserKey, "");
        User currentUser = gson.fromJson(json, User.class);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy-hhmmss", Locale.getDefault());
        String code = df.format(c);
        String orderId = "Order:"+"-" + code;


        String totalCost = model.getItem_price();

        ArrayList<OrderItem> orderItems = new ArrayList<>();
        orderItems.add(new OrderItem(model.getItem_name(),
                qtyToBuy,
                model.getItem_price(),
                model.getPost_id()
        ));

        String phno = model.getPost_id().split("-")[0];
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(currentUser.getCity())
                .child(Constantss.ROLE_FARMER)
                .child(phno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
//                // Log.e(TAG, "Fetching user : " + (resultUser != null ? resultUser.toString() : snapshot.getChildrenCount()));
                Order order = new Order(orderItems,
                        String.valueOf(orderItems.size()),
                        totalCost,
                        orderId,
                        u.getMobNo(),  //mobile number of seller
                        "ChangeDWhilePuttingOrder"  //mobile number of buyer
                );

                Gson gson = new Gson();
                String json = gson.toJson(order);
                Intent intent = new Intent(context, CheckOutActivity.class);
                intent.putExtra(CheckOutActivity.EXTRA_OrderObject, json);
                context.startActivity(intent);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });










    }

    private void fetchUserForPost(PostObject model) {
        SharedPreferences prefs = context.
                getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = prefs.getString(Constantss.sharedPrefUserKey, "");
        User currentUser = gson.fromJson(json, User.class);
        u = null;

        String phno = model.getPost_id().split("-")[0];
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(currentUser.getCity())
                .child(Constantss.ROLE_FARMER)
                .child(phno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
//               // Log.e(TAG, "Fetching user : " + (resultUser != null ? resultUser.toString() : snapshot.getChildrenCount()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
//        return resultUser;


    }

    @Override
    public int getItemCount() {
        return list.isEmpty() ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tv_username,tv_name, tv_qty, tv_cost, tv_date, tv_options;
        public LinearLayout imageViewContainer;
        public Button btn_buy;
        public ProgressBar pg;
        public TextView tv_distance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_username=itemView.findViewById(R.id.userName);
            tv_name = itemView.findViewById(R.id.post_tv_item_name);
            tv_qty = itemView.findViewById(R.id.post_tv_item_count);
            tv_cost = itemView.findViewById(R.id.post_tv_item_price);
            tv_date = itemView.findViewById(R.id.post_date);
            tv_distance = itemView.findViewById(R.id.post_distance);
            tv_options = itemView.findViewById(R.id.post_tv_options);

            imageViewContainer = itemView.findViewById(R.id.post_image_container);

            btn_buy = itemView.findViewById(R.id.post_btn_buy);

            pg = itemView.findViewById(R.id.post_progressBar);
        }
    }
}