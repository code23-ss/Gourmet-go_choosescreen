package com.example.mainscreen;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Context;


import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class TopRestaurantAdapter extends RecyclerView.Adapter<TopRestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurants;
    private Context context;

    public TopRestaurantAdapter(Context context, List<Restaurant> restaurants) {
        this.context = context;
        this.restaurants = restaurants;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_restaurant_item, parent, false); // 수정할 부분 R.layout.top_restaurant_item
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = restaurants.get(position);
        holder.nameTextView.setText(restaurant.getName());
        if (!restaurant.getImagePath().isEmpty()) {
            loadImageFromStorage(restaurant.getImagePath().get(0), holder.imageView);
        }
    }

    private void loadImageFromStorage(String imagePath, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri)
                    .apply(new RequestOptions().override(500, 400))
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error getting image URL. File not found at location: " + imagePath, exception);
            imageView.setImageResource(R.drawable.default_image);
        });
    }
    public void updateData(List<Restaurant> newRestaurants) {
        this.restaurants.clear();
        this.restaurants.addAll(newRestaurants);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.main_restaurant_image); //수정할 부분 (top_restaurant_item.xml)
            nameTextView = itemView.findViewById(R.id.main_restaurant_name); // 수정할 부분 (top_restaurant_item.xml)

            // 로그 추가
            if (imageView == null) {
                Log.e("ViewHolder", "ImageView is null");
            } else {
                Log.d("ViewHolder", "ImageView initialized successfully");
            }

            if (nameTextView == null) {
                Log.e("ViewHolder", "TextView is null");
            } else {
                Log.d("ViewHolder", "TextView initialized successfully");
            }
        }
    }
}
