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

        setupCategoryButton(R.id.korean, "categories/FoodType/Subcategories/Korean", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.chinese, "categories/FoodType/Subcategories/Chinese", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.italian, "categories/FoodType/Subcategories/Italian", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.japanese, "categories/FoodType/Subcategories/Japanese", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.fushion, "categories/FoodType/Subcategories/Fusion", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.asian, "categories/FoodType/Subcategories/Asian", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.viewmore, "categories/FoodType/Subcategories/View_more", "categories/Location/Subcategories/Seoul", "#E24443");


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

        // Initialize image click listeners
        initializeImageClickListeners();
    }

    private void setupCategoryButton(int buttonId, String foodTypeCategoryPath, String locationCategoryPath, String colorHex) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(v -> {
            handleButtonClick(button, colorHex);
            executorService.submit(() -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                // FoodType 카테고리 경로
                DocumentReference foodTypeCategoryRef = db.document(foodTypeCategoryPath);

                // Location 카테고리 경로
                DocumentReference locationCategoryRef = db.document(locationCategoryPath);

                db.collection("restaurants")
                        .whereArrayContains("category_ids", foodTypeCategoryRef)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                List<String> imagePaths = new ArrayList<>();
                                List<String> names = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");

                                    boolean matchesLocation = false;
                                    for (DocumentReference ref : categoryIds) {
                                        if (ref.getPath().startsWith(locationCategoryRef.getPath())) {
                                            matchesLocation = true;
                                            break;
                                        }
                                    }

                                    if (matchesLocation) {
                                        String imagePath = (document.get("imagePath") != null) ? ((List<String>) document.get("imagePath")).get(0) : null;
                                        String name = (document.getString("name") != null) ? document.getString("name") : "Unknown";

                                        imagePaths.add(imagePath != null ? imagePath : "R.drawable.default_image");  // 실제 기본 이미지 리소스 ID
                                        names.add(name);
                                    }
                                }

                                runOnUiThread(() -> {
                                    if (!imagePaths.isEmpty() && !names.isEmpty()) {
                                        loadContent(imagePaths.toArray(new String[0]), names.toArray(new String[0]));
                                    } else {
                                        contentLayout.removeAllViews();
                                        Log.w("Firestore", "No matching documents found.");
                                    }
                                });
                            } else {
                                Log.w("Firestore", "Error getting documents.", task.getException());
                                runOnUiThread(() -> {
                                    contentLayout.removeAllViews();
                                    // Optional: Show error message to user
                                });
                            }
                        });

            });
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

                        // 데이터를 받아온 후 loadContent 호출
                        String[] imagePaths = new String[restaurants.size()];
                        String[] texts = new String[restaurants.size()];

                        for (int i = 0; i < restaurants.size(); i++) {
                            imagePaths[i] = restaurants.get(i).getImagePath().isEmpty() ? "default_image_path" : restaurants.get(i).getImagePath().get(0); // 예시
                            texts[i] = restaurants.get(i).getName();
                        }

                        loadContent(imagePaths, texts); // 여기서 호출

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

                // final 변수를 사용하여 람다식에서 참조
                final String imagePath = imagePaths[i];
                final String text = texts[i];

                // 클릭 리스너 설정
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
                    intent.putExtra("imagePath", imagePath);
                    intent.putExtra("name", text);
                    startActivity(intent);
                });

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
            Log.e("Firebase Storage", "Error getting image URL. File not found at location.", exception);
            imageView.setImageResource(R.drawable.default_image);  // 기본 이미지 설정
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
