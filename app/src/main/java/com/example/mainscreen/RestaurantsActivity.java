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

        // Firestore에서 데이터 비동기적으로 읽기
        loadRestaurantsFromFirestore();
    }

    private void loadRestaurantsFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String locationPath = "categories/Location/Subcategories/Seoul";

        db.collection("restaurants")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Restaurant> restaurantList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // 각 문서의 category_ids 필드 가져오기
                            List<DocumentReference> categoryIds = (List<DocumentReference>) document.get("category_ids");

                            // category_ids에서 locationPath로 시작하는 경로가 있는지 확인
                            boolean matchesLocation = false;
                            for (DocumentReference ref : categoryIds) {
                                if (ref.getPath().startsWith(locationPath)) {
                                    matchesLocation = true;
                                    break;
                                }
                            }

                            // locationPath에 해당하는 문서만 리스트에 추가
                            if (matchesLocation) {
                                Restaurant restaurant = document.toObject(Restaurant.class);

                                // 로그 추가: name 및 imagePath 확인
                                Log.d(TAG, "Restaurant Name: " + restaurant.getName());
                                Log.d(TAG, "Restaurant Image Paths: " + restaurant.getImagePath());

                                restaurantList.add(restaurant);
                            }
                        }

                        // RecyclerView 어댑터 설정
                        RestaurantAdapter adapter = new RestaurantAdapter(RestaurantsActivity.this, restaurantList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        // 오류 메시지 표시
                        Toast.makeText(RestaurantsActivity.this, "Failed to load restaurant data from Firestore.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}
