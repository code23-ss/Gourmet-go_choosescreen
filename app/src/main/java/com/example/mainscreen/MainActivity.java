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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    private String locationPath = "categories/Location/Subcategories/Seoul";
    private String matchType ="startsWith";

    private static final String TAG = MainActivity.class.getSimpleName();

    private Spinner spinner;
    private LinearLayout contentLayout;
    private ExecutorService executorService;

    public static final int TAG_LOCATION_PATH = R.id.location_path;
    public static final int TAG_MATCH_TYPE = R.id.match_type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // RecyclerView 설정
        setupRecyclerView();

        // Firebase 초기화
        FirebaseApp.initializeApp(this);

        // Firebase 익명 로그인 설정
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            auth.signInAnonymously().addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // 익명 로그인 성공
                    Log.d("FirebaseAuth", "signInAnonymously:success");
                } else {
                    // 익명 로그인 실패
                    Log.e("FirebaseAuth", "signInAnonymously:failure", task.getException());
                }
            });
        }

        // Firestore에서 데이터 로드 및 UI 업데이트
        loadRestaurantsFromFirestore();

        // 카드뷰에 태그로 locationPath와 matchType 설정
        findViewById(R.id.new_gangnam_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Gangnam");
        findViewById(R.id.new_gangnam_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_gwanghwamun_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Gwanghwamun");
        findViewById(R.id.new_gwanghwamun_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_hongdae_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Hongdae");
        findViewById(R.id.new_hongdae_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_insadong_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Insadong");
        findViewById(R.id.new_insadong_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_itaewon_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Itaewon");
        findViewById(R.id.new_itaewon_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_jamsil_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Jamsil");
        findViewById(R.id.new_jamsil_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_myeongdong_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Myeongdong");
        findViewById(R.id.new_myeongdong_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.new_seongsu_card_view).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul/Sub_subcategories/Seongsu");
        findViewById(R.id.new_seongsu_card_view).setTag(TAG_MATCH_TYPE, "exactMatch");

        findViewById(R.id.book).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul");
        findViewById(R.id.book).setTag(TAG_MATCH_TYPE, "startsWith");

        findViewById(R.id.wait).setTag(TAG_LOCATION_PATH, "categories/Location/Subcategories/Seoul"); //추가 수정사항
        findViewById(R.id.wait).setTag(TAG_MATCH_TYPE, "startsWith");

        // 모든 카드뷰에 같은 클릭 핸들러를 연결
        findViewById(R.id.new_gangnam_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_gwanghwamun_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_hongdae_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_insadong_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_itaewon_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_jamsil_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_myeongdong_card_view).setOnClickListener(this::openRestaurantsActivity);
        findViewById(R.id.new_seongsu_card_view).setOnClickListener(this::openRestaurantsActivity);

        //findViewById(R.id.book).setOnClickListener(this::openRestaurantsActivity);
        //findViewById(R.id.wait).setOnClickListener(this::openWaitActivity);


        setupCategoryButton(R.id.korean, "categories/FoodType/Subcategories/Korean", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.chinese, "categories/FoodType/Subcategories/Chinese", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.italian, "categories/FoodType/Subcategories/Italian", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.japanese, "categories/FoodType/Subcategories/Japanese", "categories/Location/Subcategories/Seoul", "#E24443");
        setupCategoryButton(R.id.fusion, "categories/FoodType/Subcategories/Fusion", "categories/Location/Subcategories/Seoul", "#E24443");
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

    public void openRestaurantsActivity(View view) {
        String locationPath = (String) view.getTag(TAG_LOCATION_PATH); // 태그에서 경로를 가져옴
        String matchType = (String) view.getTag(TAG_MATCH_TYPE); // 태그에서 matchType 가져옴

        Log.d(TAG, "Sending locationPath: " + locationPath);
        Log.d(TAG, "Sending matchType: " + matchType);

        Intent intent = new Intent(this, RestaurantsActivity.class);
        intent.putExtra("locationPath", locationPath); // 경로를 전달
        intent.putExtra("matchType", matchType); // matchType을 전달

        // Intent 실행 로그
        Log.d(TAG, "Starting RestaurantsActivity with locationPath: " + locationPath + " and matchType: " + matchType);
        startActivity(intent);
    }

    public void openWaitActivity(View view) {
        String locationPath = (String) view.getTag(TAG_LOCATION_PATH); // 태그에서 경로를 가져옴
        String matchType = (String) view.getTag(TAG_MATCH_TYPE); // 태그에서 matchType을 가져옴

        Log.d(TAG, "Sending locationPath to WaitActivity: " + locationPath);
        Log.d(TAG, "Sending matchType to WaitActivity: " + matchType);

        // WaitActivity로 이동하는 Intent
        Intent intent = new Intent(this, WaitActivity.class);
        intent.putExtra("locationPath", locationPath); // 경로를 전달
        intent.putExtra("matchType", matchType); // matchType을 전달
        // Intent 실행 로그
        Log.d(TAG, "Starting WaitActivity with locationPath: " + locationPath + " and matchType: " + matchType);
        startActivity(intent);
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
                                List<String> restaurantIds = new ArrayList<>();

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");
                                    String restaurantId = document.getId();  // 여기서 문서 ID를 가져옴


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
                                        restaurantIds.add(restaurantId);  // restaurantId 추가
                                    }
                                }

                                runOnUiThread(() -> {
                                    if (!imagePaths.isEmpty() && !names.isEmpty()) {
                                        loadContent(imagePaths.toArray(new String[0]), names.toArray(new String[0]), restaurantIds.toArray(new String[0])); // restaurantIds 전달
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
                findViewById(R.id.fusion),
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
        DocumentReference seoulRef = db.document("categories/Location/Subcategories/Seoul");

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurants = new ArrayList<>();
                        List<String> imagePaths = new ArrayList<>();
                        List<String> names = new ArrayList<>(); // 변수 이름을 'texts'에서 'names'로 변경
                        List<String> restaurantIds = new ArrayList<>();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");

                            boolean matchesSeoul = false;
                            for (DocumentReference ref : categoryIds) {
                                if (ref.getPath().startsWith(seoulRef.getPath())) {
                                    matchesSeoul = true;
                                    break;
                                }
                            }

                            if (matchesSeoul) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurants.add(restaurant);

                                imagePaths.add(restaurant.getImagePath().isEmpty() ? "default_image_path" : restaurant.getImagePath().get(0));
                                names.add(restaurant.getName()); // 'texts' 대신 'names'를 사용
                                restaurantIds.add(document.getId()); // restaurantId 할당
                            }
                        }

                        if (restaurants.isEmpty()) {
                            Log.w("Firestore", "No restaurants found with location matching 'Seoul'.");
                            runOnUiThread(() -> {
                                contentLayout.removeAllViews();
                                Toast.makeText(MainActivity.this, "No restaurants found in Seoul", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            Log.d("Firestore", "Restaurants found with location matching 'Seoul': " + restaurants.size());
                            loadContent(
                                    imagePaths.toArray(new String[0]),
                                    names.toArray(new String[0]), // 'texts' 대신 'names'를 사용
                                    restaurantIds.toArray(new String[0])
                            );
                        }

                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                        runOnUiThread(() -> {
                            contentLayout.removeAllViews();
                            Toast.makeText(MainActivity.this, "Error loading restaurants.", Toast.LENGTH_SHORT).show();
                        });
                    }
                });
    }




    private void loadContent(String[] imagePaths, String[] texts, String[] restaurantIds) {
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

                // 로그 추가: imagePath가 올바른지 확인
                Log.d("MainActivity", "ImagePath[" + i + "]: " + imagePaths[i]);

                // final 변수를 사용하여 람다식에서 참조
                final String imagePath = imagePaths[i];
                final String text = texts[i];
                final String restaurantId = restaurantIds[i];  // restaurantId 가져오기

                // 클릭 리스너 설정
                itemView.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, RestaurantDetailsActivity.class);
                    intent.putExtra("name", text);
                    intent.putExtra("imagePath", imagePath);
                    intent.putExtra("restaurant_id", restaurantId); // 문서 ID를 전달
                    startActivity(intent);

                    // 로그 추가: Intent에 추가된 값 확인
                    Log.d("MainActivity", "Intent - ImagePath: " + imagePath + ", Name: " + text + ", RestaurantId: " + restaurantId);
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

                // 특정 버튼에 대해서만 데이터를 전달
                if (imageViewId == R.id.book) {
                    intent.putExtra("locationPath", locationPath);
                    intent.putExtra("matchType", matchType);
                } else if(imageViewId == R.id.wait) {
                    intent.putExtra("locationPath", locationPath);
                    intent.putExtra("matchType", matchType);
                }

                startActivity(intent);
            } else {
                // Home 버튼에 대한 특별 처리
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
                findViewById(R.id.fusion),
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

    // 수정
    private void setupRecyclerView() {
        // Top Restaurant RecyclerView 설정
        RecyclerView topRestaurantRecyclerView = findViewById(R.id.restaurantsRecyclerView); //recyclerview에 따른 수정
        topRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Michelin Restaurant RecyclerView 설정
        RecyclerView michelinRestaurantRecyclerView = findViewById(R.id.michelin_restaurants_recycler_view); //수정22
        michelinRestaurantRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // 각각의 RecyclerView에 대해 별도의 어댑터 인스턴스 생성
        List<Restaurant> topRestaurantList = new ArrayList<>();
        List<Restaurant> michelinRestaurantList = new ArrayList<>();

        MainRestaurantAdapter topRestaurantAdapter = new MainRestaurantAdapter(this, topRestaurantList);
        MainRestaurantAdapter michelinRestaurantAdapter = new MainRestaurantAdapter(this, michelinRestaurantList);

        topRestaurantRecyclerView.setAdapter(topRestaurantAdapter);
        michelinRestaurantRecyclerView.setAdapter(michelinRestaurantAdapter);

        // 각각의 어댑터에 맞는 데이터를 로드하여 전달
        loadTopRestaurantsIntoAdapter(topRestaurantAdapter);
        loadMichelinRestaurantsIntoAdapter(michelinRestaurantAdapter);
    }

    private void loadTopRestaurantsIntoAdapter(MainRestaurantAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference seoulRef = db.document("categories/Location/Subcategories/Seoul");
        DocumentReference topRestaurantRef = db.document("categories/SpecificCategories/Subcategories/Top_Restaurant");

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> topRestaurantList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");
                            boolean matchesSeoul = false;
                            boolean containsTopRestaurant = false;

                            for (DocumentReference ref : categoryIds) {
                                if (ref.getPath().startsWith(seoulRef.getPath())) {
                                    matchesSeoul = true;
                                }
                                if (ref.getPath().equals(topRestaurantRef.getPath())) {
                                    containsTopRestaurant = true;
                                }
                            }

                            if (matchesSeoul && containsTopRestaurant) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setViewType(MainRestaurantAdapter.VIEW_TYPE_TOP_RESTAURANT);
                                topRestaurantList.add(restaurant);
                            }
                        }

                        // Pass the filtered data to the adapter
                        adapter.updateData(topRestaurantList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }

    private void loadMichelinRestaurantsIntoAdapter(MainRestaurantAdapter adapter) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference seoulRef = db.document("categories/Location/Subcategories/Seoul");
        DocumentReference michelinRestaurantRef = db.document("categories/SpecificCategories/Subcategories/Michelin_Restaurant");

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> michelinRestaurantList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");
                            boolean matchesSeoul = false;
                            boolean containsMichelinRestaurant = false;

                            for (DocumentReference ref : categoryIds) {
                                if (ref.getPath().startsWith(seoulRef.getPath())) {
                                    matchesSeoul = true;
                                }
                                if (ref.getPath().equals(michelinRestaurantRef.getPath())) {
                                    containsMichelinRestaurant = true;
                                }
                            }

                            if (matchesSeoul && containsMichelinRestaurant) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurant.setViewType(MainRestaurantAdapter.VIEW_TYPE_MICHELIN_RESTAURANT);
                                michelinRestaurantList.add(restaurant);
                            }
                        }

                        // Pass the filtered data to the adapter
                        adapter.updateData(michelinRestaurantList);
                    } else {
                        Log.w("Firestore", "Error getting documents.", task.getException());
                    }
                });
    }



    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No action needed
    }
}
