package com.example.mainscreen;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class MainRestaurantAdapter extends RecyclerView.Adapter<MainRestaurantAdapter.ViewHolder> {
    private List<Restaurant> restaurants;
    private List<String> restaurantIds; // restaurantId 리스트 추가
    private Context context;

    // ViewType 상수 정의
    public static final int VIEW_TYPE_TOP_RESTAURANT = 0;
    public static final int VIEW_TYPE_MICHELIN_RESTAURANT = 1;

    public MainRestaurantAdapter(Context context, List<Restaurant> restaurants, List<String> restaurantIds) {
        this.context = context;
        this.restaurants = restaurants;
        this.restaurantIds = restaurantIds;
    }

    @Override
    public int getItemViewType(int position) {
        Restaurant restaurant = restaurants.get(position);
        List<DocumentReference> categoryIds = restaurant.getCategory_ids();

        for (DocumentReference ref : categoryIds) {
            if (ref.getPath().equals("categories/SpecificCategories/Subcategories/Top_Restaurant")) {
                return VIEW_TYPE_TOP_RESTAURANT;
            } else if (ref.getPath().equals("categories/SpecificCategories/Subcategories/Michelin_Restaurant")) {
                return VIEW_TYPE_MICHELIN_RESTAURANT;
            }
        }

        // 기본값을 설정하거나, 두 조건에 해당하지 않는 경우를 처리합니다.
        return -1; // 또는 예외 처리
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_TOP_RESTAURANT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_restaurant_item, parent, false);
        } else if (viewType == VIEW_TYPE_MICHELIN_RESTAURANT) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.michelin_restaurant_item, parent, false);
        } else {
            // 기본 레이아웃 또는 예외 처리
            view = new View(context); // 임시로 빈 뷰를 생성하거나 에러 처리를 수행
        }
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("MainRestaurantAdapter", "onBindViewHolder position: " + position + ", restaurants size: " + restaurants.size() + ", restaurantIds size: " + restaurantIds.size());
        if (position < restaurants.size() && position < restaurantIds.size()) {
            Restaurant restaurant = restaurants.get(position);
            String restaurantId = restaurantIds.get(position);

            holder.nameTextView.setText(restaurant.getName());
            if (!restaurant.getImagePath().isEmpty()) {
                loadImageFromStorage(restaurant.getImagePath().get(0), holder.imageView);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                intent.putExtra("restaurant_id", restaurantId);
                intent.putExtra("name", restaurant.getName());
                intent.putExtra("imagePath", restaurant.getImagePath().get(0));
                context.startActivity(intent);
            });
        } else {
            Log.e("MainRestaurantAdapter", "Invalid position: " + position + " with restaurant list size: " + restaurants.size() + " and restaurantIds size: " + restaurantIds.size());
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
            imageView.setImageResource(R.drawable.default_image);
        });
    }

    public void updateData(List<Restaurant> newRestaurants, List<String> newRestaurantIds) {
        Log.d("MainRestaurantAdapter", "Updating adapter with " + newRestaurants.size() + " restaurants and " + newRestaurantIds.size() + " IDs.");
        this.restaurants.clear();
        this.restaurants.addAll(newRestaurants);
        this.restaurantIds.clear();
        this.restaurantIds.addAll(newRestaurantIds);
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
            imageView = itemView.findViewById(R.id.main_restaurant_image);
            nameTextView = itemView.findViewById(R.id.main_restaurant_name);
        }
    }
}
