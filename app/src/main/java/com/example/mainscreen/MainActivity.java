package com.example.mainscreen;

import com.google.firebase.FirebaseApp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Spinner spinner;
    private LinearLayout contentLayout;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase 초기화
        FirebaseApp.initializeApp(this);

        // Firestore에서 데이터 로드 및 UI 업데이트
        loadRestaurantsFromFirestore();

        // Firestore 데이터 로드 후에 버튼 설정
        setupCategoryButton(R.id.korean, "koreanCategoryId", "#E24443");
        setupCategoryButton(R.id.chinese, "chineseCategoryId", "#E24443");
        setupCategoryButton(R.id.italian, "italianCategoryId", "#E24443");
        setupCategoryButton(R.id.japanese, "japaneseCategoryId", "#E24443");
        setupCategoryButton(R.id.fushion, "fushionCategoryId", "#E24443");
        setupCategoryButton(R.id.asian, "asianCategoryId", "#E24443");
        setupCategoryButton(R.id.viewmore, "viewmoreCategoryId", "#E24443");


        // Apply window insets to adjust padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hide action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // Initialize spinner
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.city_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(adapter.getPosition("Seoul"));

        // Initialize content layout
        contentLayout = findViewById(R.id.contentLayout);

        // Initialize executor service
        executorService = Executors.newFixedThreadPool(4);
    }

    private void setupCategoryButton(int buttonId, String categoryId, String colorHex) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            handleButtonClick(button, colorHex);
            executorService.submit(() -> {
                // 긴 작업을 백그라운드에서 수행
                loadCategoryData(categoryId);
            });
        });
    }

    private void loadCategoryData(String categoryId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference categoryRef = db.collection("categories").document(categoryId);

        db.collection("restaurants")
                .whereArrayContains("category_ids", categoryRef) // 카테고리별로 필터링
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> imagePaths = new ArrayList<>();
                        List<String> texts = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            imagePaths.add(document.getString("imagePath"));
                            texts.add(document.getString("name"));
                        }
                        runOnUiThread(() -> loadContent(imagePaths.toArray(new String[0]), texts.toArray(new String[0])));
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }



    private void handleButtonClick(Button button, String colorHex) {
        int color = Color.parseColor(colorHex);
        GradientDrawable drawable = (GradientDrawable) ContextCompat.getDrawable(this, R.drawable.round);
        drawable.setColor(color);
        button.setBackground(drawable);
        resetOtherButtons(button);
    }

    private void resetOtherButtons(Button clickedButton) {
        Button[] buttons = {
                findViewById(R.id.korean),
                findViewById(R.id.chinese),
                findViewById(R.id.italian),
                findViewById(R.id.japanese),
                findViewById(R.id.fushion),
                findViewById(R.id.asian),
                findViewById(R.id.viewmore)
        };
        for (Button button : buttons) {
            if (button != clickedButton) {
                button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_design));
            }
        }
    }

    // Firestore 인스턴스
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Firestore에서 레스토랑 데이터를 가져오는 함수
    private void loadRestaurantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurants = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Restaurant restaurant = document.toObject(Restaurant.class);
                            restaurants.add(restaurant);
                        }
                        // 데이터를 받은 후 추가 작업을 여기에 구현할 수 있습니다.
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    private void loadContent(String[] imagePaths, String[] texts) {
        runOnUiThread(() -> {
            contentLayout.removeAllViews(); // Clear existing views

            LayoutInflater inflater = LayoutInflater.from(MainActivity.this);
            for (int i = 0; i < imagePaths.length; i++) {
                View itemView = inflater.inflate(R.layout.button_item, contentLayout, false);

                ImageView imageView = itemView.findViewById(R.id.button_image);
                loadImageFromStorage(imagePaths[i], imageView); // Firebase Storage에서 이미지 로드

                TextView textView = itemView.findViewById(R.id.button_text);
                textView.setText(texts[i]);
                textView.setTypeface(Typeface.create("casual", Typeface.NORMAL));

                contentLayout.addView(itemView);
            }
        });
    }

    private void loadImageFromStorage(String imagePath, ImageView imageView) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(MainActivity.this)
                    .load(uri)
                    .apply(new RequestOptions().override(250, 200))
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            Log.e("Firebase Storage", "Error getting image URL.", exception);
            imageView.setImageResource(R.drawable.default_image); // 기본 이미지 설정 (필요 시)
        });
    }

    private void initializeImageClickListeners() {
        setupClickListener(R.id.book, RestaurantsActivity.class);
        setupClickListener(R.id.wait, WaitActivity.class);
        setupClickListener(R.id.hotdeals, HotdealsActivity.class);
        setupClickListener(R.id.guide, GuideActivity.class);
        setupClickListener(R.id.search, SearchActivity.class);
        setupClickListener(R.id.profile, ProfileActivity.class);
        setupClickListener(R.id.home, null); // Special case for "home" button
    }

    private void setupClickListener(int imageViewId, Class<?> activityClass) {
        ImageView imageView = findViewById(imageViewId);
        imageView.setOnClickListener(view -> {
            if (activityClass != null) {
                Intent intent = new Intent(getApplicationContext(), activityClass);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "You are already on the home screen", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Properly clear Glide images
        Glide.get(this).clearMemory();
        executorService.shutdown();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reset button color to default
        resetButtonColors();
        // Set spinner selection to "Seoul"
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinner.getAdapter();
        spinner.setSelection(adapter.getPosition("Seoul"));
    }

    private void resetButtonColors() {
        Button[] buttons = {
                findViewById(R.id.korean),
                findViewById(R.id.chinese),
                findViewById(R.id.italian),
                findViewById(R.id.japanese),
                findViewById(R.id.fushion),
                findViewById(R.id.asian),
                findViewById(R.id.viewmore)
        };
        for (Button button : buttons) {
            button.setBackground(ContextCompat.getDrawable(this, R.drawable.button_design));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedCity = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Selected city: " + selectedCity, Toast.LENGTH_SHORT).show();

        Intent intent = null;
        switch (selectedCity) {
            case "Seoul":
                return; // Do nothing if "Seoul" is selected
            case "Jeju":
                intent = new Intent(this, JejuActivity.class);
                break;
            case "Busan":
                intent = new Intent(this, BusanActivity.class);
                break;
            // Optionally add more cases if needed
            default:
                Toast.makeText(this, "City not recognized", Toast.LENGTH_SHORT).show();
                return; // Exit if the city is not recognized
        }

        if (intent != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
    }
}
