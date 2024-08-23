package com.example.mainscreen;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WaitActivity2 extends AppCompatActivity {

    private RadioGroup radioGroupPeople;
    private RadioGroup radioGroupDining;
    private EditText editTextCustomAdults;
    private Button buttonContinue;
    private FirebaseAuth mAuth;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wait2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore
        firestore = FirebaseFirestore.getInstance();

        // RestaurantDetailsActivity에서 전달된 문서 ID 받기
        String restaurantId = getIntent().getStringExtra("restaurant_id");

        // Initialize the UI elements
        radioGroupPeople = findViewById(R.id.radioGroupPeople);
        radioGroupDining = findViewById(R.id.radioGroupDining);
        editTextCustomAdults = findViewById(R.id.editTextCustomAdults);
        buttonContinue = findViewById(R.id.buttonContinue);

        // Add listener for radio buttons in People group
        radioGroupPeople.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // Check if the "Custom" option is selected
                if (checkedId == R.id.radioAdultsCustom) {
                    // Show the custom number of adults input field
                    editTextCustomAdults.setVisibility(View.VISIBLE);
                } else {
                    // Hide the custom number of adults input field
                    editTextCustomAdults.setVisibility(View.GONE);
                }
            }
        });

        // Add listeners to validate inputs
        radioGroupDining.setOnCheckedChangeListener((group, checkedId) -> checkValidation());

        editTextCustomAdults.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        // Set up the button click listener
        buttonContinue.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            // Get selected values
            int selectedPeopleId = radioGroupPeople.getCheckedRadioButtonId();
            RadioButton selectedPeopleButton = findViewById(selectedPeopleId);
            String numberOfPeople = selectedPeopleButton.getText().toString();

            // If custom option is selected, override number of people
            if (selectedPeopleId == R.id.radioAdultsCustom) {
                String customPeople = editTextCustomAdults.getText().toString();
                if (TextUtils.isEmpty(customPeople)) {
                    Toast.makeText(WaitActivity2.this, "Please enter the number of people", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution if no input is provided
                }
                numberOfPeople = customPeople; // Override with custom value
            }

            int selectedDiningId = radioGroupDining.getCheckedRadioButtonId();
            RadioButton selectedDiningButton = findViewById(selectedDiningId);
            String diningOption = selectedDiningButton.getText().toString();

            // Save waiting data to Firestore
            saveWaitingData(restaurantId, numberOfPeople, diningOption);
        });
    }

    private void checkValidation() {
        int selectedPeopleId = radioGroupPeople.getCheckedRadioButtonId();
        boolean isValid = true;  // 기본적으로 유효하다고 가정합니다.

        // If "Custom" is selected in people, ensure the EditText is filled
        if (selectedPeopleId == R.id.radioAdultsCustom) {
            if (TextUtils.isEmpty(editTextCustomAdults.getText().toString().trim())) {
                isValid = false;
                Toast.makeText(this, "Please enter the number of people.", Toast.LENGTH_SHORT).show();
            }
        }

        buttonContinue.setEnabled(isValid);
    }


    private void saveWaitingData(String restaurantId, String numberOfPeople, String diningOption) {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        // 한국 시간(KST)으로 현재 날짜와 시간을 가져옴
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

        String currentDate = dateFormat.format(new Date());
        String currentTime = timeFormat.format(new Date());

        // Create waiting data map
        Map<String, Object> waitingData = new HashMap<>();
        waitingData.put("restaurant_id", restaurantId);
        waitingData.put("numberOfPeople", numberOfPeople);
        waitingData.put("diningOption", diningOption);
        waitingData.put("date", currentDate); // 현재 날짜 추가
        waitingData.put("time", currentTime); // 현재 시간 추가
        waitingData.put("userId", currentUser.getUid()); // 사용자의 UID 추가

        // Save to Firestore
        firestore.collection("waitings") // The name of the collection where waitings will be stored
                .add(waitingData)
                .addOnSuccessListener(documentReference -> {
                    String waitingsId = documentReference.getId(); // 문서 ID를 가져옴
                    Log.d("WaitingsID", "Waitings ID: " + waitingsId);
                    // Check if user is logged in
                    if (currentUser != null) {
                        // User is logged in, navigate to the next screen
                        Intent intent = new Intent(this, WaitingConfirmationActivity.class);
                        intent.putExtra("waiting_id", waitingsId); // 예약 문서 ID 전달
                        intent.putExtra("restaurant_id", restaurantId); // 문서 ID 전달
                        intent.putExtra("path", "waiting"); // 대기 경로 구분
                        startActivity(intent);
                    } else {
                        // User is not logged in, navigate to the login screen
                        Intent intent = new Intent(this, MainscreenActivity.class);
                        intent.putExtra("previousActivity", getClass().getName());
                        startActivity(intent);
                    }
                    finish(); // Close the activity and go back to the previous screen
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(WaitActivity2.this, "Error adding waiting", Toast.LENGTH_SHORT).show();
                });
    }

}
