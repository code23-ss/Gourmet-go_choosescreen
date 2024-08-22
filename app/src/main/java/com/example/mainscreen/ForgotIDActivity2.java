package com.example.mainscreen;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotIDActivity2 extends AppCompatActivity {
    TextView idTextView;
    Button backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_idactivity2);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        idTextView = findViewById(R.id.findIdtv1);
        backBtn = findViewById(R.id.backBtn);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email"); // 'email' 변수로 Intent 값 받기

        findUserId(name, email); // 'name'과 'email'로 사용자 검색

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotIDActivity2.this, MainscreenActivity.class);
                startActivity(intent);
            }
        });
    }

    // 사용자의 이름과 이메일을 이용하여 ID를 찾는 메서드
    private void findUserId(String name, String email) {
        DatabaseReference userReference = FirebaseDatabase.getInstance().getReference().child("users");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean found = false;
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String userName = userSnapshot.child("name").getValue(String.class);
                    String userEmail = userSnapshot.child("email").getValue(String.class); // 'email' 필드로 사용자 찾기

                    Log.d("FirebaseData", "UserName: " + userName + ", UserEmail: " + userEmail);

                    if (userName != null && userName.equals(name) && userEmail != null && userEmail.equals(email)) {
                        String userID = userSnapshot.child("ID").getValue(String.class); // 대문자 'ID' 필드에서 사용자의 ID를 가져옴
                        idTextView.setText("'" + userID + "' 입니다.");
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    idTextView.setText("해당하는 사용자를 찾을 수 없습니다.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("FirebaseError", "Database error: " + databaseError.getMessage());
                idTextView.setText("오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    }
}
