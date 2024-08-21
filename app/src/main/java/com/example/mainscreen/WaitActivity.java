package com.example.mainscreen;

import android.app.Activity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class WaitActivity extends AppCompatActivity {

    private static final String TAG = "WaitActivity";
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
        Log.d(TAG, "Received in WaitActivity - locationPath: " + locationPath + ", matchType: " + matchType);

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
                                // 요일 및 현재 시간 확인
                                String currentDay = getCurrentDay();
                                String currentTime = getCurrentTime();

                                // Firestore에서 요일별 영업 시간을 가져오기
                                Map<String, Object> hoursData = (Map<String, Object>) document.get("opening_hours." + currentDay);

                                if (hoursData != null) {
                                    String open = (String) hoursData.get("open");
                                    String close = (String) hoursData.get("close");

                                    // 현재 시간이 open과 close 시간 사이인지 확인
                                    if (isRestaurantOpen(currentTime, open, close)) {
                                        Restaurant restaurant = document.toObject(Restaurant.class);
                                        restaurantList.add(restaurant);
                                        documentIdList.add(document.getId()); // 문서 ID 추가
                                    }
                                }
                            }
                        }

                        Log.d(TAG, "Matched restaurants count: " + restaurantList.size());

                        if (restaurantList.isEmpty()) {
                            Log.w(TAG, "No restaurants matched the criteria.");
                        } else {
                            Log.d(TAG, "Matched restaurants count: " + restaurantList.size());
                        }

                        // RecyclerView 어댑터 설정
                        RestaurantAdapter adapter = new RestaurantAdapter(WaitActivity.this, restaurantList, documentIdList);
                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(WaitActivity.this, "Failed to load restaurant data from Firestore.", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    // 현재 요일을 반환하는 메서드
    private String getCurrentDay() {
        String[] daysOfWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Seoul"));
        return daysOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    // 현재 시간을 반환하는 메서드
    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String currentTime = sdf.format(new Date());
        // 로그 추가
        Log.d("getCurrentTime", "Current Time (KST): " + currentTime);
        return sdf.format(new Date());
    }

    // 식당이 현재 영업 중인지 확인하는 메서드
    private boolean isRestaurantOpen(String currentTime, String open, String close) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));

            Date current = sdf.parse(currentTime);
            Date openTime = sdf.parse(open);
            Date closeTime = sdf.parse(close);

            // 자정을 넘는 경우 처리
            if (closeTime.before(openTime)) {
                Calendar closeCal = Calendar.getInstance();
                closeCal.setTime(closeTime);
                closeCal.add(Calendar.DATE, 1); // closeTime을 다음 날로 이동
                closeTime = closeCal.getTime();
            }

            return current.after(openTime) && current.before(closeTime);

        } catch (Exception e) {
            Log.e(TAG, "Error parsing date/time", e);
            return false;
        }
    }
}

