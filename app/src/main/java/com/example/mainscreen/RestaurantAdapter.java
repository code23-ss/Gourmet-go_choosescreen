package com.example.mainscreen;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    private static final String TAG = "RestaurantAdapter";
    private List<Restaurant> restaurantList;
    private Context context;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList) {
        this.context = context;
        this.restaurantList = restaurantList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);

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
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);  // 이미지를 꽉 차게 만듭니다.

            loadImageFromStorage(imagePath, imageView);  // 이미지 로드 메서드 호출

            holder.imageContainer.addView(imageView);
        }
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
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
            Glide.with(context)
                    .load(uri)
                    .apply(new RequestOptions().override(500, 400))
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error getting image URL. File not found at location: " + imagePath, exception);
            imageView.setImageResource(R.drawable.default_image);
        });
    }
}
