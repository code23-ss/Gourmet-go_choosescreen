package com.example.mainscreen;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.Calendar;
import java.util.List;

public class WaitingConfirmationActivity extends AppCompatActivity {

    private TextView waitingId, waitingName, waitingAddress, partySize, team, time;
    private LinearLayout btnCancelReservation, btnCallRestaurant;
    private Button btnConfirm;
    private ImageView waitingImage;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_confirmation);

        // Initialize Firebase Firestore and Storage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI components
        waitingId = findViewById(R.id.waiting_id);
        waitingAddress = findViewById(R.id.waiting_address);
        waitingName = findViewById(R.id.waiting_name);
        partySize = findViewById(R.id.party_size);
        team = findViewById(R.id.team); // 변경된 부분
        time = findViewById(R.id.time);
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation);
        btnCallRestaurant = findViewById(R.id.btn_call_restaurant);
        btnConfirm = findViewById(R.id.btn_confirm);
        waitingImage = findViewById(R.id.waiting_image);

        // Get data from Intent
        String waitingsId = getIntent().getStringExtra("waiting_id");
        String restaurantId = getIntent().getStringExtra("restaurant_id");

        // Set booking ID
        waitingId.setText("Waiting ID: " + waitingsId); // 변경된 부분

        // Load reservation details
        loadWaitingDetails(waitingsId);

        // Load restaurant details
        loadRestaurantDetails(restaurantId);

        // Calculate the number of teams ahead
        calculateTeamsAhead(waitingsId);

        // Handle reservation cancellation
        btnCancelReservation.setOnClickListener(v -> showCancelConfirmationDialog(waitingsId));

        // Handle restaurant contact copy
        btnCallRestaurant.setOnClickListener(v -> copyRestaurantContact(restaurantId));

        // Handle confirmation button click
        btnConfirm.setOnClickListener(v -> {
            // Confirm button handling logic
            Intent intent = new Intent(WaitingConfirmationActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadWaitingDetails(String waitingsId) {
        firestore.collection("waitings").document(waitingsId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String peopleStr = documentSnapshot.getString("numberOfPeople"); // `String` 타입으로 가져옴
                        String reservationTime = documentSnapshot.getString("time");

                        int people = 0;
                        if (peopleStr != null) {
                            try {
                                people = Integer.parseInt(peopleStr); // `String`에서 `int`로 변환
                            } catch (NumberFormatException e) {
                                Log.e("Error", "Failed to parse numberOfPeople: " + peopleStr, e);
                            }
                        }

                        reservationTime = reservationTime != null ? reservationTime : "N/A";

                        partySize.setText(people + " People");
                        time.setText(reservationTime);
                    } else {
                        Toast.makeText(WaitingConfirmationActivity.this, "Waiting information not found.", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WaitingConfirmationActivity.this, "Failed to load waiting information.", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void loadRestaurantDetails(String restaurantId) {
        firestore.collection("restaurants").document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String location = documentSnapshot.getString("location");
                        waitingAddress.setText(location != null ? location : "unknown address");

                        String name = documentSnapshot.getString("name");
                        waitingName.setText(name != null ? name : "unknown name");

                        // imagePath가 List<String> 형태일 때 처리
                        Object imagePathObject = documentSnapshot.get("imagePath");

                        if (imagePathObject instanceof List) {
                            List<String> imagePathList = (List<String>) imagePathObject;
                            if (!imagePathList.isEmpty()) {
                                String imagePath = imagePathList.get(0); // 첫 번째 이미지 경로만 사용
                                loadRestaurantImage(imagePath);
                            } else {
                                Log.e("WaitingConfirmation", "imagePath list is empty.");
                                Toast.makeText(WaitingConfirmationActivity.this, "imagePath list is empty.", Toast.LENGTH_SHORT).show();
                                waitingImage.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                            }
                        } else {
                            Log.e("WaitingConfirmation", "imagePath is not a List<String>: " + imagePathObject);
                            Toast.makeText(WaitingConfirmationActivity.this, "imagePath is not a List<String>: ", Toast.LENGTH_SHORT).show();
                            waitingImage.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(WaitingConfirmationActivity.this, "Failed to load restaurant details.", Toast.LENGTH_SHORT).show());
    }

    private void calculateTeamsAhead(String waitingsId) {
        firestore.collection("waitings")
                .whereEqualTo("date", getCurrentDate())
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int teamsAhead = 0;
                    for (DocumentSnapshot document : querySnapshot) {
                        String waitingTime = document.getString("time");
                        if (waitingTime != null && !document.getId().equals(waitingsId)) {
                            // Compare time to see if they are ahead
                            if (isTimeBefore(waitingTime, time.getText().toString())) {
                                teamsAhead++;
                            }
                        }
                    }
                    team.setText(String.valueOf(teamsAhead));
                })
                .addOnFailureListener(e -> Toast.makeText(WaitingConfirmationActivity.this, "Failed to calculate teams ahead.", Toast.LENGTH_SHORT).show());
    }

    private boolean isTimeBefore(String time1, String time2) {
        // 시간 비교 로직을 구현합니다. 예를 들어 "HH:mm" 형식으로 된 시간을 비교합니다.
        // 이 방법은 간단한 문자열 비교로도 가능하지만, 더 정확하게 하려면 Date 객체로 변환하여 비교하는 것이 좋습니다.
        return time1.compareTo(time2) < 0;
    }

    private String getCurrentDate() {
        // 현재 날짜를 "yyyy-MM-dd" 형식으로 반환합니다.
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1; // 월은 0부터 시작하므로 1을 더합니다.
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return year + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    private void loadRestaurantImage(String imagePath) {
        StorageReference storageReference = storage.getReference().child(imagePath);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(waitingImage))
                .addOnFailureListener(e -> Toast.makeText(WaitingConfirmationActivity.this, "Failed to load image.", Toast.LENGTH_SHORT).show());
    }

    private void cancelReservation(String waitingsId) {
        firestore.collection("waitings").document(waitingsId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(WaitingConfirmationActivity.this, "Waiting canceled.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(WaitingConfirmationActivity.this, "Failed to cancel waiting.", Toast.LENGTH_SHORT).show());
    }

    private void showCancelConfirmationDialog(String waitingsId) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Waiting Canceled")
                .setMessage("Are you sure you want to cancel?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    cancelReservation(waitingsId);
                    Intent intent = new Intent(WaitingConfirmationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void copyRestaurantContact(String restaurantId) {
        firestore.collection("restaurants").document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String contactNumber = documentSnapshot.getString("contact_number");
                        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Restaurant Contact", contactNumber);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(WaitingConfirmationActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(WaitingConfirmationActivity.this, "Failed to copy contact number.", Toast.LENGTH_SHORT).show());
    }
}
