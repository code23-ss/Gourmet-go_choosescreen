package com.example.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final List<Restaurant> restaurantList;
    private final Context context;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.restaurants_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.nameTextView.setText(restaurant.getName());

        // 이미지 동적으로 추가
        holder.imageContainer.removeAllViews();
        List<String> imagePath = restaurant.getImagePath();
        if (imagePath != null) {
            for (String imageName : imagePath) {
                int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
                if (resId != 0) {
                    ImageView imageView = new ImageView(context);

                    // 이미지 크기와 간격 조정
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            holder.convertDpToPx(250),  // Width in pixels
                            holder.convertDpToPx(200)); // Height in pixels
                    params.setMargins(holder.convertDpToPx(8), 0, holder.convertDpToPx(8), 0); // Margins between images
                    imageView.setLayoutParams(params);

                    imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    imageView.setImageResource(resId);

                    holder.imageContainer.addView(imageView);
                }
            }
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookActivity.class);
            intent.putExtra("restaurant_name", restaurant.getName());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView;
        LinearLayout imageContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            imageContainer = itemView.findViewById(R.id.image_container);
        }

        public int convertDpToPx(int dp) {
            DisplayMetrics displayMetrics = itemView.getContext().getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }
}
