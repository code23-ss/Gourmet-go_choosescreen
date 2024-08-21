package com.example.mainscreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private List<String> documentIdList;
    private Context context;

    public RestaurantAdapter(Context context, List<Restaurant> restaurantList, List<String> documentIdList) {
        this.context = context;
        this.restaurantList = restaurantList;
        this.documentIdList = documentIdList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurants_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        String documentId = documentIdList.get(position); // 문서 ID 가져오기

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

            // 이미지 클릭 시 세부 화면으로 이동하는 리스너 추가
            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(context, RestaurantDetailsActivity.class);
                intent.putExtra("restaurant_id", documentId); // 문서 ID를 전달
                intent.putExtra("name", restaurant.getName());  // 레스토랑 이름 전달
                intent.putExtra("imagePath", imagePath);  // 이미지 경로 전달
                context.startActivity(intent);

                // 로그 추가: Intent에 추가된 값 확인
                Log.d(TAG, "Intent - ImagePath: " + imagePath + ", Name: " + restaurant.getName() + ", RestaurantId: " + documentId);
            });

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
