package com.example.mainscreen;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import java.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RestaurantDetailsActivity extends AppCompatActivity {

    private boolean isHoursExpanded = false;
    private Button buttonBook, buttonWait;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book);


        // Intent에서 문서 ID, 이미지 경로, 이름 가져오기
        Intent intent = getIntent();
        String restaurantId = intent.getStringExtra("restaurant_id");
        String name = intent.getStringExtra("name");
        LinearLayout openingHoursContainer = findViewById(R.id.opening_hours_container);
        TextView textViewCurrentStatus = findViewById(R.id.textview_current_status);
        //TextView textViewLastOrderTime = findViewById(R.id.textview_last_order_time);
        Button buttonToggleHours = findViewById(R.id.button_toggle_hours);

        Log.d("RestaurantDetailsActivity", "Received - RestaurantId: " + restaurantId + ", Name: " + name);

        // Firestore 초기화
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // 리뷰 추가 TextView를 찾아 클릭 리스너 설정
        TextView addReviewTextView = findViewById(R.id.add_review);
        addReviewTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ReviewActivity로 넘어가는 Intent 생성
                Intent reviewIntent = new Intent(RestaurantDetailsActivity.this, ReviewActivity.class);
                reviewIntent.putExtra("restaurant_id", restaurantId); // 필요한 경우, 식당 ID를 넘겨줄 수 있습니다.
                startActivity(reviewIntent);
            }
        });

        // Firestore에서 문서 ID로 필터링
        db.collection("restaurants").document(restaurantId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {

                        // 이름 가져오기 (restaurant_name)
                        TextView nameTextView = findViewById(R.id.restaurant_name);
                        nameTextView.setText(name != null ? name : "Unknown");

                        // 이미지 경로 가져오기 (image_container)
                        List<String> imagePaths = (List<String>) documentSnapshot.get("imagePath");
                        LinearLayout imageContainer = findViewById(R.id.image_container);
                        if (imagePaths != null && !imagePaths.isEmpty()) {
                            for (String path : imagePaths) {
                                ImageView imageView = new ImageView(this);
                                loadImageFromStorage(path, imageView);
                                imageContainer.addView(imageView);
                            }
                        } else {
                            ImageView imageView = new ImageView(this);
                            imageView.setImageResource(R.drawable.default_image);
                            imageContainer.addView(imageView);
                        }

                        // 가격 범위 가져오기 (button_price)
                        String priceRange = documentSnapshot.getString("price_range");
                        Button priceButton = findViewById(R.id.button_price);
                        priceButton.setText(priceRange != null ? priceRange : "N/A");

                        // category_ids 한 번만 가져오기
                        List<DocumentReference> categoryIds = (List<DocumentReference>) documentSnapshot.get("category_ids");

                        Button cuisineButton = findViewById(R.id.button_cuisine);
                        Button locationButton = findViewById(R.id.button_location);

                        if (categoryIds != null && !categoryIds.isEmpty()) {
                            for (DocumentReference ref : categoryIds) {
                                String path = ref.getPath();

                                // cuisine 정보 가져오기 (button_cuisine)
                                if (path.startsWith("categories/FoodType")) {
                                    String[] segments = path.split("/");
                                    String lastSegment = segments[segments.length - 1];
                                    cuisineButton.setText(lastSegment != null ? lastSegment : "N/A");
                                }

                                // location 정보 가져오기 (button_location)
                                if (path.startsWith("categories/Location")) {
                                    String[] segments = path.split("/");
                                    String lastSegment = segments[segments.length - 1];
                                    locationButton.setText(lastSegment != null ? lastSegment : "N/A");
                                }
                            }
                        } else {
                            cuisineButton.setText("N/A");
                            locationButton.setText("N/A");
                        }

                        // 메뉴 이미지 가져오기 (menu_container)
                        List<String> menuUrls = (List<String>) documentSnapshot.get("menu.menu_url");
                        LinearLayout menuContainer = findViewById(R.id.menu_container);
                        if (menuUrls != null && !menuUrls.isEmpty()) {
                            for (String menuUrl : menuUrls) {
                                ImageView menuImageView = new ImageView(this);
                                loadImageFromStorage(menuUrl, menuImageView);
                                menuContainer.addView(menuImageView);
                            }
                        }

                        // 영업 시간 설정
                        displayOpeningHours(openingHoursContainer, documentSnapshot);

                        // *** 현재 상태 및 라스트 오더 시간 설정 - 여기에 동적 로직을 추가합니다. ***
                        // 현재 시간 및 요일
                        Calendar calendar = Calendar.getInstance();
                        Date currentTime = calendar.getTime();
                        String currentDay = new SimpleDateFormat("EEEE", Locale.getDefault()).format(currentTime);
                        //String formattedCurrentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(currentTime);

                        // 요일별 데이터를 가져오기
                        Map<String, Object> hoursData = (Map<String, Object>) documentSnapshot.get("opening_hours." + currentDay);

                        if (hoursData != null) {
                            String open = (String) hoursData.get("open");
                            String close = (String) hoursData.get("close");
                            String breakStart = (String) hoursData.get("break_start");
                            String breakEnd = (String) hoursData.get("break_end");

                            // 현재 시간 계산
                            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            String formattedCurrentTime = sdf.format(new Date());

                            // 현재 상태 계산
                            String currentStatus = calculateCurrentStatus(formattedCurrentTime, open, close, breakStart, breakEnd);
                            textViewCurrentStatus.setText(currentStatus != null ? currentStatus : "N/A");
                        }else {
                            textViewCurrentStatus.setText("closed"); // 특정 요일에 데이터가 없는 경우 'closed'로 표시
                        }
                        // *** 동적 상태 설정 끝 ***

                        // 현재 상태 및 라스트 오더 시간 설정
                        //String currentStatus = "영업 중"; // 로직에 따라 이 값을 동적으로 설정 가능
                        //String lastOrderTime = "19:50"; // 현재 시간을 기준으로 동적으로 설정 가능

                        //textViewCurrentStatus.setText(currentStatus != null ? currentStatus : "N/A");
                        //textViewLastOrderTime.setText(lastOrderTime != null ? lastOrderTime + "에 라스트 오더" : "N/A");

                        //버튼 클릭 시 영업 시간 펼치기/접기
                        buttonToggleHours.setOnClickListener(v -> {
                            if (isHoursExpanded) {
                                openingHoursContainer.setVisibility(View.GONE);
                                buttonToggleHours.setText("▼");
                            } else {
                                openingHoursContainer.setVisibility(View.VISIBLE);
                                buttonToggleHours.setText("▲");
                            }
                            isHoursExpanded = !isHoursExpanded;
                        });

                        // 위치 정보 가져오기 (textview_location)
                        String location = documentSnapshot.getString("location");
                        TextView locationTextView = findViewById(R.id.textview_location);
                        locationTextView.setText(location != null ? location : "N/A");

                        // 연락처 정보 가져오기 (textview_contact)
                        String contactNumber = documentSnapshot.getString("contact_number");
                        TextView contactTextView = findViewById(R.id.textview_contact);
                        contactTextView.setText(contactNumber != null ? contactNumber : "N/A");

                        // 음식 종류 정보 가져오기 (textview_cuisine)
                        List<String> categories = (List<String>) documentSnapshot.get("categories");
                        TextView cuisineTextView = findViewById(R.id.textview_cuisine);
                        cuisineTextView.setText(categories != null ? TextUtils.join(" ", categories) : "N/A");
                    } else {
                        Log.d("Firestore", "No such document");
                    }
                }).addOnFailureListener(e -> {
                    Log.d("Firestore", "get failed with ", e);
                });

        buttonBook = findViewById(R.id.button_book);
        buttonWait = findViewById(R.id.button_wait);

        // 기본적으로 두 버튼을 모두 표시
        buttonBook.setVisibility(View.VISIBLE);
        buttonWait.setVisibility(View.VISIBLE);

        // 메인 화면에서 전달된 인텐트 데이터 확인
        String showButton = getIntent().getStringExtra("SHOW_BUTTON");
        Log.d("RestaurantDetailsActivity", "SHOW_BUTTON value: " + showButton);
        if ("BOOK".equals(showButton)) {
            buttonWait.setVisibility(View.GONE); // Wait 버튼 숨기기
        } else if ("WAIT".equals(showButton)) {
            buttonBook.setVisibility(View.GONE); // Book 버튼 숨기기
        }

        buttonBook.setOnClickListener(v -> {
            // Booking 버튼 클릭 시 Book2Activity로 이동
            Intent bookintent = new Intent(RestaurantDetailsActivity.this, Book2Activity.class);
            bookintent.putExtra("restaurant_id", restaurantId); // 문서 ID 전달
            startActivity(bookintent);
        });

        buttonWait.setOnClickListener(v -> {
            // Wait 버튼 클릭 시 WaitActivity2로 이동
            Intent waitintent = new Intent(RestaurantDetailsActivity.this, WaitActivity2.class);
            waitintent.putExtra("restaurant_id", restaurantId); // 문서 ID 전달
            startActivity(waitintent);
        });

    }

    private String calculateCurrentStatus(String currentTime, String open, String close, String breakStart, String breakEnd) {
        try {
            if (open == null || close == null) {
                return "closed"; // 영업 시간이 없는 경우, 'closed'로 표시
            }
            // UTC 시간에서 현재 시간 얻기
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            Date currentUTC = calendar.getTime();

            // UTC 시간을 한국 시간으로 변환
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String formattedCurrentTime = sdf.format(currentUTC);
            Date current = sdf.parse(formattedCurrentTime);

            // 영업 시간 파싱
            Date openTime = sdf.parse(open);
            Date closeTime = sdf.parse(close);

            // 로그로 현재 시간을 출력
            Log.d("calculateCurrentStatus", "Current Time (KST): " + formattedCurrentTime);
            Log.d("calculateCurrentStatus", "Open Time: " + open);
            Log.d("calculateCurrentStatus", "Close Time: " + close);

            // 자정을 넘는 경우의 처리
            if (closeTime.before(openTime)) {
                // 자정을 넘는 경우: closeTime이 다음 날에 있음
                Calendar closeCal = Calendar.getInstance();
                closeCal.setTime(closeTime);
                closeCal.add(Calendar.DATE, 1); // closeTime을 다음 날로 이동
                closeTime = closeCal.getTime();

                // 현재 시간이 자정을 넘어 다음 날 closeTime 이전인지 확인
                if (current.before(sdf.parse("23:59"))) {
                    // 현재 시간이 자정 이전이면 openTime 이후에 있는지 확인
                    if (current.after(openTime)) {
                        Log.d("calculateCurrentStatus", "Status: open");
                        return "open";
                    }
                } else {
                    // 현재 시간이 자정 이후이면 다음 날 closeTime 이전인지 확인
                    if (current.before(closeTime)) {
                        Log.d("calculateCurrentStatus", "Status: open");
                        return "open";
                    }
                }
            } else {
                // 일반적인 경우 (자정을 넘지 않는 경우)
                if (current.after(openTime) && current.before(closeTime)) {
                    Log.d("calculateCurrentStatus", "Status: open");
                    return "open";
                }
            }

            if (breakStart != null && breakEnd != null) {
                Date breakStartTime = sdf.parse(breakStart);
                Date breakEndTime = sdf.parse(breakEnd);

                // 로그로 쉬는 시간도 출력
                Log.d("calculateCurrentStatus", "Break Start Time: " + breakStart);
                Log.d("calculateCurrentStatus", "Break End Time: " + breakEnd);

                if (current.after(breakStartTime) && current.before(breakEndTime)) {
                    return "break"; // 쉬는 시간인 경우, 'break'로 표시
                }
            }

            if (current.after(openTime) && current.before(closeTime)) {
                return "open"; // 영업 시간 내인 경우, 'open'으로 표시
            } else {
                return "closed"; // 영업 시간이 아닌 경우, 'closed'로 표시
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "N/A"; // 예외 발생 시, 'N/A'로 표시
        }
    }



    private void loadImageFromStorage(String imagePath, ImageView imageView) {
        if (imagePath == null || imagePath.isEmpty()) {
            Log.e("RestaurantDetailsActivity", "Invalid imagePath: " + imagePath);
            imageView.setImageResource(R.drawable.default_image);  // 기본 이미지 설정
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child(imagePath);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(RestaurantDetailsActivity.this)
                    .load(uri)
                    .apply(new RequestOptions().override(750, 600).error(R.drawable.default_image))
                    .into(imageView);

            imageView.setOnClickListener(v -> {
                Intent intent = new Intent(RestaurantDetailsActivity.this, FullScreenImageActivity.class);
                intent.putExtra("image_url", uri.toString());  // imageUrl 대신 uri.toString() 사용
                startActivity(intent);
            });

        }).addOnFailureListener(exception -> {
            Log.e("Firebase Storage", "Error getting image URL. File not found at location.", exception);
            imageView.setImageResource(R.drawable.default_image);  // 기본 이미지 설정
        });

    }

    private void displayOpeningHours(LinearLayout openingHoursContainer, DocumentSnapshot documentSnapshot) {
        Map<String, Map<String, Object>> openingHoursData = new LinkedHashMap<>();
        openingHoursData.put("Monday", (Map<String, Object>) documentSnapshot.get("opening_hours.Monday"));
        openingHoursData.put("Tuesday", (Map<String, Object>) documentSnapshot.get("opening_hours.Tuesday"));
        openingHoursData.put("Wednesday", (Map<String, Object>) documentSnapshot.get("opening_hours.Wednesday"));
        openingHoursData.put("Thursday", (Map<String, Object>) documentSnapshot.get("opening_hours.Thursday"));
        openingHoursData.put("Friday", (Map<String, Object>) documentSnapshot.get("opening_hours.Friday"));
        openingHoursData.put("Saturday", (Map<String, Object>) documentSnapshot.get("opening_hours.Saturday"));
        openingHoursData.put("Sunday", (Map<String, Object>) documentSnapshot.get("opening_hours.Sunday"));

        for (Map.Entry<String, Map<String, Object>> entry : openingHoursData.entrySet()) {
            String day = entry.getKey();
            Map<String, Object> hoursData = entry.getValue();

            View dayView = LayoutInflater.from(this).inflate(R.layout.item_opening_hour, openingHoursContainer, false);

            TextView tvDay = dayView.findViewById(R.id.tv_day);
            TextView tvOpeningHours = dayView.findViewById(R.id.tv_opening_hours);
            TextView tvBreakHours = dayView.findViewById(R.id.tv_break_hours);
            TextView tvLastOrder = dayView.findViewById(R.id.tv_last_order);
            TextView tvLastOrder2 = dayView.findViewById(R.id.tv_last_order_2);
            //LinearLayout llHours = dayView.findViewById(R.id.ll_hours);

            tvDay.setText(day);

            if (hoursData == null || Boolean.TRUE.equals(hoursData.get("closed"))) {
                tvOpeningHours.setText("closed");
            } else {
                String open = (String) hoursData.get("open");
                String close = (String) hoursData.get("close");
                String breakStart = (String) hoursData.get("break_start");
                String breakEnd = (String) hoursData.get("break_end");
                String lastOrder = (String) hoursData.get("last_order");
                String lastOrder1 = (String) hoursData.get("last_order_1");
                String lastOrder2 = (String) hoursData.get("last_order_2");

                // 영업 시간 설정
                tvOpeningHours.setText((!TextUtils.isEmpty(open) ? open : "N/A") + " - " + (!TextUtils.isEmpty(close) ? close : "N/A"));


                // 쉬는 시간 설정
                if (!TextUtils.isEmpty(breakStart) && !TextUtils.isEmpty(breakEnd)) {
                    tvBreakHours.setVisibility(View.VISIBLE);
                    tvBreakHours.setText(breakStart + " - " + breakEnd + " break time");
                }

                // 라스트 오더 설정
                if (!TextUtils.isEmpty(lastOrder)) {
                    tvLastOrder.setVisibility(View.VISIBLE);
                    tvLastOrder.setText(lastOrder + " last order");
                } else {
                    if (!TextUtils.isEmpty(lastOrder1)) {
                        tvLastOrder.setVisibility(View.VISIBLE);
                        tvLastOrder.setText(lastOrder1 + " lunch last order");
                    }
                    if (!TextUtils.isEmpty(lastOrder2)) {
                        tvLastOrder2.setVisibility(View.VISIBLE);
                        tvLastOrder2.setText(lastOrder2 + " dinner last order");
                    }
                }

            }

            openingHoursContainer.addView(dayView);
        }
    }
}
