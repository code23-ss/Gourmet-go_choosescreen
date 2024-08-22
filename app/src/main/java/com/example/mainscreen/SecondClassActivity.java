package com.example.mainscreen;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class SecondClassActivity extends AppCompatActivity {

    private SecondClass secondClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondclass);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // SecondClass 인스턴스 생성
        secondClass = new SecondClass();

        // 버튼 ID 배열
        int[] buttonIds = {
                R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6
        };

        // 각 버튼이 호출할 메소드들을 매핑
        Runnable[] methods = {
                new Runnable() { @Override public void run() { secondClass.buttonMethod1(); }},
                new Runnable() { @Override public void run() { secondClass.buttonMethod2(); }},
                new Runnable() { @Override public void run() { secondClass.buttonMethod3(); }},
                new Runnable() { @Override public void run() { secondClass.buttonMethod4(); }},
                new Runnable() { @Override public void run() { secondClass.buttonMethod5(); }},
                new Runnable() { @Override public void run() { secondClass.buttonMethod6(); }}
        };

        // 각 버튼의 클릭 횟수를 저장할 배열
        int[] clickCounts = new int[buttonIds.length];

        // 각 버튼에 초기 클릭 리스너 설정
        for (int i = 0; i < buttonIds.length; i++) {
            Button button = findViewById(buttonIds[i]);
            final int index = i; // 배열 인덱스를 final로 선언하여 내부 클래스에서 사용

            // 클릭 리스너 정의
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickCounts[index]++;
                    if (clickCounts[index] % 2 == 0) {
                        button.setBackgroundColor(Color.TRANSPARENT); // 짝수번 클릭 시 색상 초기화
                    } else {
                        methods[index].run();
                        button.setBackgroundColor(Color.parseColor("#AFD485")); // 홀수번 클릭 시 색상 변경
                    }// 버튼 클릭 시 해당 메소드 호출
                }
            };

            // 버튼에 클릭 리스너 설정
            button.setOnClickListener(listener);

        }

        // 하이퍼링크 스타일의 버튼 설정
        TextView hyperlinkButton = findViewById(R.id.hyperlink_button);
        hyperlinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 Activity로 전환
                Intent intent = new Intent(SecondClassActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}