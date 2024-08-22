package com.example.mainscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantsActivity";
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private List<Restaurant> restaurantList;
    private List<String> documentIdList;
    private FirebaseFirestore db;
    private String locationPath;
    private String matchType;
    private String showButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent로부터 locationPath와 matchType 값을 가져오기
        locationPath = getIntent().getStringExtra("locationPath");
        matchType = getIntent().getStringExtra("matchType");
        showButton = getIntent().getStringExtra("SHOW_BUTTON");
        Log.d(TAG, "Received in RestaurantsActivity - locationPath: " + locationPath + ", matchType: " + matchType);

        // Firebase 초기화
        db = FirebaseFirestore.getInstance();

        // Firestore에서 데이터 비동기적으로 읽기
        loadRestaurantsFromFirestore(locationPath, matchType, showButton);

        // SearchView 설정
        setupSearchView();
    }

    private void loadRestaurantsFromFirestore(String locationPath, String matchType, String showButton) {
        Log.d(TAG, "Received locationPath: " + locationPath);
        Log.d(TAG, "Received matchType: " + matchType);

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        restaurantList = new ArrayList<>();
                        documentIdList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");

                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "Category IDs: " + categoryIds);

                            boolean matches = false;

                            if ("exactMatch".equals(matchType)) {
                                for (DocumentReference ref : categoryIds) {
                                    Log.d(TAG, "Checking exact match for: " + ref.getPath() + " against " + locationPath);
                                    if (ref.getPath().equals(locationPath)) {
                                        matches = true;
                                        break;
                                    }
                                }
                            } else if ("startsWith".equals(matchType)) {
                                for (DocumentReference ref : categoryIds) {
                                    Log.d(TAG, "Checking startsWith match for: " + ref.getPath() + " against " + locationPath);
                                    if (ref.getPath().startsWith(locationPath)) {
                                        matches = true;
                                        break;
                                    }
                                }
                            }

                            if (matches) {
                                Restaurant restaurant = document.toObject(Restaurant.class);
                                restaurantList.add(restaurant);
                                documentIdList.add(document.getId());
                            }
                        }

                        Log.d(TAG, "Matched restaurants count: " + restaurantList.size());

                        if (restaurantList.isEmpty()) {
                            Log.w(TAG, "No restaurants matched the criteria.");
                        } else {
                            Log.d(TAG, "Matched restaurants count: " + restaurantList.size());
                        }

                        // RecyclerView 어댑터 설정 & 어댑터를 생성할 때 showButton을 전달
                        adapter = new RestaurantAdapter(this, restaurantList, documentIdList, showButton);
                        recyclerView.setAdapter(adapter);

                    } else {
                        Toast.makeText(RestaurantsActivity.this, "Failed to load restaurant data from Firestore.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void setupSearchView() {
        SearchView searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        });
    }
}
