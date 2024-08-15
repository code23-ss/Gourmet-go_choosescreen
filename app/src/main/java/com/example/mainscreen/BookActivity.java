package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class BookActivity extends AppCompatActivity {

    private static final String TAG = "BookActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 뷰 객체 가져오기
        TextView restaurantNameTextView = findViewById(R.id.restaurant_name);
        TextView locationTextView = findViewById(R.id.textview_location);
        TextView openingHours1TextView = findViewById(R.id.textview_openinghours_1);
        TextView openingHours2TextView = findViewById(R.id.textview_openinghours_2);
        TextView contactTextView = findViewById(R.id.textview_contact);
        TextView cuisineTextView = findViewById(R.id.textview_cuisine);
        LinearLayout imageContainer = findViewById(R.id.image_container);
        LinearLayout menuContainer = findViewById(R.id.menu_container);
        Button priceButton = findViewById(R.id.button_price);
        Button cuisineButton = findViewById(R.id.button_cuisine);
        Button locationButton = findViewById(R.id.button_location);

        // Intent로부터 레스토랑 이름 가져오기
        Intent intent = getIntent();
        String restaurantName = intent.getStringExtra("restaurant_name");

        // JSON 파일에서 데이터 읽기
        List<RestaurantDetail> restaurantDetails = loadRestaurantDetailsFromJson();
        if (restaurantDetails == null) {
            Log.e(TAG, "Failed to load restaurant details from JSON.");
            return;
        }

        // 레스토랑 정보 검색
        RestaurantDetail selectedRestaurant = null;
        for (RestaurantDetail detail : restaurantDetails) {
            if (detail.getName().equals(restaurantName)) {
                selectedRestaurant = detail;
                break;
            }
        }

        if (selectedRestaurant != null) {
            // 레스토랑 정보 설정
            restaurantNameTextView.setText(selectedRestaurant.getName());
            locationTextView.setText(selectedRestaurant.getLocation());
            openingHours1TextView.setText(selectedRestaurant.getOpeningHours1());
            openingHours2TextView.setText(selectedRestaurant.getOpeningHours2());
            contactTextView.setText(selectedRestaurant.getContact());
            cuisineTextView.setText(selectedRestaurant.getCuisine());

            // 버튼 설정
            priceButton.setText(selectedRestaurant.getPriceRange());
            cuisineButton.setText(selectedRestaurant.getCuisineButton());
            locationButton.setText(selectedRestaurant.getLocationButton());

            // 이미지 추가
            imageContainer.removeAllViews();
            List<String> imageResIds = selectedRestaurant.getImages();
            if (imageResIds != null) {
                for (String imageName : imageResIds) {
                    int resId = getResources().getIdentifier(imageName, "drawable", getPackageName());
                    if (resId != 0) {
                        ImageView imageView = new ImageView(this);
                        imageView.setImageResource(resId);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                convertDpToPx(400), // Width in pixels
                                convertDpToPx(300)  // Height in pixels
                        );
                        layoutParams.setMargins(8, 8, 8, 8); // Optional: margin around images
                        imageView.setLayoutParams(layoutParams);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(this).load(resId).into(imageView);
                        imageContainer.addView(imageView);
                    } else {
                        Log.e(TAG, "Image resource not found: " + imageName);
                    }
                }
            }

            // 메뉴 추가
            menuContainer.removeAllViews();
            List<String> menuResIds = selectedRestaurant.getMenus();
            if (menuResIds != null) {
                for (String menuName : menuResIds) {
                    int resId = getResources().getIdentifier(menuName, "drawable", getPackageName());
                    if (resId != 0) {
                        ImageView menuView = new ImageView(this);
                        menuView.setImageResource(resId);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                convertDpToPx(400), // Width in pixels
                                convertDpToPx(250)  // Height in pixels
                        );
                        layoutParams.setMargins(8, 8, 8, 8); // Optional: margin around menu images
                        menuView.setLayoutParams(layoutParams);
                        menuView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        Glide.with(this).load(resId).into(menuView);
                        menuContainer.addView(menuView);
                    } else {
                        Log.e(TAG, "Menu resource not found: " + menuName);
                    }
                }
            }
        } else {
            Log.e(TAG, "Restaurant not found: " + restaurantName);
        }
    }

    private int convertDpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private List<RestaurantDetail> loadRestaurantDetailsFromJson() {
        String json = null;
        try {
            InputStream is = getAssets().open("restaurants.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Gson gson = new Gson();
        Type listType = new TypeToken<List<RestaurantDetail>>() {}.getType();
        return gson.fromJson(json, listType);
    }
}