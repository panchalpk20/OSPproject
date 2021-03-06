package com.anngrynerds.ospproject.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.CheckOut.CheckOutActivity;
import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.constants.Constantss;
import com.anngrynerds.ospproject.home.FarmerFeedFragment;
import com.anngrynerds.ospproject.pojo.Order;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.anngrynerds.ospproject.pojo.QuantityDetails;
import com.anngrynerds.ospproject.pojo.User;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputLayout;
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
    String tomato,onion,brinjal,ladyfinger,carrot;
    String Name,Quantity;
    ArrayList<String> arrayList;
    ArrayList<String> arrayList1;
    ArrayList<String> arrayList2;
    ArrayList<String> arrayList3;
    ArrayList<String> arrayList4;
    String mobileno;



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





        if(model.getDistance().isEmpty()){
            holder.tv_distance.setVisibility(View.GONE);
        }else{
            float d_km=Float.parseFloat(model.getDistance())/1000;
            holder.tv_distance.setText(MessageFormat.format("{0}",
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
            holder.whatsapp.setVisibility(View.INVISIBLE);
            holder.callfarmer.setVisibility(View.INVISIBLE);
            holder.btn_buy.setCompoundDrawables(null, null, null, null);
            holder.tv_options.setText(MessageFormat.format("{0}", model.getReportCount()));
            holder.tv_username.setVisibility(View.GONE);

            if(model.getReportCount()>10){
                holder.tv_options.setTextColor(Color.RED);
                holder.tv_username.setText(R.string.disabled);
                holder.tv_username.setVisibility(View.VISIBLE);
            }




            Log.e(TAG, "onBindViewHolder: "+holder.tv_options.getText().toString() );

            holder.btn_buy.setText(R.string.Delete);
            holder.btn_buy.setOnClickListener(v -> {
                //implement delete post



                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            ref.child(u.getCity()).child(Constantss.postsNode).child(model.getPost_id()).removeValue();

                            int newPosition = holder.getAdapterPosition();
                            list.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(newPosition, list.size());


                            break;

                        case DialogInterface.BUTTON_NEGATIVE:

                            break;
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Delete Action");
                builder.setMessage("You want to delete this post?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();



            });
        } else {

            ref.child(currentUser.getCity())
                    .child(Constantss.ROLE_FARMER)
                    .child(phno).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    u = snapshot.getValue(User.class);
//                // Log.e(TAG, "Fetching user : " + (resultUser != null ? resultUser.toString() : snapshot.getChildrenCount()));
                    holder.tv_username.setText(u.getName());
                    holder.callfarmer.setOnClickListener(v1->{
                        try {
                            String mobno = u.getMobNo();
                            Intent i = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mobno));
                            context.startActivity(i);
                        }catch (Exception e){
                            Toast.makeText(context,"Give permisson from setting",Toast.LENGTH_SHORT).show();
                        }
                    });
                    holder.whatsapp.setOnClickListener(v1->{
                        try {
                            String url = "https://api.whatsapp.com/send?phone=" + u.getMobNo();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            context.startActivity(i);
                        }catch (Exception e){
                            Toast.makeText(context,"Whatsapp not installed",Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //pop-up menu code here

            if(model.getReportCount()>10){
                holder.btn_buy.setText(R.string.this_post_has_been_marked_as_wrong);
                holder.btn_buy.setClickable(false);
                holder.tv_options.setClickable(false);
            }

            holder.tv_options.setOnClickListener(v->{

                PopupMenu popup = new PopupMenu(context, holder.tv_options);

                popup.inflate(R.menu.post_options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(item -> {

                    //todo add ids in menu xml and complete below

                    if (item.getItemId() == R.id.post_option_report) {
                        ref.child(currentUser.getCity()).child(Constantss.postsNode)
                                .child(model.getPost_id()).child("reportCount").setValue(model.getReportCount() + 1);
                        return true;
                    }
                    return false;

                });
                //displaying the popup
                popup.show();


            });


            //holder.btn_buy.setText(");
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
                EditText bs_tv_qtyTobuy = bottomSheetDialog.findViewById(R.id.bs_buy_tv_buyqty);
                TextInputLayout ipl_name=bottomSheetDialog.findViewById(R.id.profile_ipl_namee);




                ProgressBar bs_pg = bottomSheetDialog.findViewById(R.id.bs_buy_pg);

                    LinearLayout bs_imageContainer = bottomSheetDialog.findViewById(R.id.bs_buy_imagecontainer);

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
                    arrayList = new ArrayList<>();
                    arrayList1 = new ArrayList<>();
                    arrayList2 = new ArrayList<>();
                    arrayList3 = new ArrayList<>();
                    arrayList4 = new ArrayList<>();

                    tomato = "Tomato";
                    onion = "onion";
                    brinjal = "brinjal";
                    ladyfinger = "ladyfinger";
                    carrot = "carrot";

                    if (model.getItem_name().equals("totomoto") || model.getItem_name().equals("totomoto")) {
                        //arrayList.add(model.getItem_qty().toString());

                    }
                    if (model.getItem_name().equals("Onion") || model.getItem_name().equals("onion")) {
                        arrayList1.add(model.getItem_qty().toString());
                        saveArrayList(arrayList1, tomato);
                    }
                    if (model.getItem_name().equals("brinjal") || model.getItem_name().equals("Brinjal")) {
                        arrayList2.add(model.getItem_qty().toString());
                        saveArrayList(arrayList2, tomato);
                    }
                    if (model.getItem_name().equals("Ladyfinger") || model.getItem_name().equals("ladyfinger")) {
                        arrayList3.add(model.getItem_qty().toString());
                        saveArrayList(arrayList3, tomato);
                    }
                    if (model.getItem_name().equals("Carrot") || model.getItem_name().equals("carrot")) {
                        arrayList4.add(model.getItem_qty().toString());
                        saveArrayList(arrayList4, tomato);
                    }


                    assert bs_btn_putOrder != null;
                    bs_btn_putOrder.setOnClickListener(v1 -> {
                        if(bs_tv_qtyTobuy.getText().toString().isEmpty()){
                            ipl_name.setError(context.getString(R.string.entervaliddata));
                            bs_tv_qtyTobuy.requestFocus();
                        }
                        String a=bs_tv_qtyTobuy.getText().toString();
                        String b=bs_tv_stock.getText().toString();
                        long a1=Long.parseLong(a);
                        long b1=Long.parseLong(b);
                        if(a1>b1){
                            ipl_name.setError(context.getString(R.string.entervaliddata));
                            bs_tv_qtyTobuy.requestFocus();
                        }
                        else {

                                putOrder(model,
                                        bs_tv_qtyTobuy.getText().toString(),
                                        userMobileNo);


                            bottomSheetDialog.dismiss();
                        }
//                        todo Implement method

                    });


                    bottomSheetDialog.show();


            });

        }





    }

    public void saveArrayList(ArrayList<String> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();

    }

    private void putOrder(PostObject model, String qtyToBuy , String sellerNumber) {


        SharedPreferences prefs = context.
                getSharedPreferences(Constantss.sharedPrefID, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(Constantss.sharedPrefUserKey, "");
        User currentUser = gson.fromJson(json, User.class);

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("ddMMMyyyy-hhmmss", Locale.ENGLISH);
        String code = df.format(c);
        String orderId = "Order:"+"-" + code;


        String totalCost = model.getItem_price();


        String phno = model.getPost_id().split("-")[0];
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child(currentUser.getCity())
                .child(Constantss.ROLE_FARMER)
                .child(phno).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                u = snapshot.getValue(User.class);
                // Log.e(TAG, "Fetching user : " + (resultUser != null ? resultUser.toString() : snapshot.getChildrenCount()));
//                Order order = new Order(orderItems,
//                        String.valueOf(orderItems.size()),
//                        totalCost,
//                        orderId,
//                        u.getMobNo(),  //mobile number of seller
//                        "ChangeDWhilePuttingOrder"  //mobile number of buyer
//                );

                Order order = new Order(orderId,
                        u.getMobNo(),
                        "ChangeHwileputting order",
                        model.getItem_name(),
                        qtyToBuy,
                        model.getItem_price(),
                        model.getPost_id(),
                        Constantss.ORDER_STATUS_PROCESSING
                        );

                Gson gson = new Gson();
                String json = gson.toJson(order);
                Intent intent = new Intent(context, CheckOutActivity.class);
                intent.putExtra(CheckOutActivity.EXTRA_OrderObject, json);
                intent.putExtra("address",u.getAddress());
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
        public Button btn_buy,callfarmer,whatsapp;
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
            callfarmer=itemView.findViewById(R.id.call);
            whatsapp=itemView.findViewById(R.id.whatsapp);

            imageViewContainer = itemView.findViewById(R.id.post_image_container);

            btn_buy = itemView.findViewById(R.id.post_btn_buy);

            pg = itemView.findViewById(R.id.post_progressBar);
        }
    }
}