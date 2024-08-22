package com.example.mainscreen;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Book2Activity extends AppCompatActivity {

    private RadioGroup radioGroupTitle, radioGrouppeople;
    private EditText editTextFirstName, editTextLastName, editTextMobileNumber, editTextEmail, editTextSpecialRequests;
    private CheckBox checkBoxPersonal;
    private CheckBox checkBoxUpdates;
    private Button buttonContinue;
    private FirebaseAuth mAuth;

    private int selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book2);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // RestaurantDetailsActivity에서 전달된 문서 ID 받기
        String restaurantId = getIntent().getStringExtra("restaurant_id");


        // Initialize views
        radioGroupTitle = findViewById(R.id.radioGroupTitle);
        radioGrouppeople = findViewById(R.id.radioGrouppeople);
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextMobileNumber = findViewById(R.id.editTextMobileNumber);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextSpecialRequests = findViewById(R.id.editTextSpecialRequests);
        checkBoxPersonal = findViewById(R.id.checkBoxPersonal);
        checkBoxUpdates = findViewById(R.id.checkBoxUpdates);
        buttonContinue = findViewById(R.id.buttonContinue);

        // Set default date and time
        Calendar now = Calendar.getInstance();
        selectedYear = now.get(Calendar.YEAR);
        selectedMonth = now.get(Calendar.MONTH);
        selectedDay = now.get(Calendar.DAY_OF_MONTH);
        selectedHour = now.get(Calendar.HOUR_OF_DAY);
        selectedMinute = now.get(Calendar.MINUTE);

        // DatePickerDialog initialization
        findViewById(R.id.datePickerButton).setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    (view, year, monthOfYear, dayOfMonth) -> {
                        selectedYear = year;
                        selectedMonth = monthOfYear;
                        selectedDay = dayOfMonth;
                    },
                    selectedYear,
                    selectedMonth,
                    selectedDay
            );
            // Set accent color
            datePickerDialog.setAccentColor(Color.parseColor("#E24443"));
            datePickerDialog.show(getSupportFragmentManager(), "DatePickerDialog");
        });

        // TimePickerDialog initialization
        findViewById(R.id.timePickerButton).setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                    (view, hourOfDay, minute, second) -> {
                        selectedHour = hourOfDay;
                        selectedMinute = minute;
                    },
                    selectedHour,
                    selectedMinute,
                    true
            );
            // Set accent color
            timePickerDialog.setAccentColor(Color.parseColor("#E24443"));
            timePickerDialog.show(getSupportFragmentManager(), "TimePickerDialog");
        });

        // Initialize button state
        buttonContinue.setEnabled(false);

        // Set listeners to check validation on each input change
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkValidation();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editTextFirstName.addTextChangedListener(textWatcher);
        editTextLastName.addTextChangedListener(textWatcher);
        editTextMobileNumber.addTextChangedListener(textWatcher);
        editTextEmail.addTextChangedListener(textWatcher);
        editTextSpecialRequests.addTextChangedListener(textWatcher);

        CompoundButton.OnCheckedChangeListener checkedChangeListener = (buttonView, isChecked) -> checkValidation();
        checkBoxPersonal.setOnCheckedChangeListener(checkedChangeListener);
        checkBoxUpdates.setOnCheckedChangeListener(checkedChangeListener);

        // Handle button click
        buttonContinue.setOnClickListener(v -> {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            if (currentUser != null) {
                // User is logged in, save reservation data
                String title = ((RadioButton) findViewById(radioGroupTitle.getCheckedRadioButtonId())).getText().toString();
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String mobileNumber = editTextMobileNumber.getText().toString();
                String email = editTextEmail.getText().toString();
                String specialRequests = editTextSpecialRequests.getText().toString();
                boolean personal = checkBoxPersonal.isChecked();
                boolean updates = checkBoxUpdates.isChecked();
                int people = Integer.parseInt(((RadioButton) findViewById(radioGrouppeople.getCheckedRadioButtonId())).getText().toString());

                // Save reservation data to Firestore
                saveReservationData(restaurantId, title, firstName, lastName, mobileNumber, email, specialRequests, personal, updates, people);
            } else {
                // User is not logged in, navigate to the login screen
                Intent intent = new Intent(this, MainscreenActivity.class);
                intent.putExtra("previousActivity", getClass().getName());
                startActivity(intent);
                finish(); // Close the current activity
            }
        });
    }

    private void checkValidation() {
        // Check if all fields are filled and both checkboxes are checked
        boolean allFieldsFilled = !editTextFirstName.getText().toString().trim().isEmpty() &&
                !editTextLastName.getText().toString().trim().isEmpty() &&
                !editTextMobileNumber.getText().toString().trim().isEmpty() &&
                !editTextEmail.getText().toString().trim().isEmpty();

        boolean allCheckboxesChecked = checkBoxPersonal.isChecked() && checkBoxUpdates.isChecked();

        // Enable the button only if all fields are filled and all checkboxes are checked
        boolean isValidationPassed = allFieldsFilled && allCheckboxesChecked;
        buttonContinue.setEnabled(isValidationPassed);

        // Show a toast message if the button is not enabled
        if (!isValidationPassed) {
            Toast.makeText(this, "Please fill all fields and check all checkboxes.", Toast.LENGTH_SHORT).show();
        }
    }


    private void saveReservationData(String restaurantId, String title, String firstName, String lastName, String mobileNumber, String email,
                                     String specialRequests, boolean personal, boolean updates, int people) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Create reservation data map
            Map<String, Object> reservation = new HashMap<>();
            reservation.put("restaurantId", restaurantId); // 식당의 문서 ID 추가
            reservation.put("title", title);
            reservation.put("firstName", firstName);
            reservation.put("lastName", lastName);
            reservation.put("mobileNumber", mobileNumber);
            reservation.put("email", email);
            reservation.put("specialRequests", specialRequests);
            reservation.put("personal", personal);
            reservation.put("updates", updates);
            reservation.put("people", people);
            reservation.put("date", String.format("%d/%d/%d", selectedDay, selectedMonth + 1, selectedYear));
            reservation.put("time", String.format("%d:%02d", selectedHour, selectedMinute));
            reservation.put("userId", currentUser.getUid()); // 사용자의 UID 추가

            // Save to Firestore
            firestore.collection("reservations")
                    .add(reservation)
                    .addOnSuccessListener(documentReference -> {
                        String reservationId = documentReference.getId(); // 문서 ID를 가져옴
                        Log.d("ReservationID", "Reservation ID: " + reservationId);

                        // 예약 성공 후 예약 확인 화면으로 이동
                        Intent intent = new Intent(this, BookingConfirmationActivity.class);
                        intent.putExtra("reservation_id", reservationId); // 예약 문서 ID 전달
                        intent.putExtra("restaurant_id", restaurantId); // 문서 ID 전달
                        intent.putExtra("path", "booking"); // 예약 경로 구분
                        startActivity(intent);
                        finish(); // 현재 액티비티 종료
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Book2Activity", "Error adding reservation", e);
                        Toast.makeText(Book2Activity.this, "Error making reservation", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // User is not logged in, navigate to the login screen
            Intent intent = new Intent(this, MainscreenActivity.class);
            intent.putExtra("previousActivity", getClass().getName());
            startActivity(intent);
            finish(); // Close the current activity
        }
    }

}
