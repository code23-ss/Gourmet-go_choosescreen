package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private FirebaseAuth auth; // Firebase를 사용하는 권한
    private FirebaseFirestore firestore;
    private CountryCodePicker codePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        auth = FirebaseAuth.getInstance(); // Firebase에서 인스턴스를 가져올 것이다!
        firestore = FirebaseFirestore.getInstance();

        codePicker = findViewById(R.id.country_code); // CountryCodePicker 초기화

        Button registerButton = findViewById(R.id.buttonRegister); // 회원가입 버튼 객체 생성

        registerButton.setOnClickListener(v -> registerUser()); // 눌렀을 때 registerUser 함수를 쓸 것이다!
    }

    private void saveUserData(String name, String ID, String email, String password, String countryCode) {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid(); // 현재 로그인한 사용자의 UID 가져오기

            Map<String, String> user = new HashMap<>();
            user.put("name", name);
            user.put("ID", ID);
            user.put("email", email);
            user.put("password", password);
            user.put("countryCode", countryCode);

            // UID를 문서 ID로 사용하여 Firestore에 저장
            firestore.collection("users").document(uid).set(user)
                    .addOnSuccessListener(aVoid -> Log.d("CreateAccountActivity", "DocumentSnapshot successfully written!"))
                    .addOnFailureListener(e -> Log.e("CreateAccountActivity", "Error writing document", e));
        }
    }


    private void registerUser() { // xml에 있는 id의 이름을 가져와서 객체로 생성
        String name = ((EditText) findViewById(R.id.editTextName)).getText().toString(); // 이름 필드 가져오기
        String ID = ((EditText) findViewById(R.id.editTextUserID)).getText().toString();
        String email = ((EditText) findViewById(R.id.editTextEmail)).getText().toString();
        String password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
        String countryCode = codePicker.getSelectedCountryCode(); // 선택된 국가 코드 가져오기

        auth.createUserWithEmailAndPassword(email, password) // firebase 권한으로 email, password를 만든다.
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 회원가입 성공 시 Firestore에 사용자 세부 정보 저장
                        saveUserData(name, ID, email, password, countryCode);
                        // 회원가입 성공 메시지 표시
                        Toast.makeText(CreateAccountActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        // 메인 액티비티로 이동
                        navigateToMainActivity();
                    } else {
                        // 회원가입 실패 시 사용자에게 메시지 표시
                        Toast.makeText(CreateAccountActivity.this, "Registration failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(CreateAccountActivity.this, MainscreenActivity.class); // 로그인 화면으로 돌아가게 하는 intent 이용!
        startActivity(intent);
        finish();  // 현재 액티비티를 종료하여 뒤로가기 버튼으로 다시 돌아오지 않도록 한다.
    }
}
