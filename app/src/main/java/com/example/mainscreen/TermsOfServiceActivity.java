package com.example.mainscreen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class TermsOfServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terms_of_service);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // 약관 텍스트 설정
        TextView termsContent = findViewById(R.id.terms_content);
        termsContent.setText(getTermsOfServiceText());

        // "확인" 버튼 설정
        Button acceptButton = findViewById(R.id.button_accept);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자가 약관을 수락하면 창을 닫는다
                finish();
            }
        });
    }

    // 서비스 이용 약관 텍스트
    private String getTermsOfServiceText() {
        return "Terms of Service\n\n"
                + "Welcome to our service. Please read these terms and conditions carefully before using our application.\n\n"
                + "1. Acceptance of Terms\n"
                + "By using our service, you agree to comply with and be bound by these terms. If you do not agree, please do not use the service.\n\n"
                + "2. Use of the Service\n"
                + "You agree to use the service only for lawful purposes. You must not misuse the service in any way that may cause harm to others.\n\n"
                + "3. Privacy Policy\n"
                + "We are committed to protecting your privacy. Please review our Privacy Policy to understand our practices.\n\n"
                + "4. Limitation of Liability\n"
                + "We are not liable for any damages or losses arising from your use of the service.\n\n"
                + "5. Changes to Terms\n"
                + "We reserve the right to change these terms at any time. Your continued use of the service will signify your acceptance of the updated terms.\n\n"
                + "6. Contact Us\n"
                + "If you have any questions about these terms, please contact us at support@example.com.\n\n"
                + "Thank you for using our service!";
    }
}
