package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RestaurantDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);

        // Intent로 전달된 Restaurant 객체 가져오기
        Intent intent = getIntent();
        Restaurant restaurant = (Restaurant) intent.getSerializableExtra("restaurant");

        // UI 요소 참조
        TextView nameTextView = findViewById(R.id.restaurant_name);
        TextView locationTextView = findViewById(R.id.textview_location);
        TextView contactTextView = findViewById(R.id.textview_contact);
        LinearLayout imageContainer = findViewById(R.id.image_container);
        //LinearLayout cuisineContainer = findViewById(R.id.textview_cuisine);

        // UI 요소에 데이터 설정
        if (restaurant != null) {
            nameTextView.setText(restaurant.getName());
            locationTextView.setText(restaurant.getLocation());
            contactTextView.setText(restaurant.getContact_number());

            // 이미지를 동적으로 추가
            FirebaseStorage storage = FirebaseStorage.getInstance();
            List<String> imagePaths = restaurant.getImagePath();
            if (imagePaths != null) {
                for (String imagePath : imagePaths) {
                    StorageReference storageRef = storage.getReference().child(imagePath);
                    ImageView imageView = new ImageView(this);
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(RestaurantDetailsActivity.this)
                                .load(uri)
                                .into(imageView);
                    }).addOnFailureListener(exception -> {
                        imageView.setImageResource(R.drawable.default_image);  // 기본 이미지 설정
                    });
                    imageContainer.addView(imageView);
                }
            }

            // 카테고리(음식 종류)를 동적으로 버튼으로 추가
            List<String> categories = restaurant.getCategories();
            if (categories != null) {
                for (String category : categories) {
                    Button categoryButton = new Button(this);
                    categoryButton.setText(category);
                    //cuisineContainer.addView(categoryButton);
                }
            }
        }
    }
}
