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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView bookingId, bookingName, bookingAddress, partySize, date, time, contactInfo;
    private LinearLayout btnCancelReservation, btnCallRestaurant;
    private Button btnConfirm;
    private ImageView bookingImage;

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        // Initialize Firebase Firestore and Storage
        firestore = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Initialize UI components
        bookingId = findViewById(R.id.booking_id);
        bookingAddress = findViewById(R.id.booking_address);
        bookingName = findViewById(R.id.booking_name);
        partySize = findViewById(R.id.party_size);
        date = findViewById(R.id.date);
        time = findViewById(R.id.time);
        contactInfo = findViewById(R.id.contact_info);
        btnCancelReservation = findViewById(R.id.btn_cancel_reservation);
        btnCallRestaurant = findViewById(R.id.btn_call_restaurant);
        btnConfirm = findViewById(R.id.btn_confirm);
        bookingImage = findViewById(R.id.booking_image);

        // Get data from Intent
        String reservationId = getIntent().getStringExtra("reservation_id");
        String restaurantId = getIntent().getStringExtra("restaurant_id");
        String Path = getIntent().getStringExtra("path");

        // Set booking ID
        bookingId.setText("Booking ID: " + reservationId);

        // Load reservation details
        loadReservationDetails(reservationId);

        // Load restaurant details
        loadRestaurantDetails(restaurantId);

        // Handle reservation cancellation
        btnCancelReservation.setOnClickListener(v -> showCancelConfirmationDialog(reservationId));

        // Handle restaurant contact copy
        btnCallRestaurant.setOnClickListener(v -> copyRestaurantContact(restaurantId));

        // Handle confirmation button click
        btnConfirm.setOnClickListener(v -> {
            if ("booking".equals(Path)) {
                // Booking 경로에서 온 경우
                Intent intent = new Intent(BookingConfirmationActivity.this, MainActivity.class);
                startActivity(intent);
            }
            finish();
        });
    }
    private void loadReservationDetails(String reservationId) {
        firestore.collection("reservations").document(reservationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Long peopleLong = documentSnapshot.getLong("people");  // `Long` 타입으로 가져옴
                        String reservationDate = documentSnapshot.getString("date");
                        String reservationTime = documentSnapshot.getString("time");
                        String title = documentSnapshot.getString("title");
                        String firstName = documentSnapshot.getString("firstName");
                        String lastName = documentSnapshot.getString("lastName");
                        String mobileNumber = documentSnapshot.getString("mobileNumber");
                        String email = documentSnapshot.getString("email");

                        int people = peopleLong != null ? peopleLong.intValue() : 0;  // `Long`에서 `int`로 변환
                        reservationDate = reservationDate != null ? reservationDate : "N/A";
                        reservationTime = reservationTime != null ? reservationTime : "N/A";
                        title = title != null ? title : "";
                        firstName = firstName != null ? firstName : "";
                        lastName = lastName != null ? lastName : "";
                        mobileNumber = mobileNumber != null ? mobileNumber : "N/A";
                        email = email != null ? email : "N/A";

                        partySize.setText(people + " People");
                        date.setText(reservationDate);
                        time.setText(reservationTime);
                        contactInfo.setText(title + " " + firstName + " " + lastName + "\n" + mobileNumber + " · " + email);
                    } else {
                        Toast.makeText(BookingConfirmationActivity.this, "Reservation information not found", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(BookingConfirmationActivity.this, "Failed to load reservation information", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }


    private void loadRestaurantDetails(String restaurantId) {
        firestore.collection("restaurants").document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String location = documentSnapshot.getString("location");
                        bookingAddress.setText(location != null ? location : "unknown address");

                        String name = documentSnapshot.getString("name");
                        bookingName.setText(name != null ? name : "unknown name");

                        // imagePath가 List<String> 형태일 때 처리
                        Object imagePathObject = documentSnapshot.get("imagePath");

                        if (imagePathObject instanceof List) {
                            List<String> imagePathList = (List<String>) imagePathObject;
                            if (!imagePathList.isEmpty()) {
                                String imagePath = imagePathList.get(0); // 첫 번째 이미지 경로만 사용
                                loadRestaurantImage(imagePath);
                            } else {
                                Log.e("BookingConfirmation", "imagePath list is empty.");
                                Toast.makeText(BookingConfirmationActivity.this, "imagePath list is empty.", Toast.LENGTH_SHORT).show();
                                bookingImage.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                            }
                        } else {
                            Log.e("BookingConfirmation", "imagePath is not a List<String>: " + imagePathObject);
                            Toast.makeText(BookingConfirmationActivity.this, "imagePath is not a List<String>: ", Toast.LENGTH_SHORT).show();
                            bookingImage.setImageResource(R.drawable.default_image); // 기본 이미지 설정
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(BookingConfirmationActivity.this, "Failed to load restaurant details.", Toast.LENGTH_SHORT).show());
    }


    private void loadRestaurantImage(String imagePath) {
        StorageReference storageReference = storage.getReference().child(imagePath);
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> Glide.with(this).load(uri).into(bookingImage))
                .addOnFailureListener(e -> Toast.makeText(BookingConfirmationActivity.this, "Failed to load image.", Toast.LENGTH_SHORT).show());
    }

    private void cancelReservation(String reservationId) {
        firestore.collection("reservations").document(reservationId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(BookingConfirmationActivity.this, "Reservation canceled.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(BookingConfirmationActivity.this, "Failed to cancel reservation.", Toast.LENGTH_SHORT).show());
    }

    private void showCancelConfirmationDialog(String reservationId) {
        // AlertDialog.Builder를 사용해 대화상자를 구성
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reservation Canceled")
                .setMessage("Are you sure you want to cancel?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // 확인 버튼을 눌렀을 때 예약 삭제
                    cancelReservation(reservationId);
                    Intent intent = new Intent(BookingConfirmationActivity.this, CancelActivity.class);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    // 취소 버튼을 눌렀을 때 대화상자 닫기
                    dialog.dismiss();
                })
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
                        Toast.makeText(BookingConfirmationActivity.this, "Copied", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(BookingConfirmationActivity.this, "Failed to copy contact number.", Toast.LENGTH_SHORT).show());
    }
}
