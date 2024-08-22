package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class DeleteAccountActivity extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delete_account);

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Firebase instances
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // UI elements
        emailEditText = findViewById(R.id.email_edit_text);
        passwordEditText = findViewById(R.id.password_edit_text);
        deleteButton = findViewById(R.id.delete_button);

        // Delete button click listener
        deleteButton.setOnClickListener(v -> deleteAccount());
    }

    private void deleteAccount() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(DeleteAccountActivity.this, "Email and Password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Re-authenticate user
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            // Delete user data from Firestore
                            deleteUserFromFirestore(user.getUid());
                            // Delete user from Firebase Authentication
                            deleteUserFromFirebase(user);
                        }
                    } else {
                        Toast.makeText(DeleteAccountActivity.this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteUserFromFirestore(String userId) {
        db.collection("users").document(userId).delete()
                .addOnSuccessListener(aVoid -> {
                    // Successfully deleted user data from Firestore
                    Toast.makeText(DeleteAccountActivity.this, "User data deleted from Firestore", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(DeleteAccountActivity.this, "Failed to delete user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserFromFirebase(FirebaseUser user) {
        user.delete()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(DeleteAccountActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                        // Navigate back to login screen
                        navigateToLoginScreen();
                    } else {
                        Toast.makeText(DeleteAccountActivity.this, "Failed to delete account: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(DeleteAccountActivity.this, MainscreenActivity.class);
        startActivity(intent);
        finish(); // Close this activity
    }
}