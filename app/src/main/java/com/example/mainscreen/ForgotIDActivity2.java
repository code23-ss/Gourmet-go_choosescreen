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

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
        String email = intent.getStringExtra("email");

        findUserId(name, email);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotIDActivity2.this, MainscreenActivity.class);
                startActivity(intent);
            }
        });
    }

    private void findUserId(String name, String email) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users")
                .whereEqualTo("name", name)
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean found = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userID = document.getString("ID");
                            idTextView.setText("'" + userID + "' 입니다.");
                            found = true;
                            break;
                        }

                        if (!found) {
                            idTextView.setText("해당하는 사용자를 찾을 수 없습니다.");
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                        idTextView.setText("오류가 발생했습니다. 다시 시도해주세요.");
                    }
                });
    }
}