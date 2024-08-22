package com.example.mainscreen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AskActivity extends AppCompatActivity {

    private EditText inquiryInput;
    private Button submitButton;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Firebase 인스턴스 초기화
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // EditText와 Button 초기화
        inquiryInput = findViewById(R.id.inquiry_input);
        submitButton = findViewById(R.id.submit_button);

        // Submit 버튼 클릭 리스너 설정
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inquiryText = inquiryInput.getText().toString().trim();

                if (!inquiryText.isEmpty()) {
                    // Firestore에 문의사항 저장
                    saveInquiryToFirestore(inquiryText);
                } else {
                    // 텍스트 상자가 비어 있을 경우 경고 메시지
                    Toast.makeText(AskActivity.this, "Please write your inquiry before submitting.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void saveInquiryToFirestore(String inquiryText) {
        // 현재 로그인한 사용자 정보 가져오기
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            // 로그인한 사용자의 고유 ID (UID) 가져오기
            String userId = currentUser.getUid();

            // 문의사항을 해시맵에 저장
            Map<String, Object> inquiry = new HashMap<>();
            inquiry.put("userId", userId); // 로그인한 사용자의 UID 저장
            inquiry.put("inquiryText", inquiryText);
            inquiry.put("timestamp", System.currentTimeMillis());

            // Firestore에 문의사항 저장
            firestore.collection("asks")
                    .add(inquiry)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // 저장 성공 시 메시지 표시 및 액티비티 종료
                            Toast.makeText(AskActivity.this, "Inquiry submitted successfully.", Toast.LENGTH_SHORT).show();
                            inquiryInput.setText(""); // 텍스트 상자 비우기
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // 저장 실패 시 오류 메시지 표시
                            Toast.makeText(AskActivity.this, "Failed to submit inquiry: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // 사용자가 로그인되지 않았을 때
            Toast.makeText(AskActivity.this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }
}
