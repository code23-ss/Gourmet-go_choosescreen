package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class FullScreenImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        // Intent로 전달된 이미지 URL 가져오기
        String imageUrl = getIntent().getStringExtra("image_url");

        // ImageView 참조
        ImageView fullScreenImageView = findViewById(R.id.full_screen_image);

        // Glide를 사용하여 이미지를 로드하고 ImageView에 설정
        Glide.with(this)
                .load(imageUrl)
                .into(fullScreenImageView);

        // 이미지 클릭 시 Activity 종료
        fullScreenImageView.setOnClickListener(view -> finish());
    }
}
