package com.example.mainscreen;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private final List<Restaurant> restaurants;

    public RestaurantAdapter(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.restaurants_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);

        // 식당 이름 설정
        holder.restaurantName.setText(restaurant.getName());

        // 기존 이미지 제거
        holder.imageContainer.removeAllViews();

        // 동적으로 이미지 추가
        for (int imageResId : restaurant.getImageResIds()) {
            ImageView imageView = new ImageView(holder.itemView.getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    holder.convertDpToPx(250), // width in pixels
                    holder.convertDpToPx(200)); // height in pixels
            params.setMargins(8, 0, 8, 0); // Optional: margin between images
            imageView.setLayoutParams(params);

            // 이미지 설정 및 크기 조정
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setImageResource(imageResId);

            holder.imageContainer.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        HorizontalScrollView imageScrollView;
        LinearLayout imageContainer;
        TextView restaurantName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageScrollView = itemView.findViewById(R.id.image_scroll_view);
            imageContainer = itemView.findViewById(R.id.image_container);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
        }

        // dp를 px로 변환하는 메소드
        public int convertDpToPx(int dp) {
            float density = itemView.getContext().getResources().getDisplayMetrics().density;
            return Math.round(dp * density);
        }
    }
}

