package com.example.mainscreen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.view.View;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // 개인정보 보호 정책 텍스트 설정
        TextView privacyPolicyContent = findViewById(R.id.privacy_policy_content);
        privacyPolicyContent.setText(getPrivacyPolicyText());

        // "확인" 버튼 설정
        Button acceptButton = findViewById(R.id.button_accept_privacy_policy);
        acceptButton.setOnClickListener(v -> {
            // 사용자가 개인정보 보호 정책을 수락하면 창을 닫는다
            finish();
        });
    }

    // 개인정보 보호 정책 텍스트
    private String getPrivacyPolicyText() {
        return "Privacy Policy\n\n"
                + "Welcome to our privacy policy. This document outlines how we collect, use, and protect your personal information.\n\n"
                + "1. Information Collection\n"
                + "We collect information that you provide directly to us, such as your name, email address, and phone number. We also collect information about your usage of our services.\n\n"
                + "2. Use of Information\n"
                + "The information we collect is used to provide and improve our services, communicate with you, and ensure the security of our services.\n\n"
                + "3. Information Sharing\n"
                + "We do not share your personal information with third parties except as required by law or as necessary to provide our services.\n\n"
                + "4. Data Security\n"
                + "We implement security measures to protect your personal information from unauthorized access and use.\n\n"
                + "5. Changes to Privacy Policy\n"
                + "We may update our privacy policy from time to time. We will notify you of any changes by posting the new policy on this page.\n\n"
                + "6. Contact Us\n"
                + "If you have any questions or concerns about our privacy policy, please contact us at privacy@example.com.\n\n"
                + "Thank you for trusting us with your information!";
    }
}