package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainscreenActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private CheckBox saveIdCheckBox;
    private Button loginButton;
    private TextView createAccountTextView;
    private TextView findIDTextView;
    private TextView findPasswordTextView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainscreen);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // XML 파일에 정의된 UI 컴포넌트를 Java 객체로 초기화
        usernameEditText = findViewById(R.id.EmailAddress);
        passwordEditText = findViewById(R.id.Password);
        saveIdCheckBox = findViewById(R.id.saveId);
        loginButton = findViewById(R.id.buttonLogin);
        createAccountTextView = findViewById(R.id.createAccount);
        findIDTextView = findViewById(R.id.findID);
        findPasswordTextView = findViewById(R.id.findPassword);

        // 로그인 버튼 클릭 이벤트 리스너 설정
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = usernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // 간단한 유효성 검사
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainscreenActivity.this, "이메일과 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    // Firebase Authentication을 사용하여 로그인 시도
                    loginUser(email, password);
                }
            }
        });

        // 회원가입 텍스트뷰 클릭 이벤트 리스너 설정
        createAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, CreateAccountActivity.class);
                startActivity(intent);
            }
        });

        // 아이디 찾기 텍스트뷰 클릭 이벤트 리스너 설정
        findIDTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, ForgotIDActivity.class);
                startActivity(intent);
            }
        });

        // 비밀번호 찾기 텍스트뷰 클릭 이벤트 리스너 설정
        findPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainscreenActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공 시
                        String previousActivity = getIntent().getStringExtra("previousActivity");

                        if (previousActivity != null) {
                            try {
                                // 이전 Activity로 돌아가기
                                Class<?> previousClass = Class.forName(previousActivity);
                                Intent intent = new Intent(MainscreenActivity.this, previousClass);
                                startActivity(intent);
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // 이전 Activity 정보가 없는 경우 기본적으로 SecondClassActivity로 이동
                            Intent intent = new Intent(MainscreenActivity.this, SecondClassActivity.class);
                            startActivity(intent);
                        }
                        finish(); // 현재 활동을 종료하여 백 스택에서 제거
                    } else {
                        // 로그인 실패, 사용자에게 오류 메시지 표시
                        Toast.makeText(MainscreenActivity.this, "Login failed. Please check your email and password.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}