package com.example.mainscreen;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RestaurantsActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantsActivity";
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.restaurants);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        // RecyclerView 찾기
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Intent로부터 locationPath와 matchType 값을 가져오기
        String locationPath = getIntent().getStringExtra("locationPath");
        String matchType = getIntent().getStringExtra("matchType");
        Log.d(TAG, "Received in RestaurantsActivity - locationPath: " + locationPath + ", matchType: " + matchType);

        // Firestore에서 데이터 비동기적으로 읽기
        loadRestaurantsFromFirestore(locationPath, matchType);
    }

    //*카드뷰 수정사항 반영 load~Firestore()
    private void loadRestaurantsFromFirestore(String locationPath, String matchType) {

        Log.d(TAG, "Received locationPath: " + locationPath);
        Log.d(TAG, "Received matchType: " + matchType);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurantList = new ArrayList<>();
                        List<String> documentIdList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");

                            // 각 문서의 ID와 category_ids 로그 출력
                            Log.d(TAG, "Document ID: " + document.getId());
                            Log.d(TAG, "Category IDs: " + categoryIds);

                            boolean matches = false;

                            // matchType에 따라 처리 방식 결정
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
                                documentIdList.add(document.getId()); // 문서 ID 추가
                            }
                        }
                            // 매칭된 레스토랑의 수 로그 출력
                            Log.d(TAG, "Matched restaurants count: " + restaurantList.size());


                        if (restaurantList.isEmpty()) {
                            Log.w(TAG, "No restaurants matched the criteria.");
                        }else {
                            Log.d(TAG, "Matched restaurants count: " + restaurantList.size());
                        }

                        // RecyclerView 어댑터 설정
                        RestaurantAdapter adapter = new RestaurantAdapter(RestaurantsActivity.this, restaurantList, documentIdList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(RestaurantsActivity.this, "Failed to load restaurant data from Firestore.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }

                });
    }
}
