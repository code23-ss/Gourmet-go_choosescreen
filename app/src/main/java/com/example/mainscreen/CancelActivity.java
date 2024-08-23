package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class CancelActivity extends AppCompatActivity {

    private Button buttonBackToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel);  // activity_cancel.xml을 호출

        // 버튼 초기화
        buttonBackToMain = findViewById(R.id.buttonBackToMain);

        // 버튼 클릭 리스너 설정
        buttonBackToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // MainActivity로 이동
                Intent intent = new Intent(CancelActivity.this, MainActivity.class);
                startActivity(intent);
                finish();  // 현재 액티비티를 종료하여 백스택에서 제거
            }
        });
    }
}
