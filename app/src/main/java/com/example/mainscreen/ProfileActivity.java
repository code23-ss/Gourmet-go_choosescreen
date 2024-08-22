package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileActivity extends AppCompatActivity {
    //private FirebaseFirestore db;
    private TextView userIdTextView;
    private String currentUserId; // 현재 사용자 ID를 저장할 변수

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        firestore = FirebaseFirestore.getInstance();

        // Firestore 인스턴스 초기화
        //db = FirebaseFirestore.getInstance();

        // 현재 사용자 ID 설정
        currentUserId = getCurrentUserId(); // 이 함수는 실제로 사용자의 ID를 반환해야 합니다.

        // UI 요소 초기화
        userIdTextView = findViewById(R.id.user_id_label);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Firestore에서 사용자 ID를 가져와서 TextView에 설정
        fetchUserIdFromFirestore(currentUserId);

        // reservation ImageView 클릭 리스너 설정
        ImageView reservationImageView = findViewById(R.id.reservation);
        reservationImageView.setOnClickListener(v -> {
            // MyReservationActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, MyReservationActivity.class);
            startActivity(intent);
        });

        // TextView 클릭 리스너 설정
        TextView faqTextView = findViewById(R.id.ask);
        faqTextView.setOnClickListener(v -> {
            // AskActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, AskActivity.class);
            startActivity(intent);
        });

        TextView termsServiceTextView = findViewById(R.id.terms_service);
        termsServiceTextView.setOnClickListener(v -> {
            // TermsOfServiceActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, TermsOfServiceActivity.class);
            startActivity(intent);
        });

        TextView privacyPolicyTextView = findViewById(R.id.privacy_policy);
        privacyPolicyTextView.setOnClickListener(v -> {
            // PrivacyPolicyActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, PrivacyPolicyActivity.class);
            startActivity(intent);
        });

        // Delete Account TextView 클릭 리스너 설정
        TextView deleteAccountTextView = findViewById(R.id.delete_account);
        deleteAccountTextView.setOnClickListener(v -> {
            // DeleteAccountActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, DeleteAccountActivity.class);
            startActivity(intent);
        });

        // Logout TextView 클릭 리스너 설정
        TextView logoutTextView = findViewById(R.id.logout);
        logoutTextView.setOnClickListener(v -> logoutUser());

        // Change Password TextView 클릭 리스너 설정
        TextView changePasswordTextView = findViewById(R.id.change_password);
        changePasswordTextView.setOnClickListener(v -> {
            // ChangePasswordActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });

        // Change Email TextView 클릭 리스너 설정
        TextView changeEmailTextView = findViewById(R.id.change_email);
        changeEmailTextView.setOnClickListener(v -> {
            // ChangeEmailActivity로 이동
            Intent intent = new Intent(ProfileActivity.this, ChangeEmailActivity.class);
            startActivity(intent);
        });
    }

    private void fetchUserIdFromFirestore(String currentUserId) {
        if (currentUserId != null) {
            // 디버깅: 로그를 통해 현재 사용자 ID 확인
            Toast.makeText(ProfileActivity.this, "Fetching data for user ID: " + currentUserId, Toast.LENGTH_SHORT).show();

            // 현재 사용자의 Firestore 문서 가져오기
            firestore.collection("users").document(currentUserId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // 디버깅: 문서 존재 여부 및 필드 값을 확인
                                Toast.makeText(ProfileActivity.this, "Document exists: " + document.getId(), Toast.LENGTH_SHORT).show();

                                String userId = document.getString("ID"); // Firestore 문서에서 'ID' 필드 가져오기 (대문자)
                                if (userId != null) {
                                    userIdTextView.setText(userId);
                                } else {
                                    Toast.makeText(ProfileActivity.this, "ID field is missing in the document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProfileActivity.this, "No such document. Check Firestore document path and ID", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // 디버깅: 실패 이유를 로그로 출력
                            Toast.makeText(ProfileActivity.this, "Failed to get document. Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "User ID is not available. Check authentication state", Toast.LENGTH_SHORT).show();
        }
    }

    private String getCurrentUserId() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            String userId = auth.getCurrentUser().getUid(); // 실제 사용자 ID를 반환
            Log.d("ProfileActivity", "User ID: " + userId);
            return userId;
        } else {
            // 사용자 로그인 상태가 아니면 null 반환
            Log.d("ProfileActivity", "No user logged in");
            return null;
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut(); // Firebase에서 로그아웃 수행
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
        // 로그인 화면으로 이동
        Intent intent = new Intent(ProfileActivity.this, MainscreenActivity.class);
        startActivity(intent);
        finish(); // 현재 액티비티 종료
    }

    private long backKeyPressedTime = 0;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "'Back' button pressed again to exit.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
        }
    }
}