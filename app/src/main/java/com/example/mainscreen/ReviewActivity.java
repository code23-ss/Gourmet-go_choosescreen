package com.example.mainscreen;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hbb20.CountryCodePicker;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ReviewActivity extends AppCompatActivity {

    private LinearLayout container;
    private String restaurantId;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_details);

        container = findViewById(R.id.container);
        ImageView editButton = findViewById(R.id.edit);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Intent로부터 restaurantId 받아오기
        restaurantId = getIntent().getStringExtra("restaurantId");

        // 리뷰를 불러오기
        loadReviews();

        // 리뷰 추가 버튼 활성화 조건 확인
        checkUserEligibility(editButton);

        // 리뷰 추가 버튼 클릭 리스너는 다른 곳에서 구현 중이므로 생략
        // 클릭 리스너 설정을 onCreate에서 바로 추가
        editButton.setOnClickListener(v -> {
            Log.d("ReviewActivity", "Edit button clicked");
            if (!editButton.isEnabled()) {
                Toast.makeText(ReviewActivity.this, "You do not have permission to write a review.", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ReviewActivity.this, WriteCommentActivity.class);
                intent.putExtra("restaurantId", restaurantId);  // restaurantId를 다음 액티비티로 전달
                intent.putExtra("userId", auth.getCurrentUser().getUid());
                startActivity(intent);
            }
        });
    }

    private void loadReviews() {
        db.collection("reviews")
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                        addReviewToUI(document);
                    }
                });
    }

    private void addReviewToUI(DocumentSnapshot document) {
        // XML에서 id 참조
        View reviewView = getLayoutInflater().inflate(R.layout.review_item, null);

        TextView usernameTextView = reviewView.findViewById(R.id.username);
        TextView visitDateTextView = reviewView.findViewById(R.id.visit_date);
        ImageView flagImageView = reviewView.findViewById(R.id.country_code_picker);
        ImageView reviewImageView = reviewView.findViewById(R.id.review_image);
        TextView reviewTextView = reviewView.findViewById(R.id.review);
        CountryCodePicker countryCodePicker = reviewView.findViewById(R.id.country_code_picker);

        String userId = document.getString("userId");
        db.collection("users").document(userId).get().addOnSuccessListener(userDocument -> {
            usernameTextView.setText(userDocument.getString("username"));
            String countryCode = userDocument.getString("countryCode");
            // 국기 설정
            if (countryCode != null) {
                countryCodePicker.setCountryForNameCode(countryCode.toLowerCase());
            }
        });

        visitDateTextView.setText(document.getString("date"));
        reviewTextView.setText(document.getString("reviewText"));

        String reviewImageUrl = document.getString("reviewImage");
        if (reviewImageUrl != null && !reviewImageUrl.isEmpty()) {
            // Glide 또는 Picasso로 이미지 로드
            Glide.with(this).load(reviewImageUrl).into(reviewImageView);
        } else {
            reviewImageView.setVisibility(View.GONE);
        }

        container.addView(reviewView);
    }

    private void checkUserEligibility(ImageView editButton) {
        String userId = auth.getCurrentUser().getUid();

        db.collection("reservations")
                .whereEqualTo("userId", userId)
                .whereEqualTo("restaurantId", restaurantId)
                .get()
                .addOnSuccessListener(reservationSnapshot -> {
                    if (!reservationSnapshot.isEmpty()) {
                        enableEditButton(editButton);
                        editButton.setClickable(true);
                    } else {
                        db.collection("waitings")
                                .whereEqualTo("userId", userId)
                                .whereEqualTo("restaurantId", restaurantId)
                                .get()
                                .addOnSuccessListener(waitingSnapshot -> {
                                    if (!waitingSnapshot.isEmpty()) {
                                        enableEditButton(editButton);
                                        editButton.setClickable(true);
                                    } else {
                                        disableEditButton(editButton);
                                        editButton.setClickable(false);
                                    }
                                });
                    }
                });
    }

    private void enableEditButton(ImageView editButton) {
        editButton.setEnabled(true);
        editButton.setAlpha(1.0f);  // 완전 불투명
    }

    private void disableEditButton(ImageView editButton) {
        editButton.setEnabled(false);
        editButton.setAlpha(0.5f);  // 반투명
    }
}
