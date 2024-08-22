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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException;

public class ChangeEmailActivity extends AppCompatActivity {
    private EditText nameEditText, idEditText, emailEditText;
    private Button findPwBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_email); // Make sure the layout file is named `change_email.xml`

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Initialize UI elements
        nameEditText = findViewById(R.id.findPw_nameEditText);
        idEditText = findViewById(R.id.findPw_idEditText);
        emailEditText = findViewById(R.id.findPw_emailEditText);
        findPwBtn = findViewById(R.id.findPwBtn);

        findPwBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = nameEditText.getText().toString().trim();
                final String id = idEditText.getText().toString().trim();
                final String email = emailEditText.getText().toString().trim();

                // Check if name, ID, and email are entered
                if (name.isEmpty() || id.isEmpty() || email.isEmpty()) {
                    Toast.makeText(ChangeEmailActivity.this, "Please enter name, ID, and email.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Validate if the email exists and matches the entered name and ID
                verifyUserAndSendEmail(name, id, email);
            }
        });
    }

    private void verifyUserAndSendEmail(String name, String id, String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Assuming the ID refers to the user's email in this example
        auth.fetchSignInMethodsForEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getSignInMethods().isEmpty()) {
                            Toast.makeText(ChangeEmailActivity.this, "No account found with this email.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Normally you would verify the name and ID against a database, but here we just send an email.
                        sendVerificationEmail(email);
                    } else {
                        Toast.makeText(ChangeEmailActivity.this, "Failed to verify email. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendVerificationEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(ChangeEmailActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        // Navigate to another activity or perform other actions
                        Intent intent = new Intent(ChangeEmailActivity.this, ProfileActivity.class);
                        startActivity(intent);
                        finish(); // Close this activity
                    } else {
                        // Handle different exceptions
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            Toast.makeText(ChangeEmailActivity.this, "Invalid email format.", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthInvalidUserException e) {
                            Toast.makeText(ChangeEmailActivity.this, "Email does not exist.", Toast.LENGTH_SHORT).show();
                        } catch (FirebaseAuthRecentLoginRequiredException e) {
                            Toast.makeText(ChangeEmailActivity.this, "Recent login required.", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(ChangeEmailActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
