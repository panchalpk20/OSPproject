package com.anngrynerds.ospproject.adapters;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.anngrynerds.ospproject.R;
import com.anngrynerds.ospproject.pojo.PostObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class FeedAdapter extends FirebaseRecyclerAdapter<
        PostObject, FeedAdapter.ViewHolder> {
    /**
     * Initialize a {@link RecyclerView.Adapter} that listens to a Firebase query. See
     * {@link FirebaseRecyclerOptions} for configuration options.
     *
     * @param options
     */

    Context context;
    public FeedAdapter(@NonNull FirebaseRecyclerOptions<PostObject> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PostObject model) {
            //todo

        holder.tv_date.setText(model.getDate());
        holder.tv_name.setText(model.getItem_name());
        holder.tv_cost.setText(model.getItem_price());
        holder.tv_qty.setText(model.getItem_qty());

        holder.pg.setVisibility(View.VISIBLE);
        for(String url: model.getFilePathList()){

            Log.e("feedAdp: ", "url: "+url );


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

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_qty, tv_cost, tv_date;
        LinearLayout imageViewContainer;
        Button btn_buy;
        ProgressBar pg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.post_tv_item_name);
            tv_qty = itemView.findViewById(R.id.post_tv_item_count);
            tv_cost = itemView.findViewById(R.id.post_tv_item_price);
            tv_date = itemView.findViewById(R.id.post_date);

            imageViewContainer = itemView.findViewById(R.id.post_image_container);

            btn_buy = itemView.findViewById(R.id.post_btn_buy);

            pg = itemView.findViewById(R.id.post_progressBar);
        }
    }
}
