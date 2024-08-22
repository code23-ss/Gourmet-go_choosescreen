package com.example.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RestaurantAdapter";
    private List<Restaurant> restaurantList;
    private List<Restaurant> filteredRestaurantList;
    private List<String> documentIdList;
    private Context context;
    private String showButton;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, List<String> documentIdList, String showButton) {
        this.context = context;
        this.restaurantList = new ArrayList<>(restaurantList);
        this.filteredRestaurantList = new ArrayList<>(restaurantList);
        this.documentIdList = documentIdList;
        this.showButton = showButton;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = filteredRestaurantList.get(position);
        String documentId = documentIdList.get(position);

        // Name 설정
        holder.nameTextView.setText(restaurant.getName());

        // 이전에 추가된 이미지뷰들 제거
        holder.imageContainer.removeAllViews();

        // 각 이미지를 불러와서 imageContainer에 추가
        for (String imagePath : restaurant.getImagePath()) {
            Log.d(TAG, "Loading image from path: " + imagePath);

            ImageView imageView = new ImageView(context);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
            );
            params.setMargins(8, 0, 8, 0);
            imageView.setLayoutParams(params);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            loadImageFromStorage(imagePath, imageView);

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                intent.putExtra("restaurant_id", documentId);
                intent.putExtra("name", restaurant.getName());
                intent.putExtra("imagePath", imagePath);
                intent.putExtra("SHOW_BUTTON", showButton);
                context.startActivity(intent);

                Log.d(TAG, "Intent - ImagePath: " + imagePath + ", Name: " + restaurant.getName() + ", RestaurantId: " + documentId);
            });

            holder.imageContainer.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return filteredRestaurantList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterPattern = constraint.toString().toLowerCase().trim();
                FilterResults results = new FilterResults();
                List<Restaurant> filteredList = new ArrayList<>();

                if (filterPattern.isEmpty()) {
                    filteredList.addAll(restaurantList);
                } else {
                    for (Restaurant item : restaurantList) {
                        if (item.getName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }

                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredRestaurantList.clear();
                filteredRestaurantList.addAll((List<Restaurant>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nameTextView;
        public LinearLayout imageContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            imageContainer = itemView.findViewById(R.id.image_container);
        }
    }

    private void loadImageFromStorage(String imagePath, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (context != null && !((Activity) context).isDestroyed()) {
                Glide.with(context)
                        .load(uri)
                        .apply(new RequestOptions().override(500, 400))
                        .into(imageView);
            } else {
                Log.w(TAG, "Context is null or Activity is destroyed. Skipping image load.");
            }
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error getting image URL. File not found at location: " + imagePath, exception);
            imageView.setImageResource(R.drawable.default_image);
        });
    }
}
