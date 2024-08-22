package com.example.mainscreen;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.concurrent.Executor;

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Firebase 초기화
        FirebaseApp.initializeApp(this);

        // Firebase 익명 로그인 설정
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // 익명 로그인 성공
                    Log.d("FirebaseAuth", "signInAnonymously:success");
                } else {
                    // 익명 로그인 실패
                    Log.e("FirebaseAuth", "signInAnonymously:failure", task.getException());
                }
            });
        }
    }
}
