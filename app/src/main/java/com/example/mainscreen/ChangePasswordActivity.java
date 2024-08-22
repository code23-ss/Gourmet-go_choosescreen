package com.example.mainscreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class ChangePasswordActivity extends AppCompatActivity {
    EditText nameEditText, idEditText;
    Button findPwBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        nameEditText = findViewById(R.id.findPw_nameEditText);
        idEditText = findViewById(R.id.findPw_IdEditText);
        findPwBtn = findViewById(R.id.findPwBtn);

        findPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameEditText.getText().toString().trim();
                final String email = idEditText.getText().toString().trim();

                // Check if name and email are entered
                if (name.isEmpty() || email.isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please enter both name and email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send password reset email using Firebase
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangePasswordActivity.this, "Password reset email sent.", Toast.LENGTH_SHORT).show();
                                // Navigate to the main activity after sending the email
                                Intent intent = new Intent(ChangePasswordActivity.this, ProfileActivity.class);
                                startActivity(intent);
                            } else {
                                // Handle Firebase exceptions
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException e) {
                                    Toast.makeText(ChangePasswordActivity.this, "The email address does not exist.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthRecentLoginRequiredException e) {
                                    Toast.makeText(ChangePasswordActivity.this, "Recent login required.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(ChangePasswordActivity.this, "User collision occurred.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(ChangePasswordActivity.this, "Failed to send password reset email.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}